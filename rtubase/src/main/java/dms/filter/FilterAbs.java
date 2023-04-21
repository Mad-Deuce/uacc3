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
        defaultImpl = StringFilterImpl.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DateFilterImpl.class, name = "date"),
        @JsonSubTypes.Type(value = StringFilterImpl.class, name = "string"),
        @JsonSubTypes.Type(value = IntegerFilterImpl.class, name = "numeric")
})
public abstract class FilterAbs<T> {
    private String fieldName;
    private List<T> values;
    private String matchMode;
}
