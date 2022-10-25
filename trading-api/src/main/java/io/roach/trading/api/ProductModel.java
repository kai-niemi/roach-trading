package io.roach.trading.api;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

import org.springframework.hateoas.RepresentationModel;

public class ProductModel extends RepresentationModel<ProductModel> {
    private UUID id;

    private String reference;

    private BigDecimal buyPrice;

    private BigDecimal sellPrice;

    private Currency currency;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public BigDecimal getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(BigDecimal buyPrice) {
        this.buyPrice = buyPrice;
    }

    public BigDecimal getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(BigDecimal sellPrice) {
        this.sellPrice = sellPrice;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
