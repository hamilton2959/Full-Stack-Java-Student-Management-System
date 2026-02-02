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

@Component
public class ReportController {

    @FXML private ComboBox<Student> transcriptStudentComboBox;
    @FXML private ComboBox<Course> rosterCourseComboBox;
    @FXML private ComboBox<String> semesterComboBox;
    @FXML private TextField academicYearField;

    @FXML private Label reportTitleLabel;
    @FXML private Label totalStudentsLabel;
    @FXML private Label totalCoursesLabel;
    @FXML private Label totalEnrollmentsLabel;

    @FXML private TabPane reportTabPane;

    // Transcript Tab
    @FXML private Label transcriptHeaderLabel;
    @FXML private TextArea transcriptTextArea;

    // Course Roster Tab
    @FXML private Label rosterHeaderLabel;
    @FXML private TableView<Enrollment> rosterTableView;
    @FXML private TableColumn<Enrollment, String> rosterRegNoColumn;
    @FXML private TableColumn<Enrollment, String> rosterStudentNameColumn;
    @FXML private TableColumn<Enrollment, LocalDate> rosterEnrollmentDateColumn;
    @FXML private TableColumn<Enrollment, String> rosterGradeColumn;
    @FXML private TableColumn<Enrollment, String> rosterSemesterColumn;
    @FXML private TableColumn<Enrollment, String> rosterYearColumn;
    @FXML private Label rosterCountLabel;

    // Summary Tab
    @FXML private TextArea summaryTextArea;

    private final StudentService studentService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    private ObservableList<Student> studentList;
    private ObservableList<Course> courseList;
    private ObservableList<Enrollment> rosterList;

