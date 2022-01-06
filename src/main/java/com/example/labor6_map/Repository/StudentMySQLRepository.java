package com.example.labor6_map.Repository;

import com.example.labor6_map.Model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentMySQLRepository implements ICrudRepository<Student>{
    private String url;
    private String user;
    private String password;

    public StudentMySQLRepository(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    /**
     *
     * @param obj, object of type Student that will be inserted in the database
     * @throws SQLException
     */
    @Override
    public void create(Student obj) throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);
        Statement statement = connection.createStatement();

        String insertIntoTable = String.format("insert into students" +
                "(studentId, firstName, lastName, totalCredits) " +
                "values (%d, \"%s\", \"%s\", 0)", obj.getStudentId(), obj.getFirstName(), obj.getLastName());
        int rows = statement.executeUpdate(insertIntoTable);
        statement.close();
        connection.close();

    }

    /**
     *
     * @return list of all students from the Database
     * it is required to go through the students table to get all students and also through the enrolled table in the database
     * to get the list of courses for each student
     * @throws SQLException
     */
    @Override
    public List<Student> getAll() throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);

        List<Student> students = new ArrayList<>();
        String allStudents = String.format("select * from students");
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(allStudents);

        while (result.next()){
            long studentId = result.getLong("studentId");
            String firstName = result.getString("firstName");
            String lastName = result.getString("lastName");
            int credits = (int) result.getLong("totalCredits");

            List<Long> studentCourses = new ArrayList<>();
            String gettingCourses = String.format("select courseId from enrolled where studentId = %d", studentId);

            Statement statementCourses = connection.createStatement();
            ResultSet listCourses = statementCourses.executeQuery(gettingCourses);
            while (listCourses.next()){
                studentCourses.add(listCourses.getLong("courseId"));
            }
           Student student = new Student(firstName, lastName, studentId,credits,studentCourses);
            students.add(student);
            statementCourses.close();
        }

        statement.close();
        connection.close();
        return students;
    }

    /**
     *need to add and delete the new/former enrollment based on the course list given as a parameter as part of the Student obj
     * also the new number of total credits of the student needs to be calculated
     * @param obj is a Student object that contains the new attributes for the Student to be updated
     * @throws SQLException
     */
    @Override
    public void update(Student obj) throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);

        int studentCredits = 0;
        List<Long> newEnrolled = obj.getEnrolledCourses();
        String oldEnrolled = String.format("Select courseId from enrolled where studentId=%d", obj.getStudentId());
        Statement statementEnrolled = connection.createStatement();
        ResultSet oldCourses = statementEnrolled.executeQuery(oldEnrolled);
        while(oldCourses.next()){
            long courseId = oldCourses.getLong("courseId");
            if (newEnrolled.contains(courseId)){
                //if a course is already enrolled and won't be deleted we need to increase the studentCredits(totalCredits)
                String getCredit = String.format("select credits from courses where courseId=%d",courseId);
                Statement statementNrCredits = connection.createStatement();
                ResultSet resultCr = statementNrCredits.executeQuery(getCredit);
                while(resultCr.next()){
                    long courseCredits= resultCr.getLong("credits");
                    studentCredits += courseCredits;
                }
                newEnrolled.remove(courseId);
                statementNrCredits.close();
            }
            else{
                //if a course is not in the list of courses, the enrollment is deleted
                String deleteEnrollment = String.format("delete enrolled from enrolled where courseId=%d and studentId=%d", courseId, obj.getStudentId());
                Statement statementDelEnrollment = connection.createStatement();
                int rows = statementDelEnrollment.executeUpdate(deleteEnrollment);
                statementDelEnrollment.close();
            }
        }
        while (newEnrolled.size() != 0){
            //inserting the new enrollment and also increasing the studentCredits
            String insertEnrollment = String.format("insert into enrolled(studentId, courseId) values (%d, %d)",
                    obj.getStudentId(), newEnrolled.get(0));
            Statement statementInsertEnrollment = connection.createStatement();
            int rows2=statementInsertEnrollment.executeUpdate(insertEnrollment);
            String getCredit2 = String.format("select credits from courses where courseId=%d",newEnrolled.get(0));
            Statement statementNrCredits2 = connection.createStatement();
            ResultSet result2 = statementNrCredits2.executeQuery(getCredit2);
            while(result2.next()){
                long courseCredits2= result2.getLong("credits");
                studentCredits += courseCredits2;
            }
            newEnrolled.remove(newEnrolled.get(0));
            statementInsertEnrollment.close();
        }

        //updating the student with the new attributes
        Statement updateStatement = connection.createStatement();
        String updateStudent = String.format("update students set firstName=\"%s\", lastName=\"%s\", totalCredits=%d " +
                        "where studentId=%d", obj.getFirstName(), obj.getLastName(), studentCredits, obj.getStudentId());
        int rows3 = updateStatement.executeUpdate(updateStudent);
        statementEnrolled.close();
        connection.close();

    }

    /**
     * before deleting the student, the enrollments associated to the student need to be deleted
     * @param obj, student to be deleted
     * @throws SQLException
     */
    @Override
    public void delete(Student obj) throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);
        Statement statement = connection.createStatement();

        String deleteEnrollments = String.format("delete enrolled from enrolled where studentId=%2d", obj.getStudentId());
        int rows = statement.executeUpdate(deleteEnrollments);
        statement.close();
        Statement statementStudent = connection.createStatement();
        String deleteStudents = String.format("delete students from students where studentId=%2d", obj.getStudentId());
        int rows2 = statementStudent.executeUpdate(deleteStudents);

        statementStudent.close();
        connection.close();
    }


    /**
     * sorts the Student repo in ascending order by the number of credits
     */
    public List<Student> sortRep() throws SQLException {
        List<Student> sortedStud = this.getAll();
        sortedStud.sort(Student::compareStudent);
        return sortedStud;

    }
}
