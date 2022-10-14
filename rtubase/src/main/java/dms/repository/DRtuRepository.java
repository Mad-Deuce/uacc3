package dms.repository;

import dms.entity.standing.data.DRtuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DRtuRepository extends JpaRepository<DRtuEntity, Long> {
}
