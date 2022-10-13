package dms.service.ddist;

import dms.entity.standing.data.DDistEntity;
import dms.entity.standing.data.DRtuEntity;

import java.util.List;

public interface DDistService {
    public List<DDistEntity> getAll();
}
