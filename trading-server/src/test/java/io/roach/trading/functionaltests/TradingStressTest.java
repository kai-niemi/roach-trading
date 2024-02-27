package io.roach.trading.functionaltests;

import io.roach.trading.AbstractIntegrationTest;
import io.roach.trading.api.OrderRequest;
import io.roach.trading.api.support.Money;
import io.roach.trading.domain.account.AccountService;
import io.roach.trading.domain.order.OrderService;
import io.roach.trading.domain.order.TradingFacade;
import io.roach.trading.domain.portfolio.PortfolioService;
import io.roach.trading.doubles.DoublesService;
import io.roach.trading.doubles.TestDoubles;
import jakarta.persistence.FetchType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

@Tag("stress")
public class TradingStressTest extends AbstractIntegrationTest {
    private static final int ORDERS_PER_THREAD = 1;

    private static final int QTY_PER_ORDER = 10;

    private static final int NUM_THREADS = 4;//Runtime.getRuntime().availableProcessors() * 4;

    @Autowired
    private TradingFacade tradingFacade;

    @Autowired
    private OrderService orderService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private DoublesService doublesService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @BeforeAll
    public void setupTest() {
        Assertions.assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
        doublesService.removeTestDoubles();
        doublesService.createTestDoubles();
    }

    @Test
    @Transactional
    @Commit
    @Order(1)
    public void whenPreparingTest_thenSetupAccounts() {
        Assertions.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());

        Assertions.assertEquals(TestDoubles.USER_INITIAL_BALANCE,
                accountService.getBalance(TestDoubles.USER_ACCOUNT_ALICE));
        Assertions.assertEquals(TestDoubles.USER_INITIAL_BALANCE,
                accountService.getBalance(TestDoubles.USER_ACCOUNT_BOB));
        Assertions.assertEquals(TestDoubles.USER_ACCOUNT_ALICE,
                accountService.getTradingAccountById(TestDoubles.USER_ACCOUNT_ALICE, FetchType.EAGER).getPortfolio()
                        .getId());
        Assertions.assertEquals(TestDoubles.USER_ACCOUNT_BOB,
                accountService.getTradingAccountById(TestDoubles.USER_ACCOUNT_BOB, FetchType.EAGER).getPortfolio()
                        .getId());

        Assertions.assertNotNull(accountService.getTradingAccountById(TestDoubles.USER_ACCOUNT_ALICE, FetchType.LAZY));
        Assertions.assertNotNull(accountService.getTradingAccountById(TestDoubles.USER_ACCOUNT_BOB, FetchType.LAZY));
    }

    @Test
    @Order(2)
    public void whenTradingConcurrently_expectNoErrors() {
        Assertions.assertFalse(TransactionSynchronizationManager.isActualTransactionActive());

        final List<Money> amounts = new ArrayList<>();

        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);

        List<Future<Money>> futures = new ArrayList<>();

        IntStream.rangeClosed(1, NUM_THREADS).forEach(value -> {
            Future<Money> future = executorService.submit(() -> {
                Money amount = Money.zero(Money.EUR);

                for (int i = 0; i < ORDERS_PER_THREAD; i++) {
                    tradingFacade.placeOrder(
                            OrderRequest.builder()
                                    .bookingAccount(TestDoubles.USER_ACCOUNT_ALICE)
                                    .buy(TestDoubles.APPLE_A.getReference())
                                    .unitPrice(TestDoubles.APPLE_A.getBuyPrice())
                                    .quantity(QTY_PER_ORDER)
                                    .ref(UUID.randomUUID().toString())
                                    .build()
                    );

                    amount = amount.plus(TestDoubles.APPLE_A.getBuyPrice().multiply(QTY_PER_ORDER));

                    tradingFacade.placeOrder(
                            OrderRequest.builder()
                                    .bookingAccount(TestDoubles.USER_ACCOUNT_BOB)
                                    .buy(TestDoubles.APPLE_B.getReference())
                                    .unitPrice(TestDoubles.APPLE_B.getBuyPrice())
                                    .quantity(QTY_PER_ORDER)
                                    .ref(UUID.randomUUID().toString())
                                    .build()
                    );

                    amount = amount.plus(TestDoubles.APPLE_B.getBuyPrice().multiply(QTY_PER_ORDER));
                }
                return amount;
            });
            futures.add(future);
        });

        logger.info("All workers submitted - awaiting futures");

        int counter = 0;

        while (!futures.isEmpty()) {
            Future<Money> future = futures.remove(0);
            try {
                amounts.add(future.get());
                logger.info("Finished %d/%d: %s".formatted(++counter, NUM_THREADS, amounts));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                Assertions.fail(e.getCause());
            }
        }

        executorService.shutdownNow();

        Money grossTotal = Money.zero(Money.EUR);
        for (Money amount : amounts) {
            grossTotal = grossTotal.plus(amount);
        }

        final Money accountTotalA =
                TestDoubles.APPLE_A.getBuyPrice().multiply(QTY_PER_ORDER).multiply(ORDERS_PER_THREAD)
                        .multiply(NUM_THREADS);

        final Money accountTotalB =
                TestDoubles.APPLE_B.getBuyPrice().multiply(QTY_PER_ORDER).multiply(ORDERS_PER_THREAD)
                        .multiply(NUM_THREADS);

        Assertions.assertEquals(accountTotalA.plus(accountTotalB), grossTotal);


        transactionTemplate.executeWithoutResult(transactionStatus -> {
            Assertions.assertEquals(TestDoubles.USER_INITIAL_BALANCE.minus(accountTotalA),
                    accountService.getBalance(TestDoubles.USER_ACCOUNT_ALICE));

            Assertions.assertEquals(TestDoubles.USER_INITIAL_BALANCE.minus(accountTotalB),
                    accountService.getBalance(TestDoubles.USER_ACCOUNT_BOB));
        });
    }

    @Test
    @Transactional
    @Commit
    @Order(3)
    public void whenWrappingTest_expectConsistentOutcome() {
        Assertions.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());

        Assertions.assertEquals(NUM_THREADS * ORDERS_PER_THREAD,
                orderService.findOrdersByAccountId(TestDoubles.USER_ACCOUNT_ALICE).size());

        Assertions.assertEquals(NUM_THREADS * ORDERS_PER_THREAD,
                orderService.findOrdersByAccountId(TestDoubles.USER_ACCOUNT_BOB).size());

        Assertions.assertEquals(
                TestDoubles.APPLE_A.getSellPrice().multiply(QTY_PER_ORDER * ORDERS_PER_THREAD * NUM_THREADS),
                portfolioService.getTotalValue(TestDoubles.USER_ACCOUNT_ALICE));

        Assertions.assertEquals(
                TestDoubles.APPLE_B.getSellPrice().multiply(QTY_PER_ORDER * ORDERS_PER_THREAD * NUM_THREADS),
                portfolioService.getTotalValue(TestDoubles.USER_ACCOUNT_BOB));
    }
}
