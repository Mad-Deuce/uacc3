package dms.service;


import dms.dto.DevRequestDTO;
import dms.entity.SDevEntity;
import dms.model.DevModel;
import dms.repository.DevRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

@Slf4j
@Service
public class DevService {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    DevRepository devRepository;

    public DevModel findDevsById(Long id) {
        return devRepository.getOne(id);
    }

    public Page<DevModel> findDevsBySpecification(Pageable pageable, DevRequestDTO devRequestDTO) {
        return devRepository.findAll(getSpecification(devRequestDTO), pageable);
    }


    private Specification<DevRequestDTO> getSpecification(DevRequestDTO devRequestDTO) {
        //Build Specification with Dev Id and Filter Text
        return (root, criteriaQuery, criteriaBuilder) ->
        {
            Join<DevModel, SDevEntity> sDev = root.join("sDev");

            criteriaQuery.distinct(false);

            Predicate predicateForId;
            Predicate predicateForGrid;

            if (devRequestDTO.getId() != null) {
                predicateForId = criteriaBuilder.like(root.get("id").as(String.class), "%" + devRequestDTO.getId() + "%");
            } else {
                predicateForId = criteriaBuilder.equal(root.get("id"), root.get("id"));
            }

            if (devRequestDTO.getGrid() != null) {
                predicateForGrid = criteriaBuilder.like(sDev.get("grid").as(String.class), "%" + devRequestDTO.getGrid() + "%");
            } else {
                predicateForGrid = criteriaBuilder.equal(sDev.get("grid"), sDev.get("grid"));
            }

            return criteriaBuilder.and(predicateForId, predicateForGrid);
        };
    }

    public void deleteDevById(Long id) {
        devRepository.deleteById(id);
    }

    public void updateDev(DevModel devModel) {
        devRepository.save(devModel);
    }

    public DevModel createDev(DevModel devModel) {
        DevModel newDev = new DevModel(devModel);
        newDev.setId(null);
        newDev.setIdObj(null);
//        log.info("before");
        DevModel savedDev = devRepository.saveAndFlush(newDev);
//        log.info("after", savedDev.getId());
        return savedDev;
    }


}
