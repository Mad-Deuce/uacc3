package com.dms_uz.rtubase.service;


import com.dms_uz.rtubase.dto.DevRequestDTO;
import com.dms_uz.rtubase.entity.DevEntity;
import com.dms_uz.rtubase.entity.SDevEntity;
import com.dms_uz.rtubase.model.DevModel;
import com.dms_uz.rtubase.repository.DevRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.List;

@Service
public class DevService {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    DevRepository devRepository;

    //    Find By Condition
    public Page<DevModel> devs(Pageable pageable, DevRequestDTO devRequestDTO) {
        return devRepository.findAll(pageable);
    }


    public Page<DevModel> findDevsBySpecification(Pageable pageable, DevRequestDTO devRequestDTO) {
        return devRepository.findAll(getSpecification(devRequestDTO), pageable);
    }


    private Specification<DevRequestDTO> getSpecification(DevRequestDTO devRequestDTO) {
        //Build Specification with Dev Id and Filter Text
        return (root, criteriaQuery, criteriaBuilder) ->
        {
            Join<DevModel, SDevEntity> sDev = root.join("sDev");

            criteriaQuery.distinct(true);

            Predicate predicateForId;
            Predicate predicateForGrid;

            if (devRequestDTO.getId() != null) {
                predicateForId = criteriaBuilder.equal(root.get("id"), devRequestDTO.getId());
            } else {
                predicateForId = criteriaBuilder.equal(root.get("id"), root.get("id"));
            }

            if (devRequestDTO.getGrid() != null) {
                predicateForGrid = criteriaBuilder.equal(sDev.get("grid"), devRequestDTO.getGrid());
            } else {
                predicateForGrid = criteriaBuilder.equal(sDev.get("grid"), sDev.get("grid"));
            }

            return criteriaBuilder.and(predicateForId, predicateForGrid);
        };
    }





}
