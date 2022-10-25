package io.roach.trading.domain.portfolio;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.roach.trading.annotation.TransactionBoundary;

@RestController
@RequestMapping(value = "/api/portfolio")
@TransactionBoundary
public class PortfolioController {
    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private PortfolioResourceAssembler portfolioResourceAssembler;

    @Autowired
    private PortfolioItemResourceAssembler portfolioItemResourceAssembler;

    @Autowired
    private PagedResourcesAssembler<PortfolioItem> portfolioItemPagedResourcesAssembler;

    @GetMapping(value = "/{id}")
    public HttpEntity<EntityModel<Portfolio>> getPortfolio(
            @PathVariable("id") UUID id) {
        return ResponseEntity.ok(portfolioResourceAssembler
                .toModel(portfolioService.getPortfolioById(id)));
    }

    @GetMapping(value = "/{id}/items")
    public HttpEntity<PagedModel<EntityModel<PortfolioItem>>> getPortfolioItems(
            @PathVariable("id") UUID id,
            @PageableDefault(size = 5) Pageable page) {
        Page<PortfolioItem> items = portfolioService.findItemsById(id, page);
        return ResponseEntity.ok(portfolioItemPagedResourcesAssembler
                .toModel(items, portfolioItemResourceAssembler));
    }

}
