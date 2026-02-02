package org.skytech.systemdestudent.model;

public class Course {
    private Long id;
    private String courseCode;
    private String courseTitle;
    private int credits;
    private String courseDescription;
    private String department;
    private String prerequisites;
    private String instructor;

    // Default constructor
    public Course() {
    }

    // Basic parameterized constructor
    public Course(Long id, String courseCode, String courseTitle, int credits) {
        this.id = id;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.credits = credits;
    }

    // Full constructor with extended fields
    public Course(Long id, String courseCode, String courseTitle, int credits,
                  String courseDescription, String department,
                  String prerequisites, String instructor) {
        this.id = id;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.credits = credits;
        this.courseDescription = courseDescription;
        this.department = department;
        this.prerequisites = prerequisites;
        this.instructor = instructor;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(String prerequisites) {
        this.prerequisites = prerequisites;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", courseCode='" + courseCode + '\'' +
                ", courseTitle='" + courseTitle + '\'' +
                ", credits=" + credits +
                ", department='" + department + '\'' +
                '}';
    }
}