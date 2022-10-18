package dms.repository;

import dms.entity.standing.data.SDevEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SDevRepository extends JpaRepository<SDevEntity, Long> {
}
