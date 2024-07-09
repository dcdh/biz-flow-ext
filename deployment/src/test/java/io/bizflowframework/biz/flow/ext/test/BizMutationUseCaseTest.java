package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.runtime.usecase.BizMutationUseCase;
import io.bizflowframework.biz.flow.ext.runtime.usecase.CommandRequest;
import io.quarkus.test.QuarkusUnitTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

public class BizMutationUseCaseTest {

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .withApplicationRoot(jar -> jar
                    .addClasses(CreateTodoBizMutationUseCase.class, Todo.class, CreateTodoCommandRequest.class,
                            CreateTodoBizMutationUseCaseException.class)
                    .addAsResource("application.properties")
                    .addAsResource("init.sql"));

    @Inject
    CreateTodoBizMutationUseCase createTodoBizMutationUseCase;

    @Test
    public void shouldReturnATodo() throws CreateTodoBizMutationUseCaseException {
        assertThat(createTodoBizMutationUseCase.execute(new CreateTodoCommandRequest())).isNotNull();
    }

    private static final class CreateTodoBizMutationUseCase implements BizMutationUseCase<Todo, CreateTodoCommandRequest, CreateTodoBizMutationUseCaseException> {

        @Override
        public Todo execute(final CreateTodoCommandRequest request) throws CreateTodoBizMutationUseCaseException {
            return new Todo();
        }
    }

    private static final class Todo {
    }

    private record CreateTodoCommandRequest() implements CommandRequest {
    }

    private static final class CreateTodoBizMutationUseCaseException extends Exception {
    }
}
