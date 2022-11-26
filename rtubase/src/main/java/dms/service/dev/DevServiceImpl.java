package dms.service.dev;


import dms.entity.DeviceEntity;
import dms.entity.DeviceLocationEntity;
import dms.exception.NoEntityException;
import dms.filter.DeviceFilter;
import dms.property.name.constant.DevicePropertyNameMapping;
import dms.repository.DevRepository;
import dms.standing.data.entity.FacilityEntity;
import dms.standing.data.entity.DeviceTypeEntity;
import dms.standing.data.entity.DeviceTypeGroupEntity;
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
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.lang.reflect.Field;
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


    public DeviceEntity findDevById(Long id) {
        return devRepository.getReferenceById(id);
    }

    public Page<DeviceEntity> findDevsByQuery(Pageable pageable, DeviceFilter deviceFilter) throws NoSuchFieldException {

        Long contentSize = (Long) em.createQuery(
                        "SELECT count (distinct d.id) " +
                                "FROM DeviceEntity d " +
                                "WHERE 1=1 " +
                                getQueryConditionsPart(deviceFilter))
                .getSingleResult();

        List<DeviceEntity> content = em.createQuery(
                        "SELECT d " +
                                "FROM DeviceEntity d " +
                                "JOIN FETCH d.sDev s " +
                                "JOIN FETCH s.grid g " +
                                "JOIN FETCH d.dObjRtu dor " +
                                "JOIN FETCH d.location do " +
                                "WHERE 1=1 " +
                                getQueryConditionsPart(deviceFilter) +
                                " ORDER BY d.id ASC", DeviceEntity.class)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        return new PageImpl<>(content, pageable, contentSize);
    }

    private String getQueryConditionsPart(DeviceFilter deviceFilter) throws NoSuchFieldException {
        StringBuilder queryConditionsPart = new StringBuilder();

        for (DevicePropertyNameMapping item : DevicePropertyNameMapping.values()) {
            if (getProperty(deviceFilter, item) != null) {
                Field field = DeviceEntity.class.getDeclaredField(item.getEntityPropertyName());
                if (java.util.Date.class.isAssignableFrom(field.getType())) {

                    if (item.getFilterPropertyName().toLowerCase().contains("min")) {
                        queryConditionsPart
                                .append(" AND CAST (d.")
                                .append(item.getEntityPropertyName())
                                .append(" as date) >= '")
                                .append(getProperty(deviceFilter, item))
                                .append("'");
                    }

                    if (item.getFilterPropertyName().toLowerCase().contains("max")) {
                        queryConditionsPart
                                .append(" AND CAST (d.")
                                .append(item.getEntityPropertyName())
                                .append(" as date) <= '")
                                .append(getProperty(deviceFilter, item))
                                .append("'");
                    }

                } else {
                    queryConditionsPart
                            .append(" AND CAST (d.")
                            .append(item.getEntityPropertyName())
                            .append(" as string) LIKE '%")
                            .append(getProperty(deviceFilter, item))
                            .append("%'");
                }
            }
        }
        return queryConditionsPart.toString();
    }

    public Page<DeviceEntity> findDevsBySpecification(Pageable pageable, DeviceFilter deviceFilter) {
        return devRepository.findAll(getSpecification(deviceFilter), pageable);
    }

    private Specification<DeviceFilter> getSpecification(DeviceFilter deviceFilter) {

        return (root, criteriaQuery, criteriaBuilder) -> {
            Join<DeviceEntity, DeviceTypeEntity> sDev = root.join("sDev");
            Join<DeviceTypeEntity, DeviceTypeGroupEntity> grid = sDev.join("grid");
            Join<DeviceEntity, DeviceLocationEntity> devObj = root.join("devObj");
            Join<DeviceEntity, FacilityEntity> dObjRtu = root.join("dObjRtu");

            Map<String, Join<?, ?>> joinsMap = new HashMap<>();
            joinsMap.put("sDev", sDev);
            joinsMap.put("grid", grid);
            joinsMap.put("devObj", devObj);
            joinsMap.put("dObjRtu", dObjRtu);

            criteriaQuery.distinct(false);

            Predicate predicateDefault = criteriaBuilder.equal(root, root);

            List<Predicate> predicates = new ArrayList<>();

            for (DevicePropertyNameMapping item : DevicePropertyNameMapping.values()) {
                if (getProperty(deviceFilter, item) != null) {

                    int splitSize = item.getEntityPropertyName().split("\\.").length;
                    String[] splitArr = item.getEntityPropertyName().split("\\.");

                    if (splitSize == 1) {
                        predicates.add(criteriaBuilder.like(
                                root.get(splitArr[0]).as(String.class),
                                "%" + getProperty(deviceFilter, item) + "%"));
                    } else {
                        predicates.add(criteriaBuilder.like(
                                joinsMap.get(splitArr[splitArr.length - 2])
                                        .get(splitArr[splitArr.length - 1]).as(String.class),
                                "%" + getProperty(deviceFilter, item) + "%"));
                    }
                }
            }

            return predicates.stream().reduce(predicateDefault, criteriaBuilder::and);
        };
    }

    @SneakyThrows
    private String getProperty(DeviceFilter deviceFilter, DevicePropertyNameMapping item) {
        return BeanUtils.getProperty(deviceFilter, item.getFilterPropertyName());
    }

    public void deleteDevById(Long id) {
        devRepository.deleteById(id);
    }

    public void updateDev(Long id, DeviceEntity dev, List<DevicePropertyNameMapping> activeProperties) {
        DeviceEntity targetDev = devRepository.findById(id).orElseThrow(
                () -> new NoEntityException("Device with the id=" + id + " not found"));
        copyProperties(dev, targetDev, getProps(activeProperties));
        devRepository.save(targetDev);
    }

    private List<String> getProps(List<DevicePropertyNameMapping> activeProperties) {
        return activeProperties.stream()
                .map(DevicePropertyNameMapping::getEntityPropertyName)
                .collect(Collectors.toList());
    }

    public DeviceEntity createDev(DeviceEntity dev) {
        dev.setId(null);
        dev.setLocation(null);
        return devRepository.saveAndFlush(dev);
    }

    private static void copyProperties(Object src, Object trg, Iterable<String> props) {

        BeanWrapper srcWrap = PropertyAccessorFactory.forBeanPropertyAccess(src);
        BeanWrapper trgWrap = PropertyAccessorFactory.forBeanPropertyAccess(trg);

        props.forEach(p -> trgWrap.setPropertyValue(p, srcWrap.getPropertyValue(p)));
    }
}
