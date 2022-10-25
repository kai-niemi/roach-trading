package io.roach.trading.domain.account;

import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

import io.roach.trading.domain.home.LinkRelations;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class SystemAccountResourceAssembler implements SimpleRepresentationModelAssembler<SystemAccount> {
    @Override
    public void addLinks(EntityModel<SystemAccount> resource) {
        SystemAccount account = resource.getContent();
        UUID id = account.getId();

        resource.add(linkTo(methodOn(AccountController.class)
                .getSystemAccount(id))
                .withSelfRel());

        resource.add(linkTo(methodOn(AccountController.class)
                .findTradingAccounts(id, PageRequest.of(0, 5)))
                .withRel(LinkRelations.TRADING_ACCOUNTS_REL));
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<SystemAccount>> resources) {
    }
}
