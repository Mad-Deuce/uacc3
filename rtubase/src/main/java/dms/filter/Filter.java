package dms.filter;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.util.List;

@Data
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        defaultImpl = StringFilter.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DateFilter.class, name = "date"),
        @JsonSubTypes.Type(value = StringFilter.class, name = "string"),
        @JsonSubTypes.Type(value = IntegerFilter.class, name = "numeric")
})
public abstract class Filter<T> {
    private String fieldName;
    private List<T> values;
    private String matchMode;
}
