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

public class ShouldFailWhenBizQueryUseCaseImplementsMultipleExceptionsTest {

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .withApplicationRoot(jar -> jar
                    .addClasses(ListTodoBizQueryUseCase.class, Todo.class, ListTodoQueryRequest.class,
                            ListTodoBizQueryUseCaseBadNamingException.class,
                            AnotherUnwantedException.class)
                    .addAsResource("application.properties")
                    .addAsResource("init.sql"))
            .assertException(throwable -> assertThat(throwable)
                    .hasNoSuppressedExceptions()
                    .rootCause()
                    .hasMessage("'io.bizflowframework.biz.flow.ext.test.ShouldFailWhenBizQueryUseCaseImplementsMultipleExceptionsTest$ListTodoBizQueryUseCase' execute method must define only one exception called 'ListTodoBizQueryUseCaseException'")
                    .hasNoSuppressedExceptions());

    @Test
    public void test() {
        Assertions.fail("Startup should have failed");
    }

    private static final class ListTodoBizQueryUseCase implements BizQueryUseCase<Todo, ListTodoQueryRequest, ListTodoBizQueryUseCaseBadNamingException> {

        @Override
        public Todo execute(final ListTodoQueryRequest request) throws ListTodoBizQueryUseCaseBadNamingException, AnotherUnwantedException {
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

    private record ListTodoQueryRequest() implements QueryRequest {
    }

    private static final class ListTodoBizQueryUseCaseBadNamingException extends Exception {
    }

    private static final class AnotherUnwantedException extends RuntimeException {
    }
}
