package com.smartbite.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "College roll number is required")
    @Size(min = 3, max = 30, message = "Roll number must be between 3 and 30 characters")
    @com.fasterxml.jackson.annotation.JsonAlias({"collegeRollNumber", "roll"})
    private String rollNumber;

    @NotBlank(message = "Email address is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be at least 6 characters long")
    private String password;

    public RegisterRequest() {
    }

    public RegisterRequest(String name, String rollNumber, String email, String password) {
        this.name = name;
        this.rollNumber = rollNumber;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getCollegeRollNumber() {
        return rollNumber;
    }

    public void setCollegeRollNumber(String collegeRollNumber) {
        if (this.rollNumber == null || this.rollNumber.isBlank()) {
            this.rollNumber = collegeRollNumber;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
