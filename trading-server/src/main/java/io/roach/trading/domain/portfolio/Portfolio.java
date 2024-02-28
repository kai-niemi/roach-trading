package io.roach.trading.domain.portfolio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.springframework.hateoas.server.core.Relation;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.roach.trading.api.support.Money;
import io.roach.trading.domain.account.Account;
import io.roach.trading.domain.account.TradingAccount;
import io.roach.trading.domain.common.AbstractEntity;
import io.roach.trading.domain.home.LinkRelations;
import io.roach.trading.domain.product.Product;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "portfolio")
@Relation(value = LinkRelations.PORTFOLIO_REL,
        collectionRelation = LinkRelations.PORTFOLIOS_REL)
public class Portfolio extends AbstractEntity<UUID> implements Iterable<PortfolioItem> {
    @Id
    @Column(name = "account_id", updatable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "account_id")
    private TradingAccount account;

    @ElementCollection
    @CollectionTable(name = "portfolio_item", joinColumns = @JoinColumn(name = "account_id"))
    @org.hibernate.annotations.Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
    @OrderColumn(name = "item_pos")
    private List<PortfolioItem> items = new ArrayList<>();

    @Column
    private String description;

    protected Portfolio() {
    }

    public Portfolio(TradingAccount account) {
        Assert.notNull(account.getId(), "Account id is null");
        this.id = account.getId();
        this.account = account;
        this.account.setPortfolio(this);
    }

    @Override
    @JsonIgnore
    public UUID getId() {
        return id;
    }

    public List<PortfolioItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public Portfolio addItem(Product product, int quantity) {
        Assert.notNull(id, "Id must be assigned");
        items.add(new PortfolioItem(product, quantity));
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonIgnore
    public Account getAccount() {
        return account;
    }

    @Override
    public Iterator<PortfolioItem> iterator() {
        return items.iterator();
    }

    @Override
    public void forEach(Consumer<? super PortfolioItem> action) {
        items.forEach(action);
    }

    @Override
    public Spliterator<PortfolioItem> spliterator() {
        return items.spliterator();
    }

    /**
     * @return sum of all portfolio item values.
     */
    public Money getTotalValue() {
        if (items.isEmpty()) {
            return Money.zero(account.getBalance().getCurrency());
        }
        Money sum = Money.zero(items.iterator().next().getProductValue().getCurrency());
        for (PortfolioItem item : items) {
            sum = sum.plus(item.getProductValue());
        }
        return sum;
    }

    @Override
    public String toString() {
        return "Portfolio{" +
                "id=" + id +
                ", description='" + description + '\'' +
                "} " + super.toString();
    }
}
