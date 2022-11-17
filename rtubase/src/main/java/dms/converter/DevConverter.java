package dms.converter;

import dms.dto.DevDTO;
import dms.entity.DeviceEntity;
import dms.entity.DeviceLocationEntity;
import dms.exception.NoEntityException;
import dms.exception.WrongDataException;
import dms.filter.DevFilter;
import dms.service.devobj.DevObjService;
import dms.standing.data.dock.val.Status;
import dms.standing.data.entity.DObjEntity;
import dms.standing.data.entity.DObjRtuEntity;
import dms.standing.data.entity.DRtuEntity;
import dms.standing.data.entity.DeviceTypeEntity;
import dms.standing.data.service.dobj.DObjService;
import dms.standing.data.service.drtu.DRtuService;
import dms.standing.data.service.sdev.SDevService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.trim;

@Component
public class DevConverter {
    private final DevObjService devObjService;
    private final SDevService sDevService;
    private final DObjService dObjService;
    private final DRtuService dRtuService;

    @Autowired
    public DevConverter(DevObjService devObjService, SDevService sDevService, DObjService dObjService, DRtuService dRtuService) {
        this.devObjService = devObjService;
        this.sDevService = sDevService;
        this.dObjService = dObjService;
        this.dRtuService = dRtuService;
    }

    public DevDTO convertEntityToDto(DeviceEntity deviceEntity) {
        DevDTO devDTO = new DevDTO();

        devDTO.setId(deviceEntity.getId());

        devDTO.setTypeId(deviceEntity.getType().getId());
        devDTO.setTypeName(deviceEntity.getType().getName());

        devDTO.setTypeGroupId(deviceEntity.getType().getDeviceTypeGroup().getId());
        devDTO.setTypeGroupName(deviceEntity.getType().getDeviceTypeGroup().getName());

        devDTO.setNumber(deviceEntity.getNumber());
        devDTO.setReleaseYear(deviceEntity.getReleaseYear());
        devDTO.setTestDate(deviceEntity.getTestDate());
        devDTO.setNextTestDate(deviceEntity.getNextTestDate());
        devDTO.setReplacementPeriod(deviceEntity.getReplacementPeriod());
        devDTO.setStatusCode(deviceEntity.getStatus().getName());
        devDTO.setStatusComment(deviceEntity.getStatus().getComm());
        devDTO.setDetail(deviceEntity.getDetail());

        devDTO.setObjectId(deviceEntity.getObject().getId());
        devDTO.setObjectName(deviceEntity.getObject().getNameObject());

        if (deviceEntity.getLocation() != null) {
            devDTO.setPlaceId(deviceEntity.getLocation().getId());
            devDTO.setDescription(deviceEntity.getLocation().getDescription());
            devDTO.setRegion(deviceEntity.getLocation().getRegion());
            devDTO.setRegionTypeCode(deviceEntity.getLocation().getRegionType().getName());
            devDTO.setRegionTypeComment(deviceEntity.getLocation().getRegionType().getComm());
            devDTO.setLocate(deviceEntity.getLocation().getLocate());
            devDTO.setLocateTypeCode(deviceEntity.getLocation().getLocateType().getName());
            devDTO.setLocateTypeComment(deviceEntity.getLocation().getLocateType().getComm());
            devDTO.setPlaceNumber(deviceEntity.getLocation().getPlaceNumber());
            devDTO.setPlaceDetail(deviceEntity.getLocation().getDetail());
        }
        return devDTO;
    }

    public Page<DevDTO> convertEntityToDto(Page<DeviceEntity> page) {
        List<DevDTO> content = page.getContent().stream().map(this::convertEntityToDto).collect(Collectors.toList());
        return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
    }

