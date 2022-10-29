package dms.service.dev;


import dms.entity.DevEntity;
import dms.entity.DevObjEntity;
import dms.exception.NoEntityException;
import dms.filter.DevFilter;
import dms.property.name.constant.DevPropertyNameMapping;
import dms.repository.DevRepository;
import dms.standing.data.entity.DObjRtuEntity;
import dms.standing.data.entity.SDevEntity;
import dms.standing.data.entity.SDevgrpEntity;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service("DevService1")
public class DevServiceImpl implements DevService {

    @PersistenceContext
    EntityManager em;

    private final DevRepository devRepository;

    @Autowired
    public DevServiceImpl(DevRepository devRepository) {
        this.devRepository = devRepository;
    }


    public DevEntity findDevById(Long id) {
        return devRepository.getReferenceById(id);
    }

    public Page<DevEntity> findDevsByQuery(Pageable pageable, DevFilter devFilter) {


        String contentSizeQueryStr = "SELECT count (distinct d.id) FROM DevEntity d WHERE 1=1 ";
        String contentQueryStr = "SELECT d FROM DevEntity d WHERE 1=1 ";

        StringBuilder contentSizeSb = new StringBuilder(contentSizeQueryStr);
        StringBuilder contentSb = new StringBuilder(contentQueryStr);

        for (DevPropertyNameMapping item : DevPropertyNameMapping.values()) {
            if (getProperty(devFilter, item) != null) {
                contentSizeSb
                        .append(" AND CAST (d.")
                        .append(item.getEntityPropertyName())
                        .append(" as string) LIKE '%")
                        .append(getProperty(devFilter, item))
                        .append("%'");

                contentSb
                        .append(" AND CAST (d.")
                        .append(item.getEntityPropertyName())
                        .append(" as string) LIKE '%")
                        .append(getProperty(devFilter, item))
                        .append("%'");

            }
        }
        contentSb.append(" ORDER BY d.id ASC");

        Query contentSizeQuery = em.createQuery(contentSizeSb.toString());
        Long contentSize = (Long) contentSizeQuery.getSingleResult();

        TypedQuery<DevEntity> contentQuery = em.createQuery(contentSb.toString(), DevEntity.class);
        contentQuery.setFirstResult((int) pageable.getOffset());
        contentQuery.setMaxResults(pageable.getPageSize());
        List<DevEntity> content = contentQuery.getResultList();

        return new PageImpl<>(content, pageable, contentSize);
    }

    public Page<DevEntity> findDevsBySpecification(Pageable pageable, DevFilter devFilter) {
        return devRepository.findAll(getSpecification(devFilter), pageable);
    }

    private Specification<DevFilter> getSpecification(DevFilter devFilter) {

        return (root, criteriaQuery, criteriaBuilder) -> {
            Join<DevEntity, SDevEntity> sDev = root.join("sDev");
            Join<SDevEntity, SDevgrpEntity> grid = sDev.join("grid");
            Join<DevEntity, DevObjEntity> devObj = root.join("devObj");
            Join<DevEntity, DObjRtuEntity> dObjRtu = root.join("dObjRtu");

            Map<String, Join<?, ?>> joinsMap = new HashMap<>();
            joinsMap.put("sDev", sDev);
            joinsMap.put("grid", grid);
            joinsMap.put("devObj", devObj);
            joinsMap.put("dObjRtu", dObjRtu);

            criteriaQuery.distinct(false);

            Predicate predicateDefault = criteriaBuilder.equal(root, root);

            List<Predicate> predicates = new ArrayList<>();

            for (DevPropertyNameMapping item : DevPropertyNameMapping.values()) {
                if (getProperty(devFilter, item) != null) {

                    int splitSize = item.getEntityPropertyName().split("\\.").length;
                    String[] splitArr = item.getEntityPropertyName().split("\\.");

                    if (splitSize == 1) {
                        predicates.add(criteriaBuilder.like(
                                root.get(splitArr[0]).as(String.class),
                                "%" + getProperty(devFilter, item) + "%"));
                    } else {
                        predicates.add(criteriaBuilder.like(
                                joinsMap.get(splitArr[splitArr.length - 2])
                                        .get(splitArr[splitArr.length - 1]).as(String.class),
                                "%" + getProperty(devFilter, item) + "%"));
                    }
                }
            }

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
