package com.smartbite.service;

import com.smartbite.dto.DashboardStatsResponse;
import com.smartbite.dto.OrderItemRequest;
import com.smartbite.dto.OrderItemResponse;
import com.smartbite.dto.OrderRequest;
import com.smartbite.dto.OrderResponse;
import com.smartbite.model.MenuItem;
import com.smartbite.model.Order;
import com.smartbite.model.OrderItem;
import com.smartbite.model.OrderStatus;
import com.smartbite.repository.MenuItemRepository;
import com.smartbite.repository.OrderRepository;
import com.smartbite.model.Role;
import com.smartbite.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private static final String[] TOKEN_PREFIXES = {"SB", "A", "B", "C"};
    private static final Random RANDOM = new SecureRandom();

    @org.springframework.beans.factory.annotation.Value("${smartbite.canteen.tables:T01,T02,T03,T04,T05,T06,T07,T08,T09,T10}")
    private String configuredTables;

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;

    public OrderService(OrderRepository orderRepository, MenuItemRepository menuItemRepository) {
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
    }

    /**
     * Create a new order with server-calculated prices, totals, and unique pickup token.
     */
    public OrderResponse createOrder(OrderRequest request) {
        return createOrder(request, null);
    }

    /**
     * Create a new order bound to authenticated user
     */
    public OrderResponse createOrder(OrderRequest request, User currentUser) {
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order must contain at least one item");
        }

        // Validate quantities
        for (OrderItemRequest itemReq : request.getItems()) {
            if (itemReq.getItemId() == null || itemReq.getItemId().trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item ID cannot be empty");
            }
            if (itemReq.getQuantity() == null || itemReq.getQuantity() < 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be at least 1 for item: " + itemReq.getItemId());
            }
            if (itemReq.getQuantity() > 50) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity cannot exceed 50 per item: " + itemReq.getItemId());
            }
        }

        // Look up all requested MenuItem entities from database to verify existence and prices
        List<String> requestedIds = request.getItems().stream()
                .map(item -> item.getItemId().trim())
                .distinct()
                .collect(Collectors.toList());

        List<MenuItem> foundMenuItems = menuItemRepository.findAllById(requestedIds);
        Map<String, MenuItem> menuItemMap = foundMenuItems.stream()
                .collect(Collectors.toMap(MenuItem::getId, Function.identity()));

        // Reject if any menu item is unknown
        for (String id : requestedIds) {
            if (!menuItemMap.containsKey(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown menu item ID: " + id);
            }
        }

        // Reject if any menu item is out of stock / unavailable
        for (String id : requestedIds) {
            MenuItem item = menuItemMap.get(id);
            if (item.getAvailability() != null && "Out of Stock".equalsIgnoreCase(item.getAvailability().trim())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item is currently out of stock: " + item.getName());
            }
        }

        // Validate table ID if provided
        String validTableId = null;
        if (request.getTableId() != null && !request.getTableId().trim().isEmpty()) {
            validTableId = request.getTableId().trim().toUpperCase();
            List<String> allowedTables = java.util.Arrays.stream(
                    (configuredTables != null ? configuredTables : "T01,T02,T03,T04,T05,T06,T07,T08,T09,T10").split(",")
            ).map(String::trim).map(String::toUpperCase).collect(Collectors.toList());

            if (!allowedTables.contains(validTableId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid table ID: " + request.getTableId() + ". Valid tables are: " + String.join(", ", allowedTables));
            }
        }

        // Generate unique pickup token safely
        String pickupToken = generateUniquePickupToken();

        // Calculate totals on the server and construct Order & OrderItems
        double grandTotal = 0.0;
        Order order = new Order();
        order.setPickupToken(pickupToken);
        order.setTableId(validTableId);

        if (currentUser != null) {
            order.setUser(currentUser);
            if (validTableId != null) {
                order.setCustomerIdentifier("Table " + validTableId + " (" + currentUser.getRollNumber() + ")");
            } else {
                order.setCustomerIdentifier(currentUser.getRollNumber() + " (" + currentUser.getName() + ")");
            }
        } else {
            if (validTableId != null) {
                String optionalName = request.getCustomerIdentifier() != null && !request.getCustomerIdentifier().trim().isEmpty() && !request.getCustomerIdentifier().equalsIgnoreCase("Campus Student")
                        ? " (" + request.getCustomerIdentifier().trim() + ")"
                        : "";
                order.setCustomerIdentifier("Table " + validTableId + optionalName);
            } else {
                order.setCustomerIdentifier(
                        request.getCustomerIdentifier() != null && !request.getCustomerIdentifier().trim().isEmpty()
                                ? request.getCustomerIdentifier().trim()
                                : "Campus Student"
                );
            }
        }
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemRequest itemReq : request.getItems()) {
            MenuItem menuItem = menuItemMap.get(itemReq.getItemId().trim());
            double unitPrice = menuItem.getPrice();
            double lineTotal = Math.round(unitPrice * itemReq.getQuantity() * 100.0) / 100.0;
            grandTotal += lineTotal;

            OrderItem orderItem = new OrderItem(
                    order,
                    menuItem.getId(),
                    menuItem.getName(),
                    itemReq.getQuantity(),
                    unitPrice,
                    lineTotal
            );
            order.addItem(orderItem);
            orderItems.add(orderItem);
        }

        order.setGrandTotal(Math.round(grandTotal * 100.0) / 100.0);

        // Persist order (cascades to order_items)
        Order savedOrder = orderRepository.save(order);

        return mapToResponse(savedOrder);
    }

    /**
     * Retrieve orders for the authenticated student
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(User currentUser) {
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required to view orders");
        }
        return orderRepository.findByUserOrderByCreatedAtDesc(currentUser).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve order by ID with role & student verification
     */
    @Transactional(readOnly = true)
    public Optional<OrderResponse> getOrderById(Long id, User currentUser) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            return Optional.empty();
        }

        // Student privacy verification: student cannot access another student's order
        if (currentUser != null && currentUser.getRole() != Role.ADMIN) {
            if (order.getUser() != null && !order.getUser().getId().equals(currentUser.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: You cannot view another student's order");
            }
        }

        return Optional.of(mapToResponse(order));
    }

    @Transactional(readOnly = true)
    public Optional<OrderResponse> getOrderById(Long id) {
        return getOrderById(id, null);
    }

    /**
     * Retrieve order by pickup token with verification
     */
    @Transactional(readOnly = true)
    public Optional<OrderResponse> getOrderByToken(String token, User currentUser) {
        if (token == null || token.trim().isEmpty()) {
            return Optional.empty();
        }
        Order order = orderRepository.findByPickupToken(token.trim()).orElse(null);
        if (order == null) {
            return Optional.empty();
        }

        if (currentUser != null && currentUser.getRole() != Role.ADMIN) {
            if (order.getUser() != null && !order.getUser().getId().equals(currentUser.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: You cannot view another student's order");
            }
        }

        return Optional.of(mapToResponse(order));
    }

    @Transactional(readOnly = true)
    public Optional<OrderResponse> getOrderByToken(String token) {
        return getOrderByToken(token, null);
    }

    /**
     * List all orders (most recent first)
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * List orders for a specific customer/roll number with privacy verification
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCustomer(String customerIdentifier, User currentUser) {
        if (customerIdentifier == null || customerIdentifier.trim().isEmpty()) {
            return Collections.emptyList();
        }

        if (currentUser != null && currentUser.getRole() != Role.ADMIN) {
            String cleanIdentifier = customerIdentifier.trim();
            if (!cleanIdentifier.equalsIgnoreCase(currentUser.getRollNumber()) &&
                !cleanIdentifier.equalsIgnoreCase(currentUser.getEmail())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: You cannot view another student's orders");
            }
            return getMyOrders(currentUser);
        }

        return orderRepository.findByCustomerIdentifierIgnoreCaseOrderByCreatedAtDesc(customerIdentifier.trim()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCustomer(String customerIdentifier) {
        return getOrdersByCustomer(customerIdentifier, null);
    }

    /**
     * Safe pickup token generation with collision detection and retry
     */
    private String generateUniquePickupToken() {
        for (int attempt = 0; attempt < 20; attempt++) {
            String prefix = TOKEN_PREFIXES[RANDOM.nextInt(TOKEN_PREFIXES.length)];
            int number = "SB".equals(prefix) ? (1001 + RANDOM.nextInt(9000)) : (100 + RANDOM.nextInt(900)); // 1001-9999 or 100-999
            String candidateToken = prefix + "-" + number;

            if (!orderRepository.existsByPickupToken(candidateToken)) {
                return candidateToken;
            }
        }
        // Fallback with timestamp suffix if high contention occurs
        return "SB-" + (System.currentTimeMillis() % 10000);
    }

    /**
     * Update order status with validation
     */
    public OrderResponse updateOrderStatus(Long orderId, String newStatusStr) {
        if (orderId == null || orderId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid order ID: " + orderId);
        }
        if (newStatusStr == null || newStatusStr.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order status cannot be empty");
        }

        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(newStatusStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid order status: " + newStatusStr);
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found with ID: " + orderId));

        order.setStatus(newStatus);
        Order updated = orderRepository.save(order);
        return mapToResponse(updated);
    }

    /**
     * Compute canteen dashboard statistics from database
     */
    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats() {
        long totalOrders = orderRepository.count();
        long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING);
        long preparingOrders = orderRepository.countByStatus(OrderStatus.PREPARING);
        long readyOrders = orderRepository.countByStatus(OrderStatus.READY);
        long completedOrders = orderRepository.countByStatus(OrderStatus.COMPLETED);
        long cancelledOrders = orderRepository.countByStatus(OrderStatus.CANCELLED);

        Instant startOfToday = LocalDate.now(ZoneId.systemDefault()).atStartOfDay(ZoneId.systemDefault()).toInstant();
        long todayOrders = orderRepository.countByCreatedAtGreaterThanEqual(startOfToday);
        Double sumRevenue = orderRepository.sumGrandTotalSince(startOfToday);
        double todayRevenue = sumRevenue != null ? Math.round(sumRevenue * 100.0) / 100.0 : 0.0;

        Double sumAll = orderRepository.sumTotalSales();
        double totalSales = sumAll != null ? Math.round(sumAll * 100.0) / 100.0 : 0.0;

        return new DashboardStatsResponse(
                totalOrders,
                pendingOrders,
                preparingOrders,
                readyOrders,
                completedOrders,
                cancelledOrders,
                todayOrders,
                todayRevenue,
                totalSales
        );
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getMenuItemId(),
                        item.getItemName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getLineTotal()
                ))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getPickupToken(),
                order.getStatus().name(),
                order.getGrandTotal(),
                order.getCustomerIdentifier(),
                order.getCreatedAt() != null ? order.getCreatedAt().toString() : null,
                itemResponses,
                order.getTableId()
        );
    }
}
