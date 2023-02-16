package dms.mapper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dms.dto.DeviceDTO;
import dms.entity.DeviceEntity;
import dms.filter.DeviceFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

    @ParameterizedTest(name = "[{index}] DTO = {arguments}")
    @MethodSource
    void dTOToEntity(DeviceDTO deviceDTO, DeviceEntity deviceEntityExpected) {

        DeviceEntity deviceEntityMapped = deviceMapper.dTOToEntity(deviceDTO);

        assertEquals(deviceEntityExpected, deviceEntityMapped);
        assertThat(deviceEntityExpected)
                .usingRecursiveComparison()
                .isEqualTo(deviceEntityMapped);
//        }
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
    void entityToDTO() throws IOException {
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try (InputStream entityIs = new ClassPathResource("mapper/Device/json/device_entity.json").getInputStream();
             InputStream dTOIs = new ClassPathResource("mapper/Device/json/device_dto.json").getInputStream()
        ) {
            DeviceEntity originalEntity = jsonMapper.readValue(entityIs, DeviceEntity.class);
            DeviceDTO originalDto = jsonMapper.readValue(dTOIs, DeviceDTO.class);

            DeviceDTO mappedDto = deviceMapper.entityToDTO(originalEntity);

            assertEquals(originalDto, mappedDto);
            assertThat(originalDto)
                    .usingRecursiveComparison()
                    .isEqualTo(mappedDto);
        }
    }

    @Test
    void dTOToFilter() throws IOException {
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try (InputStream filterIs = new ClassPathResource("mapper/Device/json/device_dto.json").getInputStream();
             InputStream dTOIs = new ClassPathResource("mapper/Device/json/device_dto.json").getInputStream()
        ) {
            DeviceFilter originalFilter = jsonMapper.readValue(filterIs, DeviceFilter.class);
            DeviceDTO originalDto = jsonMapper.readValue(dTOIs, DeviceDTO.class);

            DeviceFilter mappedFilter = deviceMapper.dTOToFilter(originalDto);

            assertEquals(originalFilter, mappedFilter);
            assertThat(originalFilter)
                    .usingRecursiveComparison()
                    .isEqualTo(mappedFilter);
        }
    }
}