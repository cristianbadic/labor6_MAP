package com.example.labor6_map.Model;

import java.util.List;

public class Teacher extends Person{
    private long teacherId;
    private List<Long> courses;

    public Teacher(String firstName, String lastName, long teacherId, List<Long> courses){
        super(firstName, lastName);
        this.teacherId = teacherId;
        this.courses = courses;
    }

    public long getTeacherId() {
        return teacherId;
    }

    public List<Long> getCourses() {
        return courses;
    }

    public void setTeacherId(long teacherId) {
        this.teacherId = teacherId;
    }

    public void setCourses(List<Long> courses) {
        this.courses = courses;
    }

    /**
     * adds a new course to the list courses
     * @param course an element of type course
     */
    public void addCourses(Course course){
        courses.add(course.getCourseId());
    }

    /**
     * removes a course from the list  courses
     * @param course an element of type course
     */
    public void removeCourses(Course course){
        courses.remove(course.getCourseId());
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "teacherId=" + teacherId +
                ", courses=" + courses +
                '}';
    }
}
