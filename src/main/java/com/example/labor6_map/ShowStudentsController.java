package com.example.labor6_map;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ShowStudentsController {
    @FXML
    Label studLabel;

    public void displayStudents(StringBuilder allStud) {
        studLabel.setText(String.valueOf(allStud));
    }
}
