package dms.standing.data.repository;

import dms.standing.data.entity.RtdFacilityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RtdFacilityRepository extends JpaRepository<RtdFacilityEntity, String> {
    List<RtdFacilityEntity> findAllByIdStartingWithOrderById(String parentId);
}
