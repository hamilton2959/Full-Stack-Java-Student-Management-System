package org.skytech.systemdestudent.service;

import org.skytech.systemdestudent.model.Course;
import org.skytech.systemdestudent.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository repository;

    @Autowired
    public CourseService(CourseRepository repository) {
        this.repository = repository;
    }

    public Course saveCourse(Course course) throws SQLException {
        // Validation logic
        validateCourse(course);

        // Check for duplicate course code if new course
        if (course.getId() == null) {
            Optional<Course> existing = repository.findByCourseCode(
                    course.getCourseCode());
            if (existing.isPresent()) {
                throw new IllegalArgumentException(
                        "Course code already exists: " + course.getCourseCode());
            }
        }

        return repository.save(course);
    }

    public Optional<Course> findCourseById(Long id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid course ID");
        }
        return repository.findById(id);
    }

    public Optional<Course> findCourseByCourseCode(String courseCode) throws SQLException {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Course code is required");
        }
        return repository.findByCourseCode(courseCode);
    }

    public List<Course> getAllCourses() throws SQLException {
        return repository.findAll();
    }

    public void deleteCourse(Long id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid course ID");
        }

        Optional<Course> course = repository.findById(id);
        if (course.isEmpty()) {
            throw new IllegalArgumentException("Course not found with ID: " + id);
        }

        repository.deleteById(id);
    }

    private void validateCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }

        if (course.getCourseCode() == null ||
                course.getCourseCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Course code is required");
        }

        if (course.getCourseTitle() == null ||
                course.getCourseTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Course title is required");
        }

        if (course.getCredits() <= 0) {
            throw new IllegalArgumentException("Credits must be greater than 0");
        }

        if (course.getCredits() > 10) {
            throw new IllegalArgumentException("Credits cannot exceed 10");
        }
    }
}