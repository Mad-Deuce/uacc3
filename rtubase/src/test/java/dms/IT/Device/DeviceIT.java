package dms.IT.Device;



import dms.dto.DeviceDTO;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

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

}