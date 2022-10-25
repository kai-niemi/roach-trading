package io.roach.trading.domain.order;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.roach.trading.annotation.Retryable;
import io.roach.trading.annotation.TransactionBoundary;
import io.roach.trading.api.OrderRequest;
import io.roach.trading.domain.home.LinkRelations;
import io.roach.trading.domain.product.Product;
import io.roach.trading.api.support.Money;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/api/order")
@TransactionBoundary
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderResourceAssembler orderResourceAssembler;

    @Autowired
    private OrderItemResourceAssembler orderItemResourceAssembler;

    @Autowired
    private PagedResourcesAssembler<BookingOrder> pagedResourcesAssembler;

    @RequestMapping(method = RequestMethod.GET)
    public HttpEntity<PagedModel<EntityModel<BookingOrder>>> findAll(@PageableDefault(size = 5) Pageable page) {
        Page<BookingOrder> entities = orderService.findOrderPage(page);

        PagedModel<EntityModel<BookingOrder>> model = pagedResourcesAssembler
                .toModel(entities, orderResourceAssembler);

        model.add(linkTo(methodOn(OrderController.class).findAll(page)).withRel(IanaLinkRelations.FIRST)
                .andAffordance(afford(methodOn(OrderController.class).placeOrder(null))));

        return ResponseEntity.ok(model);
    }

    @GetMapping(value = "/{id}")
    public HttpEntity<EntityModel<BookingOrder>> getOrder(
            @PathVariable("id") UUID id) {
        BookingOrder order = orderService.getOrderById(id);
        return ResponseEntity.ok(orderResourceAssembler.toModel(order));
    }

    @GetMapping(value = "form")
    public ResponseEntity<EntityModel<OrderRequest>> orderRequestForm() {
        Product example = Product.builder().withId(UUID.randomUUID()).withReference("Samsung Electronics")
                .withBuyPrice(Money.euro("30.50")).withSellPrice(Money.euro("29.85")).build();

        OrderRequest request =
                OrderRequest.builder()
                        .bookingAccount(UUID.randomUUID())
                        .buy(example.getReference())
                        .unitPrice(example.getBuyPrice())
                        .quantity(4)
                        .build();

        EntityModel<OrderRequest> form = EntityModel.of(request);
        form.add(linkTo(methodOn(OrderController.class)
                .placeOrder(request))
                .withRel(LinkRelations.ORDER_REL)
        );

        return ResponseEntity.ok(form);
    }

    @PostMapping
    @Retryable
    public ResponseEntity<EntityModel<BookingOrder>> placeOrder(@RequestBody OrderRequest request) {
        BookingOrder order = orderService.placeOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResourceAssembler.toModel(order));
    }

    @GetMapping(value = "/{orderId}/items/{accountId}")
    public HttpEntity<CollectionModel<EntityModel<BookingOrderItem>>> getBookingOrderItems(
            @PathVariable("orderId") UUID orderId,
            @PathVariable("accountId") UUID accountId) {
        List<BookingOrderItem> items = orderService.findOrderItemsByAccountId(
                orderId,
                accountId);
        return ResponseEntity.ok(orderItemResourceAssembler.toCollectionModel(items));
    }
}
