package dms.repository;

import dms.entity.standing.data.DDistEntity;
import dms.entity.standing.data.DRailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DDistRepository extends JpaRepository<DDistEntity, Long> {
}