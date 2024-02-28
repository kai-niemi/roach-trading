package io.roach.trading.loadtests;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import io.roach.trading.AbstractIntegrationTest;
import io.roach.trading.ProfileNames;
import io.roach.trading.api.OrderRequest;
import io.roach.trading.api.support.Money;
import io.roach.trading.domain.account.AccountService;
import io.roach.trading.domain.account.SystemAccount;
import io.roach.trading.domain.account.TradingAccount;
import io.roach.trading.domain.order.BookingOrder;
import io.roach.trading.domain.order.TradingFacade;
import io.roach.trading.domain.product.Product;
import io.roach.trading.domain.product.ProductService;
import io.roach.trading.doubles.DoublesService;
import io.roach.trading.doubles.TestDoubles;
import io.roach.trading.util.RandomData;

import static io.roach.trading.api.support.Money.euro;

//@ActiveProfiles({ProfileNames.PSQL_LOCAL_RC})
//@ActiveProfiles({ProfileNames.PSQL_LOCAL})
@ActiveProfiles({ProfileNames.CRDB_LOCAL_RC})
//@ActiveProfiles({ProfileNames.CRDB_LOCAL})
@Tag("stress")
public class TradingStressTest extends AbstractIntegrationTest {
    private static final int NUM_THREADS = Runtime.getRuntime().availableProcessors() * 2;

    private static final int BUY_ORDERS = 100;

    private static final int NUM_PRODUCTS = 100;

    private static final int NUM_SYSTEM_ACCOUNTS = 10;

    private static final int NUM_TRADING_ACCOUNTS_PER_SYSTEM_ACCOUNT = 100;

    @Autowired
    private TradingFacade tradingFacade;

    @Autowired
    private AccountService accountService;

    @Autowired
    private DoublesService doublesService;

    @Autowired
    private ProductService productService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private Money initialSystemAccountsTotal = Money.zero(Money.EUR);

    private Money initialTradingAccountsTotal = Money.zero(Money.EUR);

    private Money finalBuyTotal = Money.zero(Money.EUR);

    private Money finalSellTotal = Money.zero(Money.EUR);

    @BeforeAll
    public void setupTest() {
        Assertions.assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
        doublesService.removeTestDoubles();

        AtomicInteger userId = new AtomicInteger();

        IntStream.rangeClosed(1, NUM_SYSTEM_ACCOUNTS).forEach(value -> {
            final UUID tradingId = UUID.randomUUID();

            transactionTemplate.executeWithoutResult(transactionStatus -> {
                accountService.createSystemAccount(tradingId,
                        "TRADER:" + value, TestDoubles.TRADER_INITIAL_BALANCE);
                IntStream.rangeClosed(1, NUM_TRADING_ACCOUNTS_PER_SYSTEM_ACCOUNT).forEach(x -> {
                    accountService.createTradingAccount(tradingId, UUID.randomUUID(),
                            "USER:" + userId.incrementAndGet(), TestDoubles.USER_INITIAL_BALANCE,
                            false);
                });
            });

        });

        transactionTemplate.executeWithoutResult(transactionStatus -> {
            IntStream.rangeClosed(1, NUM_PRODUCTS).forEach(value -> {
                productService.create(Product.builder()
                        .withId(UUID.randomUUID())
                        .withReference("Apple " + value)
                        .withBuyPrice(euro("20.50"))
                        .withSellPrice(euro("19.50"))
                        .build());
            });
        });
    }

    @Test
    @Transactional
    @Commit
    @Order(1)
    public void whenPreparing_thenSumInitialTotalBalance() {
        Assertions.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());

        accountService.findSystemAccountsByPage(Pageable.unpaged())
                .getContent().forEach(systemAccount -> {
                    Assertions.assertEquals(TestDoubles.TRADER_INITIAL_BALANCE, systemAccount.getBalance());
                    initialSystemAccountsTotal = initialSystemAccountsTotal.plus(systemAccount.getBalance());

                    accountService.findTradingAccountsByPage(systemAccount.getId(), Pageable.unpaged()).getContent()
                            .forEach(tradingAccount -> {
                                Assertions.assertEquals(TestDoubles.USER_INITIAL_BALANCE, tradingAccount.getBalance());
                                initialTradingAccountsTotal = initialTradingAccountsTotal.plus(tradingAccount.getBalance());
                            });

                });

