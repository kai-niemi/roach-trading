package io.roach.product.domain;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.hateoas.server.core.Relation;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.roach.product.web.LinkRelations;
import io.roach.product.util.Money;

@Entity
@Table(name = "product")
@Relation(value = LinkRelations.PRODUCT_REL,
        collectionRelation = LinkRelations.PRODUCTS_REL)
public class Product extends AbstractEntity<UUID> {
    @Id
    private UUID id;

    @Column(name = "reference", length = 128, unique = true, nullable = false)
    private String reference;

    @Column(name = "buy_price", nullable = false)
    private BigDecimal buyPrice;

    @Column(name = "sell_price", nullable = false)
    private BigDecimal sellPrice;

    @Column(name = "currency", length = 3, nullable = false)
    private Currency currency;

    protected Product() {
    }

    public Product(UUID id, String reference, Money buyPrice, Money sellPrice) {
        if (!buyPrice.isSameCurrency(sellPrice)) {
            throw new IllegalArgumentException("Mixed currency");
        }
        this.id = id;
        this.reference = reference;
        this.buyPrice = buyPrice.getAmount();
        this.sellPrice = sellPrice.getAmount();
        this.currency = buyPrice.getCurrency();
    }

    @Override
    @JsonIgnore
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String productRef) {
        this.reference = productRef;
    }

    public Money getBuyPrice() {
        return Money.of(buyPrice, currency);
    }

    public Money getSellPrice() {
        return Money.of(sellPrice, currency);
    }

    public void setBuyPrice(Money buyPrice) {
        this.buyPrice = buyPrice.getAmount();
    }

    public void setSellPrice(Money sellPrice) {
        this.sellPrice = sellPrice.getAmount();
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Currency getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Product product = (Product) o;
        return id.equals(product.id) &&
                reference.equals(product.reference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reference);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", reference='" + reference + '\'' +
                ", buyPrice=" + buyPrice +
                ", sellPrice=" + sellPrice +
                ", currency=" + currency +
                "} " + super.toString();
    }
}
