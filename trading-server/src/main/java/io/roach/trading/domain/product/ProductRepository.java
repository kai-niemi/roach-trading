package io.roach.trading.domain.product;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.roach.trading.annotation.TransactionMandatory;

@Repository
@TransactionMandatory
public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> getByReference(String productRef);

    Optional<Product> getByForeignId(UUID foreignId);

    @Query(value = "from Product p order by random()")
    Page<Product> findAllByRandom(Pageable pageable);
}
