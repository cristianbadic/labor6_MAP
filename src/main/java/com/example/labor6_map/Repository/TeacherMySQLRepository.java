package com.example.labor6_map.Repository;

import com.example.labor6_map.Model.Teacher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeacherMySQLRepository implements ICrudRepository<Teacher>{
    private String url;
    private String user;
    private String password;

    public TeacherMySQLRepository(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    /**
     *
     * @param obj object of type Teacher that will be inserted in the database
     * @throws SQLException
     */
    @Override
    public void create(Teacher obj) throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);
        String insertIntoTable = String.format("insert into teachers(teacherID, firstName, lastName) values " +
                "(%d, \"%s\", \"%s\")", obj.getTeacherId(), obj.getFirstName(), obj.getLastName());
        Statement statement = connection.createStatement();
        int rows = statement.executeUpdate(insertIntoTable);
        statement.close();
        connection.close();
    }

    /**
     *
     * @return list of all teachers in the database
     * it is required to go through the teachers table to get all courses and also through the enrolled table in the database
     * to for the list of courses for each course
     * @throws SQLException
     */
    @Override
    public List<Teacher> getAll() throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);
        List<Teacher> teachers = new ArrayList<>();
        String allTeachers = String.format("select * from teachers");
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(allTeachers);

        while (result.next()){
            long teacherId = result.getLong("teacherId");
            String firstName = result.getString("firstName");
            String lastName = result.getString("lastName");

            List<Long> teacherCourses = new ArrayList<>();
            String gettingCourses = String.format("select c.courseId from courses c " +
                    "inner join teachers t on c.teacher = t.teacherId where c.teacher = %d", teacherId);

            Statement statementCourses = connection.createStatement();
            ResultSet listCourses = statementCourses.executeQuery(gettingCourses);
            while (listCourses.next()){
                teacherCourses.add(listCourses.getLong("courseId"));
            }
            Teacher teacher = new Teacher(firstName, lastName, teacherId, teacherCourses);
            teachers.add(teacher);
            statementCourses.close();
        }

        statement.close();
        connection.close();
        return teachers;
    }

    /**
     *
     * @param obj is a Teacher object that contains the new attributes for the teacher to be updated
     * @throws SQLException
     */
    @Override
    public void update(Teacher obj) throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);

        String updateTeacher = String.format("update teachers set firstName= \"%s\", lastName=\"%s\" " +
                "where teacherId=%d", obj.getFirstName(), obj.getLastName(), obj.getTeacherId());
        Statement statement = connection.createStatement();
        int rows = statement.executeUpdate(updateTeacher);

        statement.close();
        connection.close();
    }

    /**
     * if a teacher is deleted, we also need to delete the courses he teaches, and the enrollments related to that course
     * because courses are deleted, it is also required to update the total credits of the students formerly enrolled to the deleted courses
     * @param obj, Teacher object to be deleted
     * @throws SQLException
     */
    @Override
    public void delete(Teacher obj) throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);
        String allTeachers = String.format("select courseId, credits from courses where teacher=%d",
                obj.getTeacherId());
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(allTeachers);
        while (result.next()){
            //we remove the credits of the course to be deleted from the totalCredits of the student
            long courseID = result.getLong("courseId");
            long nrCredits = result.getLong("credits");
            Statement statementNrCredits = connection.createStatement();
            String updateCredits = String.format("update students\n" +
                    "inner join enrolled on students.studentId = enrolled.studentId\n" +
                    "set students.totalCredits = students.totalCredits-%d\n" +
                    "where students.studentId = enrolled.studentId and enrolled.courseId = %d", nrCredits, courseID);
            int rows = statementNrCredits.executeUpdate(updateCredits);
            statementNrCredits.close();
        }

        String deleteEnrollments =String.format("delete enrolled from enrolled " +
                "inner join courses on enrolled.courseId = courses.courseId " +
                "where courses.teacher=%d ", obj.getTeacherId());
        Statement statementEnrollments = connection.createStatement();
        int rows1 = statementEnrollments.executeUpdate(deleteEnrollments);
        statementEnrollments.close();

        String deleteCourses = String.format("delete courses from courses " +
                "where courses.teacher=%d ",obj.getTeacherId());
        Statement statementCourses = connection.createStatement();
        int rows2 = statementCourses.executeUpdate(deleteCourses);
        statementCourses.close();

        String deleteTeacher = String.format("delete teachers from teachers " +
                "where teachers.teacherId=%d ",obj.getTeacherId());
        Statement statementTeacher = connection.createStatement();
        int rows3 = statementTeacher.executeUpdate(deleteTeacher);
        statementTeacher.close();

        statement.close();
        connection.close();
    }

}
