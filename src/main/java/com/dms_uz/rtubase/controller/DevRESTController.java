package com.dms_uz.rtubase.controller;


import com.dms_uz.rtubase.model.DevModel;
import com.dms_uz.rtubase.service.DevService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dev")
public class DevRESTController {

    @Autowired
    private DevService devService;

    @GetMapping(value = "/{id}")
    public DevModel findById(@PathVariable("id") Long id) {
        return RestPreconditions.checkFound(devService.devsById(id));
    }


}
