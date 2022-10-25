package io.roach.trading.client.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

import io.roach.trading.api.BookingAccountModel;
import io.roach.trading.api.support.Money;
import io.roach.trading.api.OrderRequest;
import io.roach.trading.api.ProductModel;
import io.roach.trading.api.support.RandomUtils;
import io.roach.trading.client.support.HypermediaClient;

import static io.roach.trading.client.support.HypermediaClient.withCurie;

@ShellComponent
@ShellCommandGroup("workload")
public class PlaceOrder extends BaseCommand {
    public static final ParameterizedTypeReference<CollectionModel<BookingAccountModel>> ACCOUNT_MODEL_PTR
            = new ParameterizedTypeReference<CollectionModel<BookingAccountModel>>() {
    };

    public static final ParameterizedTypeReference<CollectionModel<ProductModel>> PRODUCT_MODEL_PTR
            = new ParameterizedTypeReference<CollectionModel<ProductModel>>() {
    };

    @Autowired
    private HypermediaClient hypermediaClient;

    @Autowired
    private ExecutorService executorService;

    @ShellMethod(value = "Place buy and sell orders in random", key = {"po", "place-orders"})
    @ShellMethodAvailability("connectedCheck")
    public void placeOrders(@ShellOption(help = "total orders", defaultValue = "100") int numOrders) {
        Link placeOrderLink = hypermediaClient.fromRoot()
                .follow(withCurie("orders"))
                .asTemplatedLink();

        CollectionModel<BookingAccountModel> accounts = hypermediaClient.fromRoot()
                .follow(withCurie("trading-account"))
                .follow(withCurie("random"))
                .toObject(ACCOUNT_MODEL_PTR);

        CollectionModel<ProductModel> products = hypermediaClient.fromRoot()
                .follow(withCurie("products"))
                .follow(withCurie("random"))
                .toObject(PRODUCT_MODEL_PTR);

        List<Future<?>> futureList = new ArrayList<>();

        IntStream.rangeClosed(1, numOrders).forEach(value -> {
            Future<?> f = executorService.submit(() -> {
                BookingAccountModel account = RandomUtils.selectRandom(accounts.getContent());

                if (RandomUtils.random.nextBoolean() || account.getHoldings().isEmpty()) {
                    // Buy!
                    ProductModel product = RandomUtils.selectRandom(products.getContent());
                    OrderRequest request =
                            OrderRequest.builder()
                                    .bookingAccount(account.getId())
                                    .buy(product.getReference())
                                    .unitPrice(Money.of(product.getBuyPrice(), product.getCurrency()))
                                    .quantity(RandomUtils.random.nextInt(1, 10))
                                    .ref(UUID.randomUUID().toString())
                                    .build();
                    hypermediaClient.post(placeOrderLink, request, Void.class);
                } else {
                    account.getHoldings().forEach(productItem -> {
                        // Sell!
                        OrderRequest request =
                                OrderRequest.builder()
                                        .bookingAccount(account.getId())
                                        .sell(productItem.getProductRef())
                                        .unitPrice(productItem.getBuyPrice().multiply(1.01))
                                        .quantity(productItem.getQuantity())
                                        .ref(UUID.randomUUID().toString())
                                        .build();
                        hypermediaClient.post(placeOrderLink, request, Void.class);
                    });
                }
            });
            futureList.add(f);
        });

        while (!futureList.isEmpty()) {
            Future<?> f = futureList.remove(0);
            try {
                f.get();
                logger.info("Completed {} - {} remains", f, futureList.size());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                logger.warn("", e.getCause());
            }
        }
    }
}