    public DevFilter convertDtoToFilter(DevDTO devDTO) {
        DevFilter devFilter = new DevFilter();

        devFilter.setId(devDTO.getId());
        devFilter.setTypeName(devDTO.getTypeName());

        devFilter.setTypeGroupId(devDTO.getTypeGroupId());
        devFilter.setTypeGroupName(devDTO.getTypeGroupName());

        devFilter.setNumber(devDTO.getNumber());
        devFilter.setReleaseYear(devDTO.getReleaseYear());

        devFilter.setTestDate(devDTO.getTestDate());
        devFilter.setTestDateMin(devDTO.getTestDateMin());
        devFilter.setTestDateMax(devDTO.getTestDateMax());

        devFilter.setNextTestDate(devDTO.getNextTestDate());
        devFilter.setNextTestDateMin(devDTO.getNextTestDateMin());
        devFilter.setNextTestDateMax(devDTO.getNextTestDateMax());

        devFilter.setReplacementPeriod(devDTO.getReplacementPeriod());
        devFilter.setStatusCode(devDTO.getStatusCode());
        devFilter.setStatusComment(devDTO.getStatusComment());
        devFilter.setDetail(devDTO.getDetail());

        devFilter.setObjectName(devDTO.getObjectName());

        devFilter.setDescription(devDTO.getDescription());
        devFilter.setRegion(devDTO.getRegion());
        devFilter.setRegionTypeCode(devDTO.getRegionTypeCode());
        devFilter.setRegionTypeComment(devDTO.getRegionTypeComment());
        devFilter.setLocate(devDTO.getLocate());
        devFilter.setLocateTypeCode(devDTO.getLocateTypeCode());
        devFilter.setLocateTypeComment(devDTO.getLocateTypeComment());
        devFilter.setPlaceNumber(devDTO.getPlaceNumber());
        devFilter.setPlaceDetail(devDTO.getPlaceDetail());

        return devFilter;
    }

    public DeviceEntity convertDtoToEntity(DevDTO devDTO) {
        DeviceEntity deviceEntity = new DeviceEntity();

        deviceEntity.setLocation(resolveDevObj(devDTO.getPlaceId()));
        deviceEntity.setType(resolveSDev(devDTO.getTypeId()));
        deviceEntity.setNumber(devDTO.getNumber());
        deviceEntity.setReleaseYear(devDTO.getReleaseYear());
        deviceEntity.setStatus(resolveStatus(devDTO.getStatusCode()));
        deviceEntity.setId(devDTO.getId());
        deviceEntity.setNextTestDate(devDTO.getNextTestDate());
        deviceEntity.setTestDate(devDTO.getTestDate());
        deviceEntity.setReplacementPeriod(devDTO.getReplacementPeriod());
        deviceEntity.setObject(resolveDObjRtu(devDTO.getObjectId()));
        deviceEntity.setDetail(devDTO.getDetail());


        return deviceEntity;
    }


    private DeviceLocationEntity resolveDevObj(Long devObjId) {
        if (devObjId == null) return null;
        return devObjService.findDevObjById(devObjId)
                .orElseThrow(() -> new NoEntityException("Place for device (DeviceLocationEntity) with the id=" + devObjId + " not found"));
    }

    private DeviceTypeEntity resolveSDev(Long sDevId) {
        if (sDevId == null) return null;
        return (sDevService.findSDevByID(sDevId)
                .orElseThrow(() -> new NoEntityException("Device type (DeviceTypeEntity) with the id=" + sDevId + " not found")));
    }

    private Status resolveStatus(String statusCode) {
        if (statusCode == null) return null;
        for (Status status : Status.values()) {
            if (status.getName().equals(trim(statusCode))) {
                return status;
            }
        }
        throw new WrongDataException("Wrong Status Code");
    }

    private DObjRtuEntity resolveDObjRtu(String objectId) {
        if (objectId == null) return null;
        Optional<DRtuEntity> rtu = dRtuService.findById(objectId);
        if (rtu.isPresent()) {
            return rtu.get();
        }

        Optional<DObjEntity> place = dObjService.findById(objectId);
        if (place.isPresent()) {
            return place.get();
        }

        throw new NoEntityException("Object (objectId) with the id=" + objectId + " not found");
    }
}
