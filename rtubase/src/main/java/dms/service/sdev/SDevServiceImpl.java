package dms.service.sdev;

import dms.entity.standing.data.SDevEntity;
import dms.repository.SDevRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SDevServiceImpl implements SDevService{

    private final SDevRepository sDevRepository;

    @Autowired
    public SDevServiceImpl(SDevRepository sDevRepository) {
        this.sDevRepository = sDevRepository;
    }

    @Override
    public SDevEntity findSDevByID(Long id) {
        return sDevRepository.getReferenceById(id);
    }
}
