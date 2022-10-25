package io.roach.trading.api;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"links"})
public class BookingAccountModel extends RepresentationModel<BookingAccountModel> {
    private UUID id;

    private List<Holding> holdings = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<Holding> getHoldings() {
        return holdings;
    }

    public void setHoldings(List<Holding> holdings) {
        this.holdings = holdings;
    }
}
