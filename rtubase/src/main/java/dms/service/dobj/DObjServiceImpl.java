package dms.service.dobj;

import dms.entity.standing.data.DObjEntity;
import dms.repository.DObjRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DObjServiceImpl implements DObjService {

    private final DObjRepository dObjRepository;

    @Autowired
    public DObjServiceImpl(DObjRepository dObjRepository) {
        this.dObjRepository = dObjRepository;
    }


    public List<DObjEntity> getAll() {
        return dObjRepository.findAll();
    }
}
