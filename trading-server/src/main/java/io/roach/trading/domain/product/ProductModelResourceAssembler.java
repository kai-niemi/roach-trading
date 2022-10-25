package io.roach.trading.domain.product;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import io.roach.trading.api.ProductModel;

@Component
public class ProductModelResourceAssembler extends RepresentationModelAssemblerSupport<Product, ProductModel> {
    public ProductModelResourceAssembler() {
        super(ProductController.class, ProductModel.class);
    }

    @Override
    public ProductModel toModel(Product entity) {
        ProductModel model = new ProductModel();
        model.setBuyPrice(entity.getBuyPrice().getAmount());
        model.setSellPrice(entity.getSellPrice().getAmount());
        model.setCurrency(entity.getCurrency());
        model.setReference(entity.getReference());
        return model;
    }
}
