package dms.standing.data.repository;

import dms.standing.data.entity.RtuObjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DRtuRepository extends JpaRepository<RtuObjectEntity, String> {
}
