package com.smartbite.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pickup_token", nullable = false, unique = true, length = 32)
    private String pickupToken;

    @Column(name = "customer_identifier", length = 100)
    private String customerIdentifier;

    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "grand_total", nullable = false)
    private Double grandTotal;

    @Column(name = "table_id", length = 20)
    private String tableId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public Order() {
    }

    public Order(String pickupToken, String customerIdentifier, Double grandTotal) {
        this.pickupToken = pickupToken;
        this.customerIdentifier = customerIdentifier;
        this.grandTotal = grandTotal;
        this.status = OrderStatus.PENDING;
    }

    public Order(String pickupToken, String customerIdentifier, Double grandTotal, String tableId) {
        this.pickupToken = pickupToken;
        this.customerIdentifier = customerIdentifier;
        this.grandTotal = grandTotal;
        this.tableId = tableId;
        this.status = OrderStatus.PENDING;
    }

    public Order(String pickupToken, User user, Double grandTotal) {
        this.pickupToken = pickupToken;
        this.user = user;
        if (user != null) {
            this.customerIdentifier = user.getRollNumber() + " (" + user.getName() + ")";
        }
        this.grandTotal = grandTotal;
        this.status = OrderStatus.PENDING;
    }

    public Order(String pickupToken, User user, Double grandTotal, String tableId) {
        this(pickupToken, user, grandTotal);
        this.tableId = tableId;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
        if (this.status == null) {
            this.status = OrderStatus.PENDING;
        }
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPickupToken() {
        return pickupToken;
    }

    public void setPickupToken(String pickupToken) {
        this.pickupToken = pickupToken;
    }

    public String getCustomerIdentifier() {
        return customerIdentifier;
    }

    public void setCustomerIdentifier(String customerIdentifier) {
        this.customerIdentifier = customerIdentifier;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(Double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
