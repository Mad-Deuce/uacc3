package dms.service;


import dms.dto.DevDTO;
import dms.entity.standing.data.SDevEntity;
import dms.entity.standing.data.SDevgrpEntity;
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
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Predicate;

@Slf4j
@Service("DevService1")
public class DevServiceImp implements DevService {

//    @PersistenceContext
//    private EntityManager em;

    @Autowired
    DevRepository devRepository;

    public DevModel findDevById(Long id) {
        return devRepository.getOne(id);
    }

    public Page<DevModel> findDevsBySpecification(Pageable pageable, DevDTO devDTO) {
        return devRepository.findAll(getSpecification(devDTO), pageable);
    }


    private Specification<DevDTO> getSpecification(DevDTO devDTO) {
        //Build Specification with Dev Id and Filter Text
        return (root, criteriaQuery, criteriaBuilder) ->
        {
            Join<DevModel, SDevEntity> sDev = root.join("sDev");

            Join<SDevEntity, SDevgrpEntity> grid = sDev.join("grid");

            criteriaQuery.distinct(false);

            Predicate predicateForId;
            Predicate predicateForGrid;

            if (devDTO.getId() != null) {
                predicateForId = criteriaBuilder.like(root.get("id").as(String.class), "%" + devDTO.getId() + "%");
            } else {
                predicateForId = criteriaBuilder.equal(root.get("id"), root.get("id"));
            }

            if (devDTO.getGrid() != null) {
                predicateForGrid = criteriaBuilder.like(grid.get("grid").as(String.class), "%" + devDTO.getGrid() + "%");
            } else {
                predicateForGrid = criteriaBuilder.equal(grid.get("grid"), grid.get("grid"));
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
