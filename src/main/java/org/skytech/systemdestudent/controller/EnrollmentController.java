package org.skytech.systemdestudent.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
        import javafx.scene.control.cell.PropertyValueFactory;
import org.skytech.systemdestudent.model.Course;
import org.skytech.systemdestudent.model.Enrollment;
import org.skytech.systemdestudent.model.Student;
import org.skytech.systemdestudent.service.CourseService;
import org.skytech.systemdestudent.service.EnrollmentService;
import org.skytech.systemdestudent.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class EnrollmentController {

    @FXML private ComboBox<Student> studentComboBox;
    @FXML private ComboBox<Course> courseComboBox;
    @FXML private DatePicker enrollmentDatePicker;
    @FXML private ComboBox<String> semesterComboBox;
    @FXML private TextField academicYearField;
    @FXML private ComboBox<String> gradeComboBox;

    @FXML private TableView<Enrollment> enrollmentTable;
    @FXML private TableColumn<Enrollment, Long> idColumn;
    @FXML private TableColumn<Enrollment, String> studentRegNoColumn;
    @FXML private TableColumn<Enrollment, String> studentNameColumn;
    @FXML private TableColumn<Enrollment, String> courseCodeColumn;
    @FXML private TableColumn<Enrollment, String> courseTitleColumn;
    @FXML private TableColumn<Enrollment, LocalDate> enrollmentDateColumn;
    @FXML private TableColumn<Enrollment, String> semesterColumn;
    @FXML private TableColumn<Enrollment, String> academicYearColumn;
    @FXML private TableColumn<Enrollment, String> gradeColumn;

    @FXML private Label enrollmentCountLabel;

    private final EnrollmentService enrollmentService;
    private final StudentService studentService;
    private final CourseService courseService;

    private ObservableList<Enrollment> enrollmentList;
    private ObservableList<Student> studentList;
    private ObservableList<Course> courseList;
    private Enrollment selectedEnrollment;

    @Autowired
    public EnrollmentController(EnrollmentService enrollmentService,
                                StudentService studentService,
                                CourseService courseService) {
        this.enrollmentService = enrollmentService;
        this.studentService = studentService;
        this.courseService = courseService;
        this.enrollmentList = FXCollections.observableArrayList();
        this.studentList = FXCollections.observableArrayList();
        this.courseList = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        // Setup table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        studentRegNoColumn.setCellValueFactory(new PropertyValueFactory<>("studentRegNo"));
        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        courseTitleColumn.setCellValueFactory(new PropertyValueFactory<>("courseTitle"));
        enrollmentDateColumn.setCellValueFactory(new PropertyValueFactory<>("enrollmentDate"));
        semesterColumn.setCellValueFactory(new PropertyValueFactory<>("semester"));
        academicYearColumn.setCellValueFactory(new PropertyValueFactory<>("academicYear"));
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));

        enrollmentTable.setItems(enrollmentList);

        // Setup ComboBoxes
        studentComboBox.setItems(studentList);
        courseComboBox.setItems(courseList);

        // Custom cell factory for student ComboBox
        studentComboBox.setCellFactory(param -> new ListCell<Student>() {
            @Override
            protected void updateItem(Student student, boolean empty) {
                super.updateItem(student, empty);
                if (empty || student == null) {
                    setText(null);
                } else {
                    setText(student.getRegistrationNumber() + " - " + student.getFullName());
                }
            }
        });

        studentComboBox.setButtonCell(new ListCell<Student>() {
            @Override
            protected void updateItem(Student student, boolean empty) {
                super.updateItem(student, empty);
                if (empty || student == null) {
                    setText(null);
                } else {
                    setText(student.getRegistrationNumber() + " - " + student.getFullName());
                }
            }
        });

        // Custom cell factory for course ComboBox
        courseComboBox.setCellFactory(param -> new ListCell<Course>() {
            @Override
            protected void updateItem(Course course, boolean empty) {
                super.updateItem(course, empty);
                if (empty || course == null) {
                    setText(null);
                } else {
                    setText(course.getCourseCode() + " - " + course.getCourseTitle());
                }
            }
        });

        courseComboBox.setButtonCell(new ListCell<Course>() {
            @Override
            protected void updateItem(Course course, boolean empty) {
                super.updateItem(course, empty);
                if (empty || course == null) {
                    setText(null);
                } else {
                    setText(course.getCourseCode() + " - " + course.getCourseTitle());
                }
            }
        });

        // Add selection listener
        enrollmentTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        selectedEnrollment = newValue;
                        populateFields(newValue);
                    }
                }
        );

        // Set default date
        enrollmentDatePicker.setValue(LocalDate.now());

        // Load initial data
        loadStudents();
        loadCourses();
        loadEnrollments();
    }

    @FXML
    private void handleEnroll() {
        try {
            if (!validateInput()) {
                return;
            }

            Student selectedStudent = studentComboBox.getValue();
            Course selectedCourse = courseComboBox.getValue();

            Enrollment enrollment = new Enrollment();
            enrollment.setStudentId(selectedStudent.getId());
            enrollment.setCourseId(selectedCourse.getId());
            enrollment.setEnrollmentDate(enrollmentDatePicker.getValue());
            enrollment.setSemester(semesterComboBox.getValue());
            enrollment.setAcademicYear(academicYearField.getText().trim());

            String grade = gradeComboBox.getValue();
            if (grade != null && !grade.isEmpty()) {
                enrollment.setGrade(grade);
            }

            enrollmentService.saveEnrollment(enrollment);

            clearFields();
            loadEnrollments();
            showSuccess("Student enrolled successfully!");

        } catch (IllegalArgumentException e) {
            showError("Validation Error", e.getMessage());
        } catch (Exception e) {
            showError("Error", "Failed to enroll student: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateGrade() {
        try {
            if (selectedEnrollment == null) {
                showError("No Selection", "Please select an enrollment to update grade");
                return;
            }

            String grade = gradeComboBox.getValue();
            if (grade == null || grade.isEmpty()) {
                showError("Validation Error", "Please select a grade");
                return;
            }

            enrollmentService.updateGrade(selectedEnrollment.getId(), grade);

            clearFields();
            loadEnrollments();
            showSuccess("Grade updated successfully!");

        } catch (Exception e) {
            showError("Error", "Failed to update grade: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteEnrollment() {
        try {
            if (selectedEnrollment == null) {
                showError("No Selection", "Please select an enrollment to delete");
                return;
            }

            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Deletion");
            confirmAlert.setHeaderText("Delete Enrollment");
            confirmAlert.setContentText("Are you sure you want to delete this enrollment?");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                enrollmentService.deleteEnrollment(selectedEnrollment.getId());
                clearFields();
                loadEnrollments();
                showSuccess("Enrollment deleted successfully!");
            }

        } catch (Exception e) {
            showError("Error", "Failed to delete enrollment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClearFields() {
        clearFields();
    }

    @FXML
    private void handleShowAll() {
        loadEnrollments();
    }

    @FXML
    private void handleFilterByStudent() {
        try {
            Student selectedStudent = studentComboBox.getValue();
            if (selectedStudent == null) {
                showError("No Selection", "Please select a student to filter");
                return;
            }

            enrollmentList.clear();
            List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudent(selectedStudent.getId());
            enrollmentList.addAll(enrollments);
            updateEnrollmentCount();

        } catch (Exception e) {
            showError("Error", "Failed to filter enrollments: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleFilterByCourse() {
        try {
            Course selectedCourse = courseComboBox.getValue();
            if (selectedCourse == null) {
                showError("No Selection", "Please select a course to filter");
                return;
            }

            enrollmentList.clear();
            List<Enrollment> enrollments = enrollmentService.getEnrollmentsByCourse(selectedCourse.getId());
            enrollmentList.addAll(enrollments);
            updateEnrollmentCount();

        } catch (Exception e) {
            showError("Error", "Failed to filter enrollments: " + e.getMessage());
            e.printStackTrace();
        }
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

    private void loadCourses() {
        try {
            courseList.clear();
            courseList.addAll(courseService.getAllCourses());
        } catch (Exception e) {
            showError("Error", "Failed to load courses: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadEnrollments() {
        try {
            enrollmentList.clear();
            enrollmentList.addAll(enrollmentService.getAllEnrollments());
            updateEnrollmentCount();
        } catch (Exception e) {
            showError("Error", "Failed to load enrollments: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void populateFields(Enrollment enrollment) {
        // Find and select student
        for (Student student : studentList) {
            if (student.getId().equals(enrollment.getStudentId())) {
                studentComboBox.setValue(student);
                break;
            }
        }

        // Find and select course
        for (Course course : courseList) {
            if (course.getId().equals(enrollment.getCourseId())) {
                courseComboBox.setValue(course);
                break;
            }
        }

        enrollmentDatePicker.setValue(enrollment.getEnrollmentDate());
        semesterComboBox.setValue(enrollment.getSemester());
        academicYearField.setText(enrollment.getAcademicYear());
        gradeComboBox.setValue(enrollment.getGrade());
    }

    private void clearFields() {
        studentComboBox.setValue(null);
        courseComboBox.setValue(null);
        enrollmentDatePicker.setValue(LocalDate.now());
        semesterComboBox.setValue(null);
        academicYearField.clear();
        gradeComboBox.setValue(null);
        selectedEnrollment = null;
        enrollmentTable.getSelectionModel().clearSelection();
    }

    private boolean validateInput() {
        if (studentComboBox.getValue() == null) {
            showError("Validation Error", "Please select a student");
            return false;
        }

        if (courseComboBox.getValue() == null) {
            showError("Validation Error", "Please select a course");
            return false;
        }

        if (enrollmentDatePicker.getValue() == null) {
            showError("Validation Error", "Enrollment date is required");
            return false;
        }

        if (semesterComboBox.getValue() == null || semesterComboBox.getValue().isEmpty()) {
            showError("Validation Error", "Please select a semester");
            return false;
        }

        if (academicYearField.getText().trim().isEmpty()) {
            showError("Validation Error", "Academic year is required");
            return false;
        }

        return true;
    }

    private void updateEnrollmentCount() {
        if (enrollmentCountLabel != null) {
            enrollmentCountLabel.setText("Total Enrollments: " + enrollmentList.size());
        }
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