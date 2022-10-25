package io.roach.trading.domain.account;

import java.util.UUID;

import javax.persistence.*;

import org.springframework.hateoas.server.core.Relation;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.roach.trading.api.support.Money;
import io.roach.trading.domain.home.LinkRelations;
import io.roach.trading.domain.portfolio.Portfolio;

@Entity
@Relation(value = LinkRelations.TRADING_ACCOUNT_REL,
        collectionRelation = LinkRelations.TRADING_ACCOUNT_REL)
@DiscriminatorValue("trading")
public class TradingAccount extends Account {
    /**
     * Parent account which is null for system accounts, non-null for trading accounts.
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    @PrimaryKeyJoinColumn
    @JsonIgnore
    private SystemAccount parentAccount;

    @Column(name = "parent_id", insertable = false, updatable = false)
    private UUID parentAccountId;

    /**
     * The portfolio tied to this account. Other side is responsible for the relationship.
     */
    @OneToOne(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    @JsonIgnore
    private Portfolio portfolio;

    protected TradingAccount() {
    }

    public TradingAccount(UUID id, String name, Money balance, SystemAccount parentAccount) {
        super(id, name, balance);
        Assert.notNull(id, "parentAccount is null");
        this.parentAccount = parentAccount;
        this.parentAccountId = parentAccount.getId();
    }

    @JsonIgnore
    public UUID getParentAccountId() {
        return parentAccountId;
    }

    @JsonIgnore
    public SystemAccount getParentAccount() {
        return parentAccount;
    }

    public Account setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
        if (portfolio != null) {
            this.portfolio.setAccount(this);
        }
        return this;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }
}
