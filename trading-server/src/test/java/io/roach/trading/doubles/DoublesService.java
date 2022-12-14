package io.roach.trading.doubles;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import io.roach.trading.domain.account.SystemAccountRepository;
import io.roach.trading.domain.account.AccountService;
import io.roach.trading.domain.account.TradingAccountRepository;
import io.roach.trading.domain.order.OrderItemRepository;
import io.roach.trading.domain.order.OrderRepository;
import io.roach.trading.domain.portfolio.PortfolioRepository;
import io.roach.trading.domain.product.ProductRepository;
import io.roach.trading.domain.product.ProductService;

@Service
public class DoublesService {
    @Autowired
    private AccountService accountService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SystemAccountRepository systemAccountRepository;

    @Autowired
    private TradingAccountRepository tradingAccountRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Transactional
    public void createTestDoubles() {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "wrong tx state");

        accountService.createSystemAccount(TestDoubles.SYSTEM_ACCOUNT_A, "TRADER:A", TestDoubles.TRADER_INITIAL_BALANCE);
        accountService.createSystemAccount(TestDoubles.SYSTEM_ACCOUNT_B, "TRADER:B", TestDoubles.TRADER_INITIAL_BALANCE);
        accountService.createSystemAccount(TestDoubles.SYSTEM_ACCOUNT_C, "TRADER:C", TestDoubles.TRADER_INITIAL_BALANCE);

        accountService.createTradingAccount(TestDoubles.SYSTEM_ACCOUNT_A, TestDoubles.USER_ACCOUNT_ALICE,
                "ALICE", TestDoubles.USER_INITIAL_BALANCE);
        accountService.createTradingAccount(TestDoubles.SYSTEM_ACCOUNT_B, TestDoubles.USER_ACCOUNT_BOB,
                "BOB", TestDoubles.USER_INITIAL_BALANCE);
        accountService.createTradingAccount(TestDoubles.SYSTEM_ACCOUNT_C, TestDoubles.USER_ACCOUNT_BOBBY_TABLES,
                "BOBBY_TABLES", TestDoubles.USER_INITIAL_BALANCE);

        Arrays.stream(TestDoubles.ALL_PRODUCTS).forEachOrdered(product -> productService.create(product));
    }

    @Transactional
    public void removeTestDoubles() {
        orderItemRepository.deleteAllInBatch();
        orderItemRepository.flush();

        orderRepository.deleteAllInBatch();
        orderRepository.flush();

        portfolioRepository.deleteAll();
        portfolioRepository.flush();

        tradingAccountRepository.deleteAllInBatch();
        tradingAccountRepository.flush();
        
        systemAccountRepository.deleteAllInBatch();
        systemAccountRepository.flush();

        productRepository.deleteAllInBatch();
        productRepository.flush();
    }
}
