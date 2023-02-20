package dms.service.device;


import dms.DeviceAuthService;
import dms.entity.DeviceEntity;
import dms.entity.LocationEntity;
import dms.exception.DeviceValidationException;
import dms.exception.NoEntityException;
import dms.filter.DeviceFilter;
import dms.mapper.ExplicitDeviceMatcher;
import dms.repository.DeviceRepository;
import dms.repository.LocationRepository;
import dms.standing.data.dock.val.ReplacementType;
import dms.standing.data.dock.val.Status;
import dms.standing.data.entity.DeviceTypeEntity;
import dms.standing.data.entity.DeviceTypeGroupEntity;
import dms.standing.data.entity.FacilityEntity;
import dms.standing.data.repository.LineFacilityRepository;
import dms.standing.data.repository.RtdFacilityRepository;
import dms.validation.DeviceValidator;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.lang.reflect.Field;
import java.sql.Date;
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

    private final DeviceAuthService deviceAuthService;

    private final DeviceValidator deviceValidator;
    private final DeviceRepository deviceRepository;
    private final LineFacilityRepository lineFacilityRepository;
    private final RtdFacilityRepository rtdFacilityRepository;
    private final LocationRepository locationRepository;

    @Autowired
    public DeviceServiceImpl(DeviceAuthService deviceAuthService,
                             DeviceValidator deviceValidator,
                             DeviceRepository deviceRepository,
                             LineFacilityRepository lineFacilityRepository,
                             RtdFacilityRepository rtdFacilityRepository,
                             LocationRepository locationRepository) {
        this.deviceAuthService = deviceAuthService;
        this.deviceValidator = deviceValidator;
        this.deviceRepository = deviceRepository;
        this.lineFacilityRepository = lineFacilityRepository;
        this.rtdFacilityRepository = rtdFacilityRepository;
        this.locationRepository = locationRepository;
    }

    @Override
    public DeviceEntity findDeviceById(Long id) {
        return deviceRepository.findById(id).orElseThrow();
    }

    @Override
    public Page<DeviceEntity> findDevicesByQuery(Pageable pageable, DeviceFilter deviceFilter) throws NoSuchFieldException {

        Long contentSize = (Long) em.createQuery(
                        "SELECT count (distinct d.id) " +
                                "FROM DeviceEntity d " +
                                "WHERE 1=1 " +
                                getQueryConditionsPart(deviceFilter) +
                                deviceAuthService.getAuthConditionsPartOfFindDeviceByFilterQuery())
                .getSingleResult();

        List<DeviceEntity> content = em.createQuery(
                        "SELECT d " +
                                "FROM DeviceEntity d " +
                                "JOIN FETCH d.type t " +
                                "JOIN FETCH t.group g " +
                                "LEFT JOIN FETCH d.facility f " +
                                "LEFT JOIN FETCH d.location l " +
                                "WHERE 1=1 " +
                                getQueryConditionsPart(deviceFilter) +
                                deviceAuthService.getAuthConditionsPartOfFindDeviceByFilterQuery() +
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
                String[] splitProperty = item.getEntityPropertyName().split("\\.");
                Field field = DeviceEntity.class.getDeclaredField(splitProperty[0]);
                if (java.util.Date.class.isAssignableFrom(field.getType())) {

                    if (item.getFilterPropertyName().toLowerCase().contains("min")) {
                        queryConditionsPart
                                .append(" AND CAST (d.")
                                .append(item.getEntityPropertyName())
                                .append(" as date) >= '")
                                .append(getProperty(deviceFilter, item))
                                .append("'");
                    } else if (item.getFilterPropertyName().toLowerCase().contains("max")) {
                        queryConditionsPart
                                .append(" AND CAST (d.")
                                .append(item.getEntityPropertyName())
                                .append(" as date) <= '")
                                .append(getProperty(deviceFilter, item))
                                .append("'");
                    } else {
                        queryConditionsPart
                                .append(" AND CAST (d.")
                                .append(item.getEntityPropertyName())
                                .append(" as date) = '")
                                .append(getProperty(deviceFilter, item))
                                .append("'");
                    }

                } else if (Number.class.isAssignableFrom(field.getType())) {

                    if (item.getFilterPropertyName().toLowerCase().contains("min")) {
                        queryConditionsPart
                                .append(" AND d.")
                                .append(item.getEntityPropertyName())
                                .append(" >= ")
                                .append(getProperty(deviceFilter, item));
                    } else if (item.getFilterPropertyName().toLowerCase().contains("max")) {
                        queryConditionsPart
                                .append(" AND d.")
                                .append(item.getEntityPropertyName())
                                .append(" <= ")
                                .append(getProperty(deviceFilter, item));
                    } else {
                        queryConditionsPart
                                .append(" AND lower(CAST (d.")
                                .append(item.getEntityPropertyName())
                                .append(" as string)) LIKE lower('%")
                                .append(getProperty(deviceFilter, item))
                                .append("%')");
                    }

                } else {
                    if (item.getFilterPropertyName().toLowerCase().contains("min")) {
                        queryConditionsPart
                                .append(" AND CAST (d.")
                                .append(item.getEntityPropertyName())
                                .append(" as string) >= '")
                                .append(getProperty(deviceFilter, item))
                                .append("'");
                    } else if (item.getFilterPropertyName().toLowerCase().contains("max")) {
                        queryConditionsPart
                                .append(" AND CAST (d.")
                                .append(item.getEntityPropertyName())
                                .append(" as string) <= '")
                                .append(getProperty(deviceFilter, item))
                                .append("'");
                    } else {
                        queryConditionsPart
                                .append(" AND lower(CAST (d.")
                                .append(item.getEntityPropertyName())
                                .append(" as string)) LIKE lower('%")
                                .append(getProperty(deviceFilter, item))
                                .append("%')");
                    }
                }
            }
        }
        return queryConditionsPart.toString();
    }

    @Override
    public Page<DeviceEntity> findDevicesBySpecification(Pageable pageable, DeviceFilter deviceFilter) {
        return deviceRepository.findAll(getSpecification(deviceFilter), pageable);
    }

    private Specification<DeviceFilter> getSpecification(DeviceFilter deviceFilter) {

        return (root, criteriaQuery, criteriaBuilder) -> {
            Join<DeviceEntity, DeviceTypeEntity> type = root.join("type");
            Join<DeviceTypeEntity, DeviceTypeGroupEntity> group = type.join("group");
            Join<DeviceEntity, LocationEntity> location = root.join("location");
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

    @Override
    public void deleteDeviceById(Long id) {
        deviceRepository.deleteById(id);
    }

    @Override
    public void updateDevice(Long id, DeviceEntity deviceEntity, List<ExplicitDeviceMatcher> activeProperties) {

        DeviceEntity targetDev = deviceRepository.findById(id).orElseThrow(
                () -> new NoEntityException("Device with the id=" + deviceEntity.getId() + " not found"));
        copyProperties(deviceEntity, targetDev, getProps(activeProperties));
        deviceRepository.save(targetDev);
    }

    private static void copyProperties(Object src, Object trg, Iterable<String> props) {

        BeanWrapper srcWrap = PropertyAccessorFactory.forBeanPropertyAccess(src);
        BeanWrapper trgWrap = PropertyAccessorFactory.forBeanPropertyAccess(trg);

        props.forEach(p -> trgWrap.setPropertyValue(p, srcWrap.getPropertyValue(p)));
    }

    private List<String> getProps(List<ExplicitDeviceMatcher> activeProperties) {
        return activeProperties.stream()
                .map(ExplicitDeviceMatcher::getEntityPropertyName)
                .collect(Collectors.toList());
    }

    @Override
    public DeviceEntity createDevice(DeviceEntity deviceEntity) {
        deviceValidator.onCreateValidation(deviceEntity);
        deviceEntity.setId(null);
        deviceEntity.setStatus(Status.PS31);
        deviceEntity.setLocation(null);
        Date tDate = deviceEntity.getTestDate();
        tDate = Date.valueOf(tDate.toLocalDate().plusMonths(deviceEntity.getReplacementPeriod()));
        deviceEntity.setNextTestDate(tDate);

        return deviceRepository.saveAndFlush(deviceEntity);
    }

    @Override
    public void replaceDevice(Long oldDeviceId, Long newDeviceId, String status, ReplacementType replacementType) {

        if (status.equals(Status.PS21.getName())) {
            replaceDeviceToAvzLine(oldDeviceId, newDeviceId, replacementType);
        }

        if (status.equals(Status.PS32.getName())) {
            replaceDeviceToAvzRtd(oldDeviceId, newDeviceId, replacementType);
        }

        if (status.equals(Status.PS11.getName())) {
            replaceDeviceToLine(oldDeviceId, newDeviceId, replacementType);
        }
    }

    private void replaceDeviceToAvzLine(Long oldDeviceId, Long newDeviceId, ReplacementType replacementType) {

        DeviceEntity oldDeviceEntity = deviceRepository.findById(oldDeviceId).orElseThrow(DeviceValidationException::new);
        DeviceEntity newDeviceEntity = deviceRepository.findById(newDeviceId).orElseThrow(DeviceValidationException::new);

        deviceValidator.onReplaceToAvzLineValidation(oldDeviceEntity, newDeviceEntity, replacementType);

        newDeviceEntity.setStatus(Status.PS21);
        oldDeviceEntity.setStatus(Status.PS31);

        FacilityEntity newDeviceFacility = newDeviceEntity.getFacility();
        newDeviceEntity.setFacility(oldDeviceEntity.getFacility());
        oldDeviceEntity.setFacility(newDeviceFacility);

        newDeviceEntity.setLocation(null);
        oldDeviceEntity.setLocation(null);

        newDeviceEntity.setDetail(replacementType.getComment());
        oldDeviceEntity.setDetail(replacementType.getComment());

        deviceRepository.save(newDeviceEntity);
        deviceRepository.save(oldDeviceEntity);
        deviceRepository.flush();
    }

    private void replaceDeviceToAvzRtd(Long oldDeviceId, Long newDeviceId, ReplacementType replacementType) {

        DeviceEntity oldDeviceEntity = deviceRepository.findById(oldDeviceId).orElseThrow(DeviceValidationException::new);
        DeviceEntity newDeviceEntity = deviceRepository.findById(newDeviceId).orElseThrow(DeviceValidationException::new);

        deviceValidator.onReplaceToAvzRtdValidation(oldDeviceEntity, newDeviceEntity, replacementType);

        newDeviceEntity.setStatus(Status.PS32);
        oldDeviceEntity.setStatus(Status.PS31);

        FacilityEntity newDeviceFacility = newDeviceEntity.getFacility();
        newDeviceEntity.setFacility(oldDeviceEntity.getFacility());
        oldDeviceEntity.setFacility(newDeviceFacility);

        newDeviceEntity.setLocation(null);
        oldDeviceEntity.setLocation(null);

        newDeviceEntity.setDetail(replacementType.getComment());
        oldDeviceEntity.setDetail(replacementType.getComment());

        deviceRepository.save(newDeviceEntity);
        deviceRepository.save(oldDeviceEntity);
        deviceRepository.flush();
    }

    private void replaceDeviceToLine(Long oldDeviceId, Long newDeviceId, ReplacementType replacementType) {

        DeviceEntity oldDeviceEntity = deviceRepository.findById(oldDeviceId).orElseThrow(DeviceValidationException::new);
        DeviceEntity newDeviceEntity = deviceRepository.findById(newDeviceId).orElseThrow(DeviceValidationException::new);

        deviceValidator.onReplaceToLineValidation(oldDeviceEntity, newDeviceEntity, replacementType);

        newDeviceEntity.setStatus(Status.PS11);
        oldDeviceEntity.setStatus(Status.PS31);

        FacilityEntity newDeviceFacility = newDeviceEntity.getFacility();
        newDeviceEntity.setFacility(oldDeviceEntity.getFacility());
        oldDeviceEntity.setFacility(newDeviceFacility);

        newDeviceEntity.setLocation(oldDeviceEntity.getLocation());
        oldDeviceEntity.setLocation(null);

        newDeviceEntity.setDetail(replacementType.getComment());
        oldDeviceEntity.setDetail(replacementType.getComment());

        deviceRepository.save(newDeviceEntity);
        deviceRepository.save(oldDeviceEntity);
        deviceRepository.flush();
    }

    @Override
    public void setDeviceTo(Long deviceId, String status, String facilityId, Long locationId) {
        if (status.equals(Status.PS21.getName())) {
            setDeviceToAvzLine(deviceId, facilityId);
        }

        if (status.equals(Status.PS32.getName())) {
            setDeviceToAvzRtd(deviceId, facilityId);
        }

        if (status.equals(Status.PS11.getName())) {
            setDeviceToLine(deviceId, locationId);
        }
    }

    private void setDeviceToLine(Long deviceId, Long locationId) {

        DeviceEntity deviceEntity = deviceRepository.findById(deviceId)
                .orElseThrow(DeviceValidationException::new);
        LocationEntity locationEntity = locationRepository.findById(locationId)
                .orElseThrow(DeviceValidationException::new);
        FacilityEntity facilityEntity = lineFacilityRepository.findById(locationEntity.getFacility().getId())
                .orElseThrow(DeviceValidationException::new);

        deviceValidator.onSetDeviceToLineValidation(deviceEntity, facilityEntity, locationEntity);

        deviceEntity.setStatus(Status.PS11);

        deviceEntity.setLocation(locationEntity);
        deviceEntity.setFacility(facilityEntity);
        deviceEntity.setDetail(ReplacementType.NEW.getComment());

        deviceRepository.save(deviceEntity);
        deviceRepository.flush();
    }

    private void setDeviceToAvzLine(Long deviceId, String facilityId) {

        DeviceEntity deviceEntity = deviceRepository.findById(deviceId)
                .orElseThrow(DeviceValidationException::new);
        FacilityEntity facilityEntity = lineFacilityRepository.findById(facilityId).orElseThrow();

        deviceValidator.onSetDeviceToAvzLineValidation(deviceEntity, facilityEntity);

        deviceEntity.setStatus(Status.PS21);

        deviceEntity.setLocation(null);
        deviceEntity.setFacility(facilityEntity);
        deviceEntity.setDetail(ReplacementType.NEW.getComment());

        deviceRepository.save(deviceEntity);
        deviceRepository.flush();
    }

    private void setDeviceToAvzRtd(Long deviceId, String facilityId) {

        DeviceEntity deviceEntity = deviceRepository.findById(deviceId)
                .orElseThrow(DeviceValidationException::new);
        FacilityEntity facilityEntity = rtdFacilityRepository.findById(facilityId).orElseThrow();


        deviceValidator.onSetDeviceToAvzRtdValidation(deviceEntity, facilityEntity);

        deviceEntity.setStatus(Status.PS32);

        deviceEntity.setLocation(null);
        deviceEntity.setFacility(facilityEntity);
        deviceEntity.setDetail(ReplacementType.NEW.getComment());

        deviceRepository.save(deviceEntity);
        deviceRepository.flush();
    }

    @Override
    public void unsetDevice(Long deviceId, String facilityId) {
        DeviceEntity deviceEntity = deviceRepository.findById(deviceId)
                .orElseThrow(DeviceValidationException::new);
        FacilityEntity facilityEntity = rtdFacilityRepository.findById(facilityId)
                .orElseThrow(DeviceValidationException::new);

        deviceValidator.onUnsetDeviceValidation(deviceEntity, facilityEntity);

        deviceEntity.setStatus(Status.PS31);

        deviceEntity.setLocation(null);
        deviceEntity.setFacility(facilityEntity);
        deviceEntity.setDetail(ReplacementType.DIS.getComment());

        deviceRepository.save(deviceEntity);
        deviceRepository.flush();
    }

    @PreAuthorize(
            "(hasRole('ROLE_ADMIN') || hasRole('ROLE_OPERATOR'))" +
                    "&& #deviceEntity.facility.id.substring(0, authentication.principal.permitCode.length()) " +
                    "== authentication.principal.permitCode"
    )
    @Override
    public void decommissionDevice(DeviceEntity deviceEntity) {


        deviceValidator.onDecommissionDeviceValidation(deviceEntity);

        deviceEntity.setStatus(Status.PS39);

        deviceEntity.setLocation(null);
        deviceEntity.setDetail(ReplacementType.DEC.getComment());

        deviceRepository.save(deviceEntity);
        deviceRepository.flush();
    }
}
