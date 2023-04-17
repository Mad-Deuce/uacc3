package dms.standing.data.converter;

import dms.standing.data.dock.val.Status;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import static org.apache.commons.lang3.StringUtils.trim;

@Converter(autoApply = true)
public class StatusConverter implements AttributeConverter<Status, String> {
    @Override
    public String convertToDatabaseColumn(Status status) {
        return status.getName();
    }

    @Override
    public Status convertToEntityAttribute(String name) {
//        if (name==null) return null;
        for (Status status : Status.values()) {
            if (status.getName().equals(trim(name))) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown code " + name);

    }

}
