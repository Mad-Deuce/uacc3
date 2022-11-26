package dms.mapper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dms.dto.DeviceDTO;
import dms.entity.DeviceEntity;
import dms.standing.data.service.sdev.SDevService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class DeviceMapperTest {

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    protected SDevService deviceTypeService;

    @Test
    void dTOToEntity() throws IOException {
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try (InputStream entityIs = new ClassPathResource("device_entity.json").getInputStream();
             InputStream dTOIs = new ClassPathResource("device_dto.json").getInputStream()
        ) {
            DeviceEntity originalEntity = jsonMapper.readValue(entityIs, DeviceEntity.class);
            DeviceDTO originalDto = jsonMapper.readValue(dTOIs, DeviceDTO.class);
            DeviceEntity mappedEntity = deviceMapper.dTOToEntity(originalDto);

            assertEquals(originalEntity, mappedEntity);
            assertThat(originalEntity)
                    .usingRecursiveComparison()
                    .isEqualTo(mappedEntity);
        }
    }

    @Test
    void entityToDTO() {
    }
}