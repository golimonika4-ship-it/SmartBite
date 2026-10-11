package com.smartbite.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.Objects;

@Entity
@Table(name = "menu_items")
public class MenuItem {

    @Id
    @Column(name = "id", length = 32, nullable = false)
    private String id;

    @NotBlank(message = "Item name is required")
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @NotBlank(message = "Category is required")
    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Column(name = "category_label", length = 50)
    private String categoryLabel;

    @NotNull(message = "Price is required")
    @PositiveOrZero(message = "Price must be positive or zero")
    @Column(name = "price", nullable = false)
    private Double price;

    @NotNull(message = "isVeg flag is required")
    @Column(name = "is_veg", nullable = false)
    private Boolean isVeg;

    @Column(name = "availability", length = 50)
    private String availability = "Available";

    @Column(name = "rating")
    private Double rating = 4.5;

    @Column(name = "prep_time", length = 50)
    private String prepTime;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    public MenuItem() {
    }

    public MenuItem(String id, String name, String category, String categoryLabel, Double price,
                    Boolean isVeg, String availability, Double rating, String prepTime,
                    String description, String imageUrl) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.categoryLabel = categoryLabel;
        this.price = price;
        this.isVeg = isVeg;
        this.availability = availability;
        this.rating = rating;
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

    /**
     * Provide 'image' alias matching existing frontend property name
     */
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuItem menuItem = (MenuItem) o;
        return Objects.equals(id, menuItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
