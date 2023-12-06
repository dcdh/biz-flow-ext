package io.bizflowframework.biz.flow.ext.runtime;

import io.quarkus.arc.DefaultBean;
import jakarta.inject.Singleton;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Singleton
@DefaultBean
public final class DefaultCreatedAtProvider implements CreatedAtProvider {
    @Override
    public CreatedAt now() {
        return new CreatedAt(LocalDateTime.now(ZoneOffset.UTC));
    }
}
