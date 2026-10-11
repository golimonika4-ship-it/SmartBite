package com.smartbite.controller;

import com.smartbite.dto.AvailabilityUpdateRequest;
import com.smartbite.dto.DashboardStatsResponse;
import com.smartbite.dto.MenuItemRequest;
import com.smartbite.dto.OrderResponse;
import com.smartbite.dto.OrderStatusUpdateRequest;
import com.smartbite.model.MenuItem;
import com.smartbite.service.MenuItemService;
import com.smartbite.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Admin / Kitchen Order & Menu Management Controller.
 * Protected with server-side role-based authorization (ROLE_ADMIN).
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final OrderService orderService;
    private final MenuItemService menuItemService;

    public AdminController(OrderService orderService, MenuItemService menuItemService) {
        this.orderService = orderService;
        this.menuItemService = menuItemService;
    }

    /**
     * GET /api/admin/orders
     * List recent orders, newest first
     */
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getRecentOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    /**
     * PATCH /api/admin/orders/{id}/status
     * Update an order status (PENDING, PREPARING, READY, COMPLETED, CANCELLED)
     */
    @PatchMapping("/orders/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateRequest request) {
        OrderResponse response = orderService.updateOrderStatus(id, request.getStatus());
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/admin/menu
     * Add a new food or beverage item to the canteen menu
     */
    @PostMapping("/menu")
    public ResponseEntity<MenuItem> createMenuItem(
            @Valid @RequestBody MenuItemRequest request) {
        MenuItem created = menuItemService.createMenuItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /api/admin/menu/{id}
     * Edit existing menu item details and prices
     */
    @PutMapping("/menu/{id}")
    public ResponseEntity<MenuItem> updateMenuItem(
            @PathVariable String id,
            @Valid @RequestBody MenuItemRequest request) {
        MenuItem updated = menuItemService.updateMenuItem(id, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /api/admin/menu/{id}
     * Delete a menu item from the catalog
     */
    @DeleteMapping("/menu/{id}")
    public ResponseEntity<Map<String, String>> deleteMenuItem(
            @PathVariable String id) {
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.ok(Map.of("message", "Menu item deleted successfully", "id", id));
    }

    /**
     * PATCH /api/admin/menu/{id}/availability
     * Toggle menu item stock availability (Available / Out of Stock)
     */
    @PatchMapping("/menu/{id}/availability")
    public ResponseEntity<MenuItem> updateMenuAvailability(
            @PathVariable String id,
            @Valid @RequestBody AvailabilityUpdateRequest request) {
        MenuItem updated = menuItemService.updateAvailability(id, request.getAvailability());
        return ResponseEntity.ok(updated);
    }

    /**
     * GET /api/admin/stats
     * Return canteen dashboard statistics computed from database
     */
    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
        return ResponseEntity.ok(orderService.getDashboardStats());
    }
}
