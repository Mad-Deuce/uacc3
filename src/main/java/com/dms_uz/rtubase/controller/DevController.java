package com.dms_uz.rtubase.controller;

import com.dms_uz.rtubase.model.DevModel;
import com.dms_uz.rtubase.service.DevService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DevController {
    @Autowired
    private DevService devService;


    @Autowired
    public void setDevService(DevService devService) {
        this.devService = devService;
    }

    @GetMapping("/devs")
    public String devList(
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(required = false) String ps,
            Model model,
            @PageableDefault(page = 0, size = 15)
            @SortDefault.SortDefaults({
                    @SortDefault(direction = Sort.Direction.DESC)
            })
            Pageable pageable) {

        Page<DevModel> devPage;

        if (ps != null) {
            devPage = devService.devsByPs(ps, pageable);
        } else {
            devPage = devService.allDevs(pageable);
        }

        PageWrapper<DevModel> page = new PageWrapper<DevModel>(devPage, "/devs");

        model.addAttribute("devs", page.getContent());
        model.addAttribute("page", page);
        model.addAttribute("sort", sort);
        return "devs";
    }

}
