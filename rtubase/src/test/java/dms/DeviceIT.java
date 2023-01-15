package dms;

import io.restassured.response.Response;
import net.joshka.junit.json.params.JsonFileSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.json.JsonObject;
import java.util.HashMap;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.greaterThan;
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
                .basePath("/api/devices/by-query")
                .queryParams(paramsMap)
                .port(port)
                .when()
                .get()
                .then()
                .contentType(JSON)
                .body("totalElements", greaterThan(0)).extract().response();

        System.out.println("Response Body is: " + response.jsonPath().prettyPrint());
    }

    @Test
    void findDeviceById(){
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