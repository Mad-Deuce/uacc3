package dms.standing.data.dock.val;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
    private final String comm;
    private final String valueC;
}
