package com.example.labor6_map;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CreditsController {

    @FXML
    Label creditsLabel;

    public void displayNrCredits(int credits) {
        creditsLabel.setText("You have " + credits +" credits!");
    }
}
