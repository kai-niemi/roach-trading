package io.roach.trading.domain.changefeed;

import java.util.List;
import java.util.UUID;

public interface ChangeFeedListener {
    void onProductChangeEvent(List<Payload<ProductFields, UUID>> payload);
}
