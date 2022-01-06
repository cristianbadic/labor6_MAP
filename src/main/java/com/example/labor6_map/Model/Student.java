package com.example.labor6_map.Model;

import java.util.List;

public class Student extends Person{
    private long studentId;
    private int totalCredits;
    private List<Long> enrolledCourses;

    public Student(String firstName, String lastName, long studentId, int totalCredits, List<Long> enrolledCourses ){
        super(firstName, lastName);
        this.studentId = studentId;
        this.totalCredits = totalCredits;
        this.enrolledCourses = enrolledCourses;
    }

    public long getStudentId() {
        return studentId;
    }

    public int getTotalCredits() {
        return totalCredits;
    }

    public List<Long> getEnrolledCourses() {
        return enrolledCourses;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public void setTotalCredits(int totalCredits) {
        this.totalCredits = totalCredits;
    }

    public void setEnrolledCourses(List<Long> enrolledCourses) {
        this.enrolledCourses = enrolledCourses;
    }

    /**
     * adds a new course to the list enrolledCourses, also the total number of Credits increases
     * @param course an element of type course
     */
    public void addCourses(Course course){
        this.enrolledCourses.add(course.getCourseId());
        this.totalCredits += course.getCredits();
    }

    /**
     * removes a course from the list  enrolledCourses
     * @param course an element of type course
     */
    public void removeCourses(Course course){
        this.enrolledCourses.remove(course.getCourseId());
        this.totalCredits -= course.getCredits();
    }

    /**
     * updates the number of credits of a specific course, given as parameter, if the nrw value added to the other values
     * doesn't exceed 30, else the course is removed from the list of courses
     * @param course  the course to be updated
     * @param newCredit the new number of credits
     */
    public void updateCourses(Course course, int newCredit){
        if (this.totalCredits - course.getCredits() + newCredit > 30){
            removeCourses(course);
        }
        else{
            this.totalCredits = this.totalCredits - course.getCredits() + newCredit;
        }
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId=" + studentId +
                ", totalCredits=" + totalCredits +
                ", enrolledCourses=" + enrolledCourses +
                '}';
    }

    /**
     * compares 2 Students by their total number of credits
     * @param student2 a student object with whom the current object is compared
     * @return determines witch number of total credits is smaller
     */
    public int compareStudent(Student student2){
        return Integer.compare(this.totalCredits, student2.getTotalCredits());
    }
}

