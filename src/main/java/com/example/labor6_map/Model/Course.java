package com.example.labor6_map.Model;

import java.util.List;

public class Course {
    private long courseId;
    private String name;
    private long teacher;
    private int maxEnrollment;
    private int credits;
    private List<Long> studentsEnrolled;

    public Course(long courseId, String name, long teacher, int maxEnrollment, int credits, List<Long> studentsEnrolled){
        this.courseId = courseId;
        this.name = name;
        this.teacher = teacher;
        this.maxEnrollment = maxEnrollment;
        this.credits = credits;
        this.studentsEnrolled = studentsEnrolled;
    }

    public long getCourseId() {
        return courseId;
    }

    public int getCredits() {
        return credits;
    }

    public int getMaxEnrollment() {
        return maxEnrollment;
    }

    public String getName() {
        return name;
    }

    public long getTeacher() {
        return teacher;
    }

    public List<Long> getStudentsEnrolled() {
        return studentsEnrolled;
    }

    public void setStudentsEnrolled(List<Long> studentsEnrolled) {
        this.studentsEnrolled = studentsEnrolled;
    }

    /**
     *when the nr. of credits of a course is updated, we need to check if the credits of the curse can be updatet
     * in the EnrolledCourses list of the students that are enrolled to this course, that's why the ypdateCourses method is used
     * for each student
     * @param newCredits the new number of credits
     */
    public void setCredits(int newCredits) {
        this.credits = newCredits;
    }

    public void setMaxEnrollment(int maxEnrollment) {
        this.maxEnrollment = maxEnrollment;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTeacher(Long teacher) {
        this.teacher = teacher;
    }

    public void addStudents(Student student){
        this.studentsEnrolled.add(student.getStudentId());
    }

    /**
     * removes a student from the list studentEnrolled
     * @param student an element of type Student
     */
    public void removeStudents(Student student){
        this.studentsEnrolled.remove(student.getStudentId());
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseId=" + courseId +
                ", name='" + name + '\'' +
                ", teacher=" + teacher +
                ", maxEnrollment=" + maxEnrollment +
                ", credits=" + credits +
                ", studentsEnrolled=" + studentsEnrolled +
                '}';
    }

    /**
     * compares 2 courses by their number of enrolled Students
     * @param course2 a course object with whom the current object is compared
     * @return determines witch course has fewer students
     */
    public int compareCourse(Course course2){
        return Integer.compare(this.getStudentsEnrolled().size(), course2.getStudentsEnrolled().size());
    }
}
