package dms.service.stats;


import java.util.Map;

public interface StatsService {

    Map<String, Long> getStats(String cls, String id);
}
