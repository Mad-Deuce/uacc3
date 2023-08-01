package dms.controller;


import dms.config.multitenant.TenantIdentifierResolver;
import dms.dto.DeviceDTO;
import dms.entity.DeviceViewMainEntity;
import dms.filter.Filter;
import dms.mapper.DeviceMapper;
import dms.service.device.DeviceViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/devices/view")
@Validated
public class DeviceViewController {

    private final DeviceViewService deviceService;
    private final DeviceMapper deviceMapper;

    @Autowired
    public DeviceViewController(DeviceViewService deviceService, DeviceMapper deviceMapper) {
        this.deviceService = deviceService;
        this.deviceMapper = deviceMapper;
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/")
    public ResponseEntity<?> findAllDevices()  {
        List<DeviceViewMainEntity> devices = deviceService.findAllDevices();
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(devices);
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.POST)
    @PostMapping(value = "/by-filter", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findDevicesByFilterSpec(Pageable pageable,
                                                     @RequestBody(required = false) List<Filter<Object>> filters) {

        Page<DeviceDTO> devices = deviceMapper.entityPageToDtoPage(deviceService
                .findDevicesBySpecification(pageable, filters));
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(devices);
    }

}