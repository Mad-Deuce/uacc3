package dms.standing.data.repository;

import dms.standing.data.entity.DDistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DDistRepository extends JpaRepository<DDistEntity, Long> {
}
