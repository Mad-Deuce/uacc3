package dms.standing.data.service.facility;

import dms.standing.data.entity.LineFacilityEntity;
import dms.standing.data.repository.LineFacilityRepository;
import dms.standing.data.service.facility.LineFacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LineFacilityServiceImpl implements LineFacilityService {

    private final LineFacilityRepository lineFacilityRepository;

    @Autowired
    public LineFacilityServiceImpl(LineFacilityRepository lineFacilityRepository) {
        this.lineFacilityRepository = lineFacilityRepository;
    }


    public List<LineFacilityEntity> getAll() {
        return lineFacilityRepository.findAll();
    }

    @Override
    public Optional<LineFacilityEntity> findById(String id) {
        return lineFacilityRepository.findById(id);
    }
}
