package io.roach.trading.api;

import io.roach.trading.api.support.Money;

public class Holding {
    private String productRef;

    private Money buyPrice;

    private int quantity;

    public String getProductRef() {
        return productRef;
    }

    public void setProductRef(String productRef) {
        this.productRef = productRef;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Money getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(Money buyPrice) {
        this.buyPrice = buyPrice;
    }
}
