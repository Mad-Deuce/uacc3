package dms.standing.data.repository;

import dms.standing.data.entity.SubdivisionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DDistRepository extends JpaRepository<SubdivisionEntity, Long> {
}
