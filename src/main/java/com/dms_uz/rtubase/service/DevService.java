package com.dms_uz.rtubase.service;


import com.dms_uz.rtubase.entity.DevEntity;
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

    public List<DevEntity> allDevs() {
        return devRepository.findAll();
    }

    public Page<DevEntity> allDevs(Pageable pageable) {
        return devRepository.findAll(pageable);
    }


}
