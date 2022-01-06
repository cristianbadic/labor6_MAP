package com.example.labor6_map;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class RegisterController {

    @FXML
    Label registerLabel;

    public void displayRegisterMessage(String mess) {
        registerLabel.setText(mess);
    }
}
