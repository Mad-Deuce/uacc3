package com.dms_uz.rtubase.service;


import com.dms_uz.rtubase.entity.DevEntity;
import com.dms_uz.rtubase.model.DevModel;
import com.dms_uz.rtubase.repository.DevRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class DevService {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    DevRepository devRepository;

    public List<DevModel> allDevs() {
        return devRepository.findAll();
    }

    public Page<DevModel> allDevs(Pageable pageable) {
        return devRepository.findAll(pageable);
    }

    public Page<DevModel> devsByPs(String ps, Pageable pageable) {
        return devRepository.findAllByPs(ps, pageable);
    }

    public DevModel devsById(Long id) {
        return devRepository.findAllById(id);
    }
}
