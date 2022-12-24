package dms;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeviceIT {

    @Value(value = "${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void findDevicesByQuery() {
        Response response = given()
                    .port(port)
                    .param("status", "11")
                .when()
                    .get("/api/devices/by-query")
                .then()
                    .contentType(JSON)
                    .body("numberOfElements", equalTo(35)).extract().response();
        System.out.println("Response Body is: " + response.jsonPath().prettyPrint());
    }


}