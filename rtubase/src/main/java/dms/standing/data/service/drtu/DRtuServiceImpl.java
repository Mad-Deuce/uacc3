package dms.standing.data.service.drtu;

import dms.standing.data.entity.DRtuEntity;
import dms.standing.data.repository.DRtuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public Optional<DRtuEntity> findById(String id) {
        return dRtuRepository.findById(id);
    }
}