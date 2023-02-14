package dms;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dms.dto.DeviceDTO;
import dms.entity.DeviceEntity;
import dms.mapper.DeviceMapper;
import dms.mapper.ExplicitDeviceMatcher;
import dms.repository.DeviceRepository;
import dms.standing.data.dock.val.ReplacementType;
import dms.standing.data.dock.val.Status;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    void createDevice(DeviceDTO deviceDTO) {

        Response response = given()
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
                .readTree(new File("src/test/resources/device_dto_for_create.json"));
        DeviceDTO deviceDTO = new ObjectMapper()
                .treeToValue(jsonNode, DeviceDTO.class);

        return Stream.of(
                Arguments.of(deviceDTO)
        );
    }

    @ParameterizedTest(name = "[{index}] DTO = {arguments}")
    @MethodSource
    void deleteDevice(DeviceDTO deviceDTO) {

        DeviceEntity deviceEntity = deviceMapper.dTOToEntity(deviceDTO);
        deviceEntity.setId(null);
        deviceEntity.setStatus(Status.PS31);
        deviceEntity.setLocation(null);
        Long id = deviceRepository.saveAndFlush(deviceEntity).getId();
        Assertions.assertTrue(deviceRepository.existsById(id));

        Response response = given()
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

    private static Stream<Arguments> deleteDevice() throws IOException {
        JsonNode jsonNode = new ObjectMapper()
                .readTree(new File("src/test/resources/device_dto_for_create.json"));
        DeviceDTO deviceDTO = new ObjectMapper()
                .treeToValue(jsonNode, DeviceDTO.class);

        return Stream.of(
                Arguments.of(deviceDTO)
        );
    }

    @ParameterizedTest(name = "[{index}] DTO = {arguments}")
    @MethodSource
    void updateDevice(DeviceDTO deviceDTO, String newNumber, ExplicitDeviceMatcher activeParameter) {

        DeviceEntity deviceEntity = deviceMapper.dTOToEntity(deviceDTO);
        deviceEntity.setId(null);
        deviceEntity.setStatus(Status.PS31);
        deviceEntity.setLocation(null);
        DeviceEntity beforeUpdateEntity = deviceRepository.saveAndFlush(deviceEntity);
        Long id = beforeUpdateEntity.getId();
        Assertions.assertTrue(deviceRepository.existsById(id));

        DeviceDTO beforeUpdateDTO = deviceMapper.entityToDTO(beforeUpdateEntity);
        beforeUpdateDTO.setNumber(newNumber);

        beforeUpdateDTO.setActiveProperties(new ArrayList<>());
        beforeUpdateDTO.getActiveProperties().add(activeParameter);

        Response response = given()
                .basePath("/api/devices/")
                .port(port)
                .contentType(JSON)
                .body(beforeUpdateDTO)
                .when()
                .put(id.toString())
                .then()
                .contentType(JSON)
                .extract().response();

        Assertions.assertEquals(200, response.statusCode());

        DeviceDTO afterUpdateDTO = deviceMapper.entityToDTO(deviceRepository.findById(id).orElse(null));

        Assertions.assertEquals(newNumber, afterUpdateDTO.getNumber());
        deviceRepository.deleteById(id);
    }

    private static Stream<Arguments> updateDevice() throws IOException {
        JsonNode jsonNode = new ObjectMapper()
                .readTree(new File("src/test/resources/device_dto_for_create.json"));
        DeviceDTO deviceDTO = new ObjectMapper()
                .treeToValue(jsonNode, DeviceDTO.class);

        return Stream.of(
                Arguments.of(deviceDTO, "1000001", ExplicitDeviceMatcher.NUMBER)
        );
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