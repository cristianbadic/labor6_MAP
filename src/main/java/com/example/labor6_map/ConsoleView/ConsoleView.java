package com.example.labor6_map.ConsoleView;

import com.example.labor6_map.Controller.Controller;
import com.example.labor6_map.Exception.CanNotRegister;
import com.example.labor6_map.Exception.DoesNotExistException;
import com.example.labor6_map.Exception.ExistentElementException;
import com.example.labor6_map.Exception.TeacherException;
import com.example.labor6_map.Model.Course;
import com.example.labor6_map.Model.Student;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ConsoleView {
    private Controller controller;

    public ConsoleView(Controller controller) {
        this.controller = controller;
    }

    /**
     * the option menu for the user
     *
     * @throws DoesNotExistException
     * @throws TeacherException
     * @throws ExistentElementException
     * @throws IOException
     * @throws CanNotRegister
     */
    public void run() throws DoesNotExistException, TeacherException, ExistentElementException, IOException, CanNotRegister, SQLException {
        Scanner scannerCV = new Scanner(System.in);
        boolean running = true;
        int option;
        while (running){
            this.CVMenu();
            System.out.println("Choose Option: ");
            option = scannerCV.nextInt();
            switch (option){
                case 0:
                    running = false;
                    break;
                case 1:
                    this.registerCV();
                    break;
                case 2:
                    this.retrieveFreeCourses();
                    break;
                case 3:
                    this.retrieveStudentsEnrolled();
                    break;
                case 4:
                    this.allCourses();
                    break;
                case 5:
                    this.deleteCo();
                    break;
                case 6:
                    this.addTe();
                    break;
                case 7:
                    this.addSt();
                    break;
                case 8:
                    this.addCo();
                    break;
                case 9:
                    this.sortStudents();
                    break;
                case 10:
                    this.sortCourses();
                    break;
                case 11:
                    this.filterStudents();
                    break;
                case 12:filterCourses();
                    break;
                default:
                    running = false;
            }
        }
        System.out.println("The menu is closed !\s");

    }
    public void CVMenu(){
        System.out.println("""
                0. Quit\s
                1. Register student\s
                2. Show courses with free places\s
                3. Retrieve students enrolled for course\s
                4. Show all courses\s
                5. Delete a course by a teacher\s
                6. Add a Teacher\s
                7. Add a student\s
                8. Add a course\s
                9. Sort students by the number of credits\s
                10. Sort courses by the number of enrolled students\s
                11. Filter students that are enrolled in at least a course  \s
                12. Filter courses, where more than 30 students are able to enroll\s
                """);
    }


    /**
     *  gets user input, to register
     */
    public void registerCV() throws DoesNotExistException, CanNotRegister, SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Course id: ");
        long courseId = scanner.nextLong();

        System.out.println("Student id: ");
        long studentId = scanner.nextLong();

        this.controller.register(courseId, studentId);

    }

    /**
    * shows all free courses
     */
    public void retrieveFreeCourses() throws SQLException {
        for (Course course : this.controller.retrieveCoursesWithFreePlaces()){
            System.out.println(course);
        }
    }

    /**
    *the id of the student, for witch the courses will be showed is given by the user
     */
    public void retrieveStudentsEnrolled() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Student id: ");
        long studentId = scanner.nextLong();

        for (Student stud : this.controller.retrieveStudentsEnrolledForACourse(studentId)){
            System.out.println(stud);
        }
    }

    public void allCourses() throws SQLException {
        for (Course course : this.controller.getAllCourses()){
            System.out.println(course);
        }
    }

    /**
     *user input to delete a course
     */
    public void deleteCo() throws DoesNotExistException, TeacherException, SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Course id: ");
        long courseId = scanner.nextLong();

        System.out.println("Teacher id: ");
        long teacherId = scanner.nextLong();

        this.controller.deleteCourse(teacherId, courseId);


    }

    /**
     *user input to add teacher
     */
    public void addTe() throws ExistentElementException, SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("first Name: ");
        String firstN = scanner.nextLine();

        System.out.println("last Name: ");
        String lastN = scanner.nextLine();

        System.out.println("Teacher id: ");
        long teacherId = scanner.nextLong();

        this.controller.addTeacher(firstN, lastN, teacherId);

    }

    public void addSt() throws ExistentElementException, SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("first Name: ");
        String firstN = scanner.nextLine();

        System.out.println("last Name: ");
        String lastN = scanner.nextLine();

        System.out.println("Student id: ");
        long studentId = scanner.nextLong();

        this.controller.addStudent(firstN, lastN, studentId);

    }

    public void addCo() throws DoesNotExistException, ExistentElementException, SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.println(" Course Name: ");
        String courseN = scanner.nextLine();

        System.out.println("Course ID: ");
        long courseId = scanner.nextLong();

        System.out.println("Teacher ID: ");
        long teacherId = scanner.nextLong();

        System.out.println("max Enroll: ");
        int maxE = scanner.nextInt();

        System.out.println("Number of credits: ");
        int nrCred = scanner.nextInt();

        this.controller.addCourse(courseId, courseN, teacherId, maxE, nrCred);

    }

    public void sortStudents() throws SQLException {
        List<Student> sortedStudents =this.controller.sortStudentCredits();
        for (Student student : sortedStudents){
            System.out.println(student);
        }
    }

    public void sortCourses() throws SQLException {
        List<Course> sortedCourses = this.controller.sortCourseNrStudents();
        for (Course course : sortedCourses){
            System.out.println(course);
        }
    }

   public void filterCourses() throws SQLException {
       for (Course course : this.controller.filterCourse()){
           System.out.println(course);
       }
    }

    public void filterStudents() throws SQLException {
        for (Student student : this.controller.filterStudents()){
            System.out.println(student);
        }
    }
}
