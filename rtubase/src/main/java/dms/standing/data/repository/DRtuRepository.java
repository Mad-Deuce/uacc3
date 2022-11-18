package dms.standing.data.repository;

import dms.standing.data.entity.RtuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DRtuRepository extends JpaRepository<RtuEntity, String> {
}
