package dms.service.stats;



import dms.dto.stats.OverdueDevicesStats;

public interface StatsService {

    OverdueDevicesStats getOverdueDevicesStats();
}
