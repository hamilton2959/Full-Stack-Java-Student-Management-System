package org.skytech.systemdestudent.service;

import org.skytech.systemdestudent.model.Student;
import org.skytech.systemdestudent.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository repository;

    @Autowired
    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    public Student saveStudent(Student student) throws SQLException {
        // Validation logic
        validateStudent(student);

        // Check for duplicate registration number if new student
        if (student.getId() == null) {
            Optional<Student> existing = repository.findByRegistrationNumber(
                    student.getRegistrationNumber());
            if (existing.isPresent()) {
                throw new IllegalArgumentException(
                        "Registration number already exists: " + student.getRegistrationNumber());
            }
        }

        return repository.save(student);
    }

    public Optional<Student> findStudentById(Long id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid student ID");
        }
        return repository.findById(id);
    }

    public Optional<Student> findStudentByRegistrationNumber(String regNo) throws SQLException {
        if (regNo == null || regNo.trim().isEmpty()) {
            throw new IllegalArgumentException("Registration number is required");
        }
        return repository.findByRegistrationNumber(regNo);
    }

    public List<Student> getAllStudents() throws SQLException {
        return repository.findAll();
    }

    public void deleteStudent(Long id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid student ID");
        }

        Optional<Student> student = repository.findById(id);
        if (student.isEmpty()) {
            throw new IllegalArgumentException("Student not found with ID: " + id);
        }

        repository.deleteById(id);
    }

    private void validateStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }

        if (student.getRegistrationNumber() == null ||
                student.getRegistrationNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Registration number is required");
        }

        if (student.getFirstName() == null ||
                student.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }

        if (student.getLastName() == null ||
                student.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }

        if (student.getEnrollmentDate() == null) {
            throw new IllegalArgumentException("Enrollment date is required");
        }

        // Optional: Email validation
        if (student.getEmail() != null && !student.getEmail().trim().isEmpty()) {
            if (!student.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new IllegalArgumentException("Invalid email format");
            }
        }
    }
}