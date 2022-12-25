package dms;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeviceIT {

    @Value(value = "${local.server.port}")
    private int port;

    @Test
    void findDevicesByFilter() {
        Response response = given()
                    .port(port)
                    .param("typeId", "10708310")
                    .param("typeName", "СТ")
                    .param("typeGroupId", "7")
                    .param("typeGroupName", "Трансформатори")
                    .param("number", "160")
                    .param("releaseYear", "1980")
                    .param("releaseYearMin", "1979")
                    .param("releaseYearMax", "1981")
                    .param("testDateMin", "2017-12-21")
                    .param("testDateMax", "2017-12-23")
                    .param("nextTestDateMin", "2092-12-21")
                    .param("nextTestDateMax", "2092-12-23")
                    .param("replacementPeriod", "900")
                    .param("replacementPeriodMin", "12")
                    .param("replacementPeriodMax", "1000")
                    .param("status", "11")
                    .param("facilityId", "1011023")
                    .param("facilityName", "Фен")
                    .param("locationId", "10110230001100")
                    .param("description", "К")
                    .param("regionType", "NU")
                    .param("locate", "М7")
                    .param("locateType", "TR")
                    .param("placeNumber", "К")
                .when()
                    .get("/api/devices/by-query")
                .then()
                    .contentType(JSON)
                    .body("totalElements", greaterThan(0)).extract().response();
        System.out.println("Response Body is: " + response.jsonPath().prettyPrint());
    }


}