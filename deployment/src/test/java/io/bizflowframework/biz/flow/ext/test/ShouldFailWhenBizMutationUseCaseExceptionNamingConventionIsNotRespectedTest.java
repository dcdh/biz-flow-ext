package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.runtime.usecase.BizMutationUseCase;
import io.bizflowframework.biz.flow.ext.test.usecase.CreateTodoCommandRequest;
import io.quarkus.test.QuarkusUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

public class ShouldFailWhenBizMutationUseCaseExceptionNamingConventionIsNotRespectedTest {

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .withApplicationRoot(jar -> jar
                    .addClasses(CreateTodoBizMutationUseCase.class,
                            TodoId.class,
                            TodoAggregateRoot.class,
                            CreateTodoCommandRequest.class,
                            CreateTodoBizMutationUseCaseBadNamingException.class)
                    .addAsResource("application.properties")
                    .addAsResource("init.sql"))
            .assertException(throwable -> assertThat(throwable)
                    .hasNoSuppressedExceptions()
                    .rootCause()
                    .hasMessage("'io.bizflowframework.biz.flow.ext.test.ShouldFailWhenBizMutationUseCaseExceptionNamingConventionIsNotRespectedTest$CreateTodoBizMutationUseCase' execute method must define an exception called 'CreateTodoBizMutationUseCaseException' got 'CreateTodoBizMutationUseCaseBadNamingException'")
                    .hasNoSuppressedExceptions());

    @Test
    public void test() {
        Assertions.fail("Startup should have failed");
    }

    private static final class CreateTodoBizMutationUseCase implements BizMutationUseCase<TodoAggregateRoot, CreateTodoCommandRequest, CreateTodoBizMutationUseCaseBadNamingException> {

        @Override
        public TodoAggregateRoot execute(final CreateTodoCommandRequest request) throws CreateTodoBizMutationUseCaseBadNamingException {
            throw new IllegalStateException("Should not be called");
        }
    }

    private static final class CreateTodoBizMutationUseCaseBadNamingException extends Exception {
    }
}
