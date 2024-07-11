package io.bizflowframework.biz.flow.ext.runtime.eventsourcing.api;

import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.EventStoreException;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.UnknownAggregateRootAtVersionException;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.UnknownAggregateRootException;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.serde.MissingSerdeException;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.sql.SQLException;

public final class ExceptionsMapper {
    private static final String VND_EVENT_STORE_ERROR_V1_TXT = "application/vnd.event-store-error-v1+txt";
    private static final String VND_AGGREGATE_ROOT_ERROR_V1_TXT = "application/vnd.aggregate-root-error-v1+txt";

    @ServerExceptionMapper
    public Response mapException(final EventStoreException exception) {
        final Response.Status status = exception.isForbidden() ? Response.Status.FORBIDDEN : Response.Status.INTERNAL_SERVER_ERROR;
        return Response.status(status)
                .type(VND_EVENT_STORE_ERROR_V1_TXT)
                .entity(exception.getCause().getMessage())
                .build();
    }

    @ServerExceptionMapper
    public Response mapException(final SQLException exception) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(VND_EVENT_STORE_ERROR_V1_TXT)
                .entity(exception.getMessage())
                .build();
    }

    @ServerExceptionMapper
    public Response mapException(final MissingSerdeException exception) {
        return Response.status(Response.Status.NOT_IMPLEMENTED)
                .type(VND_AGGREGATE_ROOT_ERROR_V1_TXT)
                .entity(String.format("Missing Serde for aggregate root type '%s' and event type '%s'",
                        exception.getAggregateType().type(),
                        exception.getEventType().type()))
                .build();
    }

    @ServerExceptionMapper
    public Response mapException(final UnknownAggregateRootAtVersionException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .type(VND_AGGREGATE_ROOT_ERROR_V1_TXT)
                .entity(String.format("Unknown aggregate root id '%s' of type '%s' at version '%d'",
                        exception.getAggregateRootIdentifier().aggregateId().id(),
                        exception.getAggregateRootIdentifier().aggregateType().type(),
                        exception.getAggregateVersion().version()))
                .build();
    }

    @ServerExceptionMapper
    public Response mapException(final UnknownAggregateRootException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .type(VND_AGGREGATE_ROOT_ERROR_V1_TXT)
                .entity(String.format("Unknown aggregate root id '%s' of type '%s'",
                        exception.getAggregateRootIdentifier().aggregateId().id(),
                        exception.getAggregateRootIdentifier().aggregateType().type()))
                .build();
    }

}
