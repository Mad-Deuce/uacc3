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
public enum RegionType {
    EC("EC", "Пост ЭЦ", "S,"),
    SU("SU", "Сигнальное устройство", "S,P,"),
    PD("PD", "Переезд", "S,P,"),
    NU("NU", "Напольное устройство", "S,"),
    ZZ("ZZ", "Прочие места", "S,P,"),
    PG("PG", "Перегон", "S,"),
    VS("VS", "Входные светофоры", "S,"),
    ;

    private final String name;
    private final String comment;
    private final String valueC;

    public static List<RegionType> toStatusList(List<Object> nameList) {
        List<RegionType> result = new ArrayList<>();
        nameList.forEach(item -> result.add(convertToStatus(Objects.toString(item))));
        return result;
    }

    private static RegionType convertToStatus(String name) {
//        if (name==null) return null;
        for (RegionType item : RegionType.values()) {
            if (item.getName().equals(trim(name))) {
                return item;
            }
        }
        throw new IllegalArgumentException("Unknown code " + name);
    }
}
