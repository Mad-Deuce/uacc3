package dms.mapper;

import dms.dto.DeviceDTO;
import dms.entity.DeviceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DeviceMapper {
    DeviceMapper INSTANCE = Mappers.getMapper(DeviceMapper.class);

    @Mapping(target = "typeId", source = "type.id")
    @Mapping(target = "typeName", source = "type.name")
    @Mapping(target = "typeGroupId", source = "type.group.id")
    @Mapping(target = "typeGroupName", source = "type.group.name")
    DeviceDTO entityToDTO (DeviceEntity entity);


}
