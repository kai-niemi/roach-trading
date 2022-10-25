package io.roach.trading.domain.product;

import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProductResourceAssembler implements SimpleRepresentationModelAssembler<Product> {
    @Override
    public void addLinks(EntityModel<Product> resource) {
        resource.add(linkTo(methodOn(ProductController.class)
                .getProduct(resource.getContent().getId()))
                .withSelfRel());
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<Product>> resources) {
        resources.add(linkTo(methodOn(ProductController.class)
                .findAll(PageRequest.of(0, 5)))
                .withRel("products"));
    }

}
