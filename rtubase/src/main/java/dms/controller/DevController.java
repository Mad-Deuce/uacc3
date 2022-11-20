package dms.controller;


import dms.converter.DevConverter;
import dms.dto.DeviceDTO;
import dms.entity.DeviceEntity;
import dms.service.dev.DevService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/devs")
public class DevController {

    private final DevService devService;
    private final DevConverter devConverter;

    @Autowired
    public DevController(@Qualifier("DevService1") DevService devService, DevConverter devConverter) {
        this.devService = devService;
        this.devConverter = devConverter;
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/")
    public Page<DeviceDTO> findAll(Pageable pageable, DeviceDTO deviceDTO) {
        return devConverter.convertEntityToDto(devService
                .findDevsBySpecification(pageable, devConverter.convertDtoToFilter(deviceDTO)));
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/by-query")
    public Page<DeviceEntity> findAllByQuery(Pageable pageable, DeviceDTO deviceDTO) throws NoSuchFieldException {
        return devService.findDevsByQuery(pageable, devConverter.convertDtoToFilter(deviceDTO));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping(value = "/{id}")
    public DeviceDTO findById(@PathVariable("id") Long id) {
        return devConverter.convertEntityToDto(devService.findDevById(id));
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.DELETE)
    @DeleteMapping(value = "/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        devService.deleteDevById(id);
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.PUT)
    @PutMapping(value = "/{id}")
    public void updateById(@PathVariable("id") Long id,
                           @RequestBody DeviceDTO deviceDTO) {
        devService.updateDev(id, devConverter.convertDtoToEntity(deviceDTO), deviceDTO.getActiveProperties());
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.POST)
    @PostMapping(value = "/")
    public DeviceDTO create(@RequestBody DeviceDTO deviceDTO) {
        return devConverter.convertEntityToDto(devService.createDev(devConverter.convertDtoToEntity(deviceDTO)));
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/test-convert-for-filter")
    public DeviceEntity testCFF(DeviceDTO deviceDTO) {
        return devConverter.convertDtoToEntity(deviceDTO);
    }

}