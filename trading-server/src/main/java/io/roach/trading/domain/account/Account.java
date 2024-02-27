package io.roach.trading.domain.account;

import java.util.UUID;

import jakarta.persistence.*;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.roach.trading.api.support.Money;
import io.roach.trading.domain.common.AbstractAuditableEntity;

@Entity
@Table(name = "account")
@DynamicInsert
@DynamicUpdate
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "account_type",
        discriminatorType = DiscriminatorType.STRING,
        length = 15
)
public abstract class Account extends AbstractAuditableEntity<UUID> {
    @Id
    @Column(name = "id", updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "balance", nullable = false)),
            @AttributeOverride(name = "currency", column = @Column(name = "currency", length = 3, nullable = false))
    })
    private Money balance;

    protected Account() {
    }

    public Account(UUID id, String name, Money balance) {
        Assert.notNull(id, "id is null");
        Assert.notNull(name, "name is null");
        Assert.notNull(balance, "balance is null");

        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    @Override
    @JsonIgnore
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public Money getBalance() {
        return balance;
    }

    public Account setBalance(Money balance) {
        this.balance = balance;
        return this;
    }
}
