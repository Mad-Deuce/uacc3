package dms.mapper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dms.dto.DeviceDTO;
import dms.entity.DeviceEntity;
import dms.filter.DeviceFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SqlGroup({
        @Sql(scripts = "/mapper/Device/sql/schema.sql"),
        @Sql(scripts = "/mapper/Device/sql/DeviceMapper.sql")
})
@SpringBootTest
class DeviceMapperTest {

    @Autowired
    private DeviceMapper deviceMapper;

    @Test
    void newDTOToEntity() {
        DeviceEntity deviceEntityMapped = deviceMapper.dTOToEntity(new DeviceDTO());

        assertThat(new DeviceEntity())
                .usingRecursiveComparison().ignoringExpectedNullFields()
                .isEqualTo(deviceEntityMapped);
        assertThat(new DeviceEntity())
                .usingRecursiveComparison().ignoringActualNullFields()
                .isEqualTo(deviceEntityMapped);
    }

    @ParameterizedTest(name = "[{index}] DTO = {arguments}")
    @MethodSource
    void dTOToEntity(DeviceDTO deviceDTO, DeviceEntity deviceEntityExpected) {

        DeviceEntity deviceEntityMapped = deviceMapper.dTOToEntity(deviceDTO);

        assertEquals(deviceEntityExpected, deviceEntityMapped);
        assertThat(deviceEntityExpected)
                .usingRecursiveComparison()
                .isEqualTo(deviceEntityMapped);
    }

    private static Stream<Arguments> dTOToEntity() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        JsonNode deviceDTOJsonNode = objectMapper
                .readTree(new File("src/test/resources/mapper/Device/json/device_dto.json"));
        DeviceDTO deviceDTO = objectMapper
                .treeToValue(deviceDTOJsonNode, DeviceDTO.class);

        JsonNode deviceEntityExpectedJsonNode = objectMapper
                .readTree(new File("src/test/resources/mapper/Device/json/device_entity.json"));
        DeviceEntity deviceEntityExpected = objectMapper
                .treeToValue(deviceEntityExpectedJsonNode, DeviceEntity.class);

        return Stream.of(
                Arguments.of(deviceDTO, deviceEntityExpected)
        );
    }

    @Test
    void newEntityToDTO() {
        DeviceDTO deviceDTOMapped = deviceMapper.entityToDTO(new DeviceEntity());

        assertThat(new DeviceDTO())
                .usingRecursiveComparison().ignoringExpectedNullFields()
                .isEqualTo(deviceDTOMapped);
        assertThat(new DeviceDTO())
                .usingRecursiveComparison().ignoringActualNullFields()
                .isEqualTo(deviceDTOMapped);
    }

    @ParameterizedTest(name = "[{index}] DTO = {arguments}")
    @MethodSource
    void entityToDTO(DeviceEntity deviceEntity, DeviceDTO deviceDTOExpected) {
        DeviceDTO deviceDTOMapped = deviceMapper.entityToDTO(deviceEntity);

        assertEquals(deviceDTOExpected, deviceDTOMapped);
        assertThat(deviceDTOExpected)
                .usingRecursiveComparison()
                .isEqualTo(deviceDTOMapped);
    }

    private static Stream<Arguments> entityToDTO() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        JsonNode deviceDTOJsonNode = objectMapper
                .readTree(new File("src/test/resources/mapper/Device/json/device_dto.json"));
        DeviceDTO deviceDTOExpected = objectMapper
                .treeToValue(deviceDTOJsonNode, DeviceDTO.class);

        JsonNode deviceEntityExpectedJsonNode = objectMapper
                .readTree(new File("src/test/resources/mapper/Device/json/device_entity.json"));
        DeviceEntity deviceEntity = objectMapper
                .treeToValue(deviceEntityExpectedJsonNode, DeviceEntity.class);

        return Stream.of(
                Arguments.of(deviceEntity, deviceDTOExpected)
        );
    }

    @ParameterizedTest(name = "[{index}] DTO = {arguments}")
    @MethodSource
    void dTOToFilter(DeviceDTO deviceDTO, DeviceFilter deviceFilterExpected) {
        DeviceFilter deviceFilterMapped = deviceMapper.dTOToFilter(deviceDTO);

        assertEquals(deviceFilterExpected, deviceFilterMapped);
        assertThat(deviceFilterExpected)
                .usingRecursiveComparison()
                .isEqualTo(deviceFilterMapped);
    }

    private static Stream<Arguments> dTOToFilter() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        JsonNode deviceDTOJsonNode = objectMapper
                .readTree(new File("src/test/resources/mapper/Device/json/device_dto.json"));
        DeviceDTO deviceDTO = objectMapper
                .treeToValue(deviceDTOJsonNode, DeviceDTO.class);

        JsonNode deviceEntityExpectedJsonNode = objectMapper
                .readTree(new File("src/test/resources/mapper/Device/json/device_dto.json"));
        DeviceFilter deviceFilterExpected = objectMapper
                .treeToValue(deviceEntityExpectedJsonNode, DeviceFilter.class);

        return Stream.of(
                Arguments.of(deviceDTO, deviceFilterExpected),
                Arguments.of(new DeviceDTO(), new DeviceFilter())
        );
    }
}