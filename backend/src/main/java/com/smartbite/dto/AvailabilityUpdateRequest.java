package com.smartbite.dto;

import jakarta.validation.constraints.NotBlank;

public class AvailabilityUpdateRequest {

    @NotBlank(message = "Availability status is required")
    private String availability;

    public AvailabilityUpdateRequest() {
    }

    public AvailabilityUpdateRequest(String availability) {
        this.availability = availability;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }
}
