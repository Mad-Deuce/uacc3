package dms.repository;

import dms.entity.standing.data.DRailEntity;
import org.springframework.data.jpa.repository.JpaRepository;



public interface DRailRepository extends JpaRepository<DRailEntity, Long> {
}
