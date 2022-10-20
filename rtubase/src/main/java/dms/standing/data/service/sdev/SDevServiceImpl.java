package dms.standing.data.service.sdev;

import dms.standing.data.entity.SDevEntity;
import dms.standing.data.repository.SDevRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SDevServiceImpl implements SDevService{

    private final SDevRepository sDevRepository;

    @Autowired
    public SDevServiceImpl(SDevRepository sDevRepository) {
        this.sDevRepository = sDevRepository;
    }

    @Override
    public Optional<SDevEntity> findSDevByID(Long id) {
        return sDevRepository.findById(id);
    }
}
