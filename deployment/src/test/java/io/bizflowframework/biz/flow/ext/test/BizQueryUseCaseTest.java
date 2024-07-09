package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.runtime.AggregateId;
import io.bizflowframework.biz.flow.ext.runtime.AggregateVersion;
import io.bizflowframework.biz.flow.ext.runtime.usecase.BizQueryUseCase;
import io.bizflowframework.biz.flow.ext.runtime.usecase.QueryRequest;
import io.bizflowframework.biz.flow.ext.runtime.usecase.VersionedProjection;
import io.quarkus.test.QuarkusUnitTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

public class BizQueryUseCaseTest {

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .withApplicationRoot(jar -> jar
                    .addClasses(ListTodoBizQueryUseCase.class, Todo.class, ListTodoQueryRequest.class,
                            ListTodoBizQueryUseCaseException.class)
                    .addAsResource("application.properties")
                    .addAsResource("init.sql"));

    @Inject
    ListTodoBizQueryUseCase listTodoBizQueryUseCase;

    @Test
    public void test() throws ListTodoBizQueryUseCaseException {
        assertThat(listTodoBizQueryUseCase.execute(new ListTodoQueryRequest())).isNotNull();
    }

    private static final class ListTodoBizQueryUseCase implements BizQueryUseCase<Todo, ListTodoQueryRequest, ListTodoBizQueryUseCaseException> {

        @Override
        public Todo execute(final ListTodoQueryRequest request) throws ListTodoBizQueryUseCaseException {
            return new Todo();
        }
    }

    private static final class Todo implements VersionedProjection {
        @Override
        public AggregateId aggregateId() {
            throw new IllegalStateException("Should not be called !");
        }

        @Override
        public AggregateVersion aggregateVersion() {
            throw new IllegalStateException("Should not be called !");
        }
    }

    private record ListTodoQueryRequest() implements QueryRequest {
    }

    private static final class ListTodoBizQueryUseCaseException extends Exception {
    }

}
