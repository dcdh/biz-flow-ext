package io.bizflowframework.biz.flow.ext.it;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Objects;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BizFlowExtResourceTest {

    // DefaultErrors
    @Test
    public void shouldReturnExpectedResponseWhenSerdeIsMissing() {
        given()
                .when().post("/todo/failMissingSerde")
                .then()
                .log().all()
                .statusCode(501)
                .contentType("application/vnd.aggregate-root-error-v1+txt")
                .body(is("Missing Serde for aggregate root type 'TodoAggregateRoot' and event type 'UnknownTodoEvent'"));
    }

    @Test
    public void shouldReturnExpectedResponseWhenAggregateRootIsUnknown() {
        given()
                .when().post("/todo/failUnknownAggregate")
                .then()
                .log().all()
                .statusCode(404)
                .contentType("application/vnd.aggregate-root-error-v1+txt")
                .body(is("Unknown aggregate root id 'unknown' of type 'TodoAggregateRoot'"));
    }

    @Test
    public void shouldReturnExpectedResponseWhenAggregateRootAtVersionIsUnknown() {
        given()
                .when().post("/todo/failUnknownAggregateAtVersion")
                .then()
                .log().all()
                .statusCode(404)
                .contentType("application/vnd.aggregate-root-error-v1+txt")
                .body(is("Unknown aggregate root id 'todo_to_fail_at_unknown_version' of type 'TodoAggregateRoot' at version '10'"));
    }

    @Test
    public void shouldReturnExpectedResponseWhenDoingAForbiddenAction() {
        given()
                .when().post("/todo/failOnActionForbidden")
                .then()
                .log().all()
                .statusCode(403)
                .contentType("application/vnd.event-store-error-v1+txt")
                .body(containsString("ERROR: not allowed"));
    }

    // TodoHappyPathLifecycle
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
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("expected/schema/TodoDTO.json"))
                .body("todoId", notNullValue())
                .body("description", equalTo("lorem ipsum dolor sit amet"))
                .body("status", equalTo("IN_PROGRESS"))
                .body("version", equalTo(0))
                .extract().body().path("todoId");
    }

    @Test
    @Order(2)
    public void shouldGetQueryCreatedTodo() {
        Objects.requireNonNull(todoId);
        given()
                .pathParam("todoId", todoId)
                .when().get("/todo/{todoId}")
                .then()
                .log().all()
                .statusCode(200)
                .contentType("application/vnd.todo-v1+json")
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("expected/schema/TodoDTO.json"))
                .body("todoId", equalTo(todoId))
                .body("description", equalTo("lorem ipsum dolor sit amet"))
                .body("status", equalTo("IN_PROGRESS"))
                .body("version", equalTo(0));
    }

    @Test
    @Order(3)
    public void shouldMarkTodoAsCompleted() {
        Objects.requireNonNull(todoId);
        given()
                .formParam("todoId", todoId)
                .when().post("/todo/markTodoAsCompleted")
                .then()
                .log().all()
                .statusCode(200)
                .contentType("application/vnd.todo-v1+json")
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("expected/schema/TodoDTO.json"))
                .body("todoId", equalTo(todoId))
                .body("description", equalTo("lorem ipsum dolor sit amet"))
                .body("status", equalTo("COMPLETED"))
                .body("version", equalTo(1));
    }

    @Test
    @Order(4)
    public void shouldGetQueryMarkedAsCompletedTodo() {
        Objects.requireNonNull(todoId);
        given()
                .pathParam("todoId", todoId)
                .when().get("/todo/{todoId}")
                .then()
                .log().all()
                .statusCode(200)
                .contentType("application/vnd.todo-v1+json")
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("expected/schema/TodoDTO.json"))
                .body("todoId", equalTo(todoId))
                .body("description", equalTo("lorem ipsum dolor sit amet"))
                .body("status", equalTo("COMPLETED"))
                .body("version", equalTo(1));
    }

    @Test
    @Order(5)
    public void shouldListTodo() {
        given()
                .queryParam("page[index]", "0")
                .queryParam("page[size]", "20")
                .log().all()
                .when().get("/todo")
                .then()
                .log().all()
                .statusCode(200)
                .contentType("application/vnd.todos-v1+json")
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("expected/schema/ListOfTodosDTO.json"))
                .body("todos[0].todoId", equalTo(todoId))
                .body("todos[0].description", equalTo("lorem ipsum dolor sit amet"))
                .body("todos[0].status", equalTo("COMPLETED"))
                .body("todos[0].version", equalTo(1))
                .body("nbOfElements", equalTo(1));
    }

    // TodoFailurePath
    @Test
    public void shouldFailWhenTodoIsUnknown() {
        given()
                .formParam("todoId", "my_second_awesome_todo")
                .when().post("/todo/markTodoAsCompleted")
                .then()
                .log().all()
                .statusCode(404)
                .contentType("application/vnd.unknown-todo-v1+txt")
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
                .contentType("application/vnd.todo-already-marked-as-completed-v1+txt")
                .body(is(String.format("Todo '%s' already marked as completed", todoId)));
    }
}
