package com.smartbite.service;

import com.smartbite.dto.MenuItemRequest;
import com.smartbite.model.MenuItem;
import com.smartbite.repository.MenuItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;

    public MenuItemService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public List<MenuItem> getAllMenuItems(String category, String search) {
        String cleanCategory = (category != null && !category.trim().isEmpty() && !"all".equalsIgnoreCase(category.trim()))
                ? category.trim()
                : null;
        String cleanSearch = (search != null && !search.trim().isEmpty())
                ? search.trim()
                : null;

        if (cleanCategory == null && cleanSearch == null) {
            return menuItemRepository.findAll();
        }

        return menuItemRepository.filterMenuItems(cleanCategory, cleanSearch);
    }

    public Optional<MenuItem> getMenuItemById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Optional.empty();
        }
        return menuItemRepository.findById(id.trim());
    }

    public long count() {
        return menuItemRepository.count();
    }

    @Transactional
    public List<MenuItem> saveAll(List<MenuItem> items) {
        return menuItemRepository.saveAll(items);
    }

    @Transactional
    public MenuItem saveMenuItem(MenuItem item) {
        return menuItemRepository.save(item);
    }

    /**
     * Add a new menu item with input validation and sanitization
     */
    @Transactional
    public MenuItem createMenuItem(MenuItemRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Menu item request cannot be empty");
        }

        validateMenuItemRequest(request);

        String rawId = request.getId() != null ? sanitizeString(request.getId()).toLowerCase(Locale.ROOT) : "";
        String itemId;
        if (!rawId.isEmpty()) {
            // Validate ID format: letters, numbers, hyphens
            if (!rawId.matches("^[a-z0-9_-]{2,32}$")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item ID must contain only alphanumeric characters, hyphens, and underscores (2-32 chars)");
            }
            if (menuItemRepository.existsById(rawId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "A menu item with ID '" + rawId + "' already exists");
            }
            itemId = rawId;
        } else {
            // Generate clean ID
            String catPrefix = getCategoryPrefix(request.getCategory());
            itemId = "sb-" + catPrefix + "-" + UUID.randomUUID().toString().substring(0, 6);
        }

        String cleanName = sanitizeString(request.getName());
        String cleanCategory = sanitizeCategory(request.getCategory());
        String cleanCategoryLabel = resolveCategoryLabel(cleanCategory, request.getCategoryLabel());
        double cleanPrice = Math.round(request.getPrice() * 100.0) / 100.0;
        String cleanAvailability = sanitizeAvailability(request.getAvailability());
        double cleanRating = clampRating(request.getRating());
        String cleanPrepTime = sanitizeString(request.getPrepTime());
        if (cleanPrepTime.isEmpty()) cleanPrepTime = "5-10 mins";
        String cleanDescription = sanitizeString(request.getDescription());
        String cleanImageUrl = sanitizeImageUrl(request.getImageUrl());

        MenuItem newItem = new MenuItem(
                itemId,
                cleanName,
                cleanCategory,
                cleanCategoryLabel,
                cleanPrice,
                request.getIsVeg(),
                cleanAvailability,
                cleanRating,
                cleanPrepTime,
                cleanDescription,
                cleanImageUrl
        );

        return menuItemRepository.save(newItem);
    }

    /**
     * Edit existing menu item details and prices with validation and sanitization
     */
    @Transactional
    public MenuItem updateMenuItem(String id, MenuItemRequest request) {
        if (id == null || id.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item ID cannot be empty");
        }
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Menu item request cannot be empty");
        }

        MenuItem existingItem = menuItemRepository.findById(id.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu item not found with ID: " + id));

        validateMenuItemRequest(request);

        String cleanName = sanitizeString(request.getName());
        String cleanCategory = sanitizeCategory(request.getCategory());
        String cleanCategoryLabel = resolveCategoryLabel(cleanCategory, request.getCategoryLabel());
        double cleanPrice = Math.round(request.getPrice() * 100.0) / 100.0;
        String cleanAvailability = sanitizeAvailability(request.getAvailability());
        double cleanRating = clampRating(request.getRating());
        String cleanPrepTime = sanitizeString(request.getPrepTime());
        if (cleanPrepTime.isEmpty()) cleanPrepTime = existingItem.getPrepTime();
        String cleanDescription = sanitizeString(request.getDescription());
        String cleanImageUrl = sanitizeImageUrl(request.getImageUrl());
        if (cleanImageUrl.isEmpty()) cleanImageUrl = existingItem.getImageUrl();

        existingItem.setName(cleanName);
        existingItem.setCategory(cleanCategory);
        existingItem.setCategoryLabel(cleanCategoryLabel);
        existingItem.setPrice(cleanPrice);
        existingItem.setIsVeg(request.getIsVeg());
        existingItem.setAvailability(cleanAvailability);
        existingItem.setRating(cleanRating);
        existingItem.setPrepTime(cleanPrepTime);
        existingItem.setDescription(cleanDescription);
        existingItem.setImageUrl(cleanImageUrl);

        return menuItemRepository.save(existingItem);
    }

    /**
     * Delete an existing menu item by ID
     */
    @Transactional
    public void deleteMenuItem(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item ID cannot be empty");
        }

        MenuItem existingItem = menuItemRepository.findById(id.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu item not found with ID: " + id));

        menuItemRepository.delete(existingItem);
    }

    /**
     * Update menu item availability status (Available / Out of Stock)
     */
    @Transactional
    public MenuItem updateAvailability(String id, String availability) {
        if (id == null || id.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item ID cannot be empty");
        }

        MenuItem item = menuItemRepository.findById(id.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu item not found with ID: " + id));

        if (availability == null || availability.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Availability value cannot be empty");
        }

        String standardized = sanitizeAvailability(availability);
        item.setAvailability(standardized);
        return menuItemRepository.save(item);
    }

    private void validateMenuItemRequest(MenuItemRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item name is required");
        }
        if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category is required");
        }
        if (request.getPrice() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price is required");
        }
        if (request.getPrice() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price must be positive or zero");
        }
        if (request.getIsVeg() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "isVeg dietary indicator is required");
        }
    }

    private String sanitizeString(String input) {
        if (input == null) return "";
        // Strip HTML tags for defense against XSS
        return input.replaceAll("<[^>]*>", "").trim();
    }

    private String sanitizeCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return "snacks";
        }
        String clean = sanitizeString(category).toLowerCase(Locale.ROOT);
        if (clean.contains("breakfast")) return "breakfast";
        if (clean.contains("meal")) return "meals";
        if (clean.contains("snack")) return "snacks";
        if (clean.contains("beverage") || clean.contains("drink")) return "beverages";
        return clean.replaceAll("[^a-z0-9_-]", "");
    }

    private String resolveCategoryLabel(String category, String label) {
        if (label != null && !label.trim().isEmpty()) {
            return sanitizeString(label);
        }
        switch (category) {
            case "breakfast": return "Breakfast";
            case "meals": return "Meals";
            case "snacks": return "Snacks";
            case "beverages": return "Beverages";
            default:
                if (category.length() > 1) {
                    return Character.toUpperCase(category.charAt(0)) + category.substring(1);
                }
                return category.toUpperCase(Locale.ROOT);
        }
    }

    private String sanitizeAvailability(String availability) {
        if (availability == null || availability.trim().isEmpty()) {
            return "Available";
        }
        String raw = availability.trim().toLowerCase(Locale.ROOT);
        if (raw.equals("available") || raw.equals("in_stock") || raw.equals("in stock")) {
            return "Available";
        } else if (raw.equals("out_of_stock") || raw.equals("out of stock") || raw.equals("unavailable")) {
            return "Out of Stock";
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid availability status: '" + availability + "'. Must be 'Available' or 'Out of Stock'");
        }
    }

    private double clampRating(Double rating) {
        if (rating == null) return 4.5;
        if (rating < 1.0) return 1.0;
        if (rating > 5.0) return 5.0;
        return Math.round(rating * 10.0) / 10.0;
    }

    private String sanitizeImageUrl(String url) {
        if (url == null) return "";
        String clean = url.trim();
        if (clean.startsWith("http://") || clean.startsWith("https://") || clean.startsWith("data:image/")) {
            return clean;
        }
        return "";
    }

    private String getCategoryPrefix(String category) {
        if (category == null) return "s";
        switch (category.toLowerCase(Locale.ROOT)) {
            case "breakfast": return "b";
            case "meals": return "m";
            case "beverages": return "v";
            default: return "s";
        }
    }
}
