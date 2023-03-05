package dms.standing.data.service.ddist;

import dms.standing.data.entity.SubdivisionEntity;
import dms.standing.data.repository.SubdivisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DDistServiceImpl implements DDistService {

    private final SubdivisionRepository subdivisionRepository;

    @Autowired
    public DDistServiceImpl(SubdivisionRepository subdivisionRepository){
        this.subdivisionRepository = subdivisionRepository;
    }


    public List<SubdivisionEntity> getAll(){
        return subdivisionRepository.findAll();
    }
}
