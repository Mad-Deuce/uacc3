package dms.repository;

import dms.entity.OverdueDevsStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OverdueDevsStatsRepository extends JpaRepository<OverdueDevsStatsEntity, Long> {
}