        logger.info("System accounts initial total: %s".formatted(initialSystemAccountsTotal));
        logger.info("Trading accounts initial total: %s".formatted(initialTradingAccountsTotal));
    }

    @Test
    @Order(2)
    public void whenTrading_thenLetItRipConcurrently() {
        Assertions.assertFalse(TransactionSynchronizationManager.isActualTransactionActive());

        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);

        List<TradingAccount> accountList =
                transactionTemplate.execute(status ->
                        accountService.findTradingAccountsByRandom(PageRequest.ofSize(500)).getContent());
        Assertions.assertFalse(accountList.isEmpty());

        List<Product> productList =
                transactionTemplate.execute(status ->
                        productService.findProductsByRandom(PageRequest.ofSize(500)).getContent());
        Assertions.assertFalse(productList.isEmpty());

        List<Future<Money>> buyOrders = new ArrayList<>();

        BlockingQueue<Pair<UUID, Pair<Product, Integer>>> tradingAccountIdsWithBuyOrders
                = new LinkedBlockingDeque<>();

        // Place buy orders
        IntStream.rangeClosed(1, BUY_ORDERS).forEach(value -> {
            Future<Money> future = executorService.submit(() -> {
                TradingAccount tradingAccount = RandomData.selectRandom(accountList);
                Product tradingProduct = RandomData.selectRandom(productList);
                int qty = ThreadLocalRandom.current().nextInt(1, 11);

                BookingOrder order = tradingFacade.placeOrder(
                        OrderRequest.builder()
                                .bookingAccount(tradingAccount.getId())
                                .buy(tradingProduct.getReference())
                                .unitPrice(tradingProduct.getBuyPrice())
                                .quantity(qty)
                                .ref(UUID.randomUUID().toString())
                                .build()
                );

                Pair<Product, Integer> tuple = Pair.of(tradingProduct, qty);
                tradingAccountIdsWithBuyOrders.add(Pair.of(Objects.requireNonNull(tradingAccount.getId()), tuple));

                return order.getTotalPrice();
            });
            buyOrders.add(future);
        });

        List<Future<Money>> sellOrders = new ArrayList<>();

        // Place sell orders until drained
        for (; ; ) {
            try {
                Pair<UUID, Pair<Product, Integer>> tuple = tradingAccountIdsWithBuyOrders.poll(5, TimeUnit.SECONDS);
                if (tuple == null) {
                    break;
                }
                Future<Money> future = executorService.submit(() -> {
                    List<BookingOrder> orders = tradingFacade.placeSellOrdersForHoldings(tuple.getFirst(),
                            tuple.getSecond());

                    Money sellTotal = Money.zero(Money.EUR);
                    for (BookingOrder order : orders) {
                        sellTotal = sellTotal.plus(order.getTotalPrice());
                    }

                    return sellTotal;
                });
                sellOrders.add(future);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }

        logger.info("All buy and sell workers submitted - awaiting futures");

        Money buyTotal = Money.zero(Money.EUR);
        int buyOrderFutures = 0;

        while (!buyOrders.isEmpty()) {
            Future<Money> future = buyOrders.remove(0);
            try {
                Money total = future.get();
                buyTotal = buyTotal.plus(total);
                logger.info("Finished buy order %d/%d: %s".formatted(++buyOrderFutures, BUY_ORDERS, total));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                Assertions.fail(e.getCause());
            }
        }

        Money sellTotal = Money.zero(Money.EUR);
        int sellOrderFutures = 0;

        while (!sellOrders.isEmpty()) {
            Future<Money> future = sellOrders.remove(0);
            try {
                Money total = future.get();
                sellTotal = sellTotal.plus(total);
                logger.info("Finished sell order %d/%d: %s".formatted(++sellOrderFutures, BUY_ORDERS, total));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                Assertions.fail(e.getCause());
            }
        }

        logger.info("All buy order workers completed - total: %s".formatted(buyTotal));
        logger.info("All sell order workers completed - total: %s".formatted(sellTotal));

        this.finalBuyTotal = buyTotal;
        this.finalSellTotal = sellTotal;

        executorService.shutdownNow();
    }

    @Test
    @Transactional
    @Commit
    @Order(3)
    public void whenWrapping_thenCompareInitialAndFinalTotalBalance() {
        Assertions.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());

        Money finalSystemAccountsTotal = Money.zero(Money.EUR);
        Money finalTradingAccountsTotal = Money.zero(Money.EUR);

        for (SystemAccount systemAccount : accountService.findSystemAccountsByPage(Pageable.unpaged())
                .getContent()) {
            finalSystemAccountsTotal = finalSystemAccountsTotal.plus(systemAccount.getBalance());

            for (TradingAccount tradingAccount : accountService.findTradingAccountsByPage(systemAccount.getId(),
                    Pageable.unpaged()).getContent()) {
                finalTradingAccountsTotal = finalTradingAccountsTotal.plus(tradingAccount.getBalance());
            }
        }

        logger.info("System accounts final total: %s".formatted(finalSystemAccountsTotal));
        logger.info("Trading accounts final total: %s".formatted(finalTradingAccountsTotal));

        Assertions.assertEquals(initialSystemAccountsTotal.plus(finalBuyTotal).minus(finalSellTotal),
                finalSystemAccountsTotal,
                "System accounts total discrepancy");
        Assertions.assertEquals(initialTradingAccountsTotal.minus(finalBuyTotal).plus(finalSellTotal),
                finalTradingAccountsTotal,
                "Trading accounts total discrepancy");
    }
}
