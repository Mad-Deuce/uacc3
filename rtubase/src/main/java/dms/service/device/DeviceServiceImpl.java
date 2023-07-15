package dms.service.device;


import dms.RtubaseAuthService;
import dms.entity.DeviceEntity;
import dms.entity.LocationEntity;
import dms.exception.DeviceValidationException;
import dms.exception.NoEntityException;
import dms.filter.DeviceFilter;
import dms.filter.Filter;
import dms.mapper.ExplicitDeviceMatcher;
import dms.repository.DeviceRepository;
import dms.repository.LocationRepository;
import dms.standing.data.dock.val.ReplacementType;
import dms.standing.data.dock.val.Status;
import dms.standing.data.entity.DeviceTypeEntity;
import dms.standing.data.entity.DeviceTypeGroupEntity;
import dms.standing.data.entity.FacilityEntity;
import dms.standing.data.entity.SubdivisionEntity;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.lang.reflect.Field;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service("DevService1")
public class DeviceServiceImpl implements DeviceService {


    @PersistenceContext
    EntityManager em;

    private final RtubaseAuthService rtubaseAuthService;

    private final DeviceValidator deviceValidator;
    private final DeviceRepository deviceRepository;
    private final LineFacilityRepository lineFacilityRepository;
    private final RtdFacilityRepository rtdFacilityRepository;
    private final LocationRepository locationRepository;

    @Autowired
    public DeviceServiceImpl(RtubaseAuthService rtubaseAuthService,
                             DeviceValidator deviceValidator,
                             DeviceRepository deviceRepository,
                             LineFacilityRepository lineFacilityRepository,
                             RtdFacilityRepository rtdFacilityRepository,
                             LocationRepository locationRepository) {
        this.rtubaseAuthService = rtubaseAuthService;
        this.deviceValidator = deviceValidator;
        this.deviceRepository = deviceRepository;
        this.lineFacilityRepository = lineFacilityRepository;
        this.rtdFacilityRepository = rtdFacilityRepository;
        this.locationRepository = locationRepository;
    }


    @PostAuthorize(
            "(hasRole('ADMIN') || hasRole('OPERATOR') || hasRole('VIEWER')) && " +
                    "returnObject.facility.id.substring(0, authentication.principal.permitCode.length()) " +
                    "== authentication.principal.permitCode"
    )
    @Override
    public DeviceEntity findDeviceById(Long id) {
        return deviceRepository.findById(id).orElseThrow();
    }

