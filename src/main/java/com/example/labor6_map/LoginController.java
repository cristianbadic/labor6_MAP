package com.example.labor6_map;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    /**
     * we go to the Login_teacher scene
     * @param event is click of button teacher
     * @throws IOException
     */
    public void teacher(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("Login_Teacher.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * we go to the Login_Student scene
     * @param event is click of button student
     * @throws IOException
     */
    public void student(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("Login_Student.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
