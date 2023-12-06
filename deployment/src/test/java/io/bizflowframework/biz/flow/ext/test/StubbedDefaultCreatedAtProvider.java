package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.runtime.CreatedAt;
import io.bizflowframework.biz.flow.ext.runtime.CreatedAtProvider;
import io.bizflowframework.biz.flow.ext.runtime.DefaultCreatedAtProvider;
import jakarta.inject.Singleton;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

// @InjectMock or @InjectSpy CreatedAtProvider createdAtProvider; return null for createdAtProvider property :(
// Need to provide own custom wrapper to force the response when needed and use the DefaultImplementation instead :)
@Singleton
public final class StubbedDefaultCreatedAtProvider implements CreatedAtProvider {
    private final Queue<CreatedAt> stubbedResponses = new ConcurrentLinkedQueue<>();
    private final DefaultCreatedAtProvider defaultCreatedAtProvider;

    StubbedDefaultCreatedAtProvider(final DefaultCreatedAtProvider defaultCreatedAtProvider) {
        this.defaultCreatedAtProvider = Objects.requireNonNull(defaultCreatedAtProvider);
    }

    public StubbedDefaultCreatedAtProvider addResponse(final CreatedAt response) {
        this.stubbedResponses.add(response);
        return this;
    }

    @Override
    public CreatedAt now() {
        if (stubbedResponses.isEmpty()) {
            return this.defaultCreatedAtProvider.now();
        }
        return stubbedResponses.poll();
    }
}