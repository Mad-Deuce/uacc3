package dms.converter;

import dms.dto.DevDTO;
import dms.entity.DevEntity;
import dms.entity.DevObjEntity;
import dms.exception.NoEntityException;
import dms.exception.WrongDataException;
import dms.filter.DevFilter;
import dms.service.devobj.DevObjService;
import dms.standing.data.dock.val.Status;
import dms.standing.data.entity.DObjEntity;
import dms.standing.data.entity.DObjRtuEntity;
import dms.standing.data.entity.DRtuEntity;
import dms.standing.data.entity.SDevEntity;
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

    private DevDTO convert(DevEntity devEntity) {
        DevDTO devDTO = new DevDTO();

        devDTO.setId(devEntity.getId());

        devDTO.setTypeId(devEntity.getSDev().getId());
        devDTO.setTypeName(devEntity.getSDev().getDtype());

        devDTO.setTypeGroupId(devEntity.getSDev().getGrid().getGrid());
        devDTO.setTypeGroupName(devEntity.getSDev().getGrid().getName());

        devDTO.setNumber(devEntity.getNum());
        devDTO.setReleaseYear(devEntity.getMyear());
        devDTO.setTestDate(devEntity.getDTkip());
        devDTO.setNextTestDate(devEntity.getDNkip());
        devDTO.setReplacementPeriod(devEntity.getTZam());
        devDTO.setStatusCode(devEntity.getStatus().getName());
        devDTO.setStatusComment(devEntity.getStatus().getComm());
        devDTO.setDetail(devEntity.getDetail());

        devDTO.setObjectId(devEntity.getDObjRtu().getId());
        devDTO.setObjectName(devEntity.getDObjRtu().getNameObject());

        if (devEntity.getDevObj() != null) {
            devDTO.setPlaceId(devEntity.getDevObj().getId());
            devDTO.setDescription(devEntity.getDevObj().getNshem());
            devDTO.setRegion(devEntity.getDevObj().getRegion());
            devDTO.setRegionTypeCode(devEntity.getDevObj().getRegionType().getName());
            devDTO.setRegionTypeComment(devEntity.getDevObj().getRegionType().getComm());
            devDTO.setLocate(devEntity.getDevObj().getLocate());
            devDTO.setLocateTypeCode(devEntity.getDevObj().getLocateType().getName());
            devDTO.setLocateTypeComment(devEntity.getDevObj().getLocateType().getComm());
            devDTO.setPlaceNumber(devEntity.getDevObj().getNplace());
            devDTO.setPlaceDetail(devEntity.getDevObj().getDetail());
        }
        return devDTO;
    }

    private Page<DevDTO> convert(Page<DevEntity> page) {
        List<DevDTO> content = page.getContent().stream().map(this::convert).collect(Collectors.toList());
        return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
    }

    private DevFilter convertToFilter(DevDTO devDTO) {
        DevFilter devFilter = new DevFilter();

        devFilter.setId(devDTO.getId());
        devFilter.setTypeName(devDTO.getTypeName());

        devFilter.setTypeGroupId(devDTO.getTypeGroupId());
        devFilter.setTypeGroupName(devDTO.getTypeGroupName());

        devFilter.setNumber(devDTO.getNumber());
        devFilter.setReleaseYear(devDTO.getReleaseYear());
        devFilter.setTestDate(devDTO.getTestDate());
        devFilter.setNextTestDate(devDTO.getNextTestDate());
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

    public DevEntity dtoToEntity(DevDTO devDTO) {
        DevEntity devEntity = new DevEntity();

        devEntity.setDevObj(resolveDevObj(devDTO.getPlaceId()));
        devEntity.setSDev(resolveSDev(devDTO.getTypeId()));
        devEntity.setNum(devDTO.getNumber());
        devEntity.setMyear(devDTO.getReleaseYear());
        devEntity.setStatus(resolveStatus(devDTO.getStatusCode()));
        devEntity.setId(devDTO.getId());
        devEntity.setDNkip(devDTO.getNextTestDate());
        devEntity.setDTkip(devDTO.getTestDate());
        devEntity.setTZam(devDTO.getReplacementPeriod());
        devEntity.setDObjRtu(resolveDObjRtu(devDTO.getObjectId()));
        devEntity.setDetail(devDTO.getDetail());


        return devEntity;
    }


    private DevObjEntity resolveDevObj(Long devObjId) {
        if (devObjId == null) return null;
        return devObjService.findDevObjById(devObjId)
                .orElseThrow(() -> new NoEntityException("Place for device (DevObjEntity) with the id=" + devObjId + " not found"));
    }

    private SDevEntity resolveSDev(Long sDevId) {
        if (sDevId == null) throw new WrongDataException("Id for device type (sDevId) not be NULL");
        return (sDevService.findSDevByID(sDevId)
                .orElseThrow(() -> new NoEntityException("Device type (SDevEntity) with the id=" + sDevId + " not found")));
    }

    private Status resolveStatus(String statusCode) {
        if (statusCode == null) statusCode = "31";
        if (trim(statusCode).equals("")) statusCode = "31";
        for (Status status : Status.values()) {
            if (status.getName().equals(trim(statusCode))) {
                return status;
            }
        }
        throw new WrongDataException("Wrong Status Code");
    }

    private DObjRtuEntity resolveDObjRtu(String objectId) {
        if (objectId == null) throw new WrongDataException("Id for object (DObjRtuId) not be NULL");
        if (trim(objectId).equals("")) throw new WrongDataException("Id for object (DObjRtuId) not be NULL");
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
