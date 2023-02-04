package dms;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dms.dto.DeviceDTO;
import dms.mapper.DeviceMapper;
import dms.repository.DeviceRepository;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import net.joshka.junit.json.params.JsonFileSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.json.JsonObject;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeviceIT {

    @Value(value = "${local.server.port}")
    private int port;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceMapper deviceMapper;


    @ParameterizedTest(name = "[{index}] {arguments}")
    @MethodSource
    void findDevicesByFilter(HashMap<String, ?> filter, DeviceDTO expectedResult) {

        Response response  =  given()
                .basePath("/api/devices/by-filter")
                .queryParams(filter)
                .port(port)
                .when()
                .get()
                .then().log().all()
                .extract().response();

        ResponseBody<?> body = response.body();

        DeviceDTO result = body.jsonPath().getList("content", DeviceDTO.class).get(0);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(expectedResult, result);
        Assertions.assertTrue(true);
    }

    private static Stream<Arguments> findDevicesByFilter() throws IOException {
        JsonNode jsonNode = new ObjectMapper()
                .readTree(new File("src/test/resources/device_dto_for_find_by_filter_alt.json"));
        DeviceDTO expectedResult = new ObjectMapper()
                .treeToValue(jsonNode.get("expectedResult"), DeviceDTO.class);

        HashMap<?, ?> filter1 = new ObjectMapper().convertValue(jsonNode.get("filter1"), HashMap.class);
        HashMap<?, ?> filter2 = new ObjectMapper().convertValue(jsonNode.get("filter2"), HashMap.class);
        HashMap<?, ?> filter3 = new ObjectMapper().convertValue(jsonNode.get("filter3"), HashMap.class);

        return Stream.of(
                Arguments.of(filter1, expectedResult),
                Arguments.of(filter2, expectedResult),
                Arguments.of(filter3, expectedResult)
        );
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