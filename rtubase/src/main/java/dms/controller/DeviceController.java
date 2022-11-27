package dms.controller;


import dms.converter.DevConverter;
import dms.dto.DeviceDTO;
import dms.entity.DeviceEntity;
import dms.mapper.DeviceMapper;
import dms.service.device.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceService deviceService;
    private final DevConverter devConverter;
    private final DeviceMapper deviceMapper;

    @Autowired
    public DeviceController(@Qualifier("DevService1") DeviceService deviceService,
                            DevConverter devConverter,
                            DeviceMapper deviceMapper) {
        this.deviceService = deviceService;
        this.devConverter = devConverter;
        this.deviceMapper = deviceMapper;
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/by-spec")
    public Page<DeviceDTO> findDevicesBySpecification(Pageable pageable, DeviceDTO deviceDTO) {
        return deviceMapper.entityToDTOPage(deviceService
                .findDevsBySpecification(pageable, deviceMapper.dTOToFilter(deviceDTO)));

//        return convertEntityToDto(deviceService
//                   .findDevsBySpecification(pageable, deviceMapper.dTOToFilter(deviceDTO)));
    }


//    private Page<DeviceDTO> convertEntityToDto(Page<DeviceEntity> page) {
//        List<DeviceDTO> content = page.getContent().stream().map(deviceMapper::entityToDTO).collect(Collectors.toList());
//        return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
//    }


    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/by-query")
    public Page<DeviceEntity> findAllByQuery(Pageable pageable, DeviceDTO deviceDTO) throws NoSuchFieldException {
        return deviceService.findDevsByQuery(pageable, devConverter.convertDtoToFilter(deviceDTO));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping(value = "/{id}")
    public DeviceDTO findById(@PathVariable("id") Long id) {
        return devConverter.convertEntityToDto(deviceService.findDevById(id));
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.DELETE)
    @DeleteMapping(value = "/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        deviceService.deleteDevById(id);
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.PUT)
    @PutMapping(value = "/{id}")
    public void updateById(@PathVariable("id") Long id,
                           @RequestBody DeviceDTO deviceDTO) {
        deviceService.updateDev(id, devConverter.convertDtoToEntity(deviceDTO), deviceDTO.getActiveProperties());
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.POST)
    @PostMapping(value = "/")
    public DeviceDTO create(@RequestBody DeviceDTO deviceDTO) {
        return devConverter.convertEntityToDto(deviceService.createDev(devConverter.convertDtoToEntity(deviceDTO)));
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/test-convert-for-filter")
    public DeviceEntity testCFF(DeviceDTO deviceDTO) {
        return devConverter.convertDtoToEntity(deviceDTO);
    }

}