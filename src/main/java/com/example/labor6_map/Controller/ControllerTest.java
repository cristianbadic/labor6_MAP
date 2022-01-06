package com.example.labor6_map.Controller;
import com.example.labor6_map.Exception.CanNotRegister;
import com.example.labor6_map.Exception.DoesNotExistException;
import com.example.labor6_map.Exception.ExistentElementException;
import com.example.labor6_map.Exception.TeacherException;
import com.example.labor6_map.Repository.CourseMySQLRepository;
import com.example.labor6_map.Repository.StudentMySQLRepository;
import com.example.labor6_map.Repository.TeacherMySQLRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * IMPORTANT! The tests work if the database is empty and the tests are run one after the other, not at the same time!
 */
class ControllerTest {
    private TeacherMySQLRepository tr;
    private CourseMySQLRepository cr;
    private StudentMySQLRepository sr;
    private Controller contrTest;

    @BeforeEach
    void setup() {
        tr = new TeacherMySQLRepository("jdbc:mysql://localhost:3306/university", "root", "password1234");
        sr = new StudentMySQLRepository("jdbc:mysql://localhost:3306/university", "root", "password1234");
        cr = new CourseMySQLRepository("jdbc:mysql://localhost:3306/university", "root", "password1234");
        contrTest = new Controller(cr, tr, sr);
    }
    /**
     *  the controller is added and some valid Objects are added to all 3 repositories
     * 3 teachers, 3 students and 4 courses
     */
    @Test
    void startup(){
        tr = new TeacherMySQLRepository("jdbc:mysql://localhost:3306/university", "root", "password1234");
        sr =  new StudentMySQLRepository("jdbc:mysql://localhost:3306/university", "root", "password1234");
        cr = new CourseMySQLRepository("jdbc:mysql://localhost:3306/university", "root", "password1234");
        contrTest = new Controller(cr,tr,sr);

        try {
            contrTest.addTeacher("Dan", "Stan", 1);
        } catch (ExistentElementException |SQLException e) {
            Assertions.fail();
        }

        try {
            contrTest.addTeacher("Liviu", "Bran", 2);
        } catch (ExistentElementException |SQLException e) {
            Assertions.fail();
        }

        try {
            contrTest.addTeacher("Dan", "Stefan", 3);
        } catch (ExistentElementException |SQLException e) {
            Assertions.fail();
        }

        try {
            contrTest.addStudent("Bill", "Burr", 1);
        } catch (ExistentElementException |SQLException e) {
            Assertions.fail();
        }

        try {
            contrTest.addStudent("Bob", "Balint", 2);
        } catch (ExistentElementException  |SQLException e) {
            Assertions.fail();
        }

        try {
            contrTest.addStudent("Constantin", "Dinescu", 3);
        } catch (ExistentElementException  |SQLException e) {
            Assertions.fail();
        }

        try {
            contrTest.addCourse(1, "Computer Science", 1, 30, 6);
        } catch (ExistentElementException | DoesNotExistException  |SQLException e) {
            Assertions.fail();
        }

        try {
            contrTest.addCourse(2, "English", 2, 31, 6);
        } catch (ExistentElementException | DoesNotExistException  |SQLException e) {
            Assertions.fail();
        }

        try {
            contrTest.addCourse(3, "French", 3, 2, 15);
        } catch (ExistentElementException | DoesNotExistException  |SQLException e) {
            Assertions.fail();
        }

        try {
            contrTest.addCourse(4, "History", 3, 32, 6);
        } catch (ExistentElementException | DoesNotExistException  |SQLException e) {
            Assertions.fail();
        }

        try {
            contrTest.addCourse(5, "Spanish", 1, 4, 15);
        } catch (ExistentElementException | DoesNotExistException  |SQLException e) {
            Assertions.fail();
        }

    }

    @Test
    /**
     * the first 2 calls of register, register the course
     * the 3rd call, doesn't register because course 3 has already a maximum number of students
     * the 4th call, doesn't register the student id is invalid (there isn't a student with id 4 in the repo)
     */
    void register() {
        try {
            contrTest.register(3, 1);
        } catch (DoesNotExistException | CanNotRegister |SQLException e) {
            Assertions.fail();
        }

        try {
            contrTest.register(3, 2);
        } catch (DoesNotExistException | CanNotRegister |SQLException e) {
            Assertions.fail();
        }

        try {
            contrTest.register(3, 3);
            Assertions.fail();
        } catch (DoesNotExistException |SQLException e) {
            Assertions.fail();
        }catch(CanNotRegister e){
            Assertions.assertTrue(true);
        }

        try {
            contrTest.register(1, 4);
            Assertions.fail();
        } catch (CanNotRegister |SQLException e) {
            Assertions.fail();
        }catch(DoesNotExistException e){
            Assertions.assertTrue(true);
        }

    }

    /**
     *there is one course without free places, that is why there are n-1 courses with free places
     */
    @Test
    void retrieveCoursesWithFreePlaces() {
        try {
            assertEquals(contrTest.retrieveCoursesWithFreePlaces().size(), contrTest.getAllCourses().size()-1);
        } catch (SQLException e) {
            Assertions.fail();
        }
    }

