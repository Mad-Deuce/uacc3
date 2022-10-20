package dms.service.dev;


import dms.entity.DevEntity;
import dms.standing.data.entity.SDevEntity;
import dms.standing.data.entity.SDevgrpEntity;
import dms.filter.DevFilter;
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
public class DevServiceImpl implements DevService {


    private final DevRepository devRepository;

    @Autowired
    public DevServiceImpl(DevRepository devRepository) {
        this.devRepository = devRepository;
    }

    public DevEntity findDevById(Long id) {
        return devRepository.getReferenceById(id);
    }

    public Page<DevEntity> findDevsBySpecification(Pageable pageable, DevFilter devFilter) {
        return devRepository.findAll(getSpecification(devFilter), pageable);
    }


    private Specification<DevFilter> getSpecification(DevFilter devFilter) {
        return (root, criteriaQuery, criteriaBuilder) ->
        {
            Join<DevEntity, SDevEntity> sDev = root.join("sDev");
            Join<SDevEntity, SDevgrpEntity> grid = sDev.join("grid");

            criteriaQuery.distinct(false);

            Predicate predicateForId;
            Predicate predicateForGrid;

            if (devFilter.getId() != null) {
                predicateForId = criteriaBuilder.like(root.get("id").as(String.class), "%" + devFilter.getId() + "%");
            } else {
                predicateForId = criteriaBuilder.equal(root.get("id"), root.get("id"));
            }

            if (devFilter.getTypeGroupId() != null) {
                predicateForGrid = criteriaBuilder.like(grid.get("grid").as(String.class), "%" + devFilter.getTypeGroupId() + "%");
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
