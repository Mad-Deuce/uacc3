package dms.service.structure;

import dms.dto.ObjectTreeNode;
import dms.dto.StructureDTO;

import java.util.List;

public interface StructureService {

    List<StructureDTO> getChildren(String id, String regionType, String parentCls);

    List<ObjectTreeNode> getChildrenAlt(String parentId, String parentClsId);

    StructureDTO getRoot();

    ObjectTreeNode getRootAlt();
}
