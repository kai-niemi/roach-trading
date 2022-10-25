package io.roach.product.service;

import java.util.Currency;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.roach.product.domain.Product;

/**
 * Defines the business contract for browsing products and prices.
 */
public interface ProductService {
    void createProductBatch(Currency currency, int batchSize);

    Product createProduct(Product product);

    void update(Product product);

    Product getProductById(UUID id) throws NoSuchProductException;

    Product getProductByRef(String productRef) throws NoSuchProductException;

    Page<Product> findProductsPage(Pageable page);

    void delete(UUID id);
}
