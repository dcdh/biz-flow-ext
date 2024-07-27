package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.test.usecase.CreateTodoBizMutationUseCase;
import io.bizflowframework.biz.flow.ext.test.usecase.CreateTodoBizMutationUseCaseException;
import io.bizflowframework.biz.flow.ext.test.usecase.CreateTodoCommandRequest;
import io.quarkus.test.QuarkusUnitTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

public class BizMutationUseCaseTest {

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .withApplicationRoot(jar -> jar
                    .addClasses(CreateTodoBizMutationUseCase.class,
                            TodoId.class,
                            TodoAggregateRoot.class,
                            CreateTodoCommandRequest.class,
                            CreateTodoBizMutationUseCaseException.class,
                            StubbedDefaultCreatedAtProvider.class)
                    .addAsResource("application.properties")
                    .addAsResource("init.sql"));

    @Inject
    CreateTodoBizMutationUseCase createTodoBizMutationUseCase;

    @Test
    public void shouldReturnATodo() throws CreateTodoBizMutationUseCaseException {
        assertThat(createTodoBizMutationUseCase.execute(new CreateTodoCommandRequest())).isNotNull();
    }

}
