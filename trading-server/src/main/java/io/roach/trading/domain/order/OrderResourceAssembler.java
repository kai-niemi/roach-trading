package io.roach.trading.domain.order;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

import io.roach.trading.domain.product.ProductController;
import io.roach.trading.domain.home.LinkRelations;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderResourceAssembler implements SimpleRepresentationModelAssembler<BookingOrder> {
    @Override
    public void addLinks(EntityModel<BookingOrder> resource) {
        BookingOrder entity = resource.getContent();

        resource.add(linkTo(methodOn(OrderController.class)
                .getOrder(entity.getId()))
                .withSelfRel());

        resource.add(linkTo(methodOn(ProductController.class)
                .getProduct(entity.getProductId()))
                .withRel(LinkRelations.PRODUCT_REL));

        resource.add(linkTo(methodOn(OrderController.class)
                .getBookingOrderItems(
                        entity.getId(),
                        entity.getAccount().getId()))
                .withRel(LinkRelations.ORDER_ITEMS_REL));
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<BookingOrder>> resources) {
    }
}
