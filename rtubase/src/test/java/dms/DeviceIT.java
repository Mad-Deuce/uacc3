package dms;

import io.restassured.response.Response;
import net.joshka.junit.json.params.JsonFileSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.json.JsonObject;
import java.util.HashMap;
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeviceIT {

    @Value(value = "${local.server.port}")
    private int port;

    @ParameterizedTest(name = "[{index}] {arguments}")
    @JsonFileSource(resources = "/device_filter.json")
    void findDevicesByFilter(JsonObject object) {
        HashMap<String, String> paramsMap = new HashMap<>();
        object.forEach((k, v) -> paramsMap.put(k, object.getString(k)));

        Response response = given()
                .basePath("/api/devices/by-filter")
                .queryParams(paramsMap)
                .port(port)
                .when()
                .get()
                .then()
                .contentType(JSON)
                .extract().response();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertNotNull(response.jsonPath().getString("totalElements"));
    }

    @ParameterizedTest(name = "[{index}] {arguments}")
    @JsonFileSource(resources = "/device_dto_for_create.json")
    void createDevice(JsonObject object) {
        HashMap<String, String> paramsMap = new HashMap<>();
        object.forEach((k, v) -> {
                    if (v != null && !Objects.equals(v.toString(), "null")) {
                        paramsMap.put(k, object.getString(k));
                    }
                }
        );

        Response response = given()
                .basePath("/api/devices/")
                .port(port)
                .contentType(JSON)
                .body(paramsMap)
                .when()
                .post()
                .then()
                .contentType(JSON)
                .extract().response();

        System.out.println("Response Body is: " + response.jsonPath().prettyPrint());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("10100101", response.jsonPath().getString("typeId"));

         response = given()
                .basePath("/api/devices/")
                .port(port)
                .contentType(JSON)
                .body(paramsMap)
                .when()
                .post()
                .then()
                .contentType(JSON)
                .extract().response();

        System.out.println("Response Body is: " + response.jsonPath().prettyPrint());

        Assertions.assertEquals(422, response.statusCode());
        Assertions.assertEquals("[id:id]", response.jsonPath().getString("errors.fieldName"));

    }

    @Test
    void findDeviceById() {
        Response response = given()
                .basePath("/api/devices/{id}")
                .pathParam("id", 1011000003)
                .port(port)
                .when()
                .get()
                .then()
                .contentType(JSON)
                .body(notNullValue()).extract().response();

        System.out.println("Response Body is: " + response.jsonPath().prettyPrint());
    }

}