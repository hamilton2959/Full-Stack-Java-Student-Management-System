package org.skytech.systemdestudent.repository;

import org.skytech.systemdestudent.config.DatabaseConfig;
import org.skytech.systemdestudent.model.Course;
import org.springframework.stereotype.Repository;

import java.sql.*;
        import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class CourseRepository {

    public Course save(Course course) throws SQLException {
        if (course.getId() == null) {
            return insert(course);
        } else {
            return update(course);
        }
    }

    private Course insert(Course course) throws SQLException {
        String sql = """
            INSERT INTO courses (course_code, course_title, credits, 
                                course_description, department, prerequisites, instructor)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, course.getCourseCode());
            pstmt.setString(2, course.getCourseTitle());
            pstmt.setInt(3, course.getCredits());
            pstmt.setString(4, course.getCourseDescription());
            pstmt.setString(5, course.getDepartment());
            pstmt.setString(6, course.getPrerequisites());
            pstmt.setString(7, course.getInstructor());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating course failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    course.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating course failed, no ID obtained.");
                }
            }
        }

        return course;
    }

    private Course update(Course course) throws SQLException {
        String sql = """
            UPDATE courses SET 
                course_code = ?, course_title = ?, credits = ?,
                course_description = ?, department = ?, 
                prerequisites = ?, instructor = ?
            WHERE id = ?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, course.getCourseCode());
            pstmt.setString(2, course.getCourseTitle());
            pstmt.setInt(3, course.getCredits());
            pstmt.setString(4, course.getCourseDescription());
            pstmt.setString(5, course.getDepartment());
            pstmt.setString(6, course.getPrerequisites());
            pstmt.setString(7, course.getInstructor());
            pstmt.setLong(8, course.getId());

            pstmt.executeUpdate();
        }

        return course;
    }

    public Optional<Course> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM courses WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCourse(rs));
                }
            }
        }

        return Optional.empty();
    }

    public Optional<Course> findByCourseCode(String courseCode) throws SQLException {
        String sql = "SELECT * FROM courses WHERE course_code = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, courseCode);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCourse(rs));
                }
            }
        }

        return Optional.empty();
    }

    public List<Course> findAll() throws SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses ORDER BY course_code";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }
        }

        return courses;
    }

    public void deleteById(Long id) throws SQLException {
        String sql = "DELETE FROM courses WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        }
    }

    private Course mapResultSetToCourse(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setId(rs.getLong("id"));
        course.setCourseCode(rs.getString("course_code"));
        course.setCourseTitle(rs.getString("course_title"));
        course.setCredits(rs.getInt("credits"));
        course.setCourseDescription(rs.getString("course_description"));
        course.setDepartment(rs.getString("department"));
        course.setPrerequisites(rs.getString("prerequisites"));
        course.setInstructor(rs.getString("instructor"));

        return course;
    }
}