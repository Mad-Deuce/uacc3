package dms.report_generator.xls;

import dms.dto.stats.OverdueDevicesStats;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;

@Component
public class OverdueDevicesStatsHistoryReportBuilder {

    final int HEADER_COLUMN_QUANTITY = 1;
    final int MAX_DATA_COLUMN_QUANTITY = 132;

    public Workbook getOverdueDevicesStatsHistoryReport(HashMap<LocalDate, OverdueDevicesStats> inpData) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("stats");
        format(sheet);
        return workbook;
    }

    private void format(Sheet sheet) {
        setColumnWidth(sheet);
    }

    private void setColumnWidth(Sheet sheet) {
        int k = 37;
        for (int i = 0; i < HEADER_COLUMN_QUANTITY; i++) {
            sheet.setColumnWidth(i, k * 144);
        }
        for (int i = HEADER_COLUMN_QUANTITY; i <= MAX_DATA_COLUMN_QUANTITY; i++) {
            sheet.setColumnWidth(i, k * 60);
        }
    }

}
