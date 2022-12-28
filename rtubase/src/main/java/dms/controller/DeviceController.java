package dms.controller;


import dms.converter.DevConverter;
import dms.dto.DeviceDTO;
import dms.entity.DeviceEntity;
import dms.mapper.DeviceMapper;
import dms.service.device.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceService deviceService;
    private final DevConverter devConverter;
    private final DeviceMapper deviceMapper;

    @Autowired
    public DeviceController(@Qualifier("DevService1") DeviceService deviceService,
                            DevConverter devConverter, DeviceMapper deviceMapper) {
        this.deviceService = deviceService;
        this.devConverter = devConverter;
        this.deviceMapper = deviceMapper;
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/by-spec")
    public Page<DeviceDTO> findDevicesBySpecification(Pageable pageable, DeviceDTO deviceDTO) {
        return deviceMapper.entityToDTOPage(deviceService
                .findDevicesBySpecification(pageable, deviceMapper.dTOToFilter(deviceDTO)));
    }


    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/by-query")
    public Page<DeviceDTO> findDevicesByQuery(Pageable pageable, DeviceDTO deviceDTO) throws NoSuchFieldException {
        return deviceMapper.entityToDTOPage(deviceService
                .findDevicesByQuery(pageable, deviceMapper.dTOToFilter(deviceDTO)));
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/{id}")
    public DeviceDTO findDeviceById(@PathVariable("id") Long id) {
        return deviceMapper.entityToDTO(deviceService.findDeviceById(id));
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

}