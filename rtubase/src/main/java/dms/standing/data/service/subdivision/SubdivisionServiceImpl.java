package dms.standing.data.service.subdivision;

import dms.standing.data.entity.SubdivisionEntity;
import dms.standing.data.repository.SubdivisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubdivisionServiceImpl implements SubdivisionService {

    private final SubdivisionRepository subdivisionRepository;

    @Autowired
    public SubdivisionServiceImpl(SubdivisionRepository subdivisionRepository){
        this.subdivisionRepository = subdivisionRepository;
    }


    public List<SubdivisionEntity> getAll(){
        return subdivisionRepository.findAll();
    }
}
