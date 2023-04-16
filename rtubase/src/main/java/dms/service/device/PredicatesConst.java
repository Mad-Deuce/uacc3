package dms.service.device;

import dms.filter.Filter;
import dms.mapper.ExplicitDeviceMatcher;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

public enum PredicatesConst {
    IN {
        @Override
        Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, Filter filter) {
            return criteriaBuilder.in(
                    from.get(ExplicitDeviceMatcher.getInstanceByFilterPropertyName(filter.getFieldName()).getEntityPropertyNameLastPart())
            ).value(filter.getValues());
        }
    },
    STARTS_WITH {
        @Override
        Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, Filter filter) {
            return criteriaBuilder.like(
                    from.get(filter.getFieldName()).as(String.class),
                    filter.getValues().get(0) + "%");
        }
    },
    END_WITH {
        @Override
        Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, Filter filter) {
            return criteriaBuilder.like(
                    from.get(filter.getFieldName()).as(String.class),
                    "%" + filter.getValues().get(0));
        }
    },
    CONTAINS {
        @Override
        Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, Filter filter) {
            return criteriaBuilder.like(
                    from.get(filter.getFieldName()).as(String.class),
                    "%" + filter.getValues().get(0) + "%");
        }
    },
    NOT_CONTAINS {
        @Override
        Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, Filter filter) {
            return criteriaBuilder.like(
                    from.get(filter.getFieldName()).as(String.class),
                    "%" + filter.getValues().get(0) + "%");
        }
    },
    EQUALS {
        @Override
        Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, Filter filter) {
            return criteriaBuilder.like(
                    from.get(filter.getFieldName()).as(String.class),
                    "%" + filter.getValues().get(0) + "%");
        }
    },
    NOT_EQUALS {
        @Override
        Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, Filter filter) {
            return criteriaBuilder.like(
                    from.get(filter.getFieldName()).as(String.class),
                    "%" + filter.getValues().get(0) + "%");
        }
    };

    abstract Predicate create(From<?, ?> from, CriteriaBuilder criteriaBuilder, Filter filter);
}
