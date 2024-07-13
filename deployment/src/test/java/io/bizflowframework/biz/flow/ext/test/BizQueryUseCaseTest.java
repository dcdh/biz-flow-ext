package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.test.query.QueryTodoProjection;
import io.bizflowframework.biz.flow.ext.test.usecase.ListTodoBizQueryUseCase;
import io.bizflowframework.biz.flow.ext.test.usecase.ListTodoBizQueryUseCaseException;
import io.bizflowframework.biz.flow.ext.test.usecase.ListTodoQueryRequest;
import io.quarkus.test.QuarkusUnitTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

public class BizQueryUseCaseTest {

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .withApplicationRoot(jar -> jar
                    .addClasses(ListTodoBizQueryUseCase.class,
                            QueryTodoProjection.class,
                            ListTodoQueryRequest.class,
                            ListTodoBizQueryUseCaseException.class)
                    .addAsResource("application.properties")
                    .addAsResource("init.sql"));

    @Inject
    ListTodoBizQueryUseCase listTodoBizQueryUseCase;

    @Test
    public void test() throws ListTodoBizQueryUseCaseException {
        assertThat(listTodoBizQueryUseCase.execute(new ListTodoQueryRequest())).isNotNull();
    }

}
