package dms.service.dev;


import dms.dto.DevDTO;
import dms.entity.DevEntity;
import dms.entity.standing.data.SDevEntity;
import dms.entity.standing.data.SDevgrpEntity;
import dms.repository.DevRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

@Slf4j
@Service("DevService1")
public class DevServiceImp implements DevService {


    private final DevRepository devRepository;

    @Autowired
    public DevServiceImp(DevRepository devRepository) {
        this.devRepository = devRepository;
    }

    public DevEntity findDevById(Long id) {
        return devRepository.getOne(id);
    }

    public Page<DevEntity> findDevsBySpecification(Pageable pageable, DevDTO devDTO) {
        return devRepository.findAll(getSpecification(devDTO), pageable);
    }


    private Specification<DevDTO> getSpecification(DevDTO devDTO) {
        return (root, criteriaQuery, criteriaBuilder) ->
        {
            Join<DevEntity, SDevEntity> sDev = root.join("sDev");

            Join<SDevEntity, SDevgrpEntity> grid = sDev.join("grid");

            criteriaQuery.distinct(false);

            Predicate predicateForId;
            Predicate predicateForGrid;

            if (devDTO.getDeviceId() != null) {
                predicateForId = criteriaBuilder.like(root.get("id").as(String.class), "%" + devDTO.getDeviceId() + "%");
            } else {
                predicateForId = criteriaBuilder.equal(root.get("id"), root.get("id"));
            }

            if (devDTO.getDeviceTypeGroupId() != null) {
                predicateForGrid = criteriaBuilder.like(grid.get("grid").as(String.class), "%" + devDTO.getDeviceTypeGroupId() + "%");
            } else {
                predicateForGrid = criteriaBuilder.equal(grid.get("grid"), grid.get("grid"));
            }

            return criteriaBuilder.and(predicateForId, predicateForGrid);
        };
    }

    public void deleteDevById(Long id) {
        devRepository.deleteById(id);
    }

    public void updateDev(DevEntity dev) {
        devRepository.save(dev);
    }

    public DevEntity createDev(DevEntity dev) {
        dev.setId(null);
        dev.setDevObj(null);
        return devRepository.saveAndFlush(dev);
    }


}
