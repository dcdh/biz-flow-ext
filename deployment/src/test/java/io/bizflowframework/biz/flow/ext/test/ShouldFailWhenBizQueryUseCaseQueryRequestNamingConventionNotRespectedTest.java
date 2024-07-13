package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.runtime.usecase.BizQueryUseCase;
import io.bizflowframework.biz.flow.ext.runtime.usecase.ListOfProjection;
import io.bizflowframework.biz.flow.ext.runtime.usecase.QueryRequest;
import io.bizflowframework.biz.flow.ext.test.query.QueryTodoProjection;
import io.bizflowframework.biz.flow.ext.test.usecase.ListTodoBizQueryUseCaseException;
import io.quarkus.test.QuarkusUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

public class ShouldFailWhenBizQueryUseCaseQueryRequestNamingConventionNotRespectedTest {

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .withApplicationRoot(jar -> jar
                    .addClasses(ListTodoBizQueryUseCase.class,
                            QueryTodoProjection.class,
                            ListTodoBadNamingQueryRequest.class,
                            ListTodoBizQueryUseCaseException.class)
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

    private static final class ListTodoBizQueryUseCase implements BizQueryUseCase<ListOfProjection<QueryTodoProjection>, ListTodoBadNamingQueryRequest, ListTodoBizQueryUseCaseException> {

        @Override
        public ListOfProjection<QueryTodoProjection> execute(final ListTodoBadNamingQueryRequest request) throws ListTodoBizQueryUseCaseException {
            throw new IllegalStateException("Should not be called");
        }
    }

    private record ListTodoBadNamingQueryRequest() implements QueryRequest {
    }

}
