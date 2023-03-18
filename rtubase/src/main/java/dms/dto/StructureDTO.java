package dms.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dms.standing.data.dock.val.RegionType;
import dms.standing.data.dock.val.Status;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StructureDTO {
    private String id;
    private String status;
    private String regionType;
    private String item;
    private boolean expandable;
    private boolean isLoading;
    private int level;
    private String cls;

    public StructureDTO(String id, String status, String regionType, String item, boolean expandable, int level, String cls) {
        this.id = id;
        this.status = status;
        this.regionType = regionType;
        this.item = item;
        this.expandable = expandable;
        this.isLoading=true;
        this.level=level;
        this.cls=cls;
    }
}
