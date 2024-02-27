package io.roach.trading.domain.account;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.roach.trading.api.support.Money;

/**
 * Repository contract for Account PDOs.
 */
@Repository
public interface SystemAccountRepository extends JpaRepository<SystemAccount, UUID> {
    @Query(value = "from SystemAccount where id=?1")
//    @Lock(LockModeType.PESSIMISTIC_READ)
        // Not supported :/
//    @QueryHints(value = {
//            @QueryHint(name = "javax.persistence.lock.timeout", value = "1000"),
//            @QueryHint(name = "javax.persistence.lock.scope", value = "EXTENDED")},
//            forCounting = false)
    Optional<SystemAccount> getByIdMaybe(UUID id);

    @Query("SELECT a.balance FROM SystemAccount a WHERE a.id = :id")
    Optional<Money> getBalanceById(@Param("id") UUID id);
}
