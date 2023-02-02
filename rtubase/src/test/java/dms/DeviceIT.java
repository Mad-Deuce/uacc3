package dms;

import dms.repository.DeviceRepository;
import io.restassured.response.Response;
import net.joshka.junit.json.params.JsonFileSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.json.JsonObject;
import java.util.HashMap;
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeviceIT {

    @Value(value = "${local.server.port}")
    private int port;

    @Autowired
    private DeviceRepository deviceRepository;

    @ParameterizedTest(name = "[{index}] {arguments}")
    @JsonFileSource(resources = "/device_dto_for_find_by_filter_alt.json")
    void findDevicesByFilterAlt(JsonObject object) {
        HashMap<String, String> paramsMap = new HashMap<>();

        JsonObject filter = object.getJsonObject("filter");
        filter.forEach((k, v) -> paramsMap.put(k, filter.getString(k)));

        Response response = given()
                .basePath("/api/devices/by-filter")
                .queryParams(paramsMap)
                .port(port)
                .when()
                .get()
                .then()
                .contentType(JSON)
                .extract().response();

        HashMap<String, String> expectedResult = new HashMap<>();
        HashMap<String, String> result = new HashMap<>();

        object.getJsonObject("expectedResult").forEach((k, v) -> {
                    expectedResult.put(k, v.toString().replaceAll("\"", ""));
                }
        );

        response.jsonPath().getMap("content[0]").forEach((k, v) -> {
                    if (v == null) v = "null";
                    result.put(k.toString(), v.toString());
                }
        );

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(expectedResult, result);
        Assertions.assertTrue(response.jsonPath().getInt("totalElements") > 0);
    }

    @ParameterizedTest(name = "[{index}] {arguments}")
    @JsonFileSource(resources = "/device_dto_for_find_by_filter.json")
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

        System.out.println("Response Body is: " + response.jsonPath().prettyPrint());

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
                .extract().response();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("1011000003", response.jsonPath().getString("id"));
    }

}