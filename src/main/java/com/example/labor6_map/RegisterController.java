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

public class RegisterController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    Label registerLabel;

    /**
     *
     * @param mess the message shown depending if the student was enrolled or not
     */
    public void displayRegisterMessage(String mess) {
        registerLabel.setText(mess);
    }

    /**
     * when the return button is clicked, we return to the options window of the student
     * @param event, the event is when the button is clicked
     * @throws IOException
     */
    public void backToStudentOp(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("StudentOptions.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
