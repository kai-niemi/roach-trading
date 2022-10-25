package io.roach.trading.domain.account;

import java.util.UUID;

import javax.persistence.FetchType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.roach.trading.annotation.TransactionBoundary;
import io.roach.trading.api.BookingAccountModel;
import io.roach.trading.domain.common.ZoomExpression;
import io.roach.trading.domain.home.LinkRelations;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/api/account")
@TransactionBoundary
public class AccountController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private PagedResourcesAssembler<TradingAccount> tradingAccountPagedResourcesAssembler;

    @Autowired
    private PagedResourcesAssembler<SystemAccount> systemAccountPagedResourcesAssembler;

    @Autowired
    private SystemAccountResourceAssembler systemAccountResourceAssembler;

    @Autowired
    private TradingAccountResourceAssembler tradingAccountResourceAssembler;

    @Autowired
    private AccountModelResourceAssembler accountModelResourceAssembler;

    @GetMapping("/trading")
    public HttpEntity<PagedModel<EntityModel<TradingAccount>>> findAllTradingAccounts(
            @PageableDefault(size = 5) Pageable page,
            @RequestParam(name = "onlyWithHoldings", defaultValue = "false") boolean onlyWithHoldings) {
        Page<TradingAccount> entities = accountService. findTradingAccountsByPage(page, false);
        PagedModel<EntityModel<TradingAccount>> model = tradingAccountPagedResourcesAssembler.toModel(entities,
                tradingAccountResourceAssembler);
        model.add(linkTo(methodOn(AccountController.class)
                .getRandomAccounts(500))
                .withRel("random"));
        return ResponseEntity.ok(model);
    }

    @GetMapping("/system")
    public HttpEntity<PagedModel<EntityModel<SystemAccount>>> findAllSystemAccounts(
            @PageableDefault(size = 5) Pageable page) {
        Page<SystemAccount> entities = accountService.findSystemAccountsByPage(page);
        PagedModel<EntityModel<SystemAccount>> model = systemAccountPagedResourcesAssembler.toModel(entities,
                systemAccountResourceAssembler);
        return ResponseEntity.ok(model);
    }

    @GetMapping(value = "/system/{id}")
    public ResponseEntity<EntityModel<SystemAccount>> getSystemAccount(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(systemAccountResourceAssembler
                .toModel(accountService.getSystemAccountById(id)));
    }

    @GetMapping(value = "/system/{id}/trading")
    public HttpEntity<PagedModel<EntityModel<TradingAccount>>> findTradingAccounts(@PathVariable("id") UUID parentId,
                                                                                   @PageableDefault(size = 5)
                                                                                   Pageable page) {
        Page<TradingAccount> entities = accountService.findTradingAccountsByPage(parentId, page);
        return ResponseEntity.ok(
                tradingAccountPagedResourcesAssembler.toModel(entities, tradingAccountResourceAssembler));
    }

    @GetMapping(value = "/trading/{id}")
    public ResponseEntity<EntityModel<TradingAccount>> getTradingAccount(@PathVariable("id") UUID id) {
        FetchType fetchType = ZoomExpression.fromCurrentRequest().containsRel(LinkRelations.PORTFOLIO_REL)
                ? FetchType.EAGER : FetchType.LAZY;
        return ResponseEntity.ok(tradingAccountResourceAssembler
                .toModel(accountService.getTradingAccountById(id, fetchType)));
    }

    @GetMapping(value = "/trading/random")
    public HttpEntity<CollectionModel<BookingAccountModel>> getRandomAccounts(
            @RequestParam(name = "limit", defaultValue = "500") int limit) {
        Page<TradingAccount> page = accountService.findTradingAccountsByPage(Pageable.ofSize(limit), false);
        return ResponseEntity.ok(
                accountModelResourceAssembler.toCollectionModel(page.getContent()));
    }
}
