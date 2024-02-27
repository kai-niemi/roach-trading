package io.roach.trading.domain.account;

import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.roach.trading.api.support.Money;

@Repository
public interface TradingAccountRepository extends JpaRepository<TradingAccount, UUID> {
    @Query(value = "select nextval('account_seq')", nativeQuery = true)
    Integer nextSeqNumber();

    @Query(value = "from TradingAccount where id=?1")
    @Lock(LockModeType.PESSIMISTIC_READ)
        // Not supported :/
//    @QueryHints(value = {
//            @QueryHint(name = "javax.persistence.lock.timeout", value = "1000"),
//            @QueryHint(name = "javax.persistence.lock.scope", value = "EXTENDED")},
//            forCounting = false)
    Optional<TradingAccount> getByIdWithLock(UUID id);

    @Query(value = "from TradingAccount ta ",
//            + "left join fetch ta.portfolio po "
//            + "left join fetch po.items",
            countQuery = "select count (ta) from TradingAccount ta")
    Page<TradingAccount> findAccountsByPage(Pageable pageable);

    @Query(value = "from TradingAccount ta "
            + "left join ta.portfolio po "
            + "left join po.items pi "
            + "where pi.quantity > 0",
            countQuery = "select count (ta) from TradingAccount ta "
                    + "left join ta.portfolio po "
                    + "left join po.items pi "
                    + "where pi.quantity > 0")
    Page<TradingAccount> findAccountsWithHoldingsByPage(Pageable pageable);

    @Query(value = "select a from TradingAccount a where a.parentAccountId = :parentId")
    Page<TradingAccount> findAccountsByPage(@Param("parentId") UUID parentId, Pageable pageable);

    @Query("select a.balance from TradingAccount a where a.id = :id")
    Optional<Money> getBalanceById(@Param("id") UUID id);
}

