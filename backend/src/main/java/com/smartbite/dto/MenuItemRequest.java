package com.smartbite.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class MenuItemRequest {

    private String id;

    @NotBlank(message = "Item name is required")
    @Size(min = 2, max = 150, message = "Item name must be between 2 and 150 characters")
    private String name;

    @NotBlank(message = "Category is required")
    @Size(max = 50, message = "Category must not exceed 50 characters")
    private String category;

    @Size(max = 50, message = "Category label must not exceed 50 characters")
    private String categoryLabel;

    @NotNull(message = "Price is required")
    @PositiveOrZero(message = "Price must be positive or zero")
    private Double price;

    @NotNull(message = "isVeg flag is required")
    @JsonProperty("isVeg")
    @JsonAlias({"veg", "is_veg"})
    private Boolean isVeg;

    private String availability = "Available";

    private Double rating = 4.5;

    @Size(max = 50, message = "Preparation time must not exceed 50 characters")
    private String prepTime;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    @JsonAlias({"image", "imageUrl", "image_url"})
    private String imageUrl;

    public MenuItemRequest() {
    }

    public MenuItemRequest(String id, String name, String category, String categoryLabel,
                           Double price, Boolean isVeg, String availability, Double rating,
                           String prepTime, String description, String imageUrl) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.categoryLabel = categoryLabel;
        this.price = price;
        this.isVeg = isVeg;
        this.availability = availability != null ? availability : "Available";
        this.rating = rating != null ? rating : 4.5;
        this.prepTime = prepTime;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryLabel() {
        return categoryLabel;
    }

    public void setCategoryLabel(String categoryLabel) {
        this.categoryLabel = categoryLabel;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @JsonProperty("isVeg")
    public Boolean getIsVeg() {
        return isVeg;
    }

    @JsonProperty("isVeg")
    public void setIsVeg(Boolean isVeg) {
        this.isVeg = isVeg;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getPrepTime() {
        return prepTime;
    }

    public void setPrepTime(String prepTime) {
        this.prepTime = prepTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @JsonProperty("image")
    public String getImage() {
        return imageUrl;
    }

    @JsonProperty("image")
    public void setImage(String image) {
        if (this.imageUrl == null) {
            this.imageUrl = image;
        }
    }
}
