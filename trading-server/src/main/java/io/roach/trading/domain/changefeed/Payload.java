package io.roach.trading.domain.changefeed;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * When a changefeed is created with the "diff" option, then it will include a
 * "before" element for a DELETE with the state of the row. If an UPDATE is sent,
 * then it will include both "before" and "after". For an INSERT, there is only
 * ""after" but no "before" element, which is how you can separate it from
 * a DELETE.
 * <p>
 * CREATE CHANGEFEED FOR TABLE xyz INTO 'kafka://localhost:9092' WITH updated,resolved = '15s',diff;
 * <p>
 * To summarize, with the diff option expect:
 * - INSERT (before null, after included)
 * - UPDATE (before and after included)
 * - DELETE (before included, after null)
 *
 * @param <ID> identifier type
 */
public class Payload<T extends Fields<ID>, ID> {
    private List<String> key = new ArrayList<>();

    @JsonProperty("href")
    private String href;

    @JsonProperty("topic")
    private String topic;

    @JsonProperty("updated")
    private String updated;

    @JsonProperty("before")
    private T before;

    @JsonProperty("after")
    private T after;

    public List<String> getKey() {
        return key;
    }

    public String getHref() {
        return href;
    }

    public String getTopic() {
        return topic;
    }

    public String getUpdated() {
        return updated;
    }

    public Optional<T> getBefore() {
        return Optional.ofNullable(before);
    }

    public Optional<T> getAfter() {
        return Optional.ofNullable(after);
    }

    public Optional<LogicalTimestamp> getUpdatedTimestamp() {
        return updated != null ? Optional.of(LogicalTimestamp.parse(updated)) : Optional.empty();
    }

    public ID getId() {
        Optional<T> b = getBefore();
        Optional<T> a = getAfter();
        return a.isPresent() ? after.getId() : b.isPresent() ? before.getId() : null;
    }

    public Operation getOperation() {
        Optional<T> b = getBefore();
        Optional<T> a = getAfter();
        return !b.isPresent() && a.isPresent()
                ? Operation.insert : b.isPresent() && a.isPresent()
                ? Operation.update : Operation.delete;
    }

    @Override
    public String toString() {
        return "Payload{" +
                "key=" + key +
                ", href='" + href + '\'' +
                ", topic='" + topic + '\'' +
                ", updated='" + updated + '\'' +
                ", before=" + before +
                ", after=" + after +
                '}';
    }
}

