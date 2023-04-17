package dms.service.device;

import dms.filter.Filter;
import dms.mapper.ExplicitDeviceMatcher;
import dms.standing.data.dock.val.Status;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.List;

@Slf4j
public enum PredicatesConst {
    IN {
        @Override
        Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, Filter filter) {
            Path<Object> path = from.get(ExplicitDeviceMatcher.getInstanceByFilterPropertyName(filter.getFieldName())
                    .getEntityPropertyNameLastPart());
            return criteriaBuilder.in(path).value(convertByClass(path, filter.getValues()));
        }
    },
    STARTS_WITH {
        @Override
        Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, Filter filter) {
            return criteriaBuilder.like(
                    from.get(ExplicitDeviceMatcher.getInstanceByFilterPropertyName(filter.getFieldName()).getEntityPropertyNameLastPart()).as(String.class),
                    filter.getValues().get(0) + "%");
        }
    },
    END_WITH {
        @Override
        Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, Filter filter) {
            return criteriaBuilder.like(
                    from.get(ExplicitDeviceMatcher.getInstanceByFilterPropertyName(filter.getFieldName()).getEntityPropertyNameLastPart()).as(String.class),
                    "%" + filter.getValues().get(0));
        }
    },
    CONTAINS {
        @Override
        Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, Filter filter) {
            return criteriaBuilder.like(
                    from.get(ExplicitDeviceMatcher.getInstanceByFilterPropertyName(filter.getFieldName()).getEntityPropertyNameLastPart()).as(String.class),
                    "%" + filter.getValues().get(0) + "%");
        }
    },
    NOT_CONTAINS {
        @Override
        Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, Filter filter) {
            return criteriaBuilder.notLike(
                    from.get(ExplicitDeviceMatcher.getInstanceByFilterPropertyName(filter.getFieldName()).getEntityPropertyNameLastPart()).as(String.class),
                    "%" + filter.getValues().get(0) + "%");
        }
    },
    EQUALS {
        @Override
        Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, Filter filter) {
            return criteriaBuilder.equal(
                    from.get(ExplicitDeviceMatcher.getInstanceByFilterPropertyName(filter.getFieldName()).getEntityPropertyNameLastPart()).as(String.class),
                    filter.getValues().get(0));
        }
    },
    NOT_EQUALS {
        @Override
        Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, Filter filter) {
            return criteriaBuilder.notEqual(
                    from.get(ExplicitDeviceMatcher.getInstanceByFilterPropertyName(filter.getFieldName()).getEntityPropertyNameLastPart()).as(String.class),
                    filter.getValues().get(0));
        }
    },
    GREATER_THAN_OR_EQUAL_TO {
        @Override
        Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, Filter filter) {
            return criteriaBuilder.greaterThanOrEqualTo(
                    from.get(ExplicitDeviceMatcher.getInstanceByFilterPropertyName(filter.getFieldName()).getEntityPropertyNameLastPart()).as(String.class),
                    filter.getValues().get(0));
        }
    },
    LESS_THAN_OR_EQUAL_TO {
        @Override
        Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, Filter filter) {
            return criteriaBuilder.lessThanOrEqualTo(
                    from.get(ExplicitDeviceMatcher.getInstanceByFilterPropertyName(filter.getFieldName()).getEntityPropertyNameLastPart()).as(String.class),
                    filter.getValues().get(0));
        }
    };

    abstract Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, Filter filter);

    public static List<?> convertByClass(Path<Object> path, List<String> filterValueList) {
        if (Status.class.equals(path.getJavaType())) {
            return Status.toStatusList(filterValueList);
        } else {
            return filterValueList;
        }
    }
}
