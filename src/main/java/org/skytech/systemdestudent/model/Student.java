package org.skytech.systemdestudent.model;

import java.time.LocalDate;

public class Student {
    private Long id;
    private String registrationNumber;
    private String firstName;
    private String lastName;
    private LocalDate enrollmentDate;
    private String email;
    private LocalDate dateOfBirth;
    private String department;
    private String phoneNumber;
    private String address;

    // Default constructor
    public Student() {
    }

    // Parameterized constructor
    public Student(Long id, String registrationNumber, String firstName,
                   String lastName, LocalDate enrollmentDate) {
        this.id = id;
        this.registrationNumber = registrationNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.enrollmentDate = enrollmentDate;
    }

    // Full constructor with extended fields
    public Student(Long id, String registrationNumber, String firstName,
                   String lastName, LocalDate enrollmentDate, String email,
                   LocalDate dateOfBirth, String department, String phoneNumber,
                   String address) {
        this.id = id;
        this.registrationNumber = registrationNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.enrollmentDate = enrollmentDate;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.department = department;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", enrollmentDate=" + enrollmentDate +
                ", email='" + email + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}