package io.roach.trading.functionaltests;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import io.roach.trading.AbstractIntegrationTest;
import io.roach.trading.api.OrderRequest;
import io.roach.trading.domain.account.AccountService;
import io.roach.trading.domain.order.BookingOrder;
import io.roach.trading.domain.order.NegativeBalanceException;
import io.roach.trading.domain.order.NegativeQuantityException;
import io.roach.trading.domain.order.OrderRejectedException;
import io.roach.trading.domain.order.OrderService;
import io.roach.trading.doubles.DoublesService;
import io.roach.trading.doubles.TestDoubles;

import static io.roach.trading.api.support.Money.euro;

public class NegativeTradingFunctionalTest extends AbstractIntegrationTest {
    private static final UUID ACCOUNT_A = UUID.randomUUID();

    private static final UUID ACCOUNT_B = UUID.randomUUID();

    @Autowired
    private AccountService accountService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private DoublesService doublesService;

    @BeforeAll
    public void setupTest() {
        doublesService.removeTestDoubles();
        doublesService.createTestDoubles();
    }

    @Test
    @Order(1)
    @Transactional
    @Commit
    public void whenStartingTest_thenSetupAccounts() {
        accountService.createTradingAccount(TestDoubles.SYSTEM_ACCOUNT_A, ACCOUNT_A, "USER:A", euro("100.00"));
        accountService.createTradingAccount(TestDoubles.SYSTEM_ACCOUNT_A, ACCOUNT_B, "USER:B",
                euro("1000.00"));
    }

    @Test
    @Order(2)
    @Transactional
    @Rollback
    public void whenPlacingLargeBuyOrder_thenDetectNegativeAccountBalance() {
        Assertions.assertThrows(NegativeBalanceException.class, () -> {
            orderService.placeOrder(OrderRequest.builder()
                    .bookingAccount(ACCOUNT_A)
                    .buy(TestDoubles.APPLE_A.getReference())
                    .unitPrice(TestDoubles.APPLE_A.getBuyPrice())
                    .quantity(10)
                    .ref("buy-1")
                    .build());
        });
    }

    @Test
    @Order(3)
    @Transactional
    @Rollback
    public void whenExceedingSellLimit_thenDetectTooHighSellPrice() {
        orderService.placeOrder(OrderRequest.builder()
                .bookingAccount(ACCOUNT_B)
                .buy(TestDoubles.APPLE_A.getReference())
                .unitPrice(TestDoubles.APPLE_A.getBuyPrice())
                .quantity(2)
                .ref("buy-2")
                .build()
        );

        Assertions.assertThrows(OrderRejectedException.class, () -> {
            orderService.placeOrder(OrderRequest.builder()
                    .bookingAccount(ACCOUNT_B)
                    .sell(TestDoubles.APPLE_A.getReference())
                    .unitPrice(TestDoubles.APPLE_A.getSellPrice().multiply(1.06))
                    .quantity(2)
                    .ref("sell-1")
                    .build()
            );
        });
    }

    @Test
    @Order(4)
    @Transactional
    @Rollback
    public void whenExceedingBuyLimit_detectTooLowBuyPrice() {
        Assertions.assertThrows(OrderRejectedException.class, () -> {
            orderService.placeOrder(OrderRequest.builder()
                    .bookingAccount(ACCOUNT_B)
                    .buy(TestDoubles.APPLE_A.getReference())
                    .unitPrice(TestDoubles.APPLE_A.getBuyPrice().multiply(0.94))
                    .quantity(2)
                    .ref("buy-3")
                    .build()
            );
        });
    }

    @Test
    @Order(5)
    @Transactional
    @Rollback
    public void whenExceedingQuantityLimit_thenDetectNegativePortfolioQuantity() {
        Assertions.assertThrows(NegativeQuantityException.class, () -> {
            orderService.placeOrder(OrderRequest.builder()
                    .bookingAccount(ACCOUNT_A)
                    .sell(TestDoubles.APPLE_A.getReference())
                    .unitPrice(TestDoubles.APPLE_A.getBuyPrice())
                    .quantity(1)
                    .ref("buy-4")
                    .build()
            );
        });
    }

    @Test
    @Order(6)
    @Transactional
    @Commit
    public void whenDoublePlacingOrder_thenCancelOutViaIdempotency() {
        BookingOrder first = orderService.placeOrder(OrderRequest.builder()
                .bookingAccount(ACCOUNT_A)
                .buy(TestDoubles.APPLE_A.getReference())
                .unitPrice(TestDoubles.APPLE_A.getBuyPrice())
                .quantity(2)
                .ref("buy-5")
                .build()
        );
        BookingOrder second = orderService.placeOrder(OrderRequest.builder()
                .bookingAccount(ACCOUNT_A)
                .buy(TestDoubles.APPLE_B.getReference())
                .unitPrice(TestDoubles.APPLE_B.getBuyPrice())
                .quantity(3)
                .ref("buy-5")
                .build()
        );
        Assertions.assertEquals(first.getProduct().getId(), second.getProduct().getId());
        Assertions.assertEquals(1, orderService.findOrdersByAccountId(ACCOUNT_A).size());
    }
}