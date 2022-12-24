package dms.service.device;


import dms.entity.DeviceEntity;
import dms.entity.DeviceLocationEntity;
import dms.exception.NoEntityException;
import dms.filter.DeviceFilter;
import dms.mapper.ExplicitDeviceMatcher;
import dms.repository.DeviceRepository;
import dms.standing.data.entity.DeviceTypeEntity;
import dms.standing.data.entity.DeviceTypeGroupEntity;
import dms.standing.data.entity.FacilityEntity;
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
public class DeviceServiceImpl implements DeviceService {

    @PersistenceContext
    EntityManager em;

    private final DeviceRepository deviceRepository;

    @Autowired
    public DeviceServiceImpl(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public DeviceEntity findDevById(Long id) {
        return deviceRepository.getReferenceById(id);
    }

    public Page<DeviceEntity> findDevicesByQuery(Pageable pageable, DeviceFilter deviceFilter) throws NoSuchFieldException {

        Long contentSize = (Long) em.createQuery(
                        "SELECT count (distinct d.id) " +
                                "FROM DeviceEntity d " +
                                "WHERE 1=1 " +
                                getQueryConditionsPart(deviceFilter))
                .getSingleResult();

        List<DeviceEntity> content = em.createQuery(
                        "SELECT d " +
                                "FROM DeviceEntity d " +
                                "JOIN FETCH d.type t " +
                                "JOIN FETCH t.group g " +
                                "JOIN FETCH d.facility f " +
                                "JOIN FETCH d.location l " +
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

        for (ExplicitDeviceMatcher item : ExplicitDeviceMatcher.values()) {
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

    public Page<DeviceEntity> findDevicesBySpecification(Pageable pageable, DeviceFilter deviceFilter) {
        return deviceRepository.findAll(getSpecification(deviceFilter), pageable);
    }

    private Specification<DeviceFilter> getSpecification(DeviceFilter deviceFilter) {

        return (root, criteriaQuery, criteriaBuilder) -> {
            Join<DeviceEntity, DeviceTypeEntity> type = root.join("type");
            Join<DeviceTypeEntity, DeviceTypeGroupEntity> group = type.join("group");
            Join<DeviceEntity, DeviceLocationEntity> location = root.join("location");
            Join<DeviceEntity, FacilityEntity> facility = root.join("facility");

            Map<String, Join<?, ?>> joinsMap = new HashMap<>();
            joinsMap.put("type", type);
            joinsMap.put("group", group);
            joinsMap.put("location", location);
            joinsMap.put("facility", facility);

            criteriaQuery.distinct(false);

            Predicate predicateDefault = criteriaBuilder.equal(root, root);

            List<Predicate> predicates = new ArrayList<>();

            for (ExplicitDeviceMatcher item : ExplicitDeviceMatcher.values()) {
                if (getProperty(deviceFilter, item) != null) {

                    String[] splitArr = item.getEntityPropertyName().split("\\.");

                    if (splitArr.length == 1) {
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
    private String getProperty(DeviceFilter deviceFilter, ExplicitDeviceMatcher item) {
        return BeanUtils.getProperty(deviceFilter, item.getFilterPropertyName());
    }

    public void deleteDevById(Long id) {
        deviceRepository.deleteById(id);
    }

    public void updateDev(Long id, DeviceEntity dev, List<ExplicitDeviceMatcher> activeProperties) {
        DeviceEntity targetDev = deviceRepository.findById(id).orElseThrow(
                () -> new NoEntityException("Device with the id=" + id + " not found"));
        copyProperties(dev, targetDev, getProps(activeProperties));
        deviceRepository.save(targetDev);
    }

    private List<String> getProps(List<ExplicitDeviceMatcher> activeProperties) {
        return activeProperties.stream()
                .map(ExplicitDeviceMatcher::getEntityPropertyName)
                .collect(Collectors.toList());
    }

    public DeviceEntity createDev(DeviceEntity dev) {
        dev.setId(null);
        dev.setLocation(null);
        return deviceRepository.saveAndFlush(dev);
    }

    private static void copyProperties(Object src, Object trg, Iterable<String> props) {

        BeanWrapper srcWrap = PropertyAccessorFactory.forBeanPropertyAccess(src);
        BeanWrapper trgWrap = PropertyAccessorFactory.forBeanPropertyAccess(trg);

        props.forEach(p -> trgWrap.setPropertyValue(p, srcWrap.getPropertyValue(p)));
    }
}
