package dms.controller;


import dms.dto.DeviceDTO;
import dms.entity.DeviceEntity;
import dms.entity.DeviceViewMainEntity;
import dms.export.DeviceReportExporter;
import dms.filter.Filter;
import dms.mapper.DeviceMapper;
import dms.service.device.DeviceService;
import dms.service.device.DeviceViewService;
import dms.validation.group.OnDeviceCreate;
import dms.validation.group.OnDeviceSet;
import dms.validation.group.OnDeviceUnset;
import dms.validation.group.OnDevicesReplace;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/api/devices/view")
@Validated
public class DeviceViewController {

    private final DeviceViewService deviceService;

    @Autowired
    public DeviceViewController(DeviceViewService deviceService) {
        this.deviceService = deviceService;
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

}