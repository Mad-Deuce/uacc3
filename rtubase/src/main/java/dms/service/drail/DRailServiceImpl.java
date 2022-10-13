package dms.service.drail;

import dms.entity.standing.data.DRailEntity;
import dms.repository.DRailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DRailServiceImpl implements DRailService {

    private final DRailRepository dRailRepository;

    @Autowired
    public DRailServiceImpl(DRailRepository dRailRepository){
        this.dRailRepository = dRailRepository;
    }


    public List<DRailEntity> getAll(){
        return dRailRepository.findAll();
    }
}
