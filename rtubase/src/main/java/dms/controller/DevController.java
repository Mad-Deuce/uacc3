package dms.controller;


import dms.dto.DevDTO;
import dms.model.DevModel;
import dms.service.DevService;
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
    public DevController(
            @Qualifier("DevService1") DevService devService) {
        this.devService = devService;
    }

//    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
//    @GetMapping(value = "/")
//    public Page<DevModel> findAll(Pageable pageable, DevDTO devDTO) {
//        return devService.findDevsBySpecification(pageable, devDTO);
//    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/")
    public Page<DevDTO> findAllAlt(Pageable pageable, DevDTO devDTO) {
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
    public void update(@RequestBody DevModel devModel) {
        devService.updateDev(devModel);
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.POST)
    @PostMapping(value = "/")
    public DevDTO create(@RequestBody DevModel devModel) {
        return convert(devService.createDev(devModel));
    }

    private DevDTO convert(DevModel devModel) {
        DevDTO devDTO = new DevDTO();
        devDTO.setId(devModel.getId());
        devDTO.setGrid(devModel.getSDev().getGrid());
        return devDTO;
    }

    private Page<DevDTO> convert(Page<DevModel> page) {
        List<DevDTO> content = page.getContent().stream().map(this::convert).collect(Collectors.toList());
        return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
    }


}