package com.example.labor6_map;

import com.example.labor6_map.Controller.Controller;
import com.example.labor6_map.Model.Course;
import com.example.labor6_map.Model.Student;
import com.example.labor6_map.Model.Teacher;
import com.example.labor6_map.Repository.CourseMySQLRepository;
import com.example.labor6_map.Repository.StudentMySQLRepository;
import com.example.labor6_map.Repository.TeacherMySQLRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class ShowStudentsController {
    protected static Teacher logTeacher;
    @FXML
    Label studLabel;

    public void displayStudents(StringBuilder allStud, Teacher teacher) {
        logTeacher= teacher;
        studLabel.setText(String.valueOf(allStud));
    }

    public void refresh(ActionEvent event) throws SQLException, IOException {
        TeacherMySQLRepository teachRep = new TeacherMySQLRepository("jdbc:mysql://localhost:3306/university", "root", "password1234");
        StudentMySQLRepository studentRep = new StudentMySQLRepository("jdbc:mysql://localhost:3306/university", "root", "password1234");
        CourseMySQLRepository courseRep = new CourseMySQLRepository("jdbc:mysql://localhost:3306/university", "root", "password1234");

        Controller controller = new Controller(courseRep, teachRep, studentRep);

        StringBuilder result = new StringBuilder();
        for (long courseId : logTeacher.getCourses()) {
            for (Course course : controller.getAllCourses()) {
                if (courseId == course.getCourseId()) {
                    result.append(course.getName()).append(": ");
                    for (long studentId : course.getStudentsEnrolled()) {
                        for (Student student : controller.getAllStudents()) {
                            if (studentId == student.getStudentId()) {
                                result.append(student.getFirstName()).append(" ").append(student.getLastName()).append(", ");
                                break;
                            }
                        }
                    }
                    result.append("\n");
                    break;
                }
            }
        }
       studLabel.setText(String.valueOf(result));
    }
}
