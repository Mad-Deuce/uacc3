package dms.service.location;

import dms.entity.LocationEntity;
import dms.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Autowired
    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public Optional<LocationEntity> findDevObjById(Long id) {
        if (id==null) return Optional.empty();
        return locationRepository.findById(id);
    }


}
