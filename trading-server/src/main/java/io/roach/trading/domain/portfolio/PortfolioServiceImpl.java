package io.roach.trading.domain.portfolio;

import java.util.List;
import java.util.UUID;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.roach.trading.annotation.TransactionMandatory;
import io.roach.trading.api.support.Money;
import io.roach.trading.domain.account.NoSuchTradingAccountException;
import io.roach.trading.domain.account.TradingAccountRepository;
import io.roach.trading.domain.product.NoSuchProductException;
import io.roach.trading.domain.product.Product;
import io.roach.trading.domain.product.ProductService;

@Service
@TransactionMandatory
public class PortfolioServiceImpl implements PortfolioService {
    @Autowired
    private TradingAccountRepository tradingAccountRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private ProductService productService;

    @Override
    public Portfolio getPortfolioById(UUID accountId) {
        Portfolio portfolio = portfolioRepository.getReferenceById(accountId);
        if (!Hibernate.isInitialized(portfolio.getItems())) {
            Hibernate.initialize(portfolio.getItems());
        }
        portfolio.getItems().forEach(portfolioItem -> {
            if (!Hibernate.isInitialized(portfolioItem.getProduct())) {
                Hibernate.initialize(portfolioItem.getProduct());
            }
        });
        portfolio.getTotalValue();
        return portfolio;
    }

    @Override
    public Money getTotalValue(UUID accountId) {
        Money totalValue = Money.zero(tradingAccountRepository.getBalanceById(accountId)
                .orElseThrow(() -> new NoSuchTradingAccountException(accountId)).getCurrency());

        List<Object[]> o = portfolioRepository
                .sumProductQuantityByAccountId(accountId);
        for (Object[] objects : o) {
            String productRef = (String) objects[0];
            Long quantity = (Long) objects[1];

            Product product = productService.getProductByRef(productRef);
            if (product == null) {
                throw new NoSuchProductException(productRef);
            }

            Money productValue = product.getSellPrice().multiply(quantity);

            totalValue = totalValue.plus(productValue);
        }

        return totalValue;
    }

    @Override
    public Page<PortfolioItem> findItemsById(UUID accountId, Pageable page) {
        return portfolioRepository.findById(accountId, page);
    }

    @Override
    public List<Portfolio> findAll() {
        return portfolioRepository.findAll();
    }
}
