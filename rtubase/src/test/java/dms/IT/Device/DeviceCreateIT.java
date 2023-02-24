package dms.IT.Device;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dms.dto.DeviceDTO;
import dms.repository.DeviceRepository;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;


@SqlGroup({
        @Sql(scripts = "/IT/Device/sql/DeviceCreateIT.sql")
})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeviceCreateIT {

    @Value(value = "${local.server.port}")
    private int port;

    @Autowired
    private DeviceRepository deviceRepository;

    @ParameterizedTest(name = "[{index}] DTO = {arguments}")
    @MethodSource
    void createDevice(DeviceDTO deviceDTO) {

        Response response = given()
                .auth().preemptive().basic("user_operator", "user")
                .basePath("/api/devices/")
                .port(port)
                .contentType(JSON)
                .body(deviceDTO)
                .when()
                .post()
                .then()
                .contentType(JSON)
                .extract().response();
        ResponseBody<?> body = response.body();
        Assertions.assertEquals(200, response.statusCode());

        DeviceDTO result = body.jsonPath().getObject(".", DeviceDTO.class);

        response = given()
                .auth().preemptive().basic("user_operator", "user")
                .basePath("/api/devices/")
                .port(port)
                .contentType(JSON)
                .body(deviceDTO)
                .when()
                .post()
                .then()
                .contentType(JSON)
                .extract().response();

        Assertions.assertEquals(422, response.statusCode());

        deviceRepository.deleteById(result.getId());
    }

    private static Stream<Arguments> createDevice() throws IOException {
        JsonNode jsonNode = new ObjectMapper()
                .readTree(new File("src/test/resources/IT/Device/json/DeviceCreateIT.json"));
        DeviceDTO deviceDTO = new ObjectMapper()
                .treeToValue(jsonNode, DeviceDTO.class);

        return Stream.of(
                Arguments.of(deviceDTO)
        );
    }


}