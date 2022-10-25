package io.roach.trading.domain.order;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

import io.roach.trading.domain.account.AccountController;
import io.roach.trading.domain.home.LinkRelations;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderItemResourceAssembler implements SimpleRepresentationModelAssembler<BookingOrderItem> {
    @Override
    public void addLinks(EntityModel<BookingOrderItem> resource) {
        BookingOrderItem entity = resource.getContent();

        resource.add(linkTo(methodOn(OrderController.class)
                .getBookingOrderItems(entity.getId().getOrderId(), entity.getId().getAccountId()))
                .withSelfRel());

        resource.add(linkTo(methodOn(AccountController.class)
                .getTradingAccount(entity.getId().getAccountId()))
                .withRel(LinkRelations.TRADING_ACCOUNT_REL));
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<BookingOrderItem>> resources) {

    }
}
