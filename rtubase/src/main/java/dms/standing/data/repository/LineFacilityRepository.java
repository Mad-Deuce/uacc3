package dms.standing.data.repository;

import dms.standing.data.entity.LineFacilityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LineFacilityRepository extends JpaRepository<LineFacilityEntity, String> {
    List<LineFacilityEntity> findAllByIdStartingWithOrderById(String parentId);
}
