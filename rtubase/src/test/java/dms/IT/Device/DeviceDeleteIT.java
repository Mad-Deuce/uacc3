package dms.IT.Device;


import dms.repository.DeviceRepository;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

@SqlGroup({
        @Sql(scripts = "/IT/Device/sql/DeviceDeleteIT.sql")
})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeviceDeleteIT {

    @Value(value = "${local.server.port}")
    private int port;

    @Autowired
    private DeviceRepository deviceRepository;

    @ParameterizedTest(name = "[{index}] DTO = {arguments}")
    @ValueSource(longs = {100001})
    void deleteDevice(Long id) {

        Assertions.assertTrue(deviceRepository.existsById(id));

        Response response = given()
                .auth().preemptive().basic("admin", "user")
                .basePath("/api/devices/")
                .port(port)
                .when()
                .delete(id.toString())
                .then()
                .contentType(JSON)
                .extract().response();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertFalse(deviceRepository.existsById(id));
    }

}