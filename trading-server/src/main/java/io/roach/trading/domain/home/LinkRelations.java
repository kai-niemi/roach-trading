package io.roach.trading.domain.home;

public abstract class LinkRelations {
    private LinkRelations() {
    }

    // IANA standard link relations:
    // http://www.iana.org/assignments/link-relations/link-relations.xhtml

    public static final String HELP_REL = "help";

    // Application curie name

    public static final String CURIE_NAMESPACE = "trading";

    // Curie prefixed link relations

    public static final String ACTUATOR_REL = "actuator";

    public static final String HAL_EXPLORER_REL = "hal-explorer";

    public static final String ORDER_REL = "order";

    public static final String ORDERS_REL = "orders";

    public static final String ORDER_ITEM_REL = "order-item";

    public static final String ORDER_ITEMS_REL = "order-items";

    public static final String PRODUCT_REL = "product";

    public static final String PRODUCTS_REL = "products";

    public static final String PORTFOLIO_REL = "portfolio";

    public static final String PORTFOLIOS_REL = "portfolios";

    public static final String PORTFOLIO_ITEM_REL = "portfolio-item";

    public static final String PORTFOLIO_ITEMS_REL = "portfolio-items";

    public static final String SYSTEM_ACCOUNT_REL = "system-account";

    public static final String SYSTEM_ACCOUNTS_REL = "system-accounts";

    public static final String TRADING_ACCOUNT_REL = "trading-account";

    public static final String TRADING_ACCOUNTS_REL = "trading-accounts";

    public static final String PARENT_ACCOUNT_REL = "parent-account";

    public static String withCurie(String rel) {
        return CURIE_NAMESPACE + ":" + rel;
    }

}
