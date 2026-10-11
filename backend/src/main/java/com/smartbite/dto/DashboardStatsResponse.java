package com.smartbite.dto;

public class DashboardStatsResponse {

    private long totalOrders;
    private long pendingOrders;
    private long preparingOrders;
    private long readyOrders;
    private long completedOrders;
    private long cancelledOrders;
    private long todayOrders;
    private double todayRevenue;
    private double totalSales;

    public DashboardStatsResponse() {
    }

    public DashboardStatsResponse(long totalOrders, long pendingOrders, long preparingOrders,
                                  long readyOrders, long completedOrders, long cancelledOrders,
                                  long todayOrders, double todayRevenue) {
        this.totalOrders = totalOrders;
        this.pendingOrders = pendingOrders;
        this.preparingOrders = preparingOrders;
        this.readyOrders = readyOrders;
        this.completedOrders = completedOrders;
        this.cancelledOrders = cancelledOrders;
        this.todayOrders = todayOrders;
        this.todayRevenue = todayRevenue;
        this.totalSales = todayRevenue;
    }

    public DashboardStatsResponse(long totalOrders, long pendingOrders, long preparingOrders,
                                  long readyOrders, long completedOrders, long cancelledOrders,
                                  long todayOrders, double todayRevenue, double totalSales) {
        this.totalOrders = totalOrders;
        this.pendingOrders = pendingOrders;
        this.preparingOrders = preparingOrders;
        this.readyOrders = readyOrders;
        this.completedOrders = completedOrders;
        this.cancelledOrders = cancelledOrders;
        this.todayOrders = todayOrders;
        this.todayRevenue = todayRevenue;
        this.totalSales = totalSales;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public long getPendingOrders() {
        return pendingOrders;
    }

    public void setPendingOrders(long pendingOrders) {
        this.pendingOrders = pendingOrders;
    }

    public long getPreparingOrders() {
        return preparingOrders;
    }

    public void setPreparingOrders(long preparingOrders) {
        this.preparingOrders = preparingOrders;
    }

    public long getReadyOrders() {
        return readyOrders;
    }

    public void setReadyOrders(long readyOrders) {
        this.readyOrders = readyOrders;
    }

    public long getCompletedOrders() {
        return completedOrders;
    }

    public void setCompletedOrders(long completedOrders) {
        this.completedOrders = completedOrders;
    }

    public long getCancelledOrders() {
        return cancelledOrders;
    }

    public void setCancelledOrders(long cancelledOrders) {
        this.cancelledOrders = cancelledOrders;
    }

    public long getTodayOrders() {
        return todayOrders;
    }

    public void setTodayOrders(long todayOrders) {
        this.todayOrders = todayOrders;
    }

    public double getTodayRevenue() {
        return todayRevenue;
    }

    public void setTodayRevenue(double todayRevenue) {
        this.todayRevenue = todayRevenue;
    }

    public double getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(double totalSales) {
        this.totalSales = totalSales;
    }
}
