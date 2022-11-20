package dms.mapper;

import dms.dto.DeviceDTO;
import dms.entity.DeviceEntity;
import dms.standing.data.entity.DeviceTypeEntity;
import dms.standing.data.entity.DeviceTypeGroupEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeviceMapperTest {

    @Test
    void entityToDTO() {
        DeviceEntity entity = new DeviceEntity();
        entity.setNumber("12345");
        entity.setDetail("some_detail_text");
        DeviceTypeEntity type = new DeviceTypeEntity();
        type.setId(4321L);
        type.setName("some_type_name");
        DeviceTypeGroupEntity group=new DeviceTypeGroupEntity();
        group.setId(5);
        group.setName("some_group_name");
        type.setGroup(group);
        entity.setType(type);

        DeviceDTO dto = DeviceMapper.INSTANCE.entityToDTO( entity );

        assertEquals(dto.getNumber(), entity.getNumber());
        assertEquals(dto.getDetail(), entity.getDetail());
        assertEquals(dto.getTypeId(), entity.getType().getId());
        assertEquals(dto.getTypeName(), entity.getType().getName());
        assertEquals(dto.getTypeGroupId(), entity.getType().getGroup().getId());
        assertEquals(dto.getTypeGroupName(), entity.getType().getGroup().getName());
    }
}