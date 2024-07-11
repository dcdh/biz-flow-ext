package io.bizflowframework.biz.flow.ext.runtime.eventsourcing.serde;

import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.Objects;

public record SerializedEventPayload(String payload) implements Serializable {
    public SerializedEventPayload {
        Objects.requireNonNull(payload);
    }

    public Reader reader() {
        return new StringReader(payload);
    }
}
