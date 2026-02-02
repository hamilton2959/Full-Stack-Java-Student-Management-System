package org.skytech.systemdestudent.repository;

import org.skytech.systemdestudent.config.DatabaseConfig;
import org.skytech.systemdestudent.model.Student;
import org.springframework.stereotype.Repository;

import java.sql.*;
        import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class StudentRepository {

    public Student save(Student student) throws SQLException {
        if (student.getId() == null) {
            return insert(student);
        } else {
            return update(student);
        }
    }

    private Student insert(Student student) throws SQLException {
        String sql = """
            INSERT INTO students (registration_number, first_name, last_name, 
                                 enrollment_date, email, date_of_birth, 
                                 department, phone_number, address)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, student.getRegistrationNumber());
            pstmt.setString(2, student.getFirstName());
            pstmt.setString(3, student.getLastName());
            pstmt.setDate(4, Date.valueOf(student.getEnrollmentDate()));
            pstmt.setString(5, student.getEmail());
            pstmt.setDate(6, student.getDateOfBirth() != null ?
                    Date.valueOf(student.getDateOfBirth()) : null);
            pstmt.setString(7, student.getDepartment());
            pstmt.setString(8, student.getPhoneNumber());
            pstmt.setString(9, student.getAddress());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating student failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    student.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating student failed, no ID obtained.");
                }
            }
        }

        return student;
    }

    private Student update(Student student) throws SQLException {
        String sql = """
            UPDATE students SET 
                registration_number = ?, first_name = ?, last_name = ?,
                enrollment_date = ?, email = ?, date_of_birth = ?,
                department = ?, phone_number = ?, address = ?
            WHERE id = ?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, student.getRegistrationNumber());
            pstmt.setString(2, student.getFirstName());
            pstmt.setString(3, student.getLastName());
            pstmt.setDate(4, Date.valueOf(student.getEnrollmentDate()));
            pstmt.setString(5, student.getEmail());
            pstmt.setDate(6, student.getDateOfBirth() != null ?
                    Date.valueOf(student.getDateOfBirth()) : null);
            pstmt.setString(7, student.getDepartment());
            pstmt.setString(8, student.getPhoneNumber());
            pstmt.setString(9, student.getAddress());
            pstmt.setLong(10, student.getId());

            pstmt.executeUpdate();
        }

        return student;
    }

    public Optional<Student> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM students WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStudent(rs));
                }
            }
        }

        return Optional.empty();
    }

    public Optional<Student> findByRegistrationNumber(String regNo) throws SQLException {
        String sql = "SELECT * FROM students WHERE registration_number = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, regNo);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStudent(rs));
                }
            }
        }

        return Optional.empty();
    }

    public List<Student> findAll() throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY registration_number";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        }

        return students;
    }

    public void deleteById(Long id) throws SQLException {
        String sql = "DELETE FROM students WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        }
    }

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getLong("id"));
        student.setRegistrationNumber(rs.getString("registration_number"));
        student.setFirstName(rs.getString("first_name"));
        student.setLastName(rs.getString("last_name"));
        student.setEnrollmentDate(rs.getDate("enrollment_date").toLocalDate());
        student.setEmail(rs.getString("email"));

        Date dob = rs.getDate("date_of_birth");
        if (dob != null) {
            student.setDateOfBirth(dob.toLocalDate());
        }

        student.setDepartment(rs.getString("department"));
        student.setPhoneNumber(rs.getString("phone_number"));
        student.setAddress(rs.getString("address"));

        return student;
    }
}