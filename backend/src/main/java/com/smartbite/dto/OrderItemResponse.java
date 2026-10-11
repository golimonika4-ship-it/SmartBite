package com.smartbite.dto;

public class OrderItemResponse {

    private Long id;
    private String menuItemId;
    private String itemName;
    private Integer quantity;
    private Double unitPrice;
    private Double lineTotal;

    public OrderItemResponse() {
    }

    public OrderItemResponse(Long id, String menuItemId, String itemName, Integer quantity, Double unitPrice, Double lineTotal) {
        this.id = id;
        this.menuItemId = menuItemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.lineTotal = lineTotal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(String menuItemId) {
        this.menuItemId = menuItemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(Double lineTotal) {
        this.lineTotal = lineTotal;
    }
}
