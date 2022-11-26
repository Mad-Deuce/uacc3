package dms.mapper;

import dms.dto.DeviceDTO;
import dms.entity.DeviceEntity;
import dms.filter.DeviceFilter;
import dms.service.devobj.DevObjService;
import dms.standing.data.service.device.type.group.DeviceTypeGroupService;
import dms.standing.data.service.dobj.DObjService;
import dms.standing.data.service.sdev.SDevService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.FIELD)
public abstract class DeviceMapper {

    @Autowired
    protected SDevService deviceTypeService;
    @Autowired
    protected DeviceTypeGroupService deviceTypeGroupService;
    @Autowired
    protected DevObjService locationService;
    @Autowired
    protected DObjService lineFacilityService;

    @Mapping(target = "type",
            expression = "java(deviceTypeService.findSDevByID(deviceDTO.getTypeId()).orElse(null))")
    @Mapping(target = "location",
            expression = "java(locationService.findDevObjById(deviceDTO.getLocationId()).orElse(null))")
    @Mapping(target = "facility",
            expression = "java(lineFacilityService.findById(deviceDTO.getFacilityId()).orElse(null))")
    public abstract DeviceEntity dTOToEntity(DeviceDTO deviceDTO);

    @Mapping(target = "typeId", source = "type.id")
    @Mapping(target = "typeName", source = "type.name")
    @Mapping(target = "typeGroupId", source = "type.group.id")
    @Mapping(target = "typeGroupName", source = "type.group.name")
    @Mapping(target = "statusName", source = "status.name")
    @Mapping(target = "statusComment", source = "status.comm")
    @Mapping(target = "facilityId", source = "facility.id")
    @Mapping(target = "facilityName", source = "facility.name")
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
    public abstract DeviceDTO entityToDTO (DeviceEntity entity);

    public abstract DeviceFilter dTOToFilter (DeviceDTO deviceDTO);
}
