package dms.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ObjectTreeNode {
    private String id;
    private String clsId;
//    private String regionType;

    private boolean expandable;
    private boolean isLoading;

    private int level;
    private String label;
    private String tabLabel;
    private String tabHeader;


    public ObjectTreeNode(String id, String clsId, boolean expandable, int level,
                          String label, String tabLabel, String tabHeader) {
        this.id = id;
        this.clsId = clsId;
//        this.regionType = regionType;

        this.expandable = expandable;
        this.isLoading = true;

        this.level = level;
        this.label = label;
        this.tabLabel = tabLabel;
        this.tabHeader = tabHeader;
    }
}
