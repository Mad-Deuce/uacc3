package dms.standing.data.converter;


import dms.standing.data.dock.val.ReplacementType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import static org.apache.commons.lang3.StringUtils.trim;

@Converter
public class ReplacementTypeConverter implements AttributeConverter<ReplacementType, String> {
    @Override
    public String convertToDatabaseColumn(ReplacementType replacementType) {
        return replacementType.getName();
    }

    @Override
    public ReplacementType convertToEntityAttribute(String name) {
//        if (name==null) return null;
        for (ReplacementType replacementType : ReplacementType.values()) {
            if (replacementType.getName().equals(trim(name))) {
                return replacementType;
            }
        }
        throw new IllegalArgumentException("Unknown code " + name);

    }
}
