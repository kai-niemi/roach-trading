package io.roach.trading.domain.changefeed;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class AbstractChangeEvent<T extends Fields<ID>, ID> {
    @JsonProperty("payload")
    private List<Payload<T, ID>> payload;

    @JsonProperty("resolved")
    private String resolved;

    @JsonProperty("length")
    private int length;

    public List<Payload<T,ID>> getPayload() {
        return payload;
    }

    public String getResolved() {
        return resolved;
    }

    public int getLength() {
        return length;
    }

    public Optional<LogicalTimestamp> getResolvedTimestamp() {
        return resolved != null ? Optional.of(LogicalTimestamp.parse(resolved)) : Optional.empty();
    }
}
