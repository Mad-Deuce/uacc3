package dms.standing.data.converter;

import dms.standing.data.dock.val.LocateType;

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
        if (name==null) return null;
        for (LocateType locateType : LocateType.values()) {
            if (locateType.getName().equals(trim(name))) {
                return locateType;
            }
        }
        throw new IllegalArgumentException("Unknown code " + name);

    }
}
