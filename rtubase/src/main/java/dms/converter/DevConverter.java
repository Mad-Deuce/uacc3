package dms.converter;

import dms.dto.DeviceDTO;
import dms.entity.DeviceEntity;
import dms.entity.DeviceLocationEntity;
import dms.exception.NoEntityException;
import dms.exception.WrongDataException;
import dms.filter.DeviceFilter;
import dms.service.devobj.DevObjService;
import dms.standing.data.dock.val.Status;
import dms.standing.data.entity.LineObjectEntity;
import dms.standing.data.entity.ObjectEntity;
import dms.standing.data.entity.RtuObjectEntity;
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

    public DeviceDTO convertEntityToDto(DeviceEntity deviceEntity) {
        DeviceDTO deviceDTO = new DeviceDTO();

        deviceDTO.setId(deviceEntity.getId());

        deviceDTO.setTypeId(deviceEntity.getType().getId());
        deviceDTO.setTypeName(deviceEntity.getType().getName());

        deviceDTO.setTypeGroupId(deviceEntity.getType().getGroup().getId());
        deviceDTO.setTypeGroupName(deviceEntity.getType().getGroup().getName());

        deviceDTO.setNumber(deviceEntity.getNumber());
        deviceDTO.setReleaseYear(deviceEntity.getReleaseYear());
        deviceDTO.setTestDate(deviceEntity.getTestDate());
        deviceDTO.setNextTestDate(deviceEntity.getNextTestDate());
        deviceDTO.setReplacementPeriod(deviceEntity.getReplacementPeriod());
        deviceDTO.setStatusName(deviceEntity.getStatus().getName());
        deviceDTO.setStatusComment(deviceEntity.getStatus().getComm());
        deviceDTO.setDetail(deviceEntity.getDetail());

        deviceDTO.setObjectId(deviceEntity.getObject().getId());
        deviceDTO.setObjectName(deviceEntity.getObject().getName());

        if (deviceEntity.getLocation() != null) {
            deviceDTO.setLocationId(deviceEntity.getLocation().getId());
            deviceDTO.setDescription(deviceEntity.getLocation().getDescription());
            deviceDTO.setRegion(deviceEntity.getLocation().getRegion());
            deviceDTO.setRegionTypeName(deviceEntity.getLocation().getRegionType().getName());
            deviceDTO.setRegionTypeComment(deviceEntity.getLocation().getRegionType().getComm());
            deviceDTO.setLocate(deviceEntity.getLocation().getLocate());
            deviceDTO.setLocateTypeName(deviceEntity.getLocation().getLocateType().getName());
            deviceDTO.setLocateTypeComment(deviceEntity.getLocation().getLocateType().getComm());
            deviceDTO.setPlaceNumber(deviceEntity.getLocation().getPlaceNumber());
            deviceDTO.setLocationDetail(deviceEntity.getLocation().getDetail());
        }
        return deviceDTO;
    }

    public Page<DeviceDTO> convertEntityToDto(Page<DeviceEntity> page) {
        List<DeviceDTO> content = page.getContent().stream().map(this::convertEntityToDto).collect(Collectors.toList());
        return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
    }

    public DeviceFilter convertDtoToFilter(DeviceDTO deviceDTO) {
        DeviceFilter deviceFilter = new DeviceFilter();

        deviceFilter.setId(deviceDTO.getId());
        deviceFilter.setTypeName(deviceDTO.getTypeName());

        deviceFilter.setTypeGroupId(deviceDTO.getTypeGroupId());
        deviceFilter.setTypeGroupName(deviceDTO.getTypeGroupName());

        deviceFilter.setNumber(deviceDTO.getNumber());
        deviceFilter.setReleaseYear(deviceDTO.getReleaseYear());

        deviceFilter.setTestDate(deviceDTO.getTestDate());
        deviceFilter.setTestDateMin(deviceDTO.getTestDateMin());
        deviceFilter.setTestDateMax(deviceDTO.getTestDateMax());

        deviceFilter.setNextTestDate(deviceDTO.getNextTestDate());
        deviceFilter.setNextTestDateMin(deviceDTO.getNextTestDateMin());
        deviceFilter.setNextTestDateMax(deviceDTO.getNextTestDateMax());

        deviceFilter.setReplacementPeriod(deviceDTO.getReplacementPeriod());
        deviceFilter.setStatusName(deviceDTO.getStatusName());
        deviceFilter.setStatusComment(deviceDTO.getStatusComment());
        deviceFilter.setDetail(deviceDTO.getDetail());

        deviceFilter.setObjectName(deviceDTO.getObjectName());

        deviceFilter.setDescription(deviceDTO.getDescription());
        deviceFilter.setRegion(deviceDTO.getRegion());
        deviceFilter.setRegionTypeName(deviceDTO.getRegionTypeName());
        deviceFilter.setRegionTypeComment(deviceDTO.getRegionTypeComment());
        deviceFilter.setLocate(deviceDTO.getLocate());
        deviceFilter.setLocateTypeName(deviceDTO.getLocateTypeName());
        deviceFilter.setLocateTypeComment(deviceDTO.getLocateTypeComment());
        deviceFilter.setPlaceNumber(deviceDTO.getPlaceNumber());
        deviceFilter.setLocationDetail(deviceDTO.getLocationDetail());

        return deviceFilter;
    }

    public DeviceEntity convertDtoToEntity(DeviceDTO deviceDTO) {
        DeviceEntity deviceEntity = new DeviceEntity();

        deviceEntity.setLocation(resolveDevObj(deviceDTO.getLocationId()));
        deviceEntity.setType(resolveSDev(deviceDTO.getTypeId()));
        deviceEntity.setNumber(deviceDTO.getNumber());
        deviceEntity.setReleaseYear(deviceDTO.getReleaseYear());
        deviceEntity.setStatus(resolveStatus(deviceDTO.getStatusName()));
        deviceEntity.setId(deviceDTO.getId());
        deviceEntity.setNextTestDate(deviceDTO.getNextTestDate());
        deviceEntity.setTestDate(deviceDTO.getTestDate());
        deviceEntity.setReplacementPeriod(deviceDTO.getReplacementPeriod());
        deviceEntity.setObject(resolveDObjRtu(deviceDTO.getObjectId()));
        deviceEntity.setDetail(deviceDTO.getDetail());


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

    private ObjectEntity resolveDObjRtu(String objectId) {
        if (objectId == null) return null;
        Optional<RtuObjectEntity> rtu = dRtuService.findById(objectId);
        if (rtu.isPresent()) {
            return rtu.get();
        }

        Optional<LineObjectEntity> place = dObjService.findById(objectId);
        if (place.isPresent()) {
            return place.get();
        }

        throw new NoEntityException("Object (objectId) with the id=" + objectId + " not found");
    }
}
