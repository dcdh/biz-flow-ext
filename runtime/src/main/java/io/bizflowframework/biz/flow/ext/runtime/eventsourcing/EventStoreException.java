package io.bizflowframework.biz.flow.ext.runtime.eventsourcing;

import java.sql.SQLException;
import java.util.List;

public final class EventStoreException extends RuntimeException {
    public EventStoreException(final SQLException cause) {
        super(cause);
    }

    public boolean isForbidden() {
        final String causeMessage = getCause().getMessage();
        if (causeMessage == null) {
            return false;
        }
        final List<String> forbiddenMessages = List.of(
                "ERROR: not allowed",
                "ERROR: Event already present while should not be",
                "ERROR: Previous event version mismatch"
        );
        return forbiddenMessages.stream()
                .anyMatch(causeMessage::contains);
    }
}
