package dms.service.stats;


import dms.dto.stats.OverdueDevicesStats;

import javax.transaction.Transactional;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;

public interface StatsService {

    OverdueDevicesStats getOverdueDevicesStats();

     void saveOverdueDevsStats();
    HashMap<LocalDate, OverdueDevicesStats> getOverdueDevicesStatsMap(String nodeId) throws SQLException;

}
