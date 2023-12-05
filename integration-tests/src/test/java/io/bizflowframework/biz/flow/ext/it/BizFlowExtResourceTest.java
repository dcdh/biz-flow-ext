package io.bizflowframework.biz.flow.ext.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class BizFlowExtResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/biz-flow-ext")
                .then()
                .statusCode(200)
                .body(is("Hello biz-flow-ext"));
    }
}
