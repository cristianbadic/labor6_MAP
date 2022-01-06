package com.example.labor6_map;

import com.example.labor6_map.Controller.Controller;
import com.example.labor6_map.Exception.CanNotRegister;
import com.example.labor6_map.Exception.DoesNotExistException;
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

public class StudentController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    private int totalCredits;
    protected static long idStudent;
    @FXML
    TextField firstNameTextField;
    @FXML
    TextField lastNameTextField;
    @FXML
    TextField idTextField;
    @FXML
    Label textLabel;
    @FXML
    TextField courseTextField;

    /**
     * if the first name, last name and ID of the student is valid, we go to the StudentOptions scene
     * @param event on click of the Login button
     * @throws SQLException
     * @throws IOException
     */
    public void loginStudent(ActionEvent event) throws SQLException, IOException {
        String firstN = firstNameTextField.getText();
        String lastN = lastNameTextField.getText();
        long id = Long.parseLong(idTextField.getText());
        idStudent = id;
        TeacherMySQLRepository teachRep = new TeacherMySQLRepository("jdbc:mysql://localhost:3306/university", "root", "password1234");
        StudentMySQLRepository studentRep = new StudentMySQLRepository("jdbc:mysql://localhost:3306/university", "root", "password1234");
        CourseMySQLRepository courseRep = new CourseMySQLRepository("jdbc:mysql://localhost:3306/university", "root", "password1234");

        Controller controller = new Controller(courseRep, teachRep, studentRep);

        for (Student student : controller.getAllStudents()) {
            if (student.getFirstName().equals(firstN) && student.getLastName().equals(lastN) && student.getStudentId() == id) {

                root = FXMLLoader.load(getClass().getResource("StudentOptions.fxml"));
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
                return;
            }
        }
        textLabel.setText("Invalid Login Data !");
    }

    /**
     *
     * @param event on click on the show credits button, we go to the Credits scene, where the total number of credits is shown
     * @throws SQLException
     * @throws IOException
     */
    public void showNrCredits(ActionEvent event) throws SQLException, IOException {
        TeacherMySQLRepository teachRep = new TeacherMySQLRepository("jdbc:mysql://localhost:3306/university", "root", "password1234");
        StudentMySQLRepository studentRep = new StudentMySQLRepository("jdbc:mysql://localhost:3306/university", "root", "password1234");
        CourseMySQLRepository courseRep = new CourseMySQLRepository("jdbc:mysql://localhost:3306/university", "root", "password1234");

        Controller controller = new Controller(courseRep, teachRep, studentRep);
        for (Student student : controller.getAllStudents()) {
            if (student.getStudentId() == idStudent) {
                totalCredits = student.getTotalCredits();
                break;
            }
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Credits.fxml"));
        root = loader.load();

        CreditsController creditsController = loader.getController();
        creditsController.displayNrCredits(totalCredits);

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    /**
     *we go to the Register scene, where the message "Register successful!" is shown if the registration was made
     * else "Register unsuccessful!" is shown
     * @param event click of register button
     * @throws IOException
     */
    public void registerStudent(ActionEvent event) throws IOException {
        String courseN = courseTextField.getText();
        TeacherMySQLRepository teachRep = new TeacherMySQLRepository("jdbc:mysql://localhost:3306/university", "root", "password1234");
        StudentMySQLRepository studentRep = new StudentMySQLRepository("jdbc:mysql://localhost:3306/university", "root", "password1234");
        CourseMySQLRepository courseRep = new CourseMySQLRepository("jdbc:mysql://localhost:3306/university", "root", "password1234");


        Controller controller = new Controller(courseRep, teachRep, studentRep);


        long courseID = -1;
        int len = 0;
        int len2 = 0;
        try {
            for (Course course : controller.getAllCourses()){
                if (course.getName().equals(courseN)){
                    len = course.getStudentsEnrolled().size();
                    courseID = course.getCourseId();
                    break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String message;
        try {
            controller.register(courseID, idStudent);
        } catch (DoesNotExistException | CanNotRegister | SQLException e) {
            message = "Unsuccessful!";
            //e.printStackTrace();
        }
        try {
            for (Course course : controller.getAllCourses()){
                if (course.getName().equals(courseN)){
                    len2 = course.getStudentsEnrolled().size();
                    //courseID = course.getCourseId();
                    break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if ((len+1) == len2){
            message = "Register successful!";
        }
        else{
            message = "Register unsuccessful!";
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Register.fxml"));
        root = loader.load();

        RegisterController registerController = loader.getController();
        registerController.displayRegisterMessage(message);

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void backTo(ActionEvent event, String returnScene) throws IOException {
        root = FXMLLoader.load(getClass().getResource(returnScene));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void backToLogin(ActionEvent event) throws IOException {
        backTo(event, "Login.fxml");
    }
    public void backToOptions(ActionEvent event) throws IOException {
        backTo(event, "Login_Student.fxml");
    }
}