    @Test
    void retrieveStudentsEnrolledForACourse() {

        try {
            assertEquals(contrTest.retrieveStudentsEnrolledForACourse(3).size(), 2);
        } catch (SQLException e) {
            Assertions.fail();
        }

        try {
            assertEquals(contrTest.retrieveStudentsEnrolledForACourse(1).size(), 0);
        } catch (SQLException e) {
            Assertions.fail();
        }


    }

    /**
     * the first call of deleteCourse, doesn't delete, because the courseId is invalid, (curse with id3 was already deleted)
     * the second call fails to delete because, the teacher with the given id doesn't teach the course to be deleted
     */
    @Test
    void deleteCourse() {
        try {
            contrTest.deleteCourse(6, 3);
            Assertions.fail();
        } catch (TeacherException |SQLException e) {
            Assertions.fail();
        }catch(DoesNotExistException e){
            Assertions.assertTrue(true);
        }

        try {
            contrTest.deleteCourse(2, 1);
            Assertions.fail();
        } catch (DoesNotExistException |SQLException e) {
            Assertions.fail();
        }catch(TeacherException e){
            Assertions.assertTrue(true);
        }
    }

    /**
     * add fails, because there is already a teacher with id=2, in the teachers repo
     */
    @Test
    void addTeacher() {
        try {
            try {
                contrTest.addTeacher("Mister", "White", 2);
            } catch (SQLException e) {
                Assertions.fail();
            }
            Assertions.fail();
        } catch (ExistentElementException e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    void addStudent() {
        try {
            try {
                contrTest.addStudent("Constantin", "Dinescu", 2);
            } catch (SQLException e) {
                Assertions.fail();
            }
            Assertions.fail();
        } catch (ExistentElementException e) {
            Assertions.assertTrue(true);
        }
    }

    /**
     * the first add fails because there is already a course with id=5
     * the second because there isn't a teacher with id=7 in the database
     */
    @Test
    void addCourse() {
        try {
            contrTest.addCourse(5, "Italian", 2, 10, 5);
            Assertions.fail();
        } catch (DoesNotExistException |SQLException e) {
            Assertions.fail();
        }catch (ExistentElementException e) {
            Assertions.assertTrue(true);
        }

        try {
            contrTest.addCourse(7, "DSA", 6, 10, 5);
            Assertions.fail();
        } catch ( ExistentElementException |SQLException e) {
            Assertions.fail();
        }catch (DoesNotExistException e) {
            Assertions.assertTrue(true);
        }

    }

    /**
     * student with id=1 is registered to 3 courses
     * student with id=2 is registered to 2 courses
     * student with id=3 is registered to 1 course
     * to test sorting of the students (by testing the places where the courses are in the students repository)
     */
    @Test
    void sortStudentCredits() {
        try {
            contrTest.register(1, 1);
        } catch (DoesNotExistException | CanNotRegister |SQLException e) {
            Assertions.fail();
        }

        try {
            contrTest.register(1, 2);
        } catch (DoesNotExistException | CanNotRegister |SQLException e) {
            Assertions.fail();
        }

        try {
            contrTest.register(1, 3);
        } catch (DoesNotExistException | CanNotRegister |SQLException e) {
            Assertions.fail();
        }

        try {
            contrTest.register(2, 1);
        } catch (DoesNotExistException | CanNotRegister |SQLException e) {
            Assertions.fail();
        }

        try {
            contrTest.sortStudentCredits();
        } catch (SQLException e) {
            Assertions.fail();
        }
        try {
            assertEquals(contrTest.sortStudentCredits().get(2).getStudentId(), 1);
        } catch (SQLException e) {
            Assertions.fail();
        }
        try {
            assertEquals(contrTest.sortStudentCredits().get(1).getStudentId(), 2);
        } catch (SQLException e) {
            Assertions.fail();
        }
        try {
            assertEquals(contrTest.sortStudentCredits().get(0).getStudentId(), 3);
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    /**
     * 2 students are registered to course with id =2
     * 3 students are registered to course with id =1
     * 1 student is registered to course with id =3
     * to test sorting of the courses (by testing the places where the courses are in the course's repository)
     */
    @Test
    void sortCourseNrStudents() {
        try {
            contrTest.sortCourseNrStudents();
        } catch (SQLException e) {
            Assertions.fail();
        }
        try {
            assertEquals(contrTest.sortCourseNrStudents().get(4).getCourseId(), 1);
        } catch (SQLException e) {
            Assertions.fail();
        }
        try {
            assertEquals(contrTest.sortCourseNrStudents().get(3).getCourseId(), 3);
        } catch (SQLException e) {
            Assertions.fail();
        }
        try {
            assertEquals(contrTest.sortCourseNrStudents().get(2).getCourseId(), 2);
        } catch (SQLException e) {
            Assertions.fail();
        }

    }

    @Test
    void filterStudents() {
        try {
            assertEquals(contrTest.filterStudents().size(), 3);
        } catch (SQLException e) {
            Assertions.fail();
        }

    }

    @Test
    void filterCourse() {
        try {
            assertEquals(contrTest.filterCourse().size(), contrTest.getAllCourses().size() -3);
        } catch (SQLException e) {
            Assertions.fail();
        }

        try {
            contrTest.addCourse(6, "DSA", 3, 35, 15);
        } catch (ExistentElementException | DoesNotExistException |SQLException e) {
            Assertions.fail();
        }
        try {
            assertEquals(contrTest.filterCourse().size(), contrTest.getAllCourses().size() -3);
        } catch (SQLException e) {
            Assertions.fail();
        }

    }
}