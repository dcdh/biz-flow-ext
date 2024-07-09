package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.runtime.usecase.BizMutationUseCase;
import io.bizflowframework.biz.flow.ext.runtime.usecase.CommandRequest;
import io.quarkus.test.QuarkusUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

public class ShouldFailWhenBizMutationUseCaseImplementsMultipleExceptionsTest {

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .withApplicationRoot(jar -> jar
                    .addClasses(CreateTodoBizMutationUseCase.class, Todo.class, CreateTodoCommandRequest.class,
                            CreateTodoBizMutationUseCaseException.class,
                            AnotherUnwantedException.class)
                    .addAsResource("application.properties")
                    .addAsResource("init.sql"))
            .assertException(throwable -> assertThat(throwable)
                    .hasNoSuppressedExceptions()
                    .rootCause()
                    .hasMessage("'io.bizflowframework.biz.flow.ext.test.ShouldFailWhenBizMutationUseCaseImplementsMultipleExceptionsTest$CreateTodoBizMutationUseCase' execute method must define only one exception called 'CreateTodoBizMutationUseCaseException'")
                    .hasNoSuppressedExceptions());

    @Test
    public void test() {
        Assertions.fail("Startup should have failed");
    }

    private static final class CreateTodoBizMutationUseCase implements BizMutationUseCase<Todo, CreateTodoCommandRequest, CreateTodoBizMutationUseCaseException> {

        @Override
        public Todo execute(CreateTodoCommandRequest request) throws CreateTodoBizMutationUseCaseException, AnotherUnwantedException {
            return null;
        }
    }

    private static final class Todo {
    }

    private record CreateTodoCommandRequest() implements CommandRequest {
    }

    private static final class CreateTodoBizMutationUseCaseException extends Exception {
    }

    private static final class AnotherUnwantedException extends RuntimeException {
    }

}
