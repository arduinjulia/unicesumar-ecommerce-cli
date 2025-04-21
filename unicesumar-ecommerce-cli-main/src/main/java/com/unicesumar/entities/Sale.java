package com.unicesumar.entities;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Sale extends Entity {
    private UUID userId;
    private String paymentMethod;
    private Date sale_date;
    private List<UUID> productIds;

    public Sale(UUID userId, String paymentMethod, Date sale_date, List<UUID> productIds) {
        this.userId = userId;
        this.paymentMethod = paymentMethod;
        this.sale_date = sale_date;
        this.productIds = productIds;
    }

    public Sale(UUID uuid, String paymentMethod, UUID userId, Date sale_date, List<UUID> productIds) {
        super(uuid);
        this.paymentMethod = paymentMethod;
        this.userId = userId;
        this.sale_date = sale_date;
        this.productIds = productIds;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public Date getSale_date() {
        return sale_date;
    }

    public List<UUID> getProducts() {
        return productIds;
    }

}
