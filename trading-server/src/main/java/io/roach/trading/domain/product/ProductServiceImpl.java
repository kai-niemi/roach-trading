package io.roach.trading.domain.product;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.roach.trading.annotation.TransactionMandatory;
import io.roach.trading.api.support.Money;
import io.roach.trading.domain.changefeed.ChangeFeedListener;
import io.roach.trading.domain.changefeed.Payload;
import io.roach.trading.domain.changefeed.ProductFields;

@Service
@TransactionMandatory
public class ProductServiceImpl implements ProductService, ChangeFeedListener {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void create(Product product) {
        productRepository.save(product);
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
    public void onProductChangeEvent(List<Payload<ProductFields, UUID>> payload) {
        payload.forEach(item -> {
            logger.info("Product change event (deserialized): {}", item);

            switch (item.getOperation()) {
                case insert:
                    // UPSERT
                    item.getAfter().ifPresent(productFields -> {
                        Optional<Product> op = productRepository.getByForeignId(productFields.getId());
                        Product proxy;
                        if (op.isPresent()) {
                            proxy = op.get();
                            proxy.setBuyPrice(Money.of(productFields.getBuyPrice(), productFields.getCurrency()));
                            proxy.setSellPrice(Money.of(productFields.getSellPrice(), productFields.getCurrency()));
                        } else {
                            proxy = Product.builder()
                                    .withId(UUID.randomUUID())
                                    .withReference(productFields.getReference())
                                    .withBuyPrice(Money.of(productFields.getBuyPrice(), productFields.getCurrency()))
                                    .withSellPrice(Money.of(productFields.getSellPrice(), productFields.getCurrency()))
                                    .withForeignId(productFields.getId())
                                    .build();
                        }
                        proxy.setLastModifiedAt(LocalDateTime.now());
                        productRepository.save(proxy);
                    });
                    break;
                case update:
                    item.getAfter().ifPresent(productFields -> {
                        Product proxy = productRepository.getByForeignId(productFields.getId())
                                .orElseThrow(() -> new NoSuchProductException("with foreign id: "
                                        + productFields.getId()));
                        proxy.setBuyPrice(Money.of(productFields.getBuyPrice(), productFields.getCurrency()));
                        proxy.setSellPrice(Money.of(productFields.getSellPrice(), productFields.getCurrency()));
                        proxy.setLastModifiedAt(LocalDateTime.now());
                        productRepository.save(proxy);
                    });
                    break;
                case delete:
                    item.getAfter().ifPresent(productFields -> {
                        Product proxy = productRepository.getByForeignId(productFields.getId())
                                .orElseThrow(() -> new NoSuchProductException("with foreign id: "
                                        + productFields.getId()));
                        productRepository.delete(proxy);
                    });
                    break;
                default:
                    throw new IllegalStateException("Unknown type: " + item.getOperation());
            }
        });
    }
}
