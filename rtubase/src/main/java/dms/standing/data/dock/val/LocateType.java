package dms.standing.data.dock.val;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
}
