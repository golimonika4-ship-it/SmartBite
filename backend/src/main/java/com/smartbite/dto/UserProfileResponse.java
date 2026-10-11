package com.smartbite.dto;

public class UserProfileResponse {

    private Long id;
    private String name;
    private String rollNumber;
    private String email;
    private String role;

    public UserProfileResponse() {
    }

    public UserProfileResponse(Long id, String name, String rollNumber, String email, String role) {
        this.id = id;
        this.name = name;
        this.rollNumber = rollNumber;
        this.email = email;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCollegeRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public void setCollegeRollNumber(String collegeRollNumber) {
        this.rollNumber = collegeRollNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
