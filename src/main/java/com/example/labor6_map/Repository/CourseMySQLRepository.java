package com.example.labor6_map.Repository;


import com.example.labor6_map.Model.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseMySQLRepository implements ICrudRepository<Course>{
    private String url;
    private String user;
    private String password;

    public CourseMySQLRepository(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    /**
     *
     * @param obj, object of type Course that will be inserted in the database
     * @throws SQLException
     */
    @Override
    public void create(Course obj) throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);
        Statement statement = connection.createStatement();

        String insertCourse = String.format("insert into courses(courseId, name, teacher, maxEnrollment, credits) " +
                "values (%d, \"%s\", %d, %d, %d)",
                obj.getCourseId(), obj.getName(), obj.getTeacher(), obj.getMaxEnrollment(), obj.getCredits());
        int rows = statement.executeUpdate(insertCourse);
        statement.close();
        connection.close();
    }

    /**
     *
     * @return list of all courses in the database
     * it is required to go through the courses table to get all courses and also through the enrolled table in the database
     * to get the list of students for each course
     * @throws SQLException
     */
    @Override
    public List<Course> getAll() throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);
        Statement statement = connection.createStatement();
        List<Course> courses = new ArrayList<>();
        String allStudents = String.format("select * from courses");
        ResultSet result = statement.executeQuery(allStudents);

        while (result.next()) {
            long courseId = result.getLong("courseId");
            String courseName = result.getString("name");
            long teacher = result.getLong("teacher");
            int maxEnroll = (int) result.getLong("maxEnrollment");
            int credits = (int) result.getLong("credits");


            List<Long> courseStudents = new ArrayList<>();
            String gettingStudents = String.format("select studentId from enrolled where courseId = %d", courseId);

            Statement statementStudents = connection.createStatement();
            ResultSet listStudents = statementStudents.executeQuery(gettingStudents);
            while (listStudents.next()) {
                courseStudents.add(listStudents.getLong("studentId"));
            }
            Course course = new Course(courseId, courseName, teacher, maxEnroll, credits, courseStudents);
            courses.add(course);
            statementStudents.close();
        }
        statement.close();
        connection.close();
        return courses;
    }

    /**
     * because the number of credits of a course can be changed, it is required to go through the Students table multiple times,
     * to update the total number of courses of each student associated to the course
     * @param obj is a Course object that contains the new attributes for the course to be updated
     * @throws SQLException
     */
    @Override
    public void update(Course obj) throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);
        Statement statement = connection.createStatement();

        //getting the old number of credits to update the student with the new number
        String getNrCredits = String.format("select credits from courses where courseId=%d", obj.getCourseId());
        ResultSet oldCredits = statement.executeQuery(getNrCredits);
        int formerCredits = 0;
        while(oldCredits.next()){
            formerCredits = (int) oldCredits.getLong("credits");
        }
        statement.close();

        //updating the number of credits of the students enrolled to the course
        Statement statementUpdateCredits = connection.createStatement();
        String updateCredits = String.format("update students " +
                "inner join enrolled on students.studentId=enrolled.studentId " +
                "set students.totalCredits =students.totalCredits-%d+%d " +
                "where students.studentId = enrolled.studentId and enrolled.courseId=%d", formerCredits, obj.getCredits(), obj.getCourseId());
        int rows = statementUpdateCredits.executeUpdate(updateCredits);

        //updating the Course with the new attributes
        Statement statementUpdateCourse = connection.createStatement();
        String updateCourse = String.format("update courses set name=\"%s\", maxEnrollment=%d, credits=%d " +
                "where courseId= %d", obj.getName(), obj.getMaxEnrollment(), obj.getCredits(), obj.getCourseId());
        int rows4 = statementUpdateCourse.executeUpdate(updateCourse);

        List<Long> newEnrolled = obj.getStudentsEnrolled();
        String oldEnrolled = String.format("Select studentId from enrolled where courseId=%d", obj.getCourseId());
        Statement statementEnrolled = connection.createStatement();
        ResultSet oldStudents = statementEnrolled.executeQuery(oldEnrolled);


        while(oldStudents.next()){
            long studentId = oldStudents.getLong("studentId");
            if (newEnrolled.contains(studentId)){
                newEnrolled.remove(studentId);
            }
            else{
                //deleting no longer available enrollments
                String deleteEnrollment = String.format("delete enrolled from enrolled where courseId=%d and studentId=%d", obj.getCourseId(), studentId);
                Statement statementDelEnrollment = connection.createStatement();
                int rows5 = statementDelEnrollment.executeUpdate(deleteEnrollment);

                //after we delete the old enrollment we need to update the credits of the Student
                Statement statementRemoveUnrolled = connection.createStatement();
                String updateNewStudentCredits = String.format("update students set students.totalCredits = students.totalCredits - %d " +
                        "where students.studentId=%d", obj.getCredits(), studentId);
                int rows6 = statementRemoveUnrolled.executeUpdate(updateNewStudentCredits);
                statementRemoveUnrolled.close();
                statementDelEnrollment.close();
            }
        }

        while (newEnrolled.size() != 0){
            //adding the new enrollments
            String insertEnrollment = String.format("insert into enrolled(studentId, courseId) values (%d, %d)",
                    newEnrolled.get(0), obj.getCourseId());
            Statement statementInsertEnrollment = connection.createStatement();
            int rows2=statementInsertEnrollment.executeUpdate(insertEnrollment);

            //after we add the new enrollment we need to update the credits of the Student
            Statement statementUpdateNewStudentCredits = connection.createStatement();
            String updateNewStudentCredits = String.format("update students set students.totalCredits = students.totalCredits + %d " +
                    "where students.studentId=%d", obj.getCredits(), newEnrolled.get(0));
            int rows6 = statementUpdateNewStudentCredits.executeUpdate(updateNewStudentCredits);
            statementUpdateNewStudentCredits.close();
            newEnrolled.remove(newEnrolled.get(0));
            statementInsertEnrollment.close();
        }
        statementEnrolled.close();
        connection.close();
    }

    /**
     * course is deleted from the database, also the enrollments related to the course and the
     * total credits of the students formerly enrolled to the course are updated
     * @param obj, Course object to be deleted from the database
     * @throws SQLException
     */
    @Override
    public void delete(Course obj) throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);
        Statement statement = connection.createStatement();
        String oldCredits = String.format("select s.studentId, s.totalCredits from students s " +
                "inner join enrolled e on s.studentId = e.studentId where e.courseId=%d", obj.getCourseId());
        ResultSet resultCredits = statement.executeQuery(oldCredits);

        while (resultCredits.next()) {
            long studentId = resultCredits.getLong("studentId");
            int credits = (int) resultCredits.getLong("totalCredits");
            credits = credits - obj.getCredits();
            Statement statementCredits = connection.createStatement();
            String newCredits = String.format("update students " +
                    "set students.totalCredits = %d " +
                    "where students.studentId = %d", credits, studentId);
            int rows = statementCredits.executeUpdate(newCredits);
            statementCredits.close();
        }
        Statement statementEnrolled = connection.createStatement();
        String deleteEnrollments = String.format("delete enrolled from enrolled where courseId=%d", obj.getCourseId());
        int rows2 = statementEnrolled.executeUpdate(deleteEnrollments);
        statementEnrolled.close();

        Statement statementCourse = connection.createStatement();
        String deleteCourse = String.format("delete courses from courses where courseId=%d", obj.getCourseId());
        int rows3= statementCourse.executeUpdate(deleteCourse);
        statementCourse.close();

        statement.close();
        connection.close();
    }

    /**
     * sorts the Course repo in ascending order by the number of enrolled students
     */
    public List<Course> sortRep() throws SQLException {
        List<Course> sortedC = this.getAll();
        sortedC.sort(Course::compareCourse);
        return sortedC;
    }
}
