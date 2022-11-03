package dms.standing.data.repository;

import dms.standing.data.entity.RailwayEntity;
import org.springframework.data.jpa.repository.JpaRepository;



public interface DRailRepository extends JpaRepository<RailwayEntity, Long> {
}
