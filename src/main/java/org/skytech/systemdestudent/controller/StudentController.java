package org.skytech.systemdestudent.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
        import javafx.scene.control.cell.PropertyValueFactory;
import org.skytech.systemdestudent.model.Student;
import org.skytech.systemdestudent.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class StudentController {

    @FXML private TextField regNoField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private DatePicker enrollmentDatePicker;
    @FXML private TextField emailField;
    @FXML private DatePicker dobPicker;
    @FXML private TextField departmentField;
    @FXML private TextField phoneField;
    @FXML private TextArea addressArea;

    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, Long> idColumn;
    @FXML private TableColumn<Student, String> regNoColumn;
    @FXML private TableColumn<Student, String> firstNameColumn;
    @FXML private TableColumn<Student, String> lastNameColumn;
    @FXML private TableColumn<Student, LocalDate> enrollmentDateColumn;
    @FXML private TableColumn<Student, String> emailColumn;
    @FXML private TableColumn<Student, String> departmentColumn;

    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button clearButton;

    private final StudentService studentService;
    private ObservableList<Student> studentList;
    private Student selectedStudent;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
        this.studentList = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        // Setup table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        regNoColumn.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        enrollmentDateColumn.setCellValueFactory(new PropertyValueFactory<>("enrollmentDate"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));

        // Set table data
        studentTable.setItems(studentList);

        // Add selection listener
        studentTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        populateFields(newValue);
                        selectedStudent = newValue;
                    }
                }
        );

        // Load initial data
        loadStudents();

        // Set default enrollment date
        enrollmentDatePicker.setValue(LocalDate.now());
    }

    @FXML
    private void handleAddStudent() {
        try {
            // Validate input
            if (!validateInput()) {
                return;
            }

            // Create new student
            Student newStudent = new Student();
            newStudent.setRegistrationNumber(regNoField.getText().trim());
            newStudent.setFirstName(firstNameField.getText().trim());
            newStudent.setLastName(lastNameField.getText().trim());
            newStudent.setEnrollmentDate(enrollmentDatePicker.getValue());
            newStudent.setEmail(emailField.getText().trim());
            newStudent.setDateOfBirth(dobPicker.getValue());
            newStudent.setDepartment(departmentField.getText().trim());
            newStudent.setPhoneNumber(phoneField.getText().trim());
            newStudent.setAddress(addressArea.getText().trim());

            // Save student
            Student savedStudent = studentService.saveStudent(newStudent);

            // Update UI
            clearFields();
            loadStudents();
            showSuccess("Student added successfully!");

        } catch (IllegalArgumentException e) {
            showError("Validation Error", e.getMessage());
        } catch (Exception e) {
            showError("Error", "Failed to add student: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateStudent() {
        try {
            if (selectedStudent == null) {
                showError("No Selection", "Please select a student to update");
                return;
            }

            // Validate input
            if (!validateInput()) {
                return;
            }

            // Update student object
            selectedStudent.setRegistrationNumber(regNoField.getText().trim());
            selectedStudent.setFirstName(firstNameField.getText().trim());
            selectedStudent.setLastName(lastNameField.getText().trim());
            selectedStudent.setEnrollmentDate(enrollmentDatePicker.getValue());
            selectedStudent.setEmail(emailField.getText().trim());
            selectedStudent.setDateOfBirth(dobPicker.getValue());
            selectedStudent.setDepartment(departmentField.getText().trim());
            selectedStudent.setPhoneNumber(phoneField.getText().trim());
            selectedStudent.setAddress(addressArea.getText().trim());

            // Save updated student
            studentService.saveStudent(selectedStudent);

            // Update UI
            clearFields();
            loadStudents();
            showSuccess("Student updated successfully!");

        } catch (IllegalArgumentException e) {
            showError("Validation Error", e.getMessage());
        } catch (Exception e) {
            showError("Error", "Failed to update student: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteStudent() {
        try {
            if (selectedStudent == null) {
                showError("No Selection", "Please select a student to delete");
                return;
            }

            // Confirm deletion
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Deletion");
            confirmAlert.setHeaderText("Delete Student");
            confirmAlert.setContentText("Are you sure you want to delete " +
                    selectedStudent.getFullName() + "?");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Delete student
                studentService.deleteStudent(selectedStudent.getId());

                // Update UI
                clearFields();
                loadStudents();
                showSuccess("Student deleted successfully!");
            }

        } catch (Exception e) {
            showError("Error", "Failed to delete student: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClearFields() {
        clearFields();
    }

    private void loadStudents() {
        try {
            studentList.clear();
            studentList.addAll(studentService.getAllStudents());
        } catch (Exception e) {
            showError("Error", "Failed to load students: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void populateFields(Student student) {
        regNoField.setText(student.getRegistrationNumber());
        firstNameField.setText(student.getFirstName());
        lastNameField.setText(student.getLastName());
        enrollmentDatePicker.setValue(student.getEnrollmentDate());
        emailField.setText(student.getEmail());
        dobPicker.setValue(student.getDateOfBirth());
        departmentField.setText(student.getDepartment());
        phoneField.setText(student.getPhoneNumber());
        addressArea.setText(student.getAddress());
    }

    private void clearFields() {
        regNoField.clear();
        firstNameField.clear();
        lastNameField.clear();
        enrollmentDatePicker.setValue(LocalDate.now());
        emailField.clear();
        dobPicker.setValue(null);
        departmentField.clear();
        phoneField.clear();
        addressArea.clear();
        selectedStudent = null;
        studentTable.getSelectionModel().clearSelection();
    }

    private boolean validateInput() {
        if (regNoField.getText().trim().isEmpty()) {
            showError("Validation Error", "Registration number is required");
            return false;
        }

        if (firstNameField.getText().trim().isEmpty()) {
            showError("Validation Error", "First name is required");
            return false;
        }

        if (lastNameField.getText().trim().isEmpty()) {
            showError("Validation Error", "Last name is required");
            return false;
        }

        if (enrollmentDatePicker.getValue() == null) {
            showError("Validation Error", "Enrollment date is required");
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