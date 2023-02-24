package dms.IT.Device;


import dms.dto.DeviceDTO;
import dms.entity.DeviceEntity;
import dms.mapper.ExplicitDeviceMatcher;
import dms.repository.DeviceRepository;
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

import java.util.ArrayList;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

@SqlGroup({
        @Sql(scripts = "/IT/Device/sql/DeviceUpdateIT.sql")
})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeviceUpdateIT {

    @Value(value = "${local.server.port}")
    private int port;

    @Autowired
    private DeviceRepository deviceRepository;

    @ParameterizedTest(name = "[{index}] DTO = {arguments}")
    @MethodSource
    void updateDevice(Long id, DeviceDTO deviceDTO) {

        Assertions.assertTrue(deviceRepository.existsById(id));
        DeviceEntity beforeUpdateEntity = deviceRepository.findById(id).orElseThrow(null);
        Assertions.assertEquals("00000001", beforeUpdateEntity.getNumber());

        Response response = given()
                .auth().preemptive().basic("admin", "user")
                .basePath("/api/devices/")
                .port(port)
                .contentType(JSON)
                .body(deviceDTO)
                .when()
                .put(id.toString())
                .then()
                .contentType(JSON)
                .extract().response();
        Assertions.assertEquals(200, response.statusCode());

        DeviceEntity afterUpdateEntity = deviceRepository.findById(id).orElse(null);
        assert afterUpdateEntity != null;
        Assertions.assertEquals(deviceDTO.getNumber(), afterUpdateEntity.getNumber());
    }

    private static Stream<Arguments> updateDevice() {
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setNumber("00000002");
        deviceDTO.setActiveProperties(new ArrayList<>());
        deviceDTO.getActiveProperties().add(ExplicitDeviceMatcher.NUMBER);

        return Stream.of(
                Arguments.of(100001L, deviceDTO)
        );
    }


}