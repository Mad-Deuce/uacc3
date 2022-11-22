package dms.mapper;

import dms.dto.DeviceDTO;
import dms.entity.DeviceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


public interface DeviceMapper {
    DeviceMapper INSTANCE = Mappers.getMapper(DeviceMapper.class);

    @Mapping(target = "typeId", source = "type.id")
    @Mapping(target = "typeName", source = "type.name")
    @Mapping(target = "typeGroupId", source = "type.group.id")
    @Mapping(target = "typeGroupName", source = "type.group.name")
    @Mapping(target = "statusName", source = "status.name")
    @Mapping(target = "statusComment", source = "status.comm")
    @Mapping(target = "objectId", source = "object.id")
    @Mapping(target = "objectName", source = "object.name")

    @Mapping(target = "locationId", source = "location.id")
    @Mapping(target = "description", source = "location.description")
    @Mapping(target = "region", source = "location.region")
    @Mapping(target = "regionTypeName", source = "location.regionType.name")
    @Mapping(target = "regionTypeComment", source = "location.regionType.comm")
    @Mapping(target = "locate", source = "location.locate")
    @Mapping(target = "locateTypeName", source = "location.locateType.name")
    @Mapping(target = "locateTypeComment", source = "location.locateType.comm")
    @Mapping(target = "placeNumber", source = "location.placeNumber")
    @Mapping(target = "locationDetail", source = "location.detail")
    DeviceDTO entityToDTO (DeviceEntity entity);

    @Mapping(target = "type.id", source = "typeId")
    @Mapping(target = "object.id", source = "objectId")
    @Mapping(target = "location.id", source = "locationId")
    @Mapping(target = "status", source = "status.name")
    DeviceEntity dTOToEntity (DeviceDTO dto);
}
