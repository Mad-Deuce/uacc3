package dms.standing.data.dock.val;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReplacementType {
    OTK("OTK", "По отказу прибора", ""),
    ZAM("ZAM", "По сроку проверки", ""),
    NEW("NEW", "Модернізація", ""),
    DIS("DIS", "Демонтаж", ""),
    DEC("DEC", "Списання", "")
    ;

    private final String name;
    private final String comment;
    private final String valueC;
}
