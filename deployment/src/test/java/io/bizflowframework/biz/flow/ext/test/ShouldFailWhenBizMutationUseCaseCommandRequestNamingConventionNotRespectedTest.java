package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.runtime.usecase.BizMutationUseCase;
import io.bizflowframework.biz.flow.ext.runtime.usecase.CommandRequest;
import io.quarkus.test.QuarkusUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

public class ShouldFailWhenBizMutationUseCaseCommandRequestNamingConventionNotRespectedTest {

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .withApplicationRoot(jar -> jar
                    .addClasses(CreateTodoBizMutationUseCase.class, Todo.class, CreateTodoBadNamingCommandRequest.class,
                            CreateTodoBizMutationUseCaseException.class)
                    .addAsResource("application.properties")
                    .addAsResource("init.sql"))
            .assertException(throwable -> assertThat(throwable)
                    .hasNoSuppressedExceptions()
                    .rootCause()
                    .hasMessage("Bad naming for command request 'CreateTodoBadNamingCommandRequest', expected 'CreateTodoCommandRequest'")
                    .hasNoSuppressedExceptions());

    @Test
    public void test() {
        Assertions.fail("Startup should have failed");
    }

    private static final class CreateTodoBizMutationUseCase implements BizMutationUseCase<Todo, CreateTodoBadNamingCommandRequest, CreateTodoBizMutationUseCaseException> {

        @Override
        public Todo execute(final CreateTodoBadNamingCommandRequest request) throws CreateTodoBizMutationUseCaseException {
            return null;
        }
    }

    private static final class Todo {
    }

    private record CreateTodoBadNamingCommandRequest() implements CommandRequest {
    }

    private static final class CreateTodoBizMutationUseCaseException extends Exception {
    }
}
