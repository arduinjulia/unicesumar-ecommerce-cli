package com.unicesumar.entities;

import java.util.UUID;

public class SaleProduct {
    private UUID saleId;
    private UUID productId;

    public SaleProduct(UUID saleId, UUID productId) {
        this.saleId = saleId;
        this.productId = productId;
    }

    public UUID getSaleId() {
        return saleId;
    }

    public UUID getProductId() {
        return productId;
    }
}
