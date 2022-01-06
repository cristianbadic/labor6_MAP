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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class TeacherController {

    protected static Teacher loggedTeacher;
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    TextField firstNameTextField;
    @FXML
    TextField lastNameTextField;
    @FXML
    TextField idTextField;
    @FXML
    Label textLabel;

    public void loginTeacher(ActionEvent event) throws SQLException, IOException {
        String firstN = firstNameTextField.getText();
        String lastN = lastNameTextField.getText();
        long id = Long.parseLong(idTextField.getText());
        TeacherMySQLRepository teachRep = new TeacherMySQLRepository("jdbc:mysql://localhost:3306/university", "root", "password1234");
        StudentMySQLRepository studentRep = new StudentMySQLRepository("jdbc:mysql://localhost:3306/university", "root", "password1234");
        CourseMySQLRepository courseRep = new CourseMySQLRepository("jdbc:mysql://localhost:3306/university", "root", "password1234");

        Controller controller = new Controller(courseRep, teachRep, studentRep);

        int found = 0;

        loggedTeacher = controller.getAllTeachers().get(0);
        for (Teacher teacher : controller.getAllTeachers()) {
            if (teacher.getFirstName().equals(firstN) && teacher.getLastName().equals(lastN) && teacher.getTeacherId() == id) {
                found = 1;
                loggedTeacher = teacher;
                break;
            }
        }
        if (found == 1) {
            StringBuilder result = this.buildStudentLists(loggedTeacher, controller);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("TeacherOptions.fxml"));
            root = loader.load();

            ShowStudentsController showStudentsController = loader.getController();
            showStudentsController.displayStudents(result, loggedTeacher);

            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } else {
            textLabel.setText("Invalid Login Data !");
        }

    }

    public StringBuilder buildStudentLists(Teacher loggedTeacher, Controller controller) throws SQLException {
        StringBuilder result = new StringBuilder();
        for (long courseId : loggedTeacher.getCourses()) {
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
        return result;
    }

    public void backToLog(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
