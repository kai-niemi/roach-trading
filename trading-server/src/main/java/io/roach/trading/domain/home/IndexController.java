package io.roach.trading.domain.home;

import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.roach.trading.domain.account.AccountController;
import io.roach.trading.domain.order.OrderController;
import io.roach.trading.domain.product.ProductController;

@RestController
public class IndexController {
    @GetMapping("/api")
    public ResponseEntity<RepresentationModel<?>> getApiIndex() {
        final String rootUri =
                ServletUriComponentsBuilder
                        .fromCurrentContextPath()
                        .pathSegment("api")
                        .buildAndExpand()
                        .toUriString();

        RepresentationModel<?> index = new RepresentationModel<>();

        // Observability / discovery

        index.add(Link.of("https://www.iana.org/assignments/link-relations/link-relations.xhtml")
                .withRel(LinkRelations.HELP_REL));

        index.add(Link.of(
                        ServletUriComponentsBuilder
                                .fromCurrentContextPath()
                                .pathSegment("api", "actuator")
                                .buildAndExpand()
                                .toUriString()
                ).withRel(LinkRelations.ACTUATOR_REL)
                .withTitle("Spring boot actuators for observability"));

        index.add(Link.of(ServletUriComponentsBuilder.fromCurrentContextPath()
                        .pathSegment("browser/index.html")
                        .fragment("theme=Cosmo&uri=" + rootUri)
                        .buildAndExpand()
                        .toUriString())
                .withRel(LinkRelations.HAL_EXPLORER_REL)
                .withTitle("Hypermedia API browser")
                .withType(MediaType.TEXT_HTML_VALUE)
        );

        // Accounts

        index.add(WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(AccountController.class)
                        .findAllSystemAccounts(PageRequest.of(0, 15)))
                .withRel(LinkRelations.SYSTEM_ACCOUNT_REL)
                .withTitle("System account collection resource"));
//        index.add(WebMvcLinkBuilder
//                .linkTo(WebMvcLinkBuilder.methodOn(AccountController.class)
//                        .findAllTradingAccounts(PageRequest.of(0, 15), true))
//                .withRel(LinkRelations.TRADING_ACCOUNT_REL)
//                .withTitle("Trading account collection resource"));
        index.add(WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(AccountController.class)
                        .findAllTradingAccounts(PageRequest.of(0, 15), false))
                .withRel(LinkRelations.TRADING_ACCOUNT_REL)
                .withTitle("Trading account collection resource"));

        // Products

        index.add(WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(ProductController.class)
                        .findAll(PageRequest.of(0, 15)))
                .withRel(LinkRelations.PRODUCTS_REL)
                .withTitle("Product collection resource"));

        // Orders

        index.add(WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(OrderController.class)
                        .findAll(PageRequest.of(0, 15)))
                .withRel(LinkRelations.ORDERS_REL)
                .withTitle("Order collection resource"));

        return ResponseEntity.ok(index);
    }
}
