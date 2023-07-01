package dms.service.structure;

import dms.dto.ObjectTreeNodeDto;


import java.util.List;

public interface StructureService {

    List<ObjectTreeNodeDto> getChildrenAlt(String parentId, String parentClsId);

    ObjectTreeNodeDto getRootAlt();
}
