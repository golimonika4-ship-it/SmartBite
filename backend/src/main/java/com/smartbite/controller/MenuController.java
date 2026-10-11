package com.smartbite.controller;

import com.smartbite.model.MenuItem;
import com.smartbite.service.MenuItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuItemService menuItemService;

    public MenuController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    /**
     * Retrieve menu items with optional category and search filters
     *
     * @param category category filter (e.g., "breakfast", "meals", "snacks", "beverages")
     * @param search   search term matching name or description
     * @return List of matching MenuItem records
     */
    @GetMapping
    public ResponseEntity<List<MenuItem>> getMenuItems(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {
        List<MenuItem> items = menuItemService.getAllMenuItems(category, search);
        return ResponseEntity.ok(items);
    }

    /**
     * Retrieve single menu item by ID
     *
     * @param id menu item ID (e.g. "sb-b1")
     * @return MenuItem if found, 404 otherwise
     */
    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getMenuItemById(@PathVariable String id) {
        return menuItemService.getMenuItemById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
