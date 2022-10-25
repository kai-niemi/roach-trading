package io.roach.product.web;

import java.util.Currency;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.roach.product.domain.Product;
import io.roach.product.service.ProductService;
import io.roach.product.util.Money;
import io.roach.product.util.RandomUtils;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/scheduler")
public class SchedulerController {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

    private static boolean enablePeriodicUpdates = true;

    @Autowired
    private ProductService productService;

    @Autowired
    private MeterRegistry meterRegistry;

    private Counter productsCreated;

    private Counter productsUpdated;

    private Counter productsDeleted;

    @PostConstruct
    public void init() {
        this.productsCreated = meterRegistry.counter("roach.products.created");
        this.productsUpdated = meterRegistry.counter("roach.products.updated");
        this.productsDeleted = meterRegistry.counter("roach.products.deleted");
    }

    @GetMapping
    public ResponseEntity<SchedulerModel> getScheduler() {
        return ResponseEntity.ok().body(toModel());
    }

    @PostMapping
    public ResponseEntity<SchedulerModel> toggleScheduler() {
        SchedulerController.enablePeriodicUpdates = !SchedulerController.enablePeriodicUpdates;
        logger.info("Toggled periodic updates: {}", enablePeriodicUpdates);
        return ResponseEntity.ok().body(toModel());
    }

    private SchedulerModel toModel() {
        SchedulerModel model = new SchedulerModel();
        model.setStatus(SchedulerController.enablePeriodicUpdates ? "running" : "paused");
        model.setProductsCreated((int) productsCreated.count());
        model.setProductsUpdated((int) productsUpdated.count());
        model.setProductsDeleted((int) productsDeleted.count());
        model.add(linkTo(methodOn(getClass())
                .toggleScheduler())
                .withSelfRel()
                .andAffordance(afford(methodOn(getClass()).toggleScheduler()))
                .withTitle("Toggle periodic product update scheduler")
                .withRel("toggle-scheduler"));
        return model;
    }

    @Scheduled(cron = "*/30 * * * * ?")
    public void periodicCreations() {
        if (!enablePeriodicUpdates) {
            return;
        }
        int n = random.nextInt(8, 32);
        productService.createProductBatch(Currency.getInstance("USD"), n);
        productsCreated.increment(n);

        logger.info("Created {} products", productsCreated.count());
    }

    @Scheduled(cron = "0 * * * * ?")
    public void periodicUpdates() {
        if (!enablePeriodicUpdates) {
            return;
        }
        Page<Product> products = productService.findProductsPage(PageRequest.ofSize(32));
        while (products.hasContent()) {
            products.forEach(product -> {
                if (random.nextDouble() > .8) {
                    Money buy = RandomUtils.randomMoneyBetween(5, 50, product.getCurrency());
                    Money sell = buy.minus(RandomUtils.randomMoneyBetween(1, 5, product.getCurrency()));
                    product.setBuyPrice(buy);
                    product.setSellPrice(sell);
                    productService.update(product);
                    productsUpdated.increment();
                }
            });
            if (products.hasNext()) {
                products = productService.findProductsPage(products.nextPageable());
            } else {
                break;
            }
        }
        logger.info("Updated {} products", productsUpdated.count());
    }

    @Scheduled(cron = "5 * * * * ?")
    public void periodicDeletes() {
        if (!enablePeriodicUpdates) {
            return;
        }
        Page<Product> products = productService.findProductsPage(PageRequest.ofSize(32));
        while (products.hasContent()) {
            products.forEach(product -> {
                if (random.nextDouble() > .95) {
                    productService.delete(product.getId());
                    productsDeleted.increment();
                }
            });
            if (products.hasNext()) {
                products = productService.findProductsPage(products.nextPageable());
            } else {
                break;
            }
        }

        logger.info("Deleted {} products", productsDeleted.count());
    }

}
