package io.roach.trading.domain.order;

import io.roach.trading.annotation.Retryable;
import io.roach.trading.annotation.TransactionBoundary;
import io.roach.trading.api.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TradingFacade {
    @Autowired
    private OrderService orderService;

    @TransactionBoundary
    @Retryable
    public BookingOrder placeOrder(OrderRequest request) {
        return orderService.placeOrder(request);
    }
}
