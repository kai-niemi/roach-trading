package io.roach.trading.domain.product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
import io.roach.trading.api.ProductModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/api/product")
@TransactionBoundary
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductResourceAssembler productResourceAssembler;

    @Autowired
    private ProductModelResourceAssembler productModelResourceAssembler;

    @Autowired
    private PagedResourcesAssembler<Product> pagedResourcesAssembler;

    @GetMapping
    public HttpEntity<PagedModel<EntityModel<Product>>> findAll(@PageableDefault(size = 5) Pageable page) {
        PagedModel<EntityModel<Product>> model = pagedResourcesAssembler
                .toModel(productService.findProductsPage(page), productResourceAssembler);
        model.add(linkTo(methodOn(ProductController.class)
                .getRandomProducts(500))
                .withRel("random"));
        return ResponseEntity.ok(model);
    }

    @GetMapping(value = "/{id}")
    public HttpEntity<EntityModel<Product>> getProduct(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(productResourceAssembler
                .toModel(productService.getProductById(id)));
    }

    @GetMapping(value = "/random")
    public HttpEntity<CollectionModel<ProductModel>> getRandomProducts(
            @RequestParam(name = "limit", defaultValue = "500") int limit) {
        List<Product> entities = new ArrayList<>();

        entities.addAll(productService.findProductsPage(Pageable.ofSize(512)).getContent());
//        Page<Product> page = productService.findProductsPage(Pageable.ofSize(512));
//        int n = limit / page.getTotalPages();
//        while (page.hasContent()) {
//            List<Product> pageItems = new ArrayList<>(page.getContent());
//            Collections.shuffle(pageItems);
//            entities.addAll(pageItems.subList(0, n));
//            page = productService.findProductsPage(
//                    page.hasNext() ? page.nextPageable() : Pageable.unpaged());
//        }

        return ResponseEntity.ok(
                productModelResourceAssembler.toCollectionModel(entities));
    }
}
