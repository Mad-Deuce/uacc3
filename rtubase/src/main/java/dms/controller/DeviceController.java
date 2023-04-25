package dms.controller;


import dms.dto.DeviceDTO;
import dms.entity.DeviceEntity;
import dms.export.DeviceReportExporter;
import dms.filter.Filter;
import dms.mapper.DeviceMapper;
import dms.service.device.DeviceService;
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
@RequestMapping("/api/devices")
@Validated
public class DeviceController {

    private final DeviceService deviceService;
    private final DeviceMapper deviceMapper;

    @Autowired
    public DeviceController(@Qualifier("DevService1") DeviceService deviceService,
                            DeviceMapper deviceMapper) {
        this.deviceService = deviceService;
        this.deviceMapper = deviceMapper;
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/by-filter")
    public ResponseEntity<?> findDevicesByFilter(Pageable pageable, DeviceDTO deviceDTO) throws NoSuchFieldException {
        Page<DeviceDTO> devices = deviceMapper.entityToDTOPage(deviceService
                .findDevicesByFilter(pageable, deviceMapper.dTOToFilter(deviceDTO)));
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(devices);
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.POST)
    @PostMapping(value = "/by-filter-spec", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findDevicesByFilterSpec(Pageable pageable,
                                                     @RequestBody(required = false) List<Filter<Object>> filters) {
        Page<DeviceDTO> devices = deviceMapper.entityToDTOPage(deviceService
                .findDevicesBySpecification(pageable, filters));
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(devices);
    }


    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/exportToXls")
    public void exportDevicesByFilter(Pageable pageable, DeviceDTO deviceDTO, HttpServletResponse response)
            throws NoSuchFieldException, IOException, IllegalAccessException {

//        pageable = PageRequest.of(0, Integer.MAX_VALUE);

        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=rep_devices" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<DeviceDTO> devicesList = deviceMapper.entityToDTOPage(deviceService
                .findDevicesByFilter(pageable, deviceMapper.dTOToFilter(deviceDTO))).getContent();

        DeviceReportExporter deviceReportExporter = new DeviceReportExporter();
        XSSFWorkbook workbook = deviceReportExporter.generateWorkbook(devicesList, currentDateTime, deviceDTO);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();

    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> findDeviceById(@PathVariable("id") Long id) {
        DeviceDTO device = deviceMapper.entityToDTO(deviceService.findDeviceById(id));
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(device);
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.DELETE)
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteDeviceById(@PathVariable("id") @NotNull Long id) {
        DeviceEntity deviceEntity = deviceService.findDeviceById(id);
        deviceService.deleteDeviceById(deviceEntity);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .build();
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.POST)
    @PostMapping(value = "/")
    @Validated(OnDeviceCreate.class)
    public ResponseEntity<?> createDevice(@RequestBody @Valid DeviceDTO deviceDTO) {
        DeviceDTO dto = deviceMapper.entityToDTO(deviceService.createDevice(deviceMapper.dTOToEntity(deviceDTO)));
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto);
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.PUT)
    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateDeviceById(@PathVariable("id") Long id, @RequestBody DeviceDTO deviceDTO) {
        deviceService.updateDevice(id, deviceMapper.dTOToEntity(deviceDTO), deviceDTO.getActiveProperties());
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .build();
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.PUT)
    @PutMapping(value = "/replace/{id}")
    @Validated(OnDevicesReplace.class)
    public ResponseEntity<?> replaceDeviceById(@PathVariable("id") Long oldDeviceId,
                                               @Valid @RequestBody DeviceDTO newDeviceDTO) {
        DeviceEntity oldDeviceEntity = deviceService.findDeviceById(oldDeviceId);
        DeviceEntity newDeviceEntity = deviceService.findDeviceById(newDeviceDTO.getId());
        deviceService.replaceDevice(oldDeviceEntity, newDeviceEntity, newDeviceDTO.getStatus(), newDeviceDTO.getReplacementType());
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .build();

    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.PUT)
    @PutMapping(value = "/set/{id}")
    @Validated(OnDeviceSet.class)
    public ResponseEntity<?> setDeviceById(@PathVariable("id") Long deviceId,
                                           @Valid @RequestBody DeviceDTO deviceDTO) {
        DeviceEntity deviceEntity = deviceService.findDeviceById(deviceId);
        deviceService.setDeviceTo(deviceEntity, deviceDTO.getStatus(), deviceDTO.getFacilityId(), deviceDTO.getLocationId());
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .build();
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.PUT)
    @PutMapping(value = "/unset/{id}")
    @Validated(OnDeviceUnset.class)
    public ResponseEntity<?> unsetDeviceById(@PathVariable("id") Long deviceId,
                                             @Valid @RequestBody DeviceDTO deviceDTO) {
        DeviceEntity deviceEntity = deviceService.findDeviceById(deviceId);
        deviceService.unsetDevice(deviceEntity, deviceDTO.getFacilityId());
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .build();
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.PUT)
    @PutMapping(value = "/decommission/{id}")
    @Validated(OnDeviceUnset.class)
    public ResponseEntity<?> decommissionDeviceById(@PathVariable("id") Long deviceId) {
        DeviceEntity deviceEntity = deviceService.findDeviceById(deviceId);
        deviceService.decommissionDevice(deviceEntity);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .build();

    }
}