package dms.controller;


import dms.dto.DeviceDTO;
import dms.exception.DeviceValidationException;
import dms.export.DeviceReportExporter;
import dms.mapper.DeviceMapper;
import dms.service.device.DeviceService;
import dms.validation.group.OnDeviceCreate;
import dms.validation.group.OnDevicesReplace;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
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
import java.util.NoSuchElementException;


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

    @Deprecated
    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/by-spec")
    public Page<DeviceDTO> findDevicesBySpecification(Pageable pageable, DeviceDTO deviceDTO) {
        return deviceMapper.entityToDTOPage(deviceService
                .findDevicesBySpecification(pageable, deviceMapper.dTOToFilter(deviceDTO)));
    }

    @Deprecated
    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/by-query")
    public Page<DeviceDTO> findDevicesByQuery(Pageable pageable, DeviceDTO deviceDTO) throws NoSuchFieldException {
        return deviceMapper.entityToDTOPage(deviceService
                .findDevicesByQuery(pageable, deviceMapper.dTOToFilter(deviceDTO)));
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/by-filter")
    public ResponseEntity<?> findDevicesByFilter(Pageable pageable, DeviceDTO deviceDTO) {

        Page<DeviceDTO> devices;
        try {
            devices = deviceMapper.entityToDTOPage(deviceService
                    .findDevicesByQuery(pageable, deviceMapper.dTOToFilter(deviceDTO)));
        } catch (RuntimeException | NoSuchFieldException e) {
            return ResponseEntity.unprocessableEntity()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(e)
                    ;
        }
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(devices);
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/exportToXls")
    public void exportDevicesByQuery(Pageable pageable, DeviceDTO deviceDTO, HttpServletResponse response)
            throws NoSuchFieldException, IOException, IllegalAccessException {

//        pageable = PageRequest.of(0, Integer.MAX_VALUE);

        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=rep_devices" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);


        List<DeviceDTO> devicesList = deviceMapper.entityToDTOPage(deviceService
                .findDevicesByQuery(pageable, deviceMapper.dTOToFilter(deviceDTO))).getContent();

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

        DeviceDTO device;
        try {
            device = deviceMapper.entityToDTO(deviceService.findDeviceById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.unprocessableEntity()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(e)
                    ;
        }
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(device);
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.DELETE)
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteDeviceById(@PathVariable("id") @NotNull Long id) {

        try {
            deviceService.deleteDeviceById(id);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.unprocessableEntity()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(e.getMessage());
        }
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .build();

    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.POST)
    @PostMapping(value = "/")
    @Validated(OnDeviceCreate.class)
    public ResponseEntity<?> createDevice(@Valid @RequestBody DeviceDTO deviceDTO) {
        DeviceDTO dto;
        try {
            dto = deviceMapper.entityToDTO(deviceService.createDevice(deviceMapper.dTOToEntity(deviceDTO)));
        } catch (DeviceValidationException e) {
            return ResponseEntity.unprocessableEntity()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(e);
        }
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto);
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.PUT)
    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateDeviceById(@PathVariable("id") Long id, @RequestBody DeviceDTO deviceDTO) {

        try {
            deviceService.updateDevice(id, deviceMapper.dTOToEntity(deviceDTO), deviceDTO.getActiveProperties());
        } catch (RuntimeException e) {
            return ResponseEntity.unprocessableEntity()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(e);
        }
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(null);

    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.PUT)
    @PutMapping(value = "/replace/{id}")
    @Validated(OnDevicesReplace.class)
    public ResponseEntity<?> replaceDeviceById(@PathVariable("id") Long oldDeviceId,@Valid @RequestBody DeviceDTO newDeviceDTO) {

        try {
            deviceService.replaceDevice(oldDeviceId, newDeviceDTO.getId(), newDeviceDTO.getReplacementType());
        } catch (RuntimeException e) {
            return ResponseEntity.unprocessableEntity()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(e);
        }
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(null);

    }

}