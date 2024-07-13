package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.runtime.AggregateId;
import io.bizflowframework.biz.flow.ext.runtime.AggregateVersion;
import io.bizflowframework.biz.flow.ext.runtime.usecase.BizQueryUseCase;
import io.bizflowframework.biz.flow.ext.runtime.usecase.QueryRequest;
import io.bizflowframework.biz.flow.ext.runtime.usecase.VersionedProjection;
import io.quarkus.test.QuarkusUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

public class ShouldFailWhenBizQueryUseCaseQueryRequestNamingConventionNotRespectedTest {

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .withApplicationRoot(jar -> jar
                    .addClasses(ListTodoBizQueryUseCase.class, Todo.class, ListTodoBadNamingQueryRequest.class, ListTodoBizQueryUseCaseException.class)
                    .addAsResource("application.properties")
                    .addAsResource("init.sql"))
            .assertException(throwable -> assertThat(throwable)
                    .hasNoSuppressedExceptions()
                    .rootCause()
                    .hasMessage("Bad naming for query request 'ListTodoBadNamingQueryRequest', expected 'ListTodoQueryRequest'")
                    .hasNoSuppressedExceptions());

    @Test
    public void test() {
        Assertions.fail("Startup should have failed");
    }

    private static final class ListTodoBizQueryUseCase implements BizQueryUseCase<Todo, ListTodoBadNamingQueryRequest, ListTodoBizQueryUseCaseException> {

        @Override
        public Todo execute(final ListTodoBadNamingQueryRequest request) throws ListTodoBizQueryUseCaseException {
            return null;
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

    private record ListTodoBadNamingQueryRequest() implements QueryRequest {
    }

    private static final class ListTodoBizQueryUseCaseException extends Exception {
    }
}
