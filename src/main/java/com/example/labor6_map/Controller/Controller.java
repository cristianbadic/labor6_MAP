package com.example.labor6_map.Controller;

import com.example.labor6_map.Exception.CanNotRegister;
import com.example.labor6_map.Exception.DoesNotExistException;
import com.example.labor6_map.Exception.ExistentElementException;
import com.example.labor6_map.Exception.TeacherException;
import com.example.labor6_map.Model.Course;
import com.example.labor6_map.Model.Student;
import com.example.labor6_map.Model.Teacher;
import com.example.labor6_map.Repository.CourseMySQLRepository;
import com.example.labor6_map.Repository.StudentMySQLRepository;
import com.example.labor6_map.Repository.TeacherMySQLRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    private CourseMySQLRepository coursesRep;
    private TeacherMySQLRepository teachersRep;
    private StudentMySQLRepository studentsRep;



    public Controller(CourseMySQLRepository coursesR, TeacherMySQLRepository teachersR, StudentMySQLRepository studentsR) {
        this.coursesRep = coursesR;
        this.teachersRep = teachersR;
        this.studentsRep = studentsR;
    }


    /**
     *
     * @param courseId the id of a course, in wich a student is supposed to be registered
     * @param studentId the id of a student, supposed to be registered
     * @throws DoesNotExistException, if there is no Student or
     * Course object in their specific repositorys for the ids given as parameter
     * @throws CanNotRegister if the course has reached its max enrollment number or
     * the total credits of the student supposed to be registered, would exceed 30, if added
     */
    public void register(long courseId, long studentId) throws DoesNotExistException, CanNotRegister, SQLException {
        Student student = null;
        for(Student stud: studentsRep.getAll())
            if(stud.getStudentId()==studentId){
                student = stud;
                break;
            }
        if(student==null)
            throw new DoesNotExistException("This student isn't in the repository!");

        Course course = null;
        for(Course co: coursesRep.getAll())
            if(co.getCourseId() ==courseId){
                course = co;
                break;
            }
        if(course==null)
            throw new DoesNotExistException("This course isn't in the repository!");

        if (course.getMaxEnrollment() > course.getStudentsEnrolled().size()
                && student.getTotalCredits() + course.getCredits() <31) {
            course.addStudents(student);
            student.addCourses(course);
            this.coursesRep.update(course);
            this.studentsRep.update(student);
        }
        else
            throw new CanNotRegister("The Student has to many credits or the curs is full!");
    }



    /**
     *
     * search through the course repository to find te courses with free places, witch are added to the free list
     * maxEnrolled variable, used to get the max nr. of enrollments for each curse in the repo
     * nrStudents, used to find the number of student enrolled for each specific course in the repo
     * @return list of courses
    */
    public List<Course> retrieveCoursesWithFreePlaces() throws SQLException {
        List<Course> free= new ArrayList<>();
        for (int index =0; index < this.coursesRep.getAll().size(); index ++){
            int maxEnrolled = this.coursesRep.getAll().get(index).getMaxEnrollment();
            int nrStudents = this.coursesRep.getAll().get(index).getStudentsEnrolled().size();
            if (maxEnrolled > nrStudents ){
                free.add(this.coursesRep.getAll().get(index));
            }
        }
        return free;
    }

    /**
     *
     * @param courseId course id of the course, from witch the list of students should be returned
     * @return the list of students for the specific course
     */
    public List<Student> retrieveStudentsEnrolledForACourse(long courseId) throws SQLException {
        List<Student> enrolledStud = new ArrayList<>();
        for (Student student: this.studentsRep.getAll()){
            if (student.getEnrolledCourses().contains(courseId))
                enrolledStud.add(student);
        }
        return enrolledStud;
    }

    public List<Course> getAllCourses() throws SQLException {
        return this.coursesRep.getAll();
    }

    public List<Student> getAllStudents() throws SQLException {
        return this.studentsRep.getAll();
    }

    public List<Teacher> getAllTeachers() throws SQLException {
        return this.teachersRep.getAll();
    }

    /**
     *deletes the course from the coursesRep, but also from the course list of each student or taecher, that contains it
     * @param teacherId, the id of the teacher that wants to delete one of its courses
     * @param courseId, the id of the course to be deleted
     * @throws DoesNotExistException,if there is no Teacher or
     * Student object in their specific repositorys for the ids given as parameter
     * @throws TeacherException, if Teacher doesn't teach the course
     */
    public void deleteCourse(long teacherId, long courseId) throws DoesNotExistException, TeacherException, SQLException {
        Teacher teacher = null;
        for (Teacher teach : teachersRep.getAll())
            if (teach.getTeacherId() == teacherId) {
                teacher = teach;
                break;
            }
        if (teacher == null)
            throw new DoesNotExistException("This teacher isn't in the repository!");

        Course course = null;
        for (Course co : coursesRep.getAll())
            if (co.getCourseId() == courseId) {
                course = co;
                break;
            }
        if (course == null)
            throw new DoesNotExistException("This course isn't in the repository!");

        if (course.getTeacher() != teacher.getTeacherId())
            throw new TeacherException("This course isn't taught by the teacher given!");

        for (Student student : this.studentsRep.getAll()) {
            for (long cursId : student.getEnrolledCourses())
                if (courseId == cursId){
                    student.removeCourses(course);
                    break;
                }
            this.studentsRep.update(student);
        }
        teacher.removeCourses(course);
        this.teachersRep.update(teacher);

        this.coursesRep.delete(course);
    }

    /**
     * adds a teacher to the teachersRep
     * @param firstN, first name of the Teacher object, to be added to the teachersRep
     * @param lastN, last name of the Teacher object, to be added to the teachersRep
     * @param teachId, teacher ID of the Teacher object, to be added to the teachersRep
     * @throws ExistentElementException, if there is already another object with the same id in the repo
     */
    public void addTeacher(String firstN, String lastN, long teachId) throws ExistentElementException, SQLException {
        Teacher teacher = null;
        for (Teacher teach : teachersRep.getAll())
            if (teach.getTeacherId() == teachId) {
                teacher = teach;
                break;
            }
        if (teacher != null)
            throw new ExistentElementException("This teacher is already in the repository!");

        teachersRep.create(new Teacher(firstN, lastN, teachId, new ArrayList<>()));
    }


    /**
     *
     * @param firstN, first name of the Student object, to be added to the studentsRep
     * @param lastN, last name of the Student object, to be added to the studentsRep
     * @param studId, student ID of the Student object, to be added to the studentsRep
     * @throws ExistentElementException, if there is already another object with the same id in the repo
     */
    public void addStudent(String firstN, String lastN, long studId) throws ExistentElementException, SQLException {
        Student student = null;
        for(Student stud: studentsRep.getAll())
            if(stud.getStudentId()==studId){
                student = stud;
                break;
            }
        if(student!=null)
            throw new ExistentElementException("This student is already in the repository!");
        this.studentsRep.create(new Student(firstN, lastN,studId, 0, new ArrayList<>()));
    }

    /**
     * adds a course Object to the courses repository, and also to the list of courses for the techer, that teaches the course
     * @param cId
     * @param nameC
     * @param teacherId
     * @param maxEnroll
     * @param creditsNr
     * @throws ExistentElementException, if there is already another object with the same id in the repo
     * @throws DoesNotExistException, if the teacher that is supposed to teach the course, isn't in the teachersRepo
     */
    public void addCourse(long cId, String nameC, long teacherId, int maxEnroll, int creditsNr) throws ExistentElementException, DoesNotExistException, SQLException {
        Course course = null;
        for(Course co: coursesRep.getAll())
            if(co.getCourseId() ==cId){
                course = co;
                break;
            }
        if(course!=null)
            throw new ExistentElementException("This course already in the repository!");

        Teacher teacher = null;
        for (Teacher teach : teachersRep.getAll())
            if (teach.getTeacherId() == teacherId) {
                teacher = teach;
                break;
            }
        if (teacher == null)
            throw new DoesNotExistException("This teacher isn't in the repository!");

        Course course2 = new Course(cId, nameC, teacherId, maxEnroll, creditsNr, new ArrayList<>());
        this.coursesRep.create(course2);
        teacher.addCourses(course2);

        this.teachersRep.update(teacher);
    }



    /**
     * the repository of courses is sorted in ascending order by the number of enrolled students
     */
    public List<Student> sortStudentCredits() throws SQLException {
        return this.studentsRep.sortRep();
    }

    /**
     * the repository of students is sorted in ascending order by the number of credits
     */
    public List<Course> sortCourseNrStudents() throws SQLException {
        return this.coursesRep.sortRep();
    }

    /**
     *
     * @return a list of students, that have more then 5 credits (are enrolled in at least a course)
     */
    public List<Student> filterStudents() throws SQLException {
        List<Student> students = this.studentsRep.getAll();
        return students.stream().filter(stud->stud.getTotalCredits() >= 6).toList();
    }

    /**
     *
     * @return a list of courses, where more than 30 students can be enrolled
     */
    public List<Course> filterCourse() throws SQLException {
        List<Course> courses = this.coursesRep.getAll();
        return courses.stream().filter(cour->cour.getMaxEnrollment() > 30).toList();
    }

}


