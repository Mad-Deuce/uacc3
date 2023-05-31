package dms.mapper;

import dms.dto.DeviceDTO;
import dms.entity.DeviceEntity;
import dms.entity.DeviceViewMainEntity;
import dms.filter.DeviceFilter;
import dms.service.location.LocationService;
import dms.standing.data.entity.FacilityEntity;
import dms.standing.data.service.device.type.DeviceTypeService;
import dms.standing.data.service.device.type.group.DeviceTypeGroupService;
import dms.standing.data.service.facility.LineFacilityService;
import dms.standing.data.service.facility.RtdFacilityService;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.FIELD)
public abstract class DeviceMapper {

    @Autowired
    protected DeviceTypeService deviceTypeService;
    @Autowired
    protected DeviceTypeGroupService deviceTypeGroupService;
    @Autowired
    protected LocationService locationService;
    @Autowired
    protected LineFacilityService lineFacilityService;
    @Autowired
    protected RtdFacilityService rtdFacilityService;

    @Mapping(target = "type",
            expression = "java(deviceTypeService.findSDevByID(deviceDTO.getTypeId()).orElse(null))")
    @Mapping(target = "location",
            expression = "java(locationService.findDevObjById(deviceDTO.getLocationId()).orElse(null))")
    @Mapping(target = "facility", source = "deviceDTO")
    public abstract DeviceEntity dTOToEntity(DeviceDTO deviceDTO);

    FacilityEntity mapFacility(DeviceDTO deviceDTO) {
        if (deviceDTO.getFacilityId() == null) return null;
        FacilityEntity rtdFacilityEntity = rtdFacilityService.findById(deviceDTO.getFacilityId()).orElse(null);
        FacilityEntity lineFacilityEntity = lineFacilityService.findById(deviceDTO.getFacilityId()).orElse(null);
        return (rtdFacilityEntity != null ? rtdFacilityEntity : lineFacilityEntity);
    }

    @Mapping(target = "typeId", source = "type.id")
    @Mapping(target = "typeName", source = "type.name")
    @Mapping(target = "typeGroupId", source = "type.group.id")
    @Mapping(target = "typeGroupName", source = "type.group.name")
    @Mapping(target = "statusComment", source = "status.comment")

    @Mapping(target = "railwayId", source = "facility.subdivision.railway.id")
    @Mapping(target = "railwayName", source = "facility.subdivision.railway.name")
    @Mapping(target = "subdivisionId", source = "facility.subdivision.id")
    @Mapping(target = "subdivisionShortName", source = "facility.subdivision.shortName")

    @Mapping(target = "facilityId", source = "facility.id")
    @Mapping(target = "facilityName", source = "facility.name")
    @Mapping(target = "locationId", source = "location.id")
    @Mapping(target = "description", source = "location.description")
    @Mapping(target = "region", source = "location.region")
    @Mapping(target = "regionType", source = "location.regionType.name")
    @Mapping(target = "regionTypeComment", source = "location.regionType.comment")
    @Mapping(target = "locate", source = "location.locate")
    @Mapping(target = "locateType", source = "location.locateType.name")
    @Mapping(target = "locateTypeComment", source = "location.locateType.comment")
    @Mapping(target = "placeNumber", source = "location.placeNumber")
    @Mapping(target = "locationDetail", source = "location.detail")
    public abstract DeviceDTO entityToDTO(DeviceEntity entity);

    @Mapping(target = "regionType", source = "regionType.name")
    @Mapping(target = "regionTypeComment", source = "regionType.comment")
    @Mapping(target = "statusComment", source = "status.comment")
    @Mapping(target = "locateType", source = "locateType.name")
    @Mapping(target = "locateTypeComment", source = "locateType.comment")
    public abstract DeviceDTO entityToDTO(DeviceViewMainEntity entity);

    public abstract DeviceFilter dTOToFilter(DeviceDTO deviceDTO);


    public Page<DeviceDTO> entityToDTOPage(Page<DeviceEntity> deviceEntityPage) {
        List<DeviceDTO> content = deviceEntityPage.getContent().stream().map(this::entityToDTO).collect(Collectors.toList());
        return new PageImpl<>(content, deviceEntityPage.getPageable(), deviceEntityPage.getTotalElements());
    }

    public Page<DeviceDTO> entityPageToDtoPage(Page<DeviceViewMainEntity> entityPage) {
        List<DeviceDTO> content = entityPage.getContent().stream().map(this::entityToDTO).collect(Collectors.toList());
        return new PageImpl<>(content, entityPage.getPageable(), entityPage.getTotalElements());
    }
}
