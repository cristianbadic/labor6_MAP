package com.example.labor6_map;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class CreditsController {

    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    Label creditsLabel;

    public void displayNrCredits(int credits) {
        creditsLabel.setText("You have " + credits +" credits!");
    }

    public void backToStudentOpt(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("StudentOptions.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
