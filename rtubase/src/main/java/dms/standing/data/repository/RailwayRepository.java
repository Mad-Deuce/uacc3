package dms.standing.data.repository;

import dms.standing.data.entity.RailwayEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RailwayRepository extends JpaRepository<RailwayEntity, String> {
    List<RailwayEntity> findByIdStartsWith(String id);
}
