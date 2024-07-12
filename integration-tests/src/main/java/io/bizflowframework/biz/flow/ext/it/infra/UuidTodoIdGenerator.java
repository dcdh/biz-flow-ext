package io.bizflowframework.biz.flow.ext.it.infra;

import io.bizflowframework.biz.flow.ext.it.domain.TodoId;
import io.bizflowframework.biz.flow.ext.it.domain.TodoIdGenerator;
import jakarta.inject.Singleton;

import java.util.UUID;

@Singleton
public class UuidTodoIdGenerator implements TodoIdGenerator {
    @Override
    public TodoId generate() {
        return new TodoId(UUID.randomUUID().toString());
    }
}
