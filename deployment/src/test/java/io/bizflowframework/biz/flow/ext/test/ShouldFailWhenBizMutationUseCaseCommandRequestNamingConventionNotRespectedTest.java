package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.runtime.usecase.BizMutationUseCase;
import io.bizflowframework.biz.flow.ext.runtime.usecase.CommandRequest;
import io.bizflowframework.biz.flow.ext.test.usecase.CreateTodoBizMutationUseCaseException;
import io.quarkus.test.QuarkusUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

public class ShouldFailWhenBizMutationUseCaseCommandRequestNamingConventionNotRespectedTest {

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .withApplicationRoot(jar -> jar
                    .addClasses(CreateTodoBizMutationUseCase.class,
                            TodoId.class,
                            TodoAggregateRoot.class,
                            CreateTodoBadNamingCommandRequest.class,
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

    private static final class CreateTodoBizMutationUseCase implements BizMutationUseCase<TodoAggregateRoot, CreateTodoBadNamingCommandRequest, CreateTodoBizMutationUseCaseException> {

        @Override
        public TodoAggregateRoot execute(final CreateTodoBadNamingCommandRequest request) throws CreateTodoBizMutationUseCaseException {
            throw new IllegalStateException("Should not be called");
        }
    }

    private record CreateTodoBadNamingCommandRequest() implements CommandRequest {
    }
}
