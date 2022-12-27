package dms;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.joshka.junit.json.params.JsonFileSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.json.JsonObject;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.greaterThan;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeviceIT {

    @Value(value = "${local.server.port}")
    private int port;

    @Test
    void findDevicesByFilter() throws IOException {
        File file = new File("src/test/resources/device_dto_for_filter.json");
        HashMap<?, ?> fileContent = new ObjectMapper().readValue(file, HashMap.class);
        StringBuilder parametersBuilder = new StringBuilder("?");
        fileContent.forEach((k, v) -> {
                    if (v != null) {
                        parametersBuilder
                                .append(k)
                                .append("=")
                                .append(v)
                                .append("&");
                    }
                }
        );
        parametersBuilder.deleteCharAt(parametersBuilder.length() - 1);
        String parameters = parametersBuilder.toString();

        Response response = given()
                .port(port)
                .when()
                .get("/api/devices/by-query" + parameters)
                .then()
                .contentType(JSON)
                .body("totalElements", greaterThan(0)).extract().response();
        System.out.println("Response Body is: " + response.jsonPath().prettyPrint());
    }

    @ParameterizedTest(name = "[{index}] {arguments}")
    @JsonFileSource(resources = "/device_dto_for_filter.json")
    void findDevicesByFilterAlt(JsonObject object) {
        HashMap<String, String> paramsMap = new HashMap<>();
        object.forEach((k, v) -> paramsMap.put(k, object.getString(k)));

        Response response = given()
                .queryParams(paramsMap)
                .port(port)
                .when()
                .get("/api/devices/by-query")
                .then()
                .contentType(JSON)
                .body("totalElements", greaterThan(0)).extract().response();

        System.out.println("Response Body is: " + response.jsonPath().prettyPrint());
    }

}