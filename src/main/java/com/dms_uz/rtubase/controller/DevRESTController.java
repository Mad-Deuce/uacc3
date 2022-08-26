package com.dms_uz.rtubase.controller;


import com.dms_uz.rtubase.dto.DevRequestDTO;
import com.dms_uz.rtubase.model.DevModel;
import com.dms_uz.rtubase.service.DevService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/devs")
public class DevRESTController {

    @Autowired
    private DevService devService;

    @GetMapping(value = "/")
    public Page<DevModel> findAll(Pageable pageable, DevRequestDTO devRequestDTO) {
        return RestPreconditions.checkFound(devService.findDevsBySpecification(pageable, devRequestDTO));
    }

}