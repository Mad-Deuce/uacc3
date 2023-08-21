package dms.report_generator.xls;

import dms.entity.OverdueDevsStatsEntity;
import dms.report_generator.model.OverdueDevicesStatsHistoryReportModel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OverdueDevicesStatsHistoryReportBuilder {

    final int HEADER_COLUMNS_QUANTITY = 1;
    final int HEADER_ROWS_QUANTITY = 3;
    final int MAX_DATA_COLUMN_QUANTITY = 132;

    public Workbook getOverdueDevicesStatsHistoryReport(List<OverdueDevsStatsEntity> inpData) {
        Map<String, OverdueDevicesStatsHistoryReportModel> values = convertToReportModel(inpData);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("stats");
        format(sheet);
        fillSheet(sheet, values);
        return workbook;
    }

    private Map<String, OverdueDevicesStatsHistoryReportModel> convertToReportModel(List<OverdueDevsStatsEntity> inpData) {
        Map<String, OverdueDevicesStatsHistoryReportModel> result = new HashMap<>();
        for (OverdueDevsStatsEntity inpItem : inpData) {
            OverdueDevicesStatsHistoryReportModel rowModel = result.get(inpItem.getObjectId());
            if (rowModel == null) {
                rowModel = new OverdueDevicesStatsHistoryReportModel();
                result.put(inpItem.getObjectId(), rowModel);
                rowModel.setObjectId(inpItem.getObjectId());
                rowModel.setObjectName(inpItem.getObjectName());
                rowModel.setExpiredDevicesQuantity(new HashMap<>());
                rowModel.setExpiredWarrantyDevicesQuantity(new HashMap<>());
            }
            rowModel.getExpiredDevicesQuantity().put(inpItem.getStatsDate(), inpItem.getExpDevsQuantity());
            rowModel.getExpiredWarrantyDevicesQuantity().put(inpItem.getStatsDate(), inpItem.getExpWarrantyDevsQuantity());
        }

        return result;
    }

    private void fillSheet(Sheet sheet, Map<String, OverdueDevicesStatsHistoryReportModel> inpData) {
        int headerRowIndex1 = 0;
        int dataRowIndex1 = HEADER_ROWS_QUANTITY;
        int headerRowIndex2 = HEADER_ROWS_QUANTITY + inpData.size();
        int dataRowIndex2 = 2 * HEADER_ROWS_QUANTITY + inpData.size();

        Row hRow1;
        Row row1;
        Row hRow2;
        Row row2;
        Cell cell;
        Cell cell1;
        Cell cell2;
        for (OverdueDevicesStatsHistoryReportModel statsItem : inpData.values()) {
            hRow1 = sheet.createRow(headerRowIndex1 + 1);
            hRow2 = sheet.createRow(headerRowIndex2 + 1);
            row1 = sheet.createRow(dataRowIndex1);
            row2 = sheet.createRow(dataRowIndex2);
            cell = row1.createCell(0);
            cell.setCellValue(statsItem.getObjectName());
            cell = row2.createCell(0);
            cell.setCellValue(statsItem.getObjectName());

            int columnIndex = 1;
            for (Long value : statsItem.getExpiredDevicesQuantity().values()) {
                cell1 = row1.createCell(columnIndex);
                cell1.setCellValue(value == null ? 0 : value);
                columnIndex++;
            }
            columnIndex = 1;
            for (Long value : statsItem.getExpiredWarrantyDevicesQuantity().values()) {
                cell2 = row2.createCell(columnIndex);
                cell2.setCellValue(value == null ? 0 : value);
                columnIndex++;
            }
            dataRowIndex1++;
            dataRowIndex2++;
        }

    }

    private void format(Sheet sheet) {
        setColumnWidth(sheet);
    }

    private void setColumnWidth(Sheet sheet) {
        int k = 37;
        for (int i = 0; i < HEADER_COLUMNS_QUANTITY; i++) {
            sheet.setColumnWidth(i, k * 144);
        }
        for (int i = HEADER_COLUMNS_QUANTITY; i <= MAX_DATA_COLUMN_QUANTITY; i++) {
            sheet.setColumnWidth(i, k * 60);
        }
    }

}






























