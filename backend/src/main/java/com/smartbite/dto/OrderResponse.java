package com.smartbite.dto;

import java.util.List;

public class OrderResponse {

    private Long id;
    private String pickupToken;
    private String status;
    private Double grandTotal;
    private String customerIdentifier;
    private String createdAt;
    private List<OrderItemResponse> items;
    private String tableId;

    public OrderResponse() {
    }

    public OrderResponse(Long id, String pickupToken, String status, Double grandTotal,
                         String customerIdentifier, String createdAt, List<OrderItemResponse> items) {
        this.id = id;
        this.pickupToken = pickupToken;
        this.status = status;
        this.grandTotal = grandTotal;
        this.customerIdentifier = customerIdentifier;
        this.createdAt = createdAt;
        this.items = items;
    }

    public OrderResponse(Long id, String pickupToken, String status, Double grandTotal,
                         String customerIdentifier, String createdAt, List<OrderItemResponse> items, String tableId) {
        this.id = id;
        this.pickupToken = pickupToken;
        this.status = status;
        this.grandTotal = grandTotal;
        this.customerIdentifier = customerIdentifier;
        this.createdAt = createdAt;
        this.items = items;
        this.tableId = tableId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(Double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public String getCustomerIdentifier() {
        return customerIdentifier;
    }

    public void setCustomerIdentifier(String customerIdentifier) {
        this.customerIdentifier = customerIdentifier;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }

    public void setItems(List<OrderItemResponse> items) {
        this.items = items;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }
}
