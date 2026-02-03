package org.skytech.systemdestudent.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import java.sql.*;

@Component
//@Configuration
@Configuration(proxyBeanMethods = false)
public class DatabaseConfig {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "srms_db";
    private static final String FULL_DB_URL = DB_URL + DB_NAME;
    private static final String USER = "root";
    private static final String PASS = ""; // Update with your MySQL password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(FULL_DB_URL, USER, PASS);
    }

    public static void initializeDatabase() {
        // Step 1: Create database if not exists
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement()) {

            String createDbSql = "CREATE DATABASE IF NOT EXISTS " + DB_NAME;
            stmt.executeUpdate(createDbSql);
            System.out.println("Database created/verified: " + DB_NAME);

        } catch (SQLException e) {
            System.err.println("Error creating database: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // Step 2: Create tables in the database
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Create Students table
            String createStudentsTable = """
                CREATE TABLE IF NOT EXISTS students (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    registration_number VARCHAR(50) UNIQUE NOT NULL,
                    first_name VARCHAR(100) NOT NULL,
                    last_name VARCHAR(100) NOT NULL,
                    enrollment_date DATE NOT NULL,
                    email VARCHAR(100),
                    date_of_birth DATE,
                    department VARCHAR(100),
                    phone_number VARCHAR(20),
                    address TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
            """;
            stmt.executeUpdate(createStudentsTable);
            System.out.println("Students table created/verified");

            // Create Courses table
            String createCoursesTable = """
                CREATE TABLE IF NOT EXISTS courses (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    course_code VARCHAR(20) UNIQUE NOT NULL,
                    course_title VARCHAR(200) NOT NULL,
                    credits INT NOT NULL,
                    course_description TEXT,
                    department VARCHAR(100),
                    prerequisites TEXT,
                    instructor VARCHAR(100),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
            """;
            stmt.executeUpdate(createCoursesTable);
            System.out.println("Courses table created/verified");

            // Create Enrollments table
            String createEnrollmentsTable = """
                CREATE TABLE IF NOT EXISTS enrollments (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    student_id BIGINT NOT NULL,
                    course_id BIGINT NOT NULL,
                    enrollment_date DATE NOT NULL,
                    grade VARCHAR(5),
                    semester VARCHAR(20),
                    academic_year VARCHAR(20),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
                    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
                    UNIQUE KEY unique_enrollment (student_id, course_id, semester, academic_year)
                )
            """;
            stmt.executeUpdate(createEnrollmentsTable);
            System.out.println("Enrollments table created/verified");

            // Create Grades table for detailed grade tracking
            String createGradesTable = """
                CREATE TABLE IF NOT EXISTS grades (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    enrollment_id BIGINT NOT NULL,
                    grade_value VARCHAR(5) NOT NULL,
                    grade_point DECIMAL(3,2),
                    remarks TEXT,
                    graded_date DATE,
                    graded_by VARCHAR(100),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    FOREIGN KEY (enrollment_id) REFERENCES enrollments(id) ON DELETE CASCADE
                )
            """;
            stmt.executeUpdate(createGradesTable);
            System.out.println("Grades table created/verified");

            System.out.println("Database initialization completed successfully!");

        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static String getDbUrl() {
        return FULL_DB_URL;
    }

    public static String getUser() {
        return USER;
    }

    public static String getPassword() {
        return PASS;
    }
}