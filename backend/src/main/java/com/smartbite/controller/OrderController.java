package com.smartbite.controller;

import com.smartbite.dto.OrderRequest;
import com.smartbite.dto.OrderResponse;
import com.smartbite.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.smartbite.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Place a new canteen order with automatic pickup token generation,
     * bound to the authenticated student.
     */
    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(
            @Valid @RequestBody OrderRequest request,
            @AuthenticationPrincipal User currentUser) {
        OrderResponse response = orderService.createOrder(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get orders belonging to the currently authenticated student
     */
    @GetMapping("/my")
    public ResponseEntity<List<OrderResponse>> getMyOrders(
            @AuthenticationPrincipal User currentUser) {
        List<OrderResponse> orders = orderService.getMyOrders(currentUser);
        return ResponseEntity.ok(orders);
    }

    /**
     * Get order details by database order ID with student authorization check
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        return orderService.getOrderById(id, currentUser)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get order details by pickup token with student authorization check
     */
    @GetMapping("/token/{token}")
    public ResponseEntity<OrderResponse> getOrderByToken(
            @PathVariable String token,
            @AuthenticationPrincipal User currentUser) {
        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return orderService.getOrderByToken(token.trim(), currentUser)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get orders for a specific customer identifier path with student authorization check
     */
    @GetMapping("/customer/{customerIdentifier}")
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomerPath(
            @PathVariable String customerIdentifier,
            @AuthenticationPrincipal User currentUser) {
        if (customerIdentifier == null || customerIdentifier.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(orderService.getOrdersByCustomer(customerIdentifier.trim(), currentUser));
    }

    /**
     * List recent orders, optionally filtered by customer identifier query param
     */
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(
            @RequestParam(required = false) String customerIdentifier,
            @AuthenticationPrincipal User currentUser) {
        if (customerIdentifier != null && !customerIdentifier.trim().isEmpty()) {
            return ResponseEntity.ok(orderService.getOrdersByCustomer(customerIdentifier.trim(), currentUser));
        }
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // Students can only access their own private orders
        if (currentUser.getRole() != com.smartbite.model.Role.ADMIN) {
            return ResponseEntity.ok(orderService.getMyOrders(currentUser));
        }
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}
