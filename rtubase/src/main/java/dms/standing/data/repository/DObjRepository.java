package dms.standing.data.repository;

import dms.standing.data.entity.LineFacilityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DObjRepository extends JpaRepository<LineFacilityEntity, String> {
}
