package io.roach.product.web;

public abstract class LinkRelations {
    private LinkRelations() {
    }

    // Application curie name

    public static final String CURIE_NAMESPACE = "product";

    // Curie prefixed link relations

    public static final String PRODUCT_REL = "product";

    public static final String PRODUCTS_REL = "products";

    public static final String SCHEDULER_REL = "scheduler";

    public static String withCurie(String rel) {
        return CURIE_NAMESPACE + ":" + rel;
    }

}
