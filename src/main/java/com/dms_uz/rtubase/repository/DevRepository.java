package com.dms_uz.rtubase.repository;

import com.dms_uz.rtubase.entity.DevEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DevRepository extends JpaRepository<DevEntity, Long> {

    Page<DevEntity> findAll (Pageable pageable);

}
