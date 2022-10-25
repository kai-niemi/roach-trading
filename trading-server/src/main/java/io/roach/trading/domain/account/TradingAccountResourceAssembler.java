package io.roach.trading.domain.account;

import java.util.UUID;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

import io.roach.trading.domain.home.LinkRelations;
import io.roach.trading.domain.portfolio.PortfolioController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TradingAccountResourceAssembler implements SimpleRepresentationModelAssembler<TradingAccount> {
    @Override
    public void addLinks(EntityModel<TradingAccount> resource) {
        TradingAccount account = resource.getContent();
        UUID id = account.getId();

        resource.add(linkTo(methodOn(AccountController.class)
                .getTradingAccount(id))
                .withSelfRel());

        UUID parentId = account.getParentAccount().getId();
        resource.add(linkTo(methodOn(AccountController.class)
                .getSystemAccount(parentId))
                .withRel(LinkRelations.PARENT_ACCOUNT_REL));

        resource.add(linkTo(methodOn(PortfolioController.class)
                .getPortfolio(id))
                .withRel(LinkRelations.PORTFOLIO_REL));
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<TradingAccount>> resources) {
    }
}

