package dms.converter;

import dms.dock.val.LocateType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import static org.apache.commons.lang3.StringUtils.trim;

@Converter
public class LocateTypeConverter implements AttributeConverter<LocateType, String> {
    @Override
    public String convertToDatabaseColumn(LocateType locateType) {
        return locateType.getName();
    }

    @Override
    public LocateType convertToEntityAttribute(String name) {
        for (LocateType locateType : LocateType.values()) {
            if (locateType.getName().equals(trim(name))) {
                return locateType;
            }
        }
        throw new IllegalArgumentException("Unknown code " + name);

    }
}
