package dms.IT.Device;



import dms.dto.DeviceDTO;
import dms.entity.DeviceEntity;
import dms.repository.DeviceRepository;
import dms.standing.data.dock.val.Status;
import io.restassured.response.Response;
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
        @Sql(scripts = "/IT/Device/sql/DeviceUnsetIT.sql")
})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeviceUnsetIT {

    @Autowired
    private DeviceRepository deviceRepository;

    @Value(value = "${local.server.port}")
    private int port;

    @ParameterizedTest(name = "[{index}] DTO = {arguments}")
    @MethodSource
    void unsetDevice(Long deviceId, DeviceDTO deviceDTO) {

        Response response = given()
                .auth().preemptive().basic("user_operator", "user")
                .basePath("/api/devices/")
                .port(port)
                .contentType(JSON)
                .body(deviceDTO)
                .when()
                .put("unset/" + deviceId.toString())
                .then()
                .contentType(JSON)
                .extract().response();

        Assertions.assertEquals(200, response.statusCode());

        DeviceEntity deviceEntity = deviceRepository.findById(deviceId).orElse(null);
        Assertions.assertNotNull(deviceEntity);
        Assertions.assertEquals(Status.PS31, deviceEntity.getStatus());
        Assertions.assertEquals(deviceDTO.getFacilityId(), deviceEntity.getFacility().getId());
        Assertions.assertNull(deviceEntity.getLocation());
    }

    private static Stream<Arguments> unsetDevice()  {
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setFacilityId("1011");

        return Stream.of(
                Arguments.of(100002L, deviceDTO),
                Arguments.of(100003L, deviceDTO),
                Arguments.of(100004L, deviceDTO)
        );
    }
}