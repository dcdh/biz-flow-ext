package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.runtime.usecase.BizMutationUseCase;
import io.bizflowframework.biz.flow.ext.test.usecase.CreateTodoBizMutationUseCaseException;
import io.bizflowframework.biz.flow.ext.test.usecase.CreateTodoCommandRequest;
import io.quarkus.test.QuarkusUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

public class ShouldFailWhenBizMutationUseCaseNamingConventionIsNotRespectedTest {

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .withApplicationRoot(jar -> jar
                    .addClasses(CreateTodoBizMutationUseCaseBadNaming.class,
                            TodoId.class,
                            TodoAggregateRoot.class,
                            CreateTodoCommandRequest.class,
                            CreateTodoBizMutationUseCaseException.class)
                    .addAsResource("application.properties")
                    .addAsResource("init.sql"))
            .assertException(throwable -> assertThat(throwable)
                    .hasNoSuppressedExceptions()
                    .rootCause()
                    .hasMessage("Bad naming for 'io.bizflowframework.biz.flow.ext.test.ShouldFailWhenBizMutationUseCaseNamingConventionIsNotRespectedTest$CreateTodoBizMutationUseCaseBadNaming', must end with 'BizMutationUseCase'")
                    .hasNoSuppressedExceptions());

    @Test
    public void test() {
        Assertions.fail("Startup should have failed");
    }

    private static final class CreateTodoBizMutationUseCaseBadNaming implements BizMutationUseCase<TodoAggregateRoot, CreateTodoCommandRequest, CreateTodoBizMutationUseCaseException> {

        @Override
        public TodoAggregateRoot execute(final CreateTodoCommandRequest request) throws CreateTodoBizMutationUseCaseException {
            throw new IllegalStateException("Should not be called");
        }
    }

}
