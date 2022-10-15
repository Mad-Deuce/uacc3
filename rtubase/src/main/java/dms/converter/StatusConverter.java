package dms.converter;

import dms.dock.val.Status;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;


import static org.apache.commons.lang3.StringUtils.trim;

@Converter
public class StatusConverter implements AttributeConverter<Status, String> {
    @Override
    public String convertToDatabaseColumn(Status status) {
        return status.getName();
    }

    @Override
    public Status convertToEntityAttribute(String name) {

        for (Status status : Status.values()) {
            if (status.getName().equals(trim(name))) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown code " + name);

    }

}
