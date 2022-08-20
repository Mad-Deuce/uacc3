package com.dms_uz.rtubase.controller;


import com.dms_uz.rtubase.model.DevModel;
import com.dms_uz.rtubase.service.DevService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dev")
public class DevRESTController {

    @Autowired
    private DevService devService;

    @GetMapping(value = "/")
    public Page<DevModel> findAll(Pageable pageable) {
        return RestPreconditions.checkFound(devService.allDevs(pageable));
    }

    @GetMapping(value = "/{id}")
    public List<DevModel> findById(@PathVariable("id") Long id) {
        return RestPreconditions.checkFound(devService.devsById(id));
    }

    @GetMapping(value = "/ps/{ps}")
    public List<DevModel> findByPs(@PathVariable("ps") String ps) {
        return RestPreconditions.checkFound(devService.devsByPs(ps));
    }


}
