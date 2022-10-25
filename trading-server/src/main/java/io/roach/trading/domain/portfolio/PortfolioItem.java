package io.roach.trading.domain.portfolio;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.hateoas.server.core.Relation;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.roach.trading.api.support.Money;
import io.roach.trading.domain.home.LinkRelations;
import io.roach.trading.domain.product.Product;

@Relation(value = LinkRelations.PORTFOLIO_ITEM_REL,
        collectionRelation = LinkRelations.PORTFOLIO_ITEMS_REL)
@Embeddable
public class PortfolioItem {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    protected PortfolioItem() {
    }

    protected PortfolioItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    @JsonIgnore
    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Money getProductValue() {
        return product.getSellPrice().multiply(quantity);
    }
}
