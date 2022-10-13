package dms.repository;

import dms.entity.standing.data.DRailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface DRailRepository extends JpaRepository<DRailEntity, Long> {
    List<DRailEntity> findAll();
}
