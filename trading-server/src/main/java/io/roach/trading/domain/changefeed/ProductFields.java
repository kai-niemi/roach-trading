package io.roach.trading.domain.changefeed;

import java.math.BigDecimal;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductFields implements Fields<UUID> {
    private UUID id;

    private String reference;

    @JsonProperty(value = "buy_price")
    private BigDecimal buyPrice;

    @JsonProperty(value = "sell_price")
    private BigDecimal sellPrice;

    private String currency;

    @Override
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "ProductFields{" +
                "id=" + id +
                ", reference='" + reference + '\'' +
                ", buyPrice=" + buyPrice +
                ", sellPrice=" + sellPrice +
                ", currency='" + currency + '\'' +
                '}';
    }
}

