package dms.standing.data.controller;


import dms.standing.data.entity.DeviceTypeEntity;
import dms.standing.data.entity.DeviceTypeGroupEntity;
import dms.standing.data.service.device.type.DeviceTypeService;
import dms.standing.data.service.device.type.group.DeviceTypeGroupService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/standing-data/types")
@Validated
public class DeviceTypeController {

    private final DeviceTypeGroupService deviceTypeGroupService;
    private final DeviceTypeService deviceTypeService;

    @Autowired
    public DeviceTypeController(DeviceTypeGroupService deviceTypeGroupService,
                                DeviceTypeService deviceTypeService) {
        this.deviceTypeGroupService = deviceTypeGroupService;
        this.deviceTypeService = deviceTypeService;
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/group")
    public ResponseEntity<?> findAllGroups() {
        List<DeviceTypeGroupEntity> groups = deviceTypeGroupService.findAllGroups();
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(groups);
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/")
    public ResponseEntity<?> findAllTypes() {
        List<DeviceTypeEntity> groups = deviceTypeService.findAllTypes();
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(groups);
    }
}