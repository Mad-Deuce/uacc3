package dms.service.ddist;

import dms.entity.standing.data.DDistEntity;
import dms.entity.standing.data.DRtuEntity;
import dms.repository.DDistRepository;
import dms.repository.DRtuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DDistServiceImpl implements DDistService {

    private final DDistRepository dDistRepository;

    @Autowired
    public DDistServiceImpl(DDistRepository dDistRepository){
        this.dDistRepository = dDistRepository;
    }


    public List<DDistEntity> getAll(){
        return dDistRepository.findAll();
    }
}