    @PostAuthorize("(hasRole('ADMIN') || hasRole('OPERATOR') || hasRole('VIEWER'))")
    @Override
    public Page<DeviceEntity> findDevicesByFilter(Pageable pageable, DeviceFilter deviceFilter) throws NoSuchFieldException {

        Long contentSize = (Long) em.createQuery(
                        "SELECT count (distinct d.id) " +
                                "FROM DeviceEntity d " +
                                "WHERE 1=1 " +
                                getQueryConditionsPart(deviceFilter) +
                                rtubaseAuthService.getAuthConditionsPartOfFindDeviceByFilterQuery())
                .getSingleResult();

        List<DeviceEntity> content = em.createQuery(
                        "SELECT d " +
                                " FROM DeviceEntity d " +
                                " JOIN FETCH d.type t " +
                                " JOIN FETCH t.group g " +
                                " LEFT JOIN FETCH d.facility f " +
                                " LEFT JOIN FETCH d.location l " +
                                " WHERE 1=1 " +
                                getQueryConditionsPart(deviceFilter) +
                                rtubaseAuthService.getAuthConditionsPartOfFindDeviceByFilterQuery() +
                                getQueryOrderPart(pageable) +
                                " ORDER BY d.id ASC"
                        , DeviceEntity.class)
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
                if (item.equals(ExplicitDeviceMatcher.CLS_ID)) {
                    if (getProperty(deviceFilter, item).equals("21111")) {
                        queryConditionsPart
                                .append(" AND d.status = '11' ");
                    } else if (getProperty(deviceFilter, item).equals("21114")) {
                        queryConditionsPart
                                .append(" AND d.status = '21' ");
                    } else if (getProperty(deviceFilter, item).equals("21112")) {
                        queryConditionsPart
                                .append(" AND (d.status = '21' OR d.status = '11') ");
                    }
                } else if (java.util.Date.class.isAssignableFrom(field.getType())) {

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

    private String getQueryOrderPart(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            return "";
        } else {
            for (ExplicitDeviceMatcher item : ExplicitDeviceMatcher.values()) {
                List<Sort.Order> orders = pageable.getSort().get().collect(Collectors.toList());
                for (Sort.Order order : orders) {
                    String property = order.getProperty();
                    Sort.Direction direction = order.getDirection();


                }

                if (item.getFilterPropertyName().equals(pageable.getSort())) {
                    return "";

                }
            }
        }
        return "";

    }

    @SneakyThrows
    private String getProperty(DeviceFilter deviceFilter, ExplicitDeviceMatcher item) {
        return BeanUtils.getProperty(deviceFilter, item.getFilterPropertyName());
    }


    @Override
    public Page<DeviceEntity> findDevicesBySpecification(Pageable pageable, List<Filter<Object>> filters) {
        return deviceRepository.findAll(getSpecification(filters), pageable);
    }

    private Specification<?> getSpecification(List<Filter<Object>> filters) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Join<DeviceEntity, DeviceTypeEntity> type = root.join("type");
            Join<DeviceTypeEntity, DeviceTypeGroupEntity> group = type.join("group");
            Join<DeviceEntity, LocationEntity> location = root.join("location", JoinType.LEFT);
            Join<DeviceEntity, FacilityEntity> facility = root.join("facility");
//            Join<FacilityEntity, SubdivisionEntity> subdivision = facility.join("subdivision");

            criteriaQuery.distinct(false);

            Predicate predicateDefault = criteriaBuilder.equal(root, root);

            Predicate predicateAuth = criteriaBuilder
                    .like(facility.get("id"), rtubaseAuthService.getPrincipalPermitCode() + "%");

            predicateDefault = predicateAuth;

            List<Predicate> predicatesList = new ArrayList<>();

            filters.forEach(filter -> {
                        if (!filter.getValues().isEmpty() && !filter.getValues().contains(null)) {
                            PredicatesConst predicate = PredicatesConst
                                    .valueOf(camelCaseToUnderScoreUpperCase(filter.getMatchMode()));

//                            List<Object> filterValues = new ArrayList<>();
                            List<Object> filterValues = Collections.EMPTY_LIST;

                            if (filter.getFieldName().equals("id")) {
                                filterValues = filter.getValues().stream()
                                        .map(Object::toString)
                                        .collect(Collectors.toList());
                                predicatesList.add(predicate.create(root, criteriaBuilder, filter.getFieldName(), filterValues));
                            }
                            if (filter.getFieldName().equals("number")) {
                                filterValues = filter.getValues().stream()
                                        .map(Object::toString)
                                        .collect(Collectors.toList());
                                predicatesList.add(predicate.create(root, criteriaBuilder, filter.getFieldName(), filterValues));
                            }
                            if (filter.getFieldName().equals("releaseYear")) {
                                filterValues = filter.getValues().stream()
                                        .filter(v -> v.getClass().equals(Date.class))
                                        .map(v -> Integer.toString(((Date) v).toLocalDate().getYear()))
                                        .collect(Collectors.toList());
                                predicatesList.add(predicate.create(root, criteriaBuilder, filter.getFieldName(), filterValues));
                            }
                            if (filter.getFieldName().equals("testDate")) {
                                filterValues = filter.getValues().stream()
                                        .filter(v -> v.getClass().equals(Date.class))
                                        .collect(Collectors.toList());
                                predicatesList.add(predicate.create(root, criteriaBuilder, filter.getFieldName(), filterValues));
                            }
                            if (filter.getFieldName().equals("nextTestDate")) {
                                filterValues = filter.getValues().stream()
                                        .filter(v -> v.getClass().equals(Date.class))
                                        .collect(Collectors.toList());
                                predicatesList.add(predicate.create(root, criteriaBuilder, filter.getFieldName(), filterValues));
                            }
                            if (filter.getFieldName().equals("status")) {
                                filterValues = filter.getValues().stream()
                                        .map(Object::toString)
                                        .collect(Collectors.toList());
                                predicatesList.add(predicate.create(root, criteriaBuilder, filter.getFieldName(), filterValues));
                            }
                            if (filter.getFieldName().equals("replacementPeriod")) {
                                filterValues = filter.getValues().stream()
                                        .filter(v -> v.getClass().equals(Integer.class))
                                        .collect(Collectors.toList());
                                predicatesList.add(predicate.create(root, criteriaBuilder, filter.getFieldName(), filterValues));
                            }
                            if (filter.getFieldName().equals("typeName")) {
                                filterValues = filter.getValues().stream()
                                        .map(Object::toString)
                                        .collect(Collectors.toList());
                                predicatesList.add(predicate.create(type, criteriaBuilder, filter.getFieldName(), filterValues));
                            }
                            if (filter.getFieldName().equals("typeGroupName")) {
                                filterValues = filter.getValues().stream()
                                        .map(Object::toString)
                                        .collect(Collectors.toList());
                                predicatesList.add(predicate.create(group, criteriaBuilder, filter.getFieldName(), filterValues));
                            }
                            if (filter.getFieldName().equals("facilityId")) {
                                filterValues = filter.getValues().stream()
                                        .map(Object::toString)
                                        .collect(Collectors.toList());
                                predicatesList.add(predicate.create(facility, criteriaBuilder, filter.getFieldName(), filterValues));
                            }
//                            if (filter.getFieldName().equals("subdivisionShortName")) {
//                                filterValues = filter.getValues().stream()
//                                        .map(Object::toString)
//                                        .collect(Collectors.toList());
//                                predicatesList.add(predicate.create(subdivision, criteriaBuilder, filter.getFieldName(), filterValues));
//                            }
                        }
                    }
            );

            return predicatesList.stream().reduce(predicateDefault, criteriaBuilder::and);
        };
    }

    private String camelCaseToUnderScoreUpperCase(String camelCase) {
        String result = "";
        boolean prevUpperCase = false;
        for (int i = 0; i < camelCase.length(); i++) {
            char c = camelCase.charAt(i);
            if (!Character.isLetter(c))
                return camelCase;
            if (Character.isUpperCase(c)) {
                if (prevUpperCase)
                    return camelCase;
                result += "_" + c;
                prevUpperCase = true;
            } else {
                result += Character.toUpperCase(c);
                prevUpperCase = false;
            }
        }
        return result;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void deleteDeviceById(DeviceEntity deviceEntity) {
        deviceRepository.deleteById(deviceEntity.getId());
    }

    @PreAuthorize("hasRole('ADMIN')")
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

    @PreAuthorize(
            "(hasRole('ADMIN') || hasRole('OPERATOR')) && " +
                    "#deviceEntity.facility.id.substring(0, authentication.principal.permitCode.length()) " +
                    "== authentication.principal.permitCode"
    )
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


    @PreAuthorize(
            "(hasRole('ADMIN') || hasRole('OPERATOR')) && " +
                    "#oldDeviceEntity.facility.id.substring(0, authentication.principal.permitCode.length()) " +
                    "== authentication.principal.permitCode"
    )
    @Override
    public void replaceDevice(DeviceEntity oldDeviceEntity,
                              DeviceEntity newDeviceEntity,
                              String status,
                              ReplacementType replacementType) {

        if (status.equals(Status.PS21.getName())) {
            deviceValidator.onReplaceToAvzLineValidation(oldDeviceEntity, newDeviceEntity, replacementType);
            newDeviceEntity.setStatus(Status.PS21);
            newDeviceEntity.setLocation(null);
            oldDeviceEntity.setLocation(null);
        }

        if (status.equals(Status.PS32.getName())) {
            deviceValidator.onReplaceToAvzRtdValidation(oldDeviceEntity, newDeviceEntity, replacementType);
            newDeviceEntity.setStatus(Status.PS32);
            newDeviceEntity.setLocation(null);
            oldDeviceEntity.setLocation(null);
        }

        if (status.equals(Status.PS11.getName())) {
            deviceValidator.onReplaceToLineValidation(oldDeviceEntity, newDeviceEntity, replacementType);
            newDeviceEntity.setStatus(Status.PS11);
            newDeviceEntity.setLocation(oldDeviceEntity.getLocation());
            oldDeviceEntity.setLocation(null);

        }

        oldDeviceEntity.setStatus(Status.PS31);

        FacilityEntity newDeviceFacility = newDeviceEntity.getFacility();
        newDeviceEntity.setFacility(oldDeviceEntity.getFacility());
        oldDeviceEntity.setFacility(newDeviceFacility);

        newDeviceEntity.setDetail(replacementType.getComment());
        oldDeviceEntity.setDetail(replacementType.getComment());

        deviceRepository.save(newDeviceEntity);
        deviceRepository.save(oldDeviceEntity);
        deviceRepository.flush();
    }

    @PreAuthorize(
            "(hasRole('ADMIN') || hasRole('OPERATOR')) && " +
                    "#deviceEntity.facility.id.substring(0, authentication.principal.permitCode.length()) " +
                    "== authentication.principal.permitCode"
    )
    @Override
    public void setDeviceTo(DeviceEntity deviceEntity,
                            String status,
                            String facilityId,
                            Long locationId) {

        if (status.equals(Status.PS21.getName())) {
            setDeviceToAvzLine(deviceEntity, facilityId);
        }

        if (status.equals(Status.PS32.getName())) {
            setDeviceToAvzRtd(deviceEntity, facilityId);
        }

        if (status.equals(Status.PS11.getName())) {
            setDeviceToLine(deviceEntity, locationId);
        }

        deviceRepository.save(deviceEntity);
        deviceRepository.flush();
    }

    private void setDeviceToLine(DeviceEntity deviceEntity, Long locationId) {

        LocationEntity locationEntity = locationRepository.findById(locationId)
                .orElseThrow(DeviceValidationException::new);
        FacilityEntity facilityEntity = lineFacilityRepository.findById(locationEntity.getFacility().getId())
                .orElseThrow(DeviceValidationException::new);

        deviceValidator.onSetDeviceToLineValidation(deviceEntity, facilityEntity, locationEntity);

        deviceEntity.setStatus(Status.PS11);

        deviceEntity.setLocation(locationEntity);
        deviceEntity.setFacility(facilityEntity);
        deviceEntity.setDetail(ReplacementType.NEW.getComment());
    }

    private void setDeviceToAvzLine(DeviceEntity deviceEntity, String facilityId) {

        FacilityEntity facilityEntity = lineFacilityRepository.findById(facilityId).orElseThrow();

        deviceValidator.onSetDeviceToAvzLineValidation(deviceEntity, facilityEntity);

        deviceEntity.setStatus(Status.PS21);

        deviceEntity.setLocation(null);
        deviceEntity.setFacility(facilityEntity);
        deviceEntity.setDetail(ReplacementType.NEW.getComment());
    }

    private void setDeviceToAvzRtd(DeviceEntity deviceEntity, String facilityId) {

        FacilityEntity facilityEntity = rtdFacilityRepository.findById(facilityId).orElseThrow();

        deviceValidator.onSetDeviceToAvzRtdValidation(deviceEntity, facilityEntity);

        deviceEntity.setStatus(Status.PS32);

        deviceEntity.setLocation(null);
        deviceEntity.setFacility(facilityEntity);
        deviceEntity.setDetail(ReplacementType.NEW.getComment());
    }

    @PreAuthorize(
            "(hasRole('ADMIN') || hasRole('OPERATOR')) && " +
                    "#deviceEntity.facility.id.substring(0, authentication.principal.permitCode.length()) " +
                    "== authentication.principal.permitCode"
    )
    @Override
    public void unsetDevice(DeviceEntity deviceEntity, String facilityId) {
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
            "(hasRole('ADMIN') || hasRole('OPERATOR')) && " +
                    "#deviceEntity.facility.id.substring(0, authentication.principal.permitCode.length()) " +
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
