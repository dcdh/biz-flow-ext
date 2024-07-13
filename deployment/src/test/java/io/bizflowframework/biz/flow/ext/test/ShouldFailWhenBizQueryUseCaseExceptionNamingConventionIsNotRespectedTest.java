package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.runtime.usecase.BizQueryUseCase;
import io.bizflowframework.biz.flow.ext.runtime.usecase.ListOfProjection;
import io.bizflowframework.biz.flow.ext.test.query.QueryTodoProjection;
import io.bizflowframework.biz.flow.ext.test.usecase.ListTodoQueryRequest;
import io.quarkus.test.QuarkusUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

public class ShouldFailWhenBizQueryUseCaseExceptionNamingConventionIsNotRespectedTest {

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .withApplicationRoot(jar -> jar
                    .addClasses(ListTodoBizQueryUseCase.class,
                            QueryTodoProjection.class,
                            ListTodoQueryRequest.class,
                            ListTodoBizQueryUseCaseBadNamingException.class)
                    .addAsResource("application.properties")
                    .addAsResource("init.sql"))
            .assertException(throwable -> assertThat(throwable)
                    .hasNoSuppressedExceptions()
                    .rootCause()
                    .hasMessage("'io.bizflowframework.biz.flow.ext.test.ShouldFailWhenBizQueryUseCaseExceptionNamingConventionIsNotRespectedTest$ListTodoBizQueryUseCase' execute method must define an exception called 'ListTodoBizQueryUseCaseException' got 'ListTodoBizQueryUseCaseBadNamingException'")
                    .hasNoSuppressedExceptions());

    @Test
    public void test() {
        Assertions.fail("Startup should have failed");
    }

    private static final class ListTodoBizQueryUseCase implements BizQueryUseCase<ListOfProjection<QueryTodoProjection>, ListTodoQueryRequest, ListTodoBizQueryUseCaseBadNamingException> {

        @Override
        public ListOfProjection<QueryTodoProjection> execute(final ListTodoQueryRequest request) throws ListTodoBizQueryUseCaseBadNamingException {
            throw new IllegalStateException("Should not be called");
        }
    }

    private static final class ListTodoBizQueryUseCaseBadNamingException extends Exception {
    }

}
