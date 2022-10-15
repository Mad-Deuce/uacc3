package dms.controller;


import dms.dto.DevDTO;
import dms.entity.DevEntity;
import dms.service.dev.DevService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/devs")
public class DevController {

    private final DevService devService;

    @Autowired
    public DevController(@Qualifier("DevService1") DevService devService) {
        this.devService = devService;
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/")
    public Page<DevDTO> findAll(Pageable pageable, DevDTO devDTO) {
        return convert(devService.findDevsBySpecification(pageable, devDTO));
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

    private DevDTO convert(DevEntity devEntity) {
        DevDTO devDTO = new DevDTO();
        devDTO.setId(devEntity.getId());
        devDTO.setDeviceTypeId(devEntity.getSDev().getId());
        devDTO.setDeviceTypeGroupId(devEntity.getSDev().getGrid().getGrid());
        devDTO.setGroup(devEntity.getSDev().getGrid().getName());
        devDTO.setType(devEntity.getSDev().getDtype());
        devDTO.setNumber(devEntity.getNum());
        devDTO.setReleaseYear(devEntity.getMyear());
        devDTO.setTestDate(devEntity.getDTkip());
        devDTO.setNextTestDate(devEntity.getDNkip());
        devDTO.setStatusCode(devEntity.getStatus().getName());
        devDTO.setStatusDescription(devEntity.getStatus().getComm());
        devDTO.setDeviceDetail(devEntity.getDetail());
        devDTO.setObjectId(devEntity.getDObjRtu().getId());
        devDTO.setObjectName(devEntity.getDObjRtu().getNameObject());
        if (devEntity.getDevObj() != null) {
            devDTO.setPlaceId(devEntity.getDevObj().getId());
            devDTO.setDescription(devEntity.getDevObj().getNshem());
            devDTO.setRegion(devEntity.getDevObj().getRegion());
            devDTO.setRegionTypeCode(devEntity.getDevObj().getRegionType().getName());
            devDTO.setRegionTypeDescription(devEntity.getDevObj().getRegionType().getComm());
            devDTO.setLocate(devEntity.getDevObj().getLocate());
            devDTO.setLocateTypeCode(devEntity.getDevObj().getLocateType().getName());
            devDTO.setLocateTypeDescription(devEntity.getDevObj().getLocateType().getComm());
            devDTO.setPlaceNumber(devEntity.getDevObj().getNplace());
            devDTO.setPlaceDetail(devEntity.getDevObj().getDetail());
        }
        return devDTO;
    }

    private Page<DevDTO> convert(Page<DevEntity> page) {
        List<DevDTO> content = page.getContent().stream().map(this::convert).collect(Collectors.toList());
        return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
    }

    private DevEntity convert(DevDTO devDTO) {
        DevEntity devEntity = new DevEntity();
        devEntity.setId(devDTO.getId());
        return devEntity;
    }

}