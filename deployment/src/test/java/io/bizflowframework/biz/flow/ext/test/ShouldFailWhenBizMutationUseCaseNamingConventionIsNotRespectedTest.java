package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.runtime.usecase.BizMutationUseCase;
import io.bizflowframework.biz.flow.ext.runtime.usecase.CommandRequest;
import io.quarkus.test.QuarkusUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

public class ShouldFailWhenBizMutationUseCaseNamingConventionIsNotRespectedTest {

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .withApplicationRoot(jar -> jar
                    .addClasses(CreateTodoBizMutationUseCaseBadNaming.class, Todo.class, CreateTodoCommandRequest.class,
                            CreateTodoBizMutationUseCaseException.class)
                    .addAsResource("application.properties")
                    .addAsResource("init.sql"))
            .assertException(throwable -> assertThat(throwable)
                    .hasNoSuppressedExceptions()
                    .rootCause()
                    .hasMessage("Bad naming for 'CreateTodoBizMutationUseCaseBadNaming', must end with 'BizMutationUseCase'")
                    .hasNoSuppressedExceptions());

    @Test
    public void test() {
        Assertions.fail("Startup should have failed");
    }

    private static final class CreateTodoBizMutationUseCaseBadNaming implements BizMutationUseCase<Todo, CreateTodoCommandRequest, CreateTodoBizMutationUseCaseException> {

        @Override
        public Todo execute(final CreateTodoCommandRequest request) throws CreateTodoBizMutationUseCaseException {
            return null;
        }
    }

    private static final class Todo {
    }

    private record CreateTodoCommandRequest() implements CommandRequest {
    }

    private static final class CreateTodoBizMutationUseCaseException extends Exception {
    }
}
