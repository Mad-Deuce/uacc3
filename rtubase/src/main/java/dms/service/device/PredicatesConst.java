package dms.service.device;

import dms.mapper.ExplicitDeviceMatcher;
import dms.standing.data.dock.val.LocateType;
import dms.standing.data.dock.val.RegionType;
import dms.standing.data.dock.val.Status;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.sql.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
public enum PredicatesConst {
    IN {
        @Override
        Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, String filterFieldName, List<Object> filterValues) {
            Path<Object> path = from.get(filterFieldName);
            return criteriaBuilder.in(path).value(convertByClass(path, filterValues));
        }
    },

    DATE_IS {
        @Override
        Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, String filterFieldName, List<Object> filterValues) {
            if (!filterValues.isEmpty()) {
                Path<Date> path = from.get(ExplicitDeviceMatcher.getInstanceByFilterPropertyName(filterFieldName)
                        .getEntityPropertyNameLastPart());
                return criteriaBuilder.equal(
                        path.as(Date.class),
                        filterValues.get(0));
            } else {
                return alwaysTrue(criteriaBuilder);
            }
        }
    },
    DATE_IS_NOT {
        @Override
        Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, String filterFieldName, List<Object> filterValues) {
            if (!filterValues.isEmpty()) {
                Path<Date> path = from.get(ExplicitDeviceMatcher.getInstanceByFilterPropertyName(filterFieldName)
                        .getEntityPropertyNameLastPart());
                return criteriaBuilder.notEqual(
                        path.as(Date.class),
                        filterValues.get(0));
            } else {
                return alwaysTrue(criteriaBuilder);
            }
        }
    },
    DATE_BEFORE {
        @Override
        Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, String filterFieldName, List<Object> filterValues) {
            if (!filterValues.isEmpty()) {
                Path<Date> path = from.get(ExplicitDeviceMatcher.getInstanceByFilterPropertyName(filterFieldName)
                        .getEntityPropertyNameLastPart());
                return criteriaBuilder.lessThanOrEqualTo(
                        path.as(Date.class),
                        (Date) filterValues.get(0));
            } else {
                return alwaysTrue(criteriaBuilder);
            }
        }
    },
    DATE_AFTER {
        @Override
        Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, String filterFieldName, List<Object> filterValues) {
            if (!filterValues.isEmpty()) {
                Path<Date> path = from.get(ExplicitDeviceMatcher.getInstanceByFilterPropertyName(filterFieldName)
                        .getEntityPropertyNameLastPart());
                return criteriaBuilder.greaterThanOrEqualTo(
                        path.as(Date.class),
                        (Date) filterValues.get(0));
            } else {
                return alwaysTrue(criteriaBuilder);
            }
        }
    },

    IS {
        @Override
        Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, String filterFieldName, List<Object> filterValues) {
            if (!filterValues.isEmpty()) {
                Path<String> path = from.get(ExplicitDeviceMatcher.getInstanceByFilterPropertyName(filterFieldName)
                        .getEntityPropertyNameLastPart());
                return criteriaBuilder.equal(
                        path.as(String.class),
                        Objects.toString(filterValues.get(0)));
            } else {
                return alwaysTrue(criteriaBuilder);
            }
        }
    },
    IS_NOT {
        @Override
        Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, String filterFieldName, List<Object> filterValues) {
            if (!filterValues.isEmpty()) {
                Path<String> path = from.get(ExplicitDeviceMatcher.getInstanceByFilterPropertyName(filterFieldName)
                        .getEntityPropertyNameLastPart());
                return criteriaBuilder.notEqual(
                        path.as(String.class),
                        Objects.toString(filterValues.get(0)));
            } else {
                return alwaysTrue(criteriaBuilder);
            }
        }
    },
    BEFORE {
        @Override
        Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, String filterFieldName, List<Object> filterValues) {
            if (!filterValues.isEmpty()) {
                Path<String> path = from.get(ExplicitDeviceMatcher.getInstanceByFilterPropertyName(filterFieldName)
                        .getEntityPropertyNameLastPart());
                return criteriaBuilder.lessThanOrEqualTo(
                        path.as(String.class),
                        Objects.toString(filterValues.get(0)));
            } else {
                return alwaysTrue(criteriaBuilder);
            }
        }
    },
    AFTER {
        @Override
        Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, String filterFieldName, List<Object> filterValues) {
            if (!filterValues.isEmpty()) {
                Path<String> path = from.get(ExplicitDeviceMatcher.getInstanceByFilterPropertyName(filterFieldName)
                        .getEntityPropertyNameLastPart());
                return criteriaBuilder.greaterThanOrEqualTo(
                        path.as(String.class),
                        Objects.toString(filterValues.get(0)));
            } else {
                return alwaysTrue(criteriaBuilder);
            }
        }
    },

    CONTAINS {
        @Override
        Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, String filterFieldName, List<Object> filterValues) {
            if (!filterValues.isEmpty()) {
                Path<String> path = from.get(ExplicitDeviceMatcher.getInstanceByFilterPropertyName(filterFieldName)
                        .getEntityPropertyNameLastPart());
                return criteriaBuilder.like(
                        path.as(String.class),
                        "%" + filterValues.get(0) + "%");
            } else {
                return alwaysTrue(criteriaBuilder);
            }
        }
    },
    STARTS_WITH {
        @Override
        Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, String filterFieldName, List<Object> filterValues) {
            if (!filterValues.isEmpty()) {
                Path<String> path = from.get(ExplicitDeviceMatcher.getInstanceByFilterPropertyName(filterFieldName)
                        .getEntityPropertyNameLastPart());
                return criteriaBuilder.like(
                        path.as(String.class),
                        filterValues.get(0) + "%");
            } else {
                return alwaysTrue(criteriaBuilder);
            }
        }
    },
    ENDS_WITH {
        @Override
        Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, String filterFieldName, List<Object> filterValues) {
            if (!filterValues.isEmpty()) {
                Path<String> path = from.get(ExplicitDeviceMatcher.getInstanceByFilterPropertyName(filterFieldName)
                        .getEntityPropertyNameLastPart());
                return criteriaBuilder.like(
                        path.as(String.class),
                        "%" + filterValues.get(0));
            } else {
                return alwaysTrue(criteriaBuilder);
            }
        }
    },
    EQUALS {
        @Override
        Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, String filterFieldName, List<Object> filterValues) {
            if (!filterValues.isEmpty()) {
                Path<Object> path = from.get(filterFieldName);
                return criteriaBuilder.equal(
                        path,
                        filterValues.get(0));
            } else {
                return alwaysTrue(criteriaBuilder);
            }
        }
    },
    NOT_EQUALS {
        @Override
        Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, String filterFieldName, List<Object> filterValues) {
            if (!filterValues.isEmpty()) {
                Path<Object> path = from.get(ExplicitDeviceMatcher.getInstanceByFilterPropertyName(filterFieldName)
                        .getEntityPropertyNameLastPart());
                return criteriaBuilder.notEqual(
                        path,
                        filterValues.get(0));
            } else {
                return alwaysTrue(criteriaBuilder);
            }
        }
    },
    LTE {
        @Override
        Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, String filterFieldName, List<Object> filterValues) {
            if (!filterValues.isEmpty()) {
                Path<Integer> path = from.get(ExplicitDeviceMatcher.getInstanceByFilterPropertyName(filterFieldName)
                        .getEntityPropertyNameLastPart());
                return criteriaBuilder.le(
                        path.as(Integer.class),
                        ((Integer) filterValues.get(0)));
            } else {
                return alwaysTrue(criteriaBuilder);
            }
        }
    },
    LT {
        @Override
        Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, String filterFieldName, List<Object> filterValues) {
            if (!filterValues.isEmpty()) {
                Path<Integer> path = from.get(ExplicitDeviceMatcher.getInstanceByFilterPropertyName(filterFieldName)
                        .getEntityPropertyNameLastPart());
                return criteriaBuilder.lt(
                        path.as(Integer.class),
                        ((Integer) filterValues.get(0)));
            } else {
                return alwaysTrue(criteriaBuilder);
            }
        }
    },
    GTE {
        @Override
        Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, String filterFieldName, List<Object> filterValues) {
            if (!filterValues.isEmpty()) {
                Path<Integer> path = from.get(ExplicitDeviceMatcher.getInstanceByFilterPropertyName(filterFieldName)
                        .getEntityPropertyNameLastPart());
                return criteriaBuilder.ge(
                        path.as(Integer.class),
                        ((Integer) filterValues.get(0)));
            } else {
                return alwaysTrue(criteriaBuilder);
            }
        }
    },
    GT {
        @Override
        Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, String filterFieldName, List<Object> filterValues) {
            if (!filterValues.isEmpty()) {
                Path<Integer> path = from.get(ExplicitDeviceMatcher.getInstanceByFilterPropertyName(filterFieldName)
                        .getEntityPropertyNameLastPart());
                return criteriaBuilder.gt(
                        path.as(Integer.class),
                        ((Integer) filterValues.get(0)));
            } else {
                return alwaysTrue(criteriaBuilder);
            }
        }
    },
    ;

    abstract Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, String filterFieldName, List<Object> filterValues);

    public static List<?> convertByClass(Path<Object> path, List<Object> filterValueList) {
        if (Status.class.equals(path.getJavaType())) {
            return Status.toStatusList(filterValueList);
        } else if (RegionType.class.equals(path.getJavaType())) {
            return RegionType.toStatusList(filterValueList);
        } else if (LocateType.class.equals(path.getJavaType())) {
            return LocateType.toStatusList(filterValueList);
        } else {
            return filterValueList;
        }
    }

    private static Predicate alwaysTrue(CriteriaBuilder cb) {
        return cb.equal(cb.literal("1"), "1");
    }
}
