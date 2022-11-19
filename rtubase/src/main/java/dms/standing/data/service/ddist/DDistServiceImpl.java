package dms.standing.data.service.ddist;

import dms.standing.data.entity.SubdivisionEntity;
import dms.standing.data.repository.DDistRepository;
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


    public List<SubdivisionEntity> getAll(){
        return dDistRepository.findAll();
    }
}
