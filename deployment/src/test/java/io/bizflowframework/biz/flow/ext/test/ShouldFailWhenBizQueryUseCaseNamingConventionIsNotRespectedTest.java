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

public class ShouldFailWhenBizQueryUseCaseNamingConventionIsNotRespectedTest {

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .withApplicationRoot(jar -> jar
                    .addClasses(ListTodoBizQueryUseCaseBadNaming.class,
                            QueryTodoProjection.class,
                            ListTodoQueryRequest.class,
                            ListTodoBizQueryUseCaseException.class)
                    .addAsResource("application.properties")
                    .addAsResource("init.sql"))
            .assertException(throwable -> assertThat(throwable)
                    .hasNoSuppressedExceptions()
                    .rootCause()
                    .hasMessage("Bad naming for 'ListTodoBizQueryUseCaseBadNaming', must end with 'BizQueryUseCase'")
                    .hasNoSuppressedExceptions());

    @Test
    public void test() {
        Assertions.fail("Startup should have failed");
    }

    private static final class ListTodoBizQueryUseCaseBadNaming implements BizQueryUseCase<ListOfProjection<QueryTodoProjection>, ListTodoQueryRequest, ListTodoBizQueryUseCaseException> {

        @Override
        public ListOfProjection<QueryTodoProjection> execute(final ListTodoQueryRequest request) throws ListTodoBizQueryUseCaseException {
            throw new IllegalStateException("Should not be called");
        }
    }

}
