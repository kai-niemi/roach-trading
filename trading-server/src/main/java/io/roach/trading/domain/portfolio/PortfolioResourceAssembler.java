package io.roach.trading.domain.portfolio;

import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

import io.roach.trading.domain.home.LinkRelations;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PortfolioResourceAssembler implements SimpleRepresentationModelAssembler<Portfolio> {
    @Override
    public void addLinks(EntityModel<Portfolio> resource) {
        Portfolio entity = resource.getContent();

        resource.add(linkTo(methodOn(PortfolioController.class)
                .getPortfolio(entity.getId()
                )).withSelfRel());

        resource.add(linkTo(methodOn(PortfolioController.class)
                .getPortfolioItems(
                        entity.getId(),
                        PageRequest.of(0, 5)))
                .withRel(LinkRelations.PORTFOLIO_ITEMS_REL));
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<Portfolio>> resources) {
//        resources.add(linkTo(methodOn(PortfolioController.class)
//                .listProducts(PageRequest.of(0, 5)))
//                .withRel("portfolios"));
    }
}
