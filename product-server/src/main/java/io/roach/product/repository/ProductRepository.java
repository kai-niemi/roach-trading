package io.roach.product.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.roach.product.domain.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    @Query(value = "SELECT nextval('product_seq')", nativeQuery = true)
    Integer nextSeqNumber();

    Optional<Product> getByReference(String productRef);
}
