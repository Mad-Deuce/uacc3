package dms.standing.data.dock.val;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {
    PS11("11", "На линии", "ЛИНИЯ"),
    PS21("21", "Аварийный запас станции", "СТНЦ АВЗ"),
    PS31("31", "Оборотный фонд КИП", "КИП ОБФ"),
    PS32("32", "Аварийный запас КИП", "КИП АВЗ"),
    PS39("39", "Списан", "СПИС"),
    PS51("51", "Замена с линии в КИП", "ЛИН>КИП"),
    PS52("52", "Замена с АВЗ станций в КИП", "АВЗ>КИП"),
    PS53("53", "Замена из КИП на линию", "КИП>ЛИН"),
    PS54("54", "Замена из КИП в АВЗ станций", "КИП>АВЗ"),
    PS12("12", "Учет приостановлен", "ВЫКЛ"),
    PS42("42", "Учет приостановлен", "ВЫКЛ"),
    PS2("2", "Учет приостановлен", "ВЫКЛ"),

    ;

    private final String name;
    private final String comment;
    private final String valueC;
}
