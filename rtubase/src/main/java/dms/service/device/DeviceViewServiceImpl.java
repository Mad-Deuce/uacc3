package dms.service.device;

import dms.RtubaseAuthService;
import dms.config.multitenant.TenantIdentifierResolver;
import dms.entity.DeviceViewMainEntity;
import dms.filter.Filter;
import dms.repository.DeviceViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.criteria.Predicate;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeviceViewServiceImpl implements DeviceViewService {

    @Autowired
    private TenantIdentifierResolver currentTenant;

    private final RtubaseAuthService rtubaseAuthService;

    private final DeviceViewRepository deviceRepository;

    public DeviceViewServiceImpl(RtubaseAuthService rtubaseAuthService, DeviceViewRepository deviceRepository) {
        this.rtubaseAuthService = rtubaseAuthService;
        this.deviceRepository = deviceRepository;
    }

    @Override
    public List<DeviceViewMainEntity> findAllDevices() {
        return deviceRepository.findAll();
    }

    @Override
    public Page<DeviceViewMainEntity> findDevicesBySpecification(Pageable pageable, List<Filter<Object>> filters) {
        return deviceRepository.findAll(getSpecification(filters), pageable);
    }

    private Specification<?> getSpecification(List<Filter<Object>> filters) {
        return (root, criteriaQuery, criteriaBuilder) -> {

            criteriaQuery.distinct(false);

            Predicate predicateDefault = criteriaBuilder
                    .like(root.get("rtdId"), rtubaseAuthService.getPrincipalPermitCode() + "%");

            List<Predicate> predicatesList = new ArrayList<>();

            filters.forEach(filter -> {
                        if (!filter.getValues().isEmpty() && !filter.getValues().contains(null)) {
                            PredicatesConst predicate = PredicatesConst
                                    .valueOf(camelCaseToUnderScoreUpperCase(filter.getMatchMode()));

                            List<Object> filterValues = Collections.EMPTY_LIST;

                            if (getFieldsGroup(0).contains(filter.getFieldName())) {
                                //"IN" match mode ----------------------------------------------------------------------
                                filterValues = filter.getValues().stream()
                                        .map(Object::toString)
                                        .collect(Collectors.toList());
                                predicatesList.add(predicate.create(root, criteriaBuilder, filter.getFieldName(), filterValues));
                            } else if (getFieldsGroup(1).contains(filter.getFieldName())) {
                                //STRING TYPE "CONTAINS, START_WITH, END_WITH, EQUALS" match modes ---------------------
                                filterValues = filter.getValues().stream()
                                        .map(Object::toString)
                                        .collect(Collectors.toList());
                                predicatesList.add(predicate.create(root, criteriaBuilder, filter.getFieldName(), filterValues));
                            } else if (getFieldsGroup(2).contains(filter.getFieldName())) {
                                //"IS, IS_NOT, AFTER, BEFORE" match modes ----------------------------------------------
                                filterValues = filter.getValues().stream()
                                        .filter(v -> v.getClass().equals(Date.class))
                                        .map(v -> Integer.toString(((Date) v).toLocalDate().getYear()))
                                        .collect(Collectors.toList());
                                predicatesList.add(predicate.create(root, criteriaBuilder, filter.getFieldName(), filterValues));
                            } else if (getFieldsGroup(3).contains(filter.getFieldName())) {
                                //"DATE_IS, DATE_IS_NOT, DATE_AFTER, DATE_BEFORE" match modes --------------------------
                                filterValues = filter.getValues().stream()
                                        .filter(v -> v.getClass().equals(Date.class))
                                        .collect(Collectors.toList());
                                predicatesList.add(predicate.create(root, criteriaBuilder, filter.getFieldName(), filterValues));
                            } else if (getFieldsGroup(4).contains(filter.getFieldName())) {
                                //"EQUALS, NOT_EQUALS, LT, LTE, GT, GTE" match modes ------------------------------
                                filterValues = filter.getValues().stream()
                                        .filter(v -> v.getClass().equals(Integer.class))
                                        .collect(Collectors.toList());
                                predicatesList.add(predicate.create(root, criteriaBuilder, filter.getFieldName(), filterValues));
                            }

                        }
                    }
            );

            return predicatesList.stream().reduce(predicateDefault, criteriaBuilder::and);
        };
    }

    private String camelCaseToUnderScoreUpperCase(String camelCase) {
        StringBuilder result = new StringBuilder();
        boolean prevUpperCase = false;
        for (int i = 0; i < camelCase.length(); i++) {
            char c = camelCase.charAt(i);
            if (!Character.isLetter(c))
                return camelCase;
            if (Character.isUpperCase(c)) {
                if (prevUpperCase)
                    return camelCase;
                result.append("_").append(c);
                prevUpperCase = true;
            } else {
                result.append(Character.toUpperCase(c));
                prevUpperCase = false;
            }
        }
        return result.toString();
    }

    private List<String> getFieldsGroup(Integer index) {
        //       fGroup0 - field type [STRING], match mode [IN]
        String[] fGroup0 = {"railwayName", "subdivisionShortName", "subdivisionName", "rtdName", "facilityName",
                "typeName", "typeGroupName", "status", "regionType", "locateType"};
        //       fGroup1 - field type [STRING], match modes [CONTAINS, START_WITH, END_WITH, EQUALS]
        String[] fGroup1 = {"id", "number", "detail", "region", "locate", "placeNumber", "label", "locationDetail", "railwayId"};
        //       fGroup2 - field type [STRING->INTEGER], match modes [IS, IS_NOT, AFTER, BEFORE]
        String[] fGroup2 = {"releaseYear"};
        //       fGroup3 - field type [DATE], match modes [DATE_IS, DATE_IS_NOT, DATE_AFTER, DATE_BEFORE]
        String[] fGroup3 = {"testDate", "nextTestDate"};
        //       fGroup4 - field type [INTEGER], match modes [EQUALS, NOT_EQUALS, LT, LTE, GT, GTE]
        String[] fGroup4 = {"replacementPeriod"};
        String[][] main = {fGroup0, fGroup1, fGroup2, fGroup3, fGroup4};
        return Arrays.stream(main[index]).toList();
    }
}
