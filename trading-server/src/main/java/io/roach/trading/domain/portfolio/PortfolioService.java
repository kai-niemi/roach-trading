package io.roach.trading.domain.portfolio;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.roach.trading.domain.account.NoSuchSystemAccountException;
import io.roach.trading.api.support.Money;

/**
 * Defines the business contract for managing account portfolios.
 */
public interface PortfolioService {
    /**
     * Find the portfolio for a given account.
     *
     * @param accountId a unique account reference scoped to the client namespace
     * @return the portfolio
     * @throws IllegalArgumentException if the first argument does
     * not represent a valid account reference (length > 0)
     * or is null
     * @throws NoSuchSystemAccountException if an account with the given
     * reference does not exist
     * @throws NoSuchPortfolioException if a portfolio for the given
     * account reference does not exists
     */
    Portfolio getPortfolioById(UUID accountId);

    /**
     * Get the total value of all portfolios for a given account.
     *
     * @param accountId a unique account reference scoped to the client namespace
     * @return the total value of all portfolios in the account currency
     * @throws IllegalArgumentException if the first argument does
     * not represent a valid account reference (length > 0)
     * or is null
     * @throws NoSuchSystemAccountException if an account with the given
     * reference does not exist
     */
    Money getTotalValue(UUID accountId);

    Page<PortfolioItem> findItemsById(UUID accountId, Pageable page);

    List<Portfolio> findAll();
}
