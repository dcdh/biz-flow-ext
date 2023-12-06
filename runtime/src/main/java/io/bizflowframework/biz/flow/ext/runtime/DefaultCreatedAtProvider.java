package io.bizflowframework.biz.flow.ext.runtime;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public final class DefaultCreatedAtProvider implements CreatedAtProvider {
    @Override
    public CreatedAt now() {
        return new CreatedAt(LocalDateTime.now(ZoneOffset.UTC));
    }
}
