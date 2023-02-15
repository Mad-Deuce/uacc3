package dms;



import dms.dto.DeviceDTO;
import dms.standing.data.dock.val.ReplacementType;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeviceIT {


    @Value(value = "${local.server.port}")
    private int port;


    @ParameterizedTest(name = "[{index}] id = {arguments}")
    @ValueSource(longs = {100002})
    void findDeviceById(Long value) {
        Response response = given()
                .basePath("/api/devices/{id}")
                .pathParam("id", value)
                .port(port)
                .when()
                .get()
                .then()
                .contentType(JSON)
                .extract().response();
        ResponseBody<?> body = response.body();
        DeviceDTO result = body.jsonPath().getObject("", DeviceDTO.class);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(value, result.getId());
    }

    @ParameterizedTest(name = "[{index}] DTO = {arguments}")
    @MethodSource
    void replaceDevice(Long oldDeviceId, Long newDeviceId) {

        DeviceDTO newDeviceDTO = new DeviceDTO();
        newDeviceDTO.setId(newDeviceId);
        newDeviceDTO.setReplacementType(ReplacementType.ZAM);

        Response response = given()
                .basePath("/api/devices/")
                .port(port)
                .contentType(JSON)
                .body(newDeviceDTO)
                .when()
                .put("replace/" + oldDeviceId.toString())
                .then()
                .contentType(JSON)
                .extract().response();

        Assertions.assertEquals(200, response.statusCode());

    }

    private static Stream<Arguments> replaceDevice()  {

        return Stream.of(
                Arguments.of(100004L, 100005L)
        );
    }
}