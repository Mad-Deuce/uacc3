package dms.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dms.standing.data.dock.val.RegionType;
import dms.standing.data.dock.val.Status;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StructureDTO {
    private String id;
    private Status status;
    private RegionType regionType;
    private String name;
    private boolean hasChildren;

    public StructureDTO(String id, Status status, RegionType regionType, String name, boolean hasChildren) {
        this.id = id;
        this.status = status;
        this.regionType = regionType;
        this.name = name;
        this.hasChildren = hasChildren;
    }
}
