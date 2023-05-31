package dms.standing.data.dock.val;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.trim;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
@AllArgsConstructor
public enum LocateType {
    BS("BS", "Батарейный шкаф", "NU,PD,SU,VS,PG,"),
    ST("ST", "Статив", "EC,"),
    EP("EP", "Электроприводы", "NU,"),
    TR("TR", "Трансформаторный ящик", "NU,"),
    SF("SF", "Светофор", "NU,"),
    AS("AS", "Автошлагбаум", "PD,PG,"),
    PR("PR", "Прочие устройства", "ZZ,PG,"),
    RS("RS", "Релейный шкаф", "PD,NU,SU,VS,PG,"),
    PK("PK", "Путевая коробка", "NU,SU,PG,"),
    DT("DT", "Дроссель-трансформатор", "NU,SU,PG,"),
    ES("ES", "Питающая стойка", "EC,"),
    KL("KL", "Кабельная муфта", "NU,"),
    ;

    private final String name;
    private final String comment;
    private final String valueC;

    public static List<LocateType> toStatusList(List<Object> nameList) {
        List<LocateType> result = new ArrayList<>();
        nameList.forEach(item -> result.add(convertToStatus(Objects.toString(item))));
        return result;
    }

    private static LocateType convertToStatus(String name) {
//        if (name==null) return null;
        for (LocateType item : LocateType.values()) {
            if (item.getName().equals(trim(name))) {
                return item;
            }
        }
        throw new IllegalArgumentException("Unknown code " + name);
    }
}
