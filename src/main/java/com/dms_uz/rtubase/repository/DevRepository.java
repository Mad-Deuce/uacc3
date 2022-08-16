package com.dms_uz.rtubase.repository;

import com.dms_uz.rtubase.model.DevModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DevRepository extends JpaRepository<DevModel, Long> {

    Page<DevModel> findAllByPs(String ps, Pageable pageable);
}
