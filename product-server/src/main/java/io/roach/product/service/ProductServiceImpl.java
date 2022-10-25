package io.roach.product.service;

import java.util.Currency;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.roach.product.domain.Product;
import io.roach.product.repository.ProductRepository;
import io.roach.product.util.Money;
import io.roach.product.util.RandomUtils;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createProductBatch(Currency currency, int batchSize) {
        IntStream.rangeClosed(1, batchSize).forEach(v -> {
            String ref = "p-" + productRepository.nextSeqNumber();
            Money buy = RandomUtils.randomMoneyBetween(5, 50, currency);
            Money sell = buy.minus(RandomUtils.randomMoneyBetween(1, 5, currency));

            productRepository.save(new Product(UUID.randomUUID(), ref, buy, sell));
        });
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void update(Product product) {
        Product productProxy = productRepository.getReferenceById(product.getId());
        productProxy.setCurrency(product.getBuyPrice().getCurrency());
        productProxy.setBuyPrice(product.getBuyPrice());
        productProxy.setSellPrice(product.getSellPrice());
    }

    @Override
    public Page<Product> findProductsPage(Pageable page) {
        return productRepository.findAll(page);
    }

    @Override
    public Product getProductByRef(String productRef) {
        return productRepository.getByReference(productRef)
                .orElseThrow(() -> new NoSuchProductException(productRef));
    }

    @Override
    public Product getProductById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NoSuchProductException(id));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void delete(UUID id) {
        productRepository.deleteById(id);
    }
}
