package org.skytech.systemdestudent.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
        import javafx.scene.control.cell.PropertyValueFactory;
import org.skytech.systemdestudent.model.Course;
import org.skytech.systemdestudent.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CourseController {

    @FXML private TextField courseCodeField;
    @FXML private TextField courseTitleField;
    @FXML private TextField creditsField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField departmentField;
    @FXML private TextField prerequisitesField;
    @FXML private TextField instructorField;

    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, Long> idColumn;
    @FXML private TableColumn<Course, String> codeColumn;
    @FXML private TableColumn<Course, String> titleColumn;
    @FXML private TableColumn<Course, Integer> creditsColumn;
    @FXML private TableColumn<Course, String> departmentColumn;
    @FXML private TableColumn<Course, String> instructorColumn;

    private final CourseService courseService;
    private ObservableList<Course> courseList;
    private Course selectedCourse;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
        this.courseList = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        // Setup table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("courseTitle"));
        creditsColumn.setCellValueFactory(new PropertyValueFactory<>("credits"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        instructorColumn.setCellValueFactory(new PropertyValueFactory<>("instructor"));

        courseTable.setItems(courseList);

        // Add selection listener
        courseTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        populateFields(newValue);
                        selectedCourse = newValue;
                    }
                }
        );

        loadCourses();
    }

    @FXML
    private void handleAddCourse() {
        try {
            if (!validateInput()) {
                return;
            }

            Course newCourse = new Course();
            newCourse.setCourseCode(courseCodeField.getText().trim());
            newCourse.setCourseTitle(courseTitleField.getText().trim());
            newCourse.setCredits(Integer.parseInt(creditsField.getText().trim()));
            newCourse.setCourseDescription(descriptionArea.getText().trim());
            newCourse.setDepartment(departmentField.getText().trim());
            newCourse.setPrerequisites(prerequisitesField.getText().trim());
            newCourse.setInstructor(instructorField.getText().trim());

            courseService.saveCourse(newCourse);

            clearFields();
            loadCourses();
            showSuccess("Course added successfully!");

        } catch (NumberFormatException e) {
            showError("Validation Error", "Credits must be a valid number");
        } catch (IllegalArgumentException e) {
            showError("Validation Error", e.getMessage());
        } catch (Exception e) {
            showError("Error", "Failed to add course: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateCourse() {
        try {
            if (selectedCourse == null) {
                showError("No Selection", "Please select a course to update");
                return;
            }

            if (!validateInput()) {
                return;
            }

            selectedCourse.setCourseCode(courseCodeField.getText().trim());
            selectedCourse.setCourseTitle(courseTitleField.getText().trim());
            selectedCourse.setCredits(Integer.parseInt(creditsField.getText().trim()));
            selectedCourse.setCourseDescription(descriptionArea.getText().trim());
            selectedCourse.setDepartment(departmentField.getText().trim());
            selectedCourse.setPrerequisites(prerequisitesField.getText().trim());
            selectedCourse.setInstructor(instructorField.getText().trim());

            courseService.saveCourse(selectedCourse);

            clearFields();
            loadCourses();
            showSuccess("Course updated successfully!");

        } catch (NumberFormatException e) {
            showError("Validation Error", "Credits must be a valid number");
        } catch (Exception e) {
            showError("Error", "Failed to update course: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteCourse() {
        try {
            if (selectedCourse == null) {
                showError("No Selection", "Please select a course to delete");
                return;
            }

            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Deletion");
            confirmAlert.setHeaderText("Delete Course");
            confirmAlert.setContentText("Are you sure you want to delete " +
                    selectedCourse.getCourseTitle() + "?");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                courseService.deleteCourse(selectedCourse.getId());
                clearFields();
                loadCourses();
                showSuccess("Course deleted successfully!");
            }

        } catch (Exception e) {
            showError("Error", "Failed to delete course: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClearFields() {
        clearFields();
    }

    private void loadCourses() {
        try {
            courseList.clear();
            courseList.addAll(courseService.getAllCourses());
        } catch (Exception e) {
            showError("Error", "Failed to load courses: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void populateFields(Course course) {
        courseCodeField.setText(course.getCourseCode());
        courseTitleField.setText(course.getCourseTitle());
        creditsField.setText(String.valueOf(course.getCredits()));
        descriptionArea.setText(course.getCourseDescription());
        departmentField.setText(course.getDepartment());
        prerequisitesField.setText(course.getPrerequisites());
        instructorField.setText(course.getInstructor());
    }

    private void clearFields() {
        courseCodeField.clear();
        courseTitleField.clear();
        creditsField.clear();
        descriptionArea.clear();
        departmentField.clear();
        prerequisitesField.clear();
        instructorField.clear();
        selectedCourse = null;
        courseTable.getSelectionModel().clearSelection();
    }

    private boolean validateInput() {
        if (courseCodeField.getText().trim().isEmpty()) {
            showError("Validation Error", "Course code is required");
            return false;
        }

        if (courseTitleField.getText().trim().isEmpty()) {
            showError("Validation Error", "Course title is required");
            return false;
        }

        if (creditsField.getText().trim().isEmpty()) {
            showError("Validation Error", "Credits is required");
            return false;
        }

        try {
            int credits = Integer.parseInt(creditsField.getText().trim());
            if (credits <= 0 || credits > 10) {
                showError("Validation Error", "Credits must be between 1 and 10");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Validation Error", "Credits must be a valid number");
            return false;
        }

        return true;
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}