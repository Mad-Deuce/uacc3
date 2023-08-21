package dms.repository;

import dms.entity.OverdueDevsStatsEntity;
import dms.standing.data.entity.SubdivisionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OverdueDevsStatsRepository extends JpaRepository<OverdueDevsStatsEntity, Long> {

    List<OverdueDevsStatsEntity> findByObjectIdIn(List<String> idList);
    List<OverdueDevsStatsEntity> findByObjectIdStartsWith(String parentId);
}
