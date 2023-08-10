package dms.dto.stats;


import lombok.Data;

import java.time.LocalDate;
import java.util.HashMap;


@Data
public class StatsDTO {

    private OverdueDevicesStats overdueDevicesStats;

    private HashMap<LocalDate, OverdueDevicesStats> overdueDevicesStatsMap;
}
