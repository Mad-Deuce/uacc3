package dms.controller;


import dms.converter.DevConverter;
import dms.dto.DevDTO;
import dms.entity.DevEntity;
import dms.service.dev.DevService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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
    public Page<DevDTO> findAll(Pageable pageable, DevDTO devDTO) {
        return devConverter.convertEntityToDto(devService
                .findDevsBySpecification(pageable, devConverter.convertDtoToFilter(devDTO)));
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/by-query")
    public List findAllByQuery(Pageable pageable, DevDTO devDTO) {
        return devService.findDevsByQuery(pageable, devConverter.convertDtoToFilter(devDTO));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping(value = "/{id}")
    public DevDTO findById(@PathVariable("id") Long id) {
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
                           @RequestBody DevDTO devDTO) {
        devService.updateDev(id, devConverter.convertDtoToEntity(devDTO), devDTO.getActiveProperties());
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.POST)
    @PostMapping(value = "/")
    public DevDTO create(@RequestBody DevDTO devDTO) {
        return devConverter.convertEntityToDto(devService.createDev(devConverter.convertDtoToEntity(devDTO)));
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/test-convert-for-filter")
    public DevEntity testCFF(DevDTO devDTO) {
        return devConverter.convertDtoToEntity(devDTO);
    }

}