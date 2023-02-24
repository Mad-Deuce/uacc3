package dms.IT.Device;



import dms.dto.DeviceDTO;
import dms.entity.DeviceEntity;
import dms.repository.DeviceRepository;
import dms.standing.data.dock.val.Status;
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

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

@SqlGroup({
        @Sql(scripts = "/IT/Device/sql/DeviceSetIT.sql")
})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeviceSetIT {

    @Autowired
    private DeviceRepository deviceRepository;

    @Value(value = "${local.server.port}")
    private int port;

    @ParameterizedTest(name = "[{index}] DTO = {arguments}")
    @MethodSource
    void setDevice(Long deviceId, DeviceDTO newDeviceDTO) {

        Response response = given()
                .auth().preemptive().basic("user_operator", "user")
                .basePath("/api/devices/")
                .port(port)
                .contentType(JSON)
                .body(newDeviceDTO)
                .when()
                .put("set/" + deviceId.toString())
                .then()
                .contentType(JSON)
                .extract().response();
        ResponseBody<?> body = response.body();
        body.prettyPrint();
        Assertions.assertEquals(200, response.statusCode());

        DeviceEntity deviceEntity = deviceRepository.findById(deviceId).orElse(null);
        Assertions.assertNotNull(deviceEntity);
        Assertions.assertNotEquals(Status.PS31, deviceEntity.getStatus());
    }

    private static Stream<Arguments> setDevice()  {
        DeviceDTO newDeviceDTO11 = new DeviceDTO();
        DeviceDTO newDeviceDTO21 = new DeviceDTO();
        DeviceDTO newDeviceDTO32 = new DeviceDTO();
        newDeviceDTO11.setStatus(Status.PS11.getName());
        newDeviceDTO21.setStatus(Status.PS21.getName());
        newDeviceDTO32.setStatus(Status.PS32.getName());
        newDeviceDTO11.setFacilityId("1011023");
        newDeviceDTO21.setFacilityId("1011023");
        newDeviceDTO32.setFacilityId("1011");
        newDeviceDTO11.setLocationId(10110230001100L);
        newDeviceDTO21.setLocationId(null);
        newDeviceDTO32.setLocationId(null);


        return Stream.of(
                Arguments.of(100001L, newDeviceDTO11),
                Arguments.of(100001L, newDeviceDTO21),
                Arguments.of(100001L, newDeviceDTO32)
        );
    }
}