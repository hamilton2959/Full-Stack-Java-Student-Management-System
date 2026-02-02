package org.skytech.systemdestudent.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import org.springframework.stereotype.Component;

@Component
public class MainController {

    @FXML
    private TabPane mainTabPane;

    @FXML
    private Label statusLabel;

    @FXML
    private Label recordCountLabel;

    @FXML
    public void initialize() {
        setStatus("Application initialized successfully");
    }

    @FXML
    private void handleExit() {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About SRMS");
        alert.setHeaderText("Student Records Management System");
        alert.setContentText(
                "Version: 1.0.0\n" +
                        "Developed for: ACSC 332 - CAT 2\n" +
                        "Institution: Chuka University\n" +
                        "Department: Computer Science\n\n" +
                        "A comprehensive desktop application for managing student records,\n" +
                        "courses, enrollments, and generating academic reports.\n\n" +
                        "Built with: JavaFX, Spring Boot, and MySQL"
        );
        alert.showAndWait();
    }

    public void setStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }

    public void updateRecordCount(int count) {
        if (recordCountLabel != null) {
            recordCountLabel.setText("Records: " + count);
        }
    }
}