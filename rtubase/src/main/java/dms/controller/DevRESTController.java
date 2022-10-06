package dms.controller;


import dms.dto.DevRequestDTO;
import dms.model.DevModel;
import dms.service.DevService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/devs")
public class DevRESTController {

    @Autowired
    private DevService devService;

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/")
    public Page<DevModel> findAll(Pageable pageable, DevRequestDTO devRequestDTO) {
        return RestPreconditions.checkFound(devService.findDevsBySpecification(pageable, devRequestDTO));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping(value = "/{id}")
    public DevModel findById(@PathVariable("id") Long id) {
        return RestPreconditions.checkFound(devService.findDevsById(id));
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
    public DevModel create(@RequestBody DevModel devModel) {
//        devService.createDev(devModel);
        return RestPreconditions.checkFound(devService.createDev(devModel));
    }




}