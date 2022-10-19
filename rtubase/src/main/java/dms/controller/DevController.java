package dms.controller;


import dms.converter.StatusConverter;
import dms.dock.val.Status;
import dms.dto.DevDTO;
import dms.entity.DevEntity;
import dms.entity.DevObjEntity;
import dms.entity.standing.data.SDevEntity;
import dms.entity.standing.data.SDevgrpEntity;
import dms.filter.DevFilter;
import dms.service.dev.DevService;
import dms.service.dobj.DObjService;
import dms.service.drtu.DRtuService;
import dms.service.sdev.SDevService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.trim;


@RestController
@RequestMapping("/api/devs")
public class DevController {

    private final DevService devService;
    private final SDevService sDevService;
    private final DObjService dObjService;
    private final DRtuService dRtuService;

    @Autowired
    public DevController(@Qualifier("DevService1") DevService devService,
                         SDevService sDevService, DObjService dObjService, DRtuService dRtuService) {
        this.devService = devService;
        this.sDevService = sDevService;
        this.dObjService = dObjService;
        this.dRtuService = dRtuService;
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/")
    public Page<DevDTO> findAll(Pageable pageable, DevDTO devDTO) {
        return convert(devService.findDevsBySpecification(pageable, convert(devDTO)));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping(value = "/{id}")
    public DevDTO findById(@PathVariable("id") Long id) {
        return convert(devService.findDevById(id));
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.DELETE)
    @DeleteMapping(value = "/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        devService.deleteDevById(id);
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.PUT)
    @PutMapping(value = "/")
    public void update(@RequestBody DevEntity devModel) {
        devService.updateDev(devModel);
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.POST)
    @PostMapping(value = "/{id}")
    public DevDTO create(@PathVariable("id") Long id, @RequestBody DevEntity devModel) {
        return convert(devService.createDev(devModel));
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/test-convert-for-filter")
    public DevFilter testCFF(DevDTO devDTO) {
        return convert(devDTO);
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

    private DevFilter convert(DevDTO devDTO) {
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

}