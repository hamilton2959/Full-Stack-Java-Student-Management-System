package org.skytech.systemdestudent.repository;

import org.skytech.systemdestudent.config.DatabaseConfig;
import org.skytech.systemdestudent.model.Enrollment;
import org.springframework.stereotype.Repository;

import java.sql.*;
        import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class EnrollmentRepository {

    public Enrollment save(Enrollment enrollment) throws SQLException {
        if (enrollment.getId() == null) {
            return insert(enrollment);
        } else {
            return update(enrollment);
        }
    }

    private Enrollment insert(Enrollment enrollment) throws SQLException {
        String sql = """
            INSERT INTO enrollments (student_id, course_id, enrollment_date, 
                                    grade, semester, academic_year)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setLong(1, enrollment.getStudentId());
            pstmt.setLong(2, enrollment.getCourseId());
            pstmt.setDate(3, Date.valueOf(enrollment.getEnrollmentDate()));
            pstmt.setString(4, enrollment.getGrade());
            pstmt.setString(5, enrollment.getSemester());
            pstmt.setString(6, enrollment.getAcademicYear());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating enrollment failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    enrollment.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating enrollment failed, no ID obtained.");
                }
            }
        }

        return enrollment;
    }

    private Enrollment update(Enrollment enrollment) throws SQLException {
        String sql = """
            UPDATE enrollments SET 
                student_id = ?, course_id = ?, enrollment_date = ?,
                grade = ?, semester = ?, academic_year = ?
            WHERE id = ?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, enrollment.getStudentId());
            pstmt.setLong(2, enrollment.getCourseId());
            pstmt.setDate(3, Date.valueOf(enrollment.getEnrollmentDate()));
            pstmt.setString(4, enrollment.getGrade());
            pstmt.setString(5, enrollment.getSemester());
            pstmt.setString(6, enrollment.getAcademicYear());
            pstmt.setLong(7, enrollment.getId());

            pstmt.executeUpdate();
        }

        return enrollment;
    }

    public Optional<Enrollment> findById(Long id) throws SQLException {
        String sql = """
            SELECT e.*, 
                   s.registration_number as student_reg_no,
                   CONCAT(s.first_name, ' ', s.last_name) as student_name,
                   c.course_code, c.course_title, c.credits as course_credits
            FROM enrollments e
            JOIN students s ON e.student_id = s.id
            JOIN courses c ON e.course_id = c.id
            WHERE e.id = ?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEnrollment(rs));
                }
            }
        }

        return Optional.empty();
    }

    public List<Enrollment> findAll() throws SQLException {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = """
            SELECT e.*, 
                   s.registration_number as student_reg_no,
                   CONCAT(s.first_name, ' ', s.last_name) as student_name,
                   c.course_code, c.course_title, c.credits as course_credits
            FROM enrollments e
            JOIN students s ON e.student_id = s.id
            JOIN courses c ON e.course_id = c.id
            ORDER BY e.enrollment_date DESC
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                enrollments.add(mapResultSetToEnrollment(rs));
            }
        }

        return enrollments;
    }

    public List<Enrollment> findByStudentId(Long studentId) throws SQLException {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = """
            SELECT e.*, 
                   s.registration_number as student_reg_no,
                   CONCAT(s.first_name, ' ', s.last_name) as student_name,
                   c.course_code, c.course_title, c.credits as course_credits
            FROM enrollments e
            JOIN students s ON e.student_id = s.id
            JOIN courses c ON e.course_id = c.id
            WHERE e.student_id = ?
            ORDER BY e.enrollment_date DESC
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, studentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    enrollments.add(mapResultSetToEnrollment(rs));
                }
            }
        }

        return enrollments;
    }

    public List<Enrollment> findByCourseId(Long courseId) throws SQLException {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = """
            SELECT e.*, 
                   s.registration_number as student_reg_no,
                   CONCAT(s.first_name, ' ', s.last_name) as student_name,
                   c.course_code, c.course_title, c.credits as course_credits
            FROM enrollments e
            JOIN students s ON e.student_id = s.id
            JOIN courses c ON e.course_id = c.id
            WHERE e.course_id = ?
            ORDER BY s.registration_number
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, courseId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    enrollments.add(mapResultSetToEnrollment(rs));
                }
            }
        }

        return enrollments;
    }

    public void deleteById(Long id) throws SQLException {
        String sql = "DELETE FROM enrollments WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        }
    }

    private Enrollment mapResultSetToEnrollment(ResultSet rs) throws SQLException {
        Enrollment enrollment = new Enrollment();
        enrollment.setId(rs.getLong("id"));
        enrollment.setStudentId(rs.getLong("student_id"));
        enrollment.setCourseId(rs.getLong("course_id"));
        enrollment.setEnrollmentDate(rs.getDate("enrollment_date").toLocalDate());
        enrollment.setGrade(rs.getString("grade"));
        enrollment.setSemester(rs.getString("semester"));
        enrollment.setAcademicYear(rs.getString("academic_year"));

        // Additional fields from joins
        enrollment.setStudentRegNo(rs.getString("student_reg_no"));
        enrollment.setStudentName(rs.getString("student_name"));
        enrollment.setCourseCode(rs.getString("course_code"));
        enrollment.setCourseTitle(rs.getString("course_title"));
        enrollment.setCourseCredits(rs.getInt("course_credits"));

        return enrollment;
    }
}