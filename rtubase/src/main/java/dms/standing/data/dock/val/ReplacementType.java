package dms.standing.data.dock.val;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReplacementType {
    OTK("OTK", "По отказу прибора", ""),
    ZAM("ZAM", "По сроку проверки", ""),
    ;

    private final String name;
    private final String comm;
    private final String valueC;
}
