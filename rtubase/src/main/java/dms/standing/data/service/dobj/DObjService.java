package dms.standing.data.service.dobj;

import dms.standing.data.entity.DObjEntity;

import java.util.List;
import java.util.Optional;

public interface DObjService {
    List<DObjEntity> getAll();

    Optional<DObjEntity> findById(String id);
}
