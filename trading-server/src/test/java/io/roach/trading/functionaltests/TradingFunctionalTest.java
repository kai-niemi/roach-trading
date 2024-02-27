package io.roach.trading.functionaltests;

import io.roach.trading.AbstractIntegrationTest;
import io.roach.trading.api.OrderRequest;
import io.roach.trading.domain.account.AccountService;
import io.roach.trading.domain.order.BookingOrder;
import io.roach.trading.domain.order.OrderService;
import io.roach.trading.domain.portfolio.PortfolioService;
import io.roach.trading.doubles.TestDoubles;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import static io.roach.trading.doubles.TestDoubles.USER_INITIAL_BALANCE;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TradingFunctionalTest extends AbstractIntegrationTest {
    @Autowired
    private AccountService accountService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PortfolioService portfolioService;

    @BeforeAll
    public void setupTest() {
        logger.info("Removing test doubles..");
        doublesService.removeTestDoubles();
        logger.info("Creating test doubles..");
        doublesService.createTestDoubles();
    }

    @Test
    @Transactional
    @Commit
    public void whenPlacingOrders_thenReturnCorrectBalances() {
        assertEquals(USER_INITIAL_BALANCE, accountService.getBalance(TestDoubles.USER_ACCOUNT_ALICE));

        BookingOrder o1 = orderService.placeOrder(OrderRequest.builder()
                .bookingAccount(TestDoubles.USER_ACCOUNT_ALICE)
                .buy(TestDoubles.APPLE_A.getReference())
                .unitPrice(TestDoubles.APPLE_A.getBuyPrice())
                .quantity(5)
                .ref("buy-1")
                .build()
        );
        assertEquals(TestDoubles.APPLE_A.getBuyPrice().multiply(5), o1.getTotalPrice());

        BookingOrder o2 = orderService.placeOrder(OrderRequest.builder()
                .bookingAccount(TestDoubles.USER_ACCOUNT_ALICE)
                .buy(TestDoubles.NOKIA_A.getReference())
                .unitPrice(TestDoubles.NOKIA_A.getBuyPrice())
                .quantity(2)
                .ref("buy-2")
                .build()
        );
        assertEquals(TestDoubles.NOKIA_A.getBuyPrice().multiply(2), o2.getTotalPrice());

        BookingOrder o3 = orderService.placeOrder(OrderRequest.builder()
                .bookingAccount(TestDoubles.USER_ACCOUNT_ALICE)
                .sell(TestDoubles.APPLE_A.getReference())
                .unitPrice(TestDoubles.APPLE_A.getSellPrice())
                .quantity(2)
                .ref("sell-1")
                .build()
        );
        assertEquals(TestDoubles.APPLE_A.getSellPrice().multiply(2), o3.getTotalPrice());

        assertEquals(
                USER_INITIAL_BALANCE.plus(
                        TestDoubles.APPLE_A.getBuyPrice().multiply(5).negate(),
                        TestDoubles.NOKIA_A.getBuyPrice().multiply(2).negate(),
                        TestDoubles.APPLE_A.getSellPrice().multiply(2)
                ),
                accountService.getBalance(TestDoubles.USER_ACCOUNT_ALICE));

        assertEquals(
                TestDoubles.APPLE_A.getSellPrice().multiply(3).plus(
                        TestDoubles.NOKIA_A.getSellPrice().multiply(2)
                ),
                portfolioService.getTotalValue(TestDoubles.USER_ACCOUNT_ALICE));
    }
}
