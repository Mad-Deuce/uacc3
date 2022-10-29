package dms.standing.data.converter;

import dms.standing.data.dock.val.RegionType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import static org.apache.commons.lang3.StringUtils.trim;

@Converter
public class RegionTypeConverter implements AttributeConverter<RegionType, String> {
    @Override
    public String convertToDatabaseColumn(RegionType regionType) {
        return regionType.getName();
    }

    @Override
    public RegionType convertToEntityAttribute(String name) {
        if (name==null) return null;
        for (RegionType regionType : RegionType.values()) {
            if (regionType.getName().equals(trim(name))) {
                return regionType;
            }
        }
        throw new IllegalArgumentException("Unknown code " + name);

    }
}
