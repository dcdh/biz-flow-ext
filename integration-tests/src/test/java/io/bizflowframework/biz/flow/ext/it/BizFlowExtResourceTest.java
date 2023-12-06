package io.bizflowframework.biz.flow.ext.it;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class BizFlowExtResourceTest {
    @Nested
    public class DefaultErrors {
        @Test
        public void shouldReturnExpectedResponseWhenSerdeIsMissing() {
            given()
                    .when().get("/todo/failMissingSerde")
                    .then()
                    .log().all()
                    .statusCode(501)
                    .contentType("application/vnd.aggregate-root-error-v1+json")
                    .body(is("Missing Serde for aggregate root type 'TodoAggregateRoot' and event type 'UnknownTodoEvent'"));
        }

        @Test
        public void shouldReturnExpectedResponseWhenAggregateRootIsUnknown() {
            given()
                    .when().get("/todo/failUnknownAggregate")
                    .then()
                    .log().all()
                    .statusCode(404)
                    .contentType("application/vnd.aggregate-root-error-v1+json")
                    .body(is("Unknown aggregate root id 'unknown' of type 'TodoAggregateRoot'"));
        }

        @Test
        public void shouldReturnExpectedResponseWhenAggregateRootAtVersionIsUnknown() {
            given()
                    .when().get("/todo/failUnknownAggregateAtVersion")
                    .then()
                    .log().all()
                    .statusCode(404)
                    .contentType("application/vnd.aggregate-root-error-v1+json")
                    .body(is("Unknown aggregate root id 'todo_to_fail_at_unknown_version' of type 'TodoAggregateRoot' at version '10'"));
        }

        @Test
        public void shouldReturnExpectedResponseWhenDoingAForbiddenAction() {
            given()
                    .when().get("/todo/failOnActionForbidden")
                    .then()
                    .log().all()
                    .statusCode(403)
                    .contentType("application/vnd.event-store-error-v1+json")
                    .body(containsString("ERROR: not allowed"));
        }
    }

    @Nested
    public class TodoHappyPathLifecycle {
        public static String todoId;

        @Test
        @Order(1)
        public void shouldCreateTodo() {
            todoId = given()
                    .formParam("description", "lorem ipsum dolor sit amet")
                    .when().post("/todo/createNewTodo")
                    .then()
                    .log().all()
                    .statusCode(200)
                    .contentType("application/vnd.todo-v1+json")
                    .body("todoId", notNullValue())
                    .body("description", equalTo("lorem ipsum dolor sit amet"))
                    .body("status", equalTo("IN_PROGRESS"))
                    .body("version", equalTo(0))
                    .extract().body().path("todoId");
        }

        @Test
        @Order(2)
        public void shouldMarkTodoAsCompleted() {
            Objects.requireNonNull(todoId);
            given()
                    .formParam("todoId", todoId)
                    .when().post("/todo/markTodoAsCompleted")
                    .then()
                    .log().all()
                    .statusCode(200)
                    .contentType("application/vnd.todo-v1+json")
                    .body("todoId", equalTo(todoId))
                    .body("description", equalTo("lorem ipsum dolor sit amet"))
                    .body("status", equalTo("COMPLETED"))
                    .body("version", equalTo(1));
        }
    }

    @Nested
    public class TodoFailurePath {
        @Test
        public void shouldFailWhenTodoIsUnknown() {
            given()
                    .formParam("todoId", "my_second_awesome_todo")
                    .when().post("/todo/markTodoAsCompleted")
                    .then()
                    .log().all()
                    .statusCode(404)
                    .contentType("application/vnd.unknown-todo-v1+json")
                    .body(is("Todo 'my_second_awesome_todo' is unknown"));
        }

        @Test
        public void shouldFailWhenTodoIsAlreadyMarkedAsCompleted() {
            // Given
            final String todoId;
            todoId = given()
                    .formParam("description", "lorem ipsum dolor sit amet")
                    .when().post("/todo/createNewTodo")
                    .then()
                    .log().all()
                    .statusCode(200)
                    .extract().body().path("todoId");
            Objects.requireNonNull(todoId);
            given()
                    .formParam("todoId", todoId)
                    .when().post("/todo/markTodoAsCompleted")
                    .then()
                    .log().all()
                    .statusCode(200);

            // When && Then
            given()
                    .formParam("todoId", todoId)
                    .when().post("/todo/markTodoAsCompleted")
                    .then()
                    .log().all()
                    .statusCode(409)
                    .contentType("application/vnd.todo-already-marked-as-completed-v1+json")
                    .body(is(String.format("Todo '%s' already marked as completed", todoId)));
        }
    }
}
