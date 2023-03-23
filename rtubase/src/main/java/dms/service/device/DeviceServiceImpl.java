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
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.Field;
import java.sql.Date;
import java.util.List;
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
                                deviceAuthService.getAuthConditionsPartOfFindDeviceByFilterQuery())
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

    @SneakyThrows
    private String getProperty(DeviceFilter deviceFilter, ExplicitDeviceMatcher item) {
        return BeanUtils.getProperty(deviceFilter, item.getFilterPropertyName());
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