    @Autowired
    public ReportController(StudentService studentService,
                            CourseService courseService,
                            EnrollmentService enrollmentService) {
        this.studentService = studentService;
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
        this.studentList = FXCollections.observableArrayList();
        this.courseList = FXCollections.observableArrayList();
        this.rosterList = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        // Setup ComboBoxes
        transcriptStudentComboBox.setItems(studentList);
        rosterCourseComboBox.setItems(courseList);

        // Custom cell factory for student ComboBox
        transcriptStudentComboBox.setCellFactory(param -> new ListCell<Student>() {
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

        transcriptStudentComboBox.setButtonCell(new ListCell<Student>() {
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
        rosterCourseComboBox.setCellFactory(param -> new ListCell<Course>() {
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

        rosterCourseComboBox.setButtonCell(new ListCell<Course>() {
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

        // Setup roster table columns
        rosterRegNoColumn.setCellValueFactory(new PropertyValueFactory<>("studentRegNo"));
        rosterStudentNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        rosterEnrollmentDateColumn.setCellValueFactory(new PropertyValueFactory<>("enrollmentDate"));
        rosterGradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));
        rosterSemesterColumn.setCellValueFactory(new PropertyValueFactory<>("semester"));
        rosterYearColumn.setCellValueFactory(new PropertyValueFactory<>("academicYear"));

        rosterTableView.setItems(rosterList);

        // Load initial data
        loadStudents();
        loadCourses();
        updateStatistics();
    }

    @FXML
    private void handleGenerateTranscript() {
        try {
            Student student = transcriptStudentComboBox.getValue();
            if (student == null) {
                showError("No Selection", "Please select a student");
                return;
            }

            // Get student's enrollments
            List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudent(student.getId());

            // Build transcript
            StringBuilder transcript = new StringBuilder();
            transcript.append("═══════════════════════════════════════════════════════\n");
            transcript.append("                 STUDENT TRANSCRIPT\n");
            transcript.append("═══════════════════════════════════════════════════════\n\n");

            transcript.append("Student Information:\n");
            transcript.append("───────────────────────────────────────────────────────\n");
            transcript.append(String.format("Registration No: %s\n", student.getRegistrationNumber()));
            transcript.append(String.format("Name: %s\n", student.getFullName()));
            transcript.append(String.format("Department: %s\n", student.getDepartment() != null ? student.getDepartment() : "N/A"));
            transcript.append(String.format("Email: %s\n", student.getEmail() != null ? student.getEmail() : "N/A"));
            transcript.append(String.format("Enrollment Date: %s\n\n", student.getEnrollmentDate()));

            transcript.append("Academic Records:\n");
            transcript.append("═══════════════════════════════════════════════════════\n");
            transcript.append(String.format("%-12s %-30s %-8s %-10s %-8s\n",
                    "Course Code", "Course Title", "Credits", "Semester", "Grade"));
            transcript.append("───────────────────────────────────────────────────────\n");

            int totalCredits = 0;
            for (Enrollment enrollment : enrollments) {
                transcript.append(String.format("%-12s %-30s %-8d %-10s %-8s\n",
                        enrollment.getCourseCode(),
                        enrollment.getCourseTitle().length() > 30 ?
                                enrollment.getCourseTitle().substring(0, 27) + "..." :
                                enrollment.getCourseTitle(),
                        enrollment.getCourseCredits(),
                        enrollment.getSemester() != null ? enrollment.getSemester() : "N/A",
                        enrollment.getGrade() != null ? enrollment.getGrade() : "In Progress"));
                totalCredits += enrollment.getCourseCredits();
            }

            transcript.append("───────────────────────────────────────────────────────\n");
            transcript.append(String.format("Total Courses: %d\n", enrollments.size()));
            transcript.append(String.format("Total Credits: %d\n", totalCredits));
            transcript.append("═══════════════════════════════════════════════════════\n");
            transcript.append(String.format("\nGenerated on: %s\n", LocalDate.now()));

            transcriptTextArea.setText(transcript.toString());
            transcriptHeaderLabel.setText("Transcript for " + student.getFullName());
            reportTabPane.getSelectionModel().select(0); // Switch to transcript tab

        } catch (Exception e) {
            showError("Error", "Failed to generate transcript: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGenerateCourseRoster() {
        try {
            Course course = rosterCourseComboBox.getValue();
            if (course == null) {
                showError("No Selection", "Please select a course");
                return;
            }

            // Get course enrollments
            List<Enrollment> enrollments = enrollmentService.getEnrollmentsByCourse(course.getId());

            rosterList.clear();
            rosterList.addAll(enrollments);

            rosterHeaderLabel.setText("Course Roster: " + course.getCourseCode() + " - " + course.getCourseTitle());
            rosterCountLabel.setText("Total Students: " + enrollments.size());

            reportTabPane.getSelectionModel().select(1); // Switch to roster tab

        } catch (Exception e) {
            showError("Error", "Failed to generate course roster: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGenerateSummary() {
        try {
            String semester = semesterComboBox.getValue();
            String academicYear = academicYearField.getText().trim();

            List<Enrollment> allEnrollments = enrollmentService.getAllEnrollments();

            // Filter enrollments if criteria provided
            List<Enrollment> filteredEnrollments = allEnrollments;
            if (semester != null && !semester.equals("All")) {
                filteredEnrollments = filteredEnrollments.stream()
                        .filter(e -> semester.equals(e.getSemester()))
                        .toList();
            }
            if (!academicYear.isEmpty()) {
                filteredEnrollments = filteredEnrollments.stream()
                        .filter(e -> academicYear.equals(e.getAcademicYear()))
                        .toList();
            }

            // Build summary
            StringBuilder summary = new StringBuilder();
            summary.append("═══════════════════════════════════════════════════════\n");
            summary.append("             ENROLLMENT SUMMARY REPORT\n");
            summary.append("═══════════════════════════════════════════════════════\n\n");

            if (semester != null && !semester.equals("All")) {
                summary.append(String.format("Semester: %s\n", semester));
            }
            if (!academicYear.isEmpty()) {
                summary.append(String.format("Academic Year: %s\n", academicYear));
            }
            summary.append(String.format("Report Date: %s\n\n", LocalDate.now()));

            summary.append("Summary Statistics:\n");
            summary.append("───────────────────────────────────────────────────────\n");
            summary.append(String.format("Total Enrollments: %d\n", filteredEnrollments.size()));

            long enrollmentsWithGrades = filteredEnrollments.stream()
                    .filter(e -> e.getGrade() != null && !e.getGrade().isEmpty())
                    .count();
            summary.append(String.format("Enrollments with Grades: %d\n", enrollmentsWithGrades));
            summary.append(String.format("Enrollments In Progress: %d\n\n",
                    filteredEnrollments.size() - enrollmentsWithGrades));

            summary.append("Grade Distribution:\n");
            summary.append("───────────────────────────────────────────────────────\n");

            String[] grades = {"A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "D-", "E", "F"};
            for (String grade : grades) {
                long count = filteredEnrollments.stream()
                        .filter(e -> grade.equals(e.getGrade()))
                        .count();
                if (count > 0) {
                    summary.append(String.format("Grade %s: %d\n", grade, count));
                }
            }

            summary.append("\n═══════════════════════════════════════════════════════\n");

            summaryTextArea.setText(summary.toString());
            reportTabPane.getSelectionModel().select(2); // Switch to summary tab

        } catch (Exception e) {
            showError("Error", "Failed to generate summary: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefreshStatistics() {
        updateStatistics();
    }

    @FXML
    private void handlePrint() {
        showInfo("Feature Not Implemented", "Print functionality will be added in future version");
    }

    @FXML
    private void handleExportPDF() {
        showInfo("Feature Not Implemented", "PDF export will be added in future version");
    }

    @FXML
    private void handleExportExcel() {
        showInfo("Feature Not Implemented", "Excel export will be added in future version");
    }

    @FXML
    private void handleClearReport() {
        transcriptTextArea.clear();
        summaryTextArea.clear();
        rosterList.clear();
        transcriptHeaderLabel.setText("Student Transcript");
        rosterHeaderLabel.setText("Course Roster");
        rosterCountLabel.setText("Total Students: 0");
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

    private void updateStatistics() {
        try {
            int studentCount = studentService.getAllStudents().size();
            int courseCount = courseService.getAllCourses().size();
            int enrollmentCount = enrollmentService.getAllEnrollments().size();

            totalStudentsLabel.setText(String.valueOf(studentCount));
            totalCoursesLabel.setText(String.valueOf(courseCount));
            totalEnrollmentsLabel.setText(String.valueOf(enrollmentCount));

        } catch (Exception e) {
            showError("Error", "Failed to update statistics: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}