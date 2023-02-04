package dms;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dms.dto.DeviceDTO;
import dms.entity.DeviceEntity;
import dms.mapper.DeviceMapper;
import dms.repository.DeviceRepository;
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

    @ParameterizedTest(name = "[{index}] {arguments}")
    @MethodSource
    void findDevicesByFilter(HashMap<String, ?> filter, DeviceDTO expectedResult) {

        Response response = given()
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
    @ValueSource(strings = {"транс"})
    void findDevicesByFilterLikeString(String value) {

        Response response = given()
                .basePath("/api/devices/by-filter")
                .queryParam("typeGroupName", value)
                .port(port)
                .when()
                .get()
                .then().log().all()
                .extract().response();
        ResponseBody<?> body = response.body();
        List<DeviceDTO> result = body.jsonPath().getList("content", DeviceDTO.class);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(result.size() > 0);

        Pattern pattern = Pattern.compile(".*" + value + ".*");

        result.forEach(item -> {
                    Matcher matcher = pattern.matcher(item.getTypeGroupName().toLowerCase());
                    Assertions.assertTrue(matcher.matches());
                }
        );

    }

    @ParameterizedTest(name = "[{index}] {arguments}")
    @ValueSource(strings = {"1987"})
    void findDevicesByFilterMinString(String value) {

        Response response = given()
                .basePath("/api/devices/by-filter")
                .queryParam("releaseYearMin", value)
                .port(port)
                .when()
                .get()
                .then().log().all()
                .extract().response();
        ResponseBody<?> body = response.body();
        List<DeviceDTO> result = body.jsonPath().getList("content", DeviceDTO.class);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(result.size() > 0);

        result.forEach(item ->
                Assertions.assertTrue(Integer.parseInt(item.getReleaseYear()) >= Integer.parseInt(value))
        );


    }

    @ParameterizedTest(name = "[{index}] {arguments}")
    @ValueSource(strings = {"1980"})
    void findDevicesByFilterMaxString(String value) {

        Response response = given()
                .basePath("/api/devices/by-filter")
                .queryParam("releaseYearMax", value)
                .port(port)
                .when()
                .get()
                .then().log().all()
                .extract().response();
        ResponseBody<?> body = response.body();
        List<DeviceDTO> result = body.jsonPath().getList("content", DeviceDTO.class);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(result.size() > 0);

        result.forEach(item ->
                Assertions.assertTrue(Integer.parseInt(item.getReleaseYear()) <= Integer.parseInt(value))
        );


    }

    @ParameterizedTest(name = "[{index}] {arguments}")
    @ValueSource(strings = {"2017-12-22"})
    void findDevicesByFilterDate(String value) {

        Response response = given()
                .basePath("/api/devices/by-filter")
                .queryParam("testDate", value)
                .port(port)
                .when()
                .get()
                .then().log().all()
                .extract().response();
        ResponseBody<?> body = response.body();
        List<DeviceDTO> result = body.jsonPath().getList("content", DeviceDTO.class);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(result.size() > 0);

        result.forEach(item -> Assertions.assertEquals(item.getTestDate().toString(), value));
    }

    @ParameterizedTest(name = "[{index}] {arguments}")
    @ValueSource(strings = {"2025-12-22"})
    void findDevicesByFilterMaxDate(String value) {

        Response response = given()
                .basePath("/api/devices/by-filter")
                .queryParam("nextTestDateMax", value)
                .port(port)
                .when()
                .get()
                .then().log().all()
                .extract().response();
        ResponseBody<?> body = response.body();
        List<DeviceDTO> result = body.jsonPath().getList("content", DeviceDTO.class);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(result.size() > 0);

        result.forEach(item ->
                Assertions.assertTrue(item.getNextTestDate()
                        .compareTo(Date.valueOf(value)) <= 0)
        );
    }

    @ParameterizedTest(name = "[{index}] {arguments}")
    @ValueSource(strings = {"2025-12-22"})
    void findDevicesByFilterMinDate(String value) {

        Response response = given()
                .basePath("/api/devices/by-filter")
                .queryParam("nextTestDateMin", value)
                .port(port)
                .when()
                .get()
                .then().log().all()
                .extract().response();
        ResponseBody<?> body = response.body();
        List<DeviceDTO> result = body.jsonPath().getList("content", DeviceDTO.class);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(result.size() > 0);

        result.forEach(item ->
                Assertions.assertTrue(item.getNextTestDate()
                        .compareTo(Date.valueOf(value)) >= 0)
        );
    }

    @ParameterizedTest(name = "[{index}] {arguments}")
    @ValueSource(longs = {1111})
    void findDevicesByFilterLikeLong(Long value) {

        Response response = given()
                .basePath("/api/devices/by-filter")
                .queryParam("id", value)
                .port(port)
                .when()
                .get()
                .then().log().all()
                .extract().response();
        ResponseBody<?> body = response.body();
        List<DeviceDTO> result = body.jsonPath().getList("content", DeviceDTO.class);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(result.size() > 0);

        Pattern pattern = Pattern.compile(".*" + value.toString() + ".*");

        result.forEach(item -> {
                    Matcher matcher = pattern.matcher(item.getId().toString().toLowerCase());
                    Assertions.assertTrue(matcher.matches());
                }
        );

    }

    @ParameterizedTest(name = "[{index}] {arguments}")
    @ValueSource(ints = {36})
    void findDevicesByFilterMaxInteger(int value) {

        Response response = given()
                .basePath("/api/devices/by-filter")
                .queryParam("replacementPeriodMax", value)
                .port(port)
                .when()
                .get()
                .then().log().all()
                .extract().response();
        ResponseBody<?> body = response.body();
        List<DeviceDTO> result = body.jsonPath().getList("content", DeviceDTO.class);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(result.size() > 0);

        result.forEach(item ->
                Assertions.assertTrue(item.getReplacementPeriod() <= value)
        );

    }

    @ParameterizedTest(name = "[{index}] {arguments}")
    @ValueSource(ints = {60})
    void findDevicesByFilterMinInteger(int value) {

        Response response = given()
                .basePath("/api/devices/by-filter")
                .queryParam("replacementPeriodMin", value)
                .port(port)
                .when()
                .get()
                .then().log().all()
                .extract().response();
        ResponseBody<?> body = response.body();
        List<DeviceDTO> result = body.jsonPath().getList("content", DeviceDTO.class);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(result.size() > 0);

        result.forEach(item ->
                Assertions.assertTrue(item.getReplacementPeriod() >= value)
        );

    }

    @ParameterizedTest(name = "[{index}] {arguments}")
    @ValueSource(longs = {1011000003})
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

    @ParameterizedTest(name = "[{index}] {arguments}")
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

    @ParameterizedTest(name = "[{index}] {arguments}")
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


}