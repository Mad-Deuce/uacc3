package dms.standing.data.service.dobj;

import dms.standing.data.entity.LineObjectEntity;

import java.util.List;
import java.util.Optional;

public interface DObjService {
    List<LineObjectEntity> getAll();

    Optional<LineObjectEntity> findById(String id);
}
