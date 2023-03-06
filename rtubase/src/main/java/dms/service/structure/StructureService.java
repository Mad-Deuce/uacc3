package dms.service.structure;

import dms.dto.StructureDTO;
import dms.standing.data.dock.val.RegionType;

import java.util.List;

public interface StructureService {

    List<StructureDTO> getChildren(String id, String regionType);

    StructureDTO getRoot();
}
