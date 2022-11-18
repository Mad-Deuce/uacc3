package dms.standing.data.service.dobj;

import dms.standing.data.entity.LineObjectEntity;
import dms.standing.data.repository.DObjRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DObjServiceImpl implements DObjService {

    private final DObjRepository dObjRepository;

    @Autowired
    public DObjServiceImpl(DObjRepository dObjRepository) {
        this.dObjRepository = dObjRepository;
    }


    public List<LineObjectEntity> getAll() {
        return dObjRepository.findAll();
    }

    @Override
    public Optional<LineObjectEntity> findById(String id) {
        return dObjRepository.findById(id);
    }
}
