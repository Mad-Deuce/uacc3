package dms.standing.data.repository;

import dms.standing.data.entity.RtuFacilityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DRtuRepository extends JpaRepository<RtuFacilityEntity, String> {
}
