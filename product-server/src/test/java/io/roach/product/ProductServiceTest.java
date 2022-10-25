package io.roach.product;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import io.roach.product.domain.Product;
import io.roach.product.service.NoSuchProductException;
import io.roach.product.service.ProductService;
import io.roach.product.util.Money;

public class ProductServiceTest extends AbstractIntegrationTest {
    @Autowired
    private ProductService productService;

    @BeforeAll
    public void setupTest() {
    }

    @Test
    @Order(1)
    public void whenCreatingProducts_thenSucceed() {
        Assertions.assertFalse(TransactionSynchronizationManager.isActualTransactionActive());

        Product p1 = productService
                .createProduct(new Product(UUID.randomUUID(), Doubles.NOKIA_A.getReference(), Money.euro("100.00"),
                        Money.euro("100.00")));

        Product p2 = productService.getProductById(p1.getId());
        Assertions.assertEquals(p1, p2);
    }

    @Test
    @Order(2)
    public void whenFindingProductById_thenThrowNoSuchProductException() {
        Assertions.assertThrows(NoSuchProductException.class, () -> {
            productService.getProductById(UUID.randomUUID());
            Assertions.fail("Must not succeed");
        });
    }
}
