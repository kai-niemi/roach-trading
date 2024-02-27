package io.roach.trading.domain.account;

import java.util.UUID;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import org.springframework.hateoas.server.core.Relation;

import io.roach.trading.api.support.Money;
import io.roach.trading.domain.home.LinkRelations;

@Entity
@Relation(value = LinkRelations.SYSTEM_ACCOUNT_REL,
        collectionRelation = LinkRelations.SYSTEM_ACCOUNTS_REL)
@DiscriminatorValue("system")
public class SystemAccount extends Account {
    protected SystemAccount() {
    }

    public SystemAccount(UUID id, String name, Money balance) {
        super(id, name, balance);
    }
}
