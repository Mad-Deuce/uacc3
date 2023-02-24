package dms.IT.Device;



import dms.dto.DeviceDTO;
import dms.entity.DeviceEntity;
import dms.repository.DeviceRepository;
import dms.standing.data.dock.val.ReplacementType;
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
        @Sql(scripts = "/IT/Device/sql/DeviceReplaceIT.sql")
})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeviceReplaceIT {

    @Autowired
    private DeviceRepository deviceRepository;

    @Value(value = "${local.server.port}")
    private int port;

    @ParameterizedTest(name = "[{index}] DTO = {arguments}")
    @MethodSource
    void replaceDevice(Long oldDeviceId, DeviceDTO newDeviceDTO) {

        Response response = given()
                .auth().preemptive().basic("user_operator", "user")
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

        DeviceEntity oldDeviceEntity = deviceRepository.findById(oldDeviceId).orElse(null);
        Assertions.assertNotNull(oldDeviceEntity);
        Assertions.assertEquals(Status.PS31, oldDeviceEntity.getStatus());
        Assertions.assertEquals("1011", oldDeviceEntity.getFacility().getId());
        Assertions.assertNull(oldDeviceEntity.getLocation());

        DeviceEntity newDeviceEntity = deviceRepository.findById(newDeviceDTO.getId()).orElse(null);
        Assertions.assertNotNull(newDeviceEntity);
        Assertions.assertNotEquals(Status.PS31, newDeviceEntity.getStatus());

    }

    private static Stream<Arguments> replaceDevice()  {
        DeviceDTO newDeviceDTO11 = new DeviceDTO();
        DeviceDTO newDeviceDTO21 = new DeviceDTO();
        DeviceDTO newDeviceDTO32 = new DeviceDTO();
        newDeviceDTO11.setId(100001L);
        newDeviceDTO21.setId(100001L);
        newDeviceDTO32.setId(100001L);
        newDeviceDTO11.setReplacementType(ReplacementType.ZAM);
        newDeviceDTO21.setReplacementType(ReplacementType.OTK);
        newDeviceDTO32.setReplacementType(ReplacementType.ZAM);
        newDeviceDTO11.setStatus(Status.PS11.getName());
        newDeviceDTO21.setStatus(Status.PS21.getName());
        newDeviceDTO32.setStatus(Status.PS32.getName());
        return Stream.of(
                Arguments.of(100002L, newDeviceDTO11),
                Arguments.of(100003L, newDeviceDTO21),
                Arguments.of(100004L, newDeviceDTO32)
        );
    }
}