package dms.service.dobj;

import dms.entity.standing.data.DObjEntity;

import java.util.List;

public interface DObjService {
    List<DObjEntity> getAll();

    DObjEntity findById(Long id);
}
