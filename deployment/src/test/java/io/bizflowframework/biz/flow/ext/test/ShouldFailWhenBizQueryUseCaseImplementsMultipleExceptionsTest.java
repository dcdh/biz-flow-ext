package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.runtime.usecase.BizQueryUseCase;
import io.bizflowframework.biz.flow.ext.runtime.usecase.ListOfProjection;
import io.bizflowframework.biz.flow.ext.test.query.QueryTodoProjection;
import io.bizflowframework.biz.flow.ext.test.usecase.ListTodoBizQueryUseCaseException;
import io.bizflowframework.biz.flow.ext.test.usecase.ListTodoQueryRequest;
import io.quarkus.test.QuarkusUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

public class ShouldFailWhenBizQueryUseCaseImplementsMultipleExceptionsTest {

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .withApplicationRoot(jar -> jar
                    .addClasses(ListTodoBizQueryUseCase.class,
                            QueryTodoProjection.class,
                            ListTodoQueryRequest.class,
                            ListTodoBizQueryUseCaseException.class,
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

    private static final class ListTodoBizQueryUseCase implements BizQueryUseCase<ListOfProjection<QueryTodoProjection>, ListTodoQueryRequest, ListTodoBizQueryUseCaseException> {

        @Override
        public ListOfProjection<QueryTodoProjection> execute(final ListTodoQueryRequest request) throws ListTodoBizQueryUseCaseException, AnotherUnwantedException {
            throw new IllegalStateException("Should not be called");
        }
    }

    private static final class AnotherUnwantedException extends RuntimeException {
    }
}
