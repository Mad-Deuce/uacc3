package dms.standing.data.service.facility;

import dms.standing.data.entity.RtdFacilityEntity;
import dms.standing.data.repository.RtdFacilityRepository;
import dms.standing.data.service.facility.RtdFacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RtdFacilityServiceImpl implements RtdFacilityService {

    private final RtdFacilityRepository rtdFacilityRepository;

    @Autowired
    public RtdFacilityServiceImpl(RtdFacilityRepository rtdFacilityRepository){
        this.rtdFacilityRepository = rtdFacilityRepository;
    }


    public List<RtdFacilityEntity> getAll(){
        return rtdFacilityRepository.findAll();
    }

    @Override
    public Optional<RtdFacilityEntity> findById(String id) {
        return rtdFacilityRepository.findById(id);
    }
}
