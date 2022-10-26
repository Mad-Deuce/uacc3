package dms.service.dev;


import dms.entity.DevEntity;
import dms.exception.NoEntityException;
import dms.filter.DevFilter;
import dms.property.name.constant.DevPropertyNameMapping;
import dms.repository.DevRepository;
import dms.standing.data.entity.SDevEntity;
import dms.standing.data.entity.SDevgrpEntity;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


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

        return (root, criteriaQuery, criteriaBuilder) -> {
            Join<DevEntity, SDevEntity> sDev = root.join("sDev");
            Join<SDevEntity, SDevgrpEntity> grid = sDev.join("grid");

            criteriaQuery.distinct(false);

            Predicate predicateDefault = criteriaBuilder.equal(root, root);

            List<Predicate> predicates = new ArrayList<>();

            for (DevPropertyNameMapping item : DevPropertyNameMapping.values()) {
                if (getProperty(devFilter, item) != null) {
                    predicates.add(criteriaBuilder.like(
                            root.get(item.getEntityPropertyName()).as(String.class),
                            "%" + getProperty(devFilter, item) + "%"));
                }
            }

//            if (devFilter.getTypeName() != null) {
//                predicates.add(criteriaBuilder.like(
//                        sDev.get("dtype").as(String.class),
//                        "%" + devFilter.getTypeName() + "%"));
//            }

            return predicates.stream().reduce(predicateDefault, criteriaBuilder::and);
        };
    }

    @SneakyThrows
    private String getProperty(DevFilter devFilter, DevPropertyNameMapping item) {
        return BeanUtils.getProperty(devFilter, item.getFilterPropertyName());
    }

    public void deleteDevById(Long id) {
        devRepository.deleteById(id);
    }

    public void updateDev(Long id, DevEntity dev, List<DevPropertyNameMapping> activeProperties) {
        DevEntity targetDev = devRepository.findById(id).orElseThrow(
                () -> new NoEntityException("Device with the id=" + id + " not found"));
        copyProperties(dev, targetDev, getProps(activeProperties));
        devRepository.save(targetDev);
    }

    private List<String> getProps(List<DevPropertyNameMapping> activeProperties) {
        return activeProperties.stream()
                .map(DevPropertyNameMapping::getEntityPropertyName)
                .collect(Collectors.toList());
    }

    public DevEntity createDev(DevEntity dev) {
        dev.setId(null);
        dev.setDevObj(null);
        return devRepository.saveAndFlush(dev);
    }

    private static void copyProperties(Object src, Object trg, Iterable<String> props) {

        BeanWrapper srcWrap = PropertyAccessorFactory.forBeanPropertyAccess(src);
        BeanWrapper trgWrap = PropertyAccessorFactory.forBeanPropertyAccess(trg);

        props.forEach(p -> trgWrap.setPropertyValue(p, srcWrap.getPropertyValue(p)));
    }
}
