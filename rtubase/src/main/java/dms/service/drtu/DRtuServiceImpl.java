package dms.service.drtu;

import dms.entity.standing.data.DDistEntity;
import dms.entity.standing.data.DRtuEntity;
import dms.repository.DDistRepository;
import dms.repository.DRtuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DRtuServiceImpl implements DRtuService {

    private final DRtuRepository dRtuRepository;

    @Autowired
    public DRtuServiceImpl(DRtuRepository dRtuRepository){
        this.dRtuRepository = dRtuRepository;
    }


    public List<DRtuEntity> getAll(){
        return dRtuRepository.findAll();
    }

    @Override
    public DRtuEntity findById(Long id) {
        return dRtuRepository.getReferenceById(id);
    }
}
