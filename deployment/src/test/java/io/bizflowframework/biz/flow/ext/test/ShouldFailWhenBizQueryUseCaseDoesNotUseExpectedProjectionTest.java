package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.runtime.usecase.BizQueryUseCase;
import io.bizflowframework.biz.flow.ext.runtime.usecase.Projection;
import io.bizflowframework.biz.flow.ext.runtime.usecase.QueryRequest;
import io.quarkus.test.QuarkusUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

public class ShouldFailWhenBizQueryUseCaseDoesNotUseExpectedProjectionTest {

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .withApplicationRoot(jar -> jar
                    .addClasses(ListTodoBizQueryUseCase.class, Todo.class, ListTodoQueryRequest.class,
                            ListTodoBizQueryUseCaseException.class)
                    .addAsResource("application.properties")
                    .addAsResource("init.sql"))
            .assertException(throwable -> assertThat(throwable)
                    .hasNoSuppressedExceptions()
                    .rootCause()
                    .hasMessage("BizQueryUseCase 'io.bizflowframework.biz.flow.ext.test.ShouldFailWhenBizQueryUseCaseDoesNotUseExpectedProjectionTest$ListTodoBizQueryUseCase' projection must implement VersionedProjection or be a ListOfProjection")
                    .hasNoSuppressedExceptions());

    @Test
    public void test() {
        Assertions.fail("Startup should have failed");
    }

    private static final class ListTodoBizQueryUseCase implements BizQueryUseCase<Todo, ListTodoQueryRequest, ListTodoBizQueryUseCaseException> {

        @Override
        public Todo execute(final ListTodoQueryRequest request) throws ListTodoBizQueryUseCaseException {
            return null;
        }
    }

    private static final class Todo implements Projection {
    }

    private record ListTodoQueryRequest() implements QueryRequest {
    }

    private static final class ListTodoBizQueryUseCaseException extends Exception {
    }
}
