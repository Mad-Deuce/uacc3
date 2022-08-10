package com.dms_uz.rtubase.controller;

import com.dms_uz.rtubase.entity.DevEntity;
import com.dms_uz.rtubase.service.DevService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DevController {
    @Autowired
    private DevService devService;


    @Autowired
    public void setDevService(DevService devService) {
        this.devService = devService;
    }


//    @GetMapping("/devs")
//    public String userList(Model model) {
//        model.addAttribute("allDevs", devService.allDevs());
//        return "devs";
//    }

    @GetMapping("/devs")
    public String list(Model model, Pageable pageable){
        Page<DevEntity> productPage = devService.allDevs(pageable);
        PageWrapper<DevEntity> page = new PageWrapper<DevEntity>(productPage, "/devs");
        model.addAttribute("devs", page.getContent());
        model.addAttribute("page", page);
        return "devs";
    }
}
