package dms.service.stats;


import dms.dto.stats.OverdueDevicesStats;
import dms.entity.OverdueDevsStatsEntity;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

public interface StatsService {

    OverdueDevicesStats getOverdueDevicesStats();

    void saveAllSchemaOverdueDevsStats();

    void saveCurrentSchemaOverdueDevsStats();

    HashMap<LocalDate, OverdueDevicesStats> getOverdueDevicesStatsMap(String nodeId) throws SQLException;

    List<OverdueDevsStatsEntity> getOverdueDevicesStatsEntityList(String parentId);

    List<OverdueDevsStatsEntity> getOverdueDevicesStatsEntityList(String parentId, LocalDate schemaDate);
}
