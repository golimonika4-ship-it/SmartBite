package com.smartbite.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class OrderRequest {

    private String customerIdentifier;
    private String tableId;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemRequest> items;

    public OrderRequest() {
    }

    public OrderRequest(String customerIdentifier, List<OrderItemRequest> items) {
        this.customerIdentifier = customerIdentifier;
        this.items = items;
    }

    public OrderRequest(String customerIdentifier, String tableId, List<OrderItemRequest> items) {
        this.customerIdentifier = customerIdentifier;
        this.tableId = tableId;
        this.items = items;
    }

    public String getCustomerIdentifier() {
        return customerIdentifier;
    }

    public void setCustomerIdentifier(String customerIdentifier) {
        this.customerIdentifier = customerIdentifier;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
}
