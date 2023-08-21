package dms.report_generator.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
public class OverdueDevicesStatsHistoryReportModel {
    String objectId;
    String objectName;
    Map<LocalDate, Long> expiredDevicesQuantity;
    Map<LocalDate, Long> expiredWarrantyDevicesQuantity;
}

