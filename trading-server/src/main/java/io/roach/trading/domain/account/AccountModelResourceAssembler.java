package io.roach.trading.domain.account;

import java.util.ArrayList;
import java.util.List;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import io.roach.trading.api.BookingAccountModel;
import io.roach.trading.api.Holding;
import io.roach.trading.domain.portfolio.PortfolioItem;

@Component
public class AccountModelResourceAssembler extends RepresentationModelAssemblerSupport<TradingAccount, BookingAccountModel> {
    public AccountModelResourceAssembler() {
        super(AccountController.class, BookingAccountModel.class);
    }

    @Override
    public BookingAccountModel toModel(TradingAccount entity) {
        List<Holding> holdings = new ArrayList<>();

        for (PortfolioItem item : entity.getPortfolio()) {
            Holding h = new Holding();
            h.setQuantity(item.getQuantity());
            h.setProductRef(item.getProduct().getReference());
            h.setBuyPrice(item.getProduct().getBuyPrice());
            holdings.add(h);
        }

        BookingAccountModel model = new BookingAccountModel();
        model.setId(entity.getId());
        model.setHoldings(holdings);

        return model;
    }
}

