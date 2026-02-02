package org.skytech.systemdestudent.service;

import org.skytech.systemdestudent.model.Enrollment;
import org.skytech.systemdestudent.repository.CourseRepository;
import org.skytech.systemdestudent.repository.EnrollmentRepository;
import org.skytech.systemdestudent.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             StudentRepository studentRepository,
                             CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    public Enrollment saveEnrollment(Enrollment enrollment) throws SQLException {
        // Validation logic
        validateEnrollment(enrollment);

        // Verify student exists
        if (studentRepository.findById(enrollment.getStudentId()).isEmpty()) {
            throw new IllegalArgumentException(
                    "Student not found with ID: " + enrollment.getStudentId());
        }

        // Verify course exists
        if (courseRepository.findById(enrollment.getCourseId()).isEmpty()) {
            throw new IllegalArgumentException(
                    "Course not found with ID: " + enrollment.getCourseId());
        }

        return enrollmentRepository.save(enrollment);
    }

    public Optional<Enrollment> findEnrollmentById(Long id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid enrollment ID");
        }
        return enrollmentRepository.findById(id);
    }

    public List<Enrollment> getAllEnrollments() throws SQLException {
        return enrollmentRepository.findAll();
    }

    public List<Enrollment> getEnrollmentsByStudent(Long studentId) throws SQLException {
        if (studentId == null || studentId <= 0) {
            throw new IllegalArgumentException("Invalid student ID");
        }
        return enrollmentRepository.findByStudentId(studentId);
    }

    public List<Enrollment> getEnrollmentsByCourse(Long courseId) throws SQLException {
        if (courseId == null || courseId <= 0) {
            throw new IllegalArgumentException("Invalid course ID");
        }
        return enrollmentRepository.findByCourseId(courseId);
    }

    public void deleteEnrollment(Long id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid enrollment ID");
        }

        Optional<Enrollment> enrollment = enrollmentRepository.findById(id);
        if (enrollment.isEmpty()) {
            throw new IllegalArgumentException("Enrollment not found with ID: " + id);
        }

        enrollmentRepository.deleteById(id);
    }

    public Enrollment updateGrade(Long enrollmentId, String grade) throws SQLException {
        if (enrollmentId == null || enrollmentId <= 0) {
            throw new IllegalArgumentException("Invalid enrollment ID");
        }

        Optional<Enrollment> enrollmentOpt = enrollmentRepository.findById(enrollmentId);
        if (enrollmentOpt.isEmpty()) {
            throw new IllegalArgumentException("Enrollment not found with ID: " + enrollmentId);
        }

        validateGrade(grade);

        Enrollment enrollment = enrollmentOpt.get();
        enrollment.setGrade(grade);

        return enrollmentRepository.save(enrollment);
    }

    private void validateEnrollment(Enrollment enrollment) {
        if (enrollment == null) {
            throw new IllegalArgumentException("Enrollment cannot be null");
        }

        if (enrollment.getStudentId() == null || enrollment.getStudentId() <= 0) {
            throw new IllegalArgumentException("Valid student ID is required");
        }

        if (enrollment.getCourseId() == null || enrollment.getCourseId() <= 0) {
            throw new IllegalArgumentException("Valid course ID is required");
        }

        if (enrollment.getEnrollmentDate() == null) {
            throw new IllegalArgumentException("Enrollment date is required");
        }

        // Validate grade if provided
        if (enrollment.getGrade() != null && !enrollment.getGrade().trim().isEmpty()) {
            validateGrade(enrollment.getGrade());
        }
    }

    private void validateGrade(String grade) {
        if (grade == null || grade.trim().isEmpty()) {
            throw new IllegalArgumentException("Grade cannot be empty");
        }

        String[] validGrades = {"A", "A-", "B+", "B", "B-", "C+", "C", "C-",
                "D+", "D", "D-", "E", "F"};
        boolean isValid = false;
        for (String validGrade : validGrades) {
            if (validGrade.equals(grade.trim())) {
                isValid = true;
                break;
            }
        }

        if (!isValid) {
            throw new IllegalArgumentException("Invalid grade. Must be one of: A, A-, B+, B, B-, C+, C, C-, D+, D, D-, E, F");
        }
    }
}