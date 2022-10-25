package io.roach.trading.domain.portfolio;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

import io.roach.trading.domain.product.Product;
import io.roach.trading.domain.product.ProductController;
import io.roach.trading.domain.home.LinkRelations;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PortfolioItemResourceAssembler implements SimpleRepresentationModelAssembler<PortfolioItem> {
    @Override
    public void addLinks(EntityModel<PortfolioItem> resource) {
        PortfolioItem entity = resource.getContent();
//        Portfolio portfolio = entity.getPortfolio();
        Product product = entity.getProduct();

//        resource.add(linkTo(methodOn(PortfolioController.class)
//                .getPortfolio(portfolio.getId()))
//                .withRel(LinkRelations.PORTFOLIO_REL));

        resource.add(linkTo(methodOn(ProductController.class)
                .getProduct(product.getId()))
                .withRel(LinkRelations.PRODUCT_REL));
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<PortfolioItem>> resources) {
    }
}
