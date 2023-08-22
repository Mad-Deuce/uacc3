package dms.report_generator.xls;

import dms.entity.OverdueDevsStatsEntity;
import dms.report_generator.model.OverdueDevicesStatsHistoryReportModel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
        Map<String, OverdueDevicesStatsHistoryReportModel> result = new TreeMap<>();
        for (OverdueDevsStatsEntity inpItem : inpData) {
            OverdueDevicesStatsHistoryReportModel rowModel = result.get(inpItem.getObjectId());
            if (rowModel == null) {
                rowModel = new OverdueDevicesStatsHistoryReportModel();
                result.put(inpItem.getObjectId(), rowModel);
                rowModel.setObjectId(inpItem.getObjectId());
                rowModel.setObjectName(inpItem.getObjectName());
                rowModel.setExpiredDevicesQuantity(new TreeMap<>());
                rowModel.setExpiredWarrantyDevicesQuantity(new TreeMap<>());
            }
            rowModel.getExpiredDevicesQuantity().put(inpItem.getStatsDate(), inpItem.getExpDevsQuantity());
            rowModel.getExpiredWarrantyDevicesQuantity().put(inpItem.getStatsDate(), inpItem.getExpWarrantyDevsQuantity());
        }

        return result;
    }

    private void fillSheet(Sheet sheet, Map<String, OverdueDevicesStatsHistoryReportModel> inpData) {
//        DataFormat format = sheet.getWorkbook().createDataFormat();
//        CellStyle dateStyle = sheet.getWorkbook().createCellStyle();
//        dateStyle.setDataFormat(format.getFormat("dd.mm.yyyy"));
        CellStyle dateStyle = getHeaderStyle3(sheet);


        int headerRowIndex1 = 0;
        int dataRowIndex1 = HEADER_ROWS_QUANTITY;
        int totalRowIndex1 = HEADER_ROWS_QUANTITY + inpData.size();

        int headerRowIndex2 = HEADER_ROWS_QUANTITY + inpData.size() + 1;
        int dataRowIndex2 = 2 * HEADER_ROWS_QUANTITY + inpData.size() + 1;
        int totalRowIndex2 = 2 * HEADER_ROWS_QUANTITY + 2 * inpData.size() + 1;

        Row headerRow11 = sheet.createRow(headerRowIndex1);
        Row headerRow12 = sheet.createRow(headerRowIndex1 + 1);
        Row headerRow13 = sheet.createRow(headerRowIndex1 + 2);
        Row dataRow1;
        Row totalRow1 = sheet.createRow(totalRowIndex1);

        Row headerRow21 = sheet.createRow(headerRowIndex2);
        Row headerRow22 = sheet.createRow(headerRowIndex2 + 1);
        Row headerRow23 = sheet.createRow(headerRowIndex2 + 2);
        Row dataRow2;
        Row totalRow2 = sheet.createRow(totalRowIndex2);

        Cell headerCell;
        Cell dataCell;
        Cell totalCell;


        headerCell = headerRow11.createCell(0);
        headerCell.setCellValue("Структурний підрозділ");
        sheet.addMergedRegion(new CellRangeAddress(headerRowIndex1, dataRowIndex1 - 1, 0, 0));
        headerCell = headerRow21.createCell(0);
        headerCell.setCellValue("Структурний підрозділ");
        sheet.addMergedRegion(new CellRangeAddress(headerRowIndex2, dataRowIndex2 - 1, 0, 0));


        totalCell = totalRow1.createCell(0);
        totalCell.setCellValue("Ш");
        totalCell = totalRow2.createCell(0);
        totalCell.setCellValue("Ш");

        for (OverdueDevicesStatsHistoryReportModel statsItem : inpData.values()) {

            dataRow1 = sheet.createRow(dataRowIndex1);
            headerCell = dataRow1.createCell(0);
            headerCell.setCellValue(statsItem.getObjectName());

            int columnIndex = 1;
            for (LocalDate key : statsItem.getExpiredWarrantyDevicesQuantity().keySet()) {

                headerCell = headerRow11.createCell(columnIndex);
                headerCell.setCellValue("З урахуванням гарантійного терміну придатності");
                if (!isMergedCell(sheet, headerRowIndex1, columnIndex)) {
                    sheet.addMergedRegion(new CellRangeAddress(
                            headerRowIndex1,
                            headerRowIndex1,
                            columnIndex,
                            columnIndex + 7));
                }

                headerCell = headerRow12.createCell(columnIndex);
                headerCell.setCellValue("Станом на");
                if (!isMergedCell(sheet, headerRowIndex1 + 1, columnIndex)) {
                    sheet.addMergedRegion(new CellRangeAddress(
                            headerRowIndex1 + 1,
                            headerRowIndex1 + 1,
                            columnIndex,
                            columnIndex + 1));
                }

                headerCell = headerRow13.createCell(columnIndex);
                headerCell.setCellValue(key);
                headerCell.setCellStyle(dateStyle);
                if (!isMergedCell(sheet, headerRowIndex1 + 2, columnIndex)) {
                    sheet.addMergedRegion(new CellRangeAddress(
                            headerRowIndex1 + 2,
                            headerRowIndex1 + 2,
                            columnIndex,
                            columnIndex + 1));
                }

                dataCell = dataRow1.createCell(columnIndex);
                Long value = statsItem.getExpiredWarrantyDevicesQuantity().get(key);
                dataCell.setCellValue(value == null ? 0 : value);

                dataCell = dataRow1.createCell(columnIndex + 1);
                if (columnIndex > 1) dataCell.setCellFormula(getIfFormula(dataRowIndex1, columnIndex));

                totalCell = totalRow1.createCell(columnIndex);
                totalCell.setCellFormula(getSumFormula(HEADER_ROWS_QUANTITY + 1, totalRowIndex1, columnIndex));

                totalCell = totalRow1.createCell(columnIndex + 1);
                if (columnIndex > 1) totalCell.setCellFormula(getIfFormula(totalRowIndex1, columnIndex));

                columnIndex = columnIndex + 2;
            }


            dataRow2 = sheet.createRow(dataRowIndex2);
            headerCell = dataRow2.createCell(0);
            headerCell.setCellValue(statsItem.getObjectName());

            columnIndex = 1;
            for (LocalDate key : statsItem.getExpiredDevicesQuantity().keySet()) {

                headerCell = headerRow21.createCell(columnIndex);
                headerCell.setCellValue("Без урахуванням гарантійного терміну придатності");
                if (!isMergedCell(sheet, headerRowIndex2, columnIndex)) {
                    sheet.addMergedRegion(new CellRangeAddress(
                            headerRowIndex2,
                            headerRowIndex2,
                            columnIndex,
                            columnIndex + 7));
                }

                headerCell = headerRow22.createCell(columnIndex);
                headerCell.setCellValue("Станом на");
                if (!isMergedCell(sheet, headerRowIndex2 + 1, columnIndex)) {
                    sheet.addMergedRegion(new CellRangeAddress(
                            headerRowIndex2 + 1,
                            headerRowIndex2 + 1,
                            columnIndex,
                            columnIndex + 1));
                }


                headerCell = headerRow23.createCell(columnIndex);
                headerCell.setCellValue(key);
                headerCell.setCellStyle(dateStyle);
                if (!isMergedCell(sheet, headerRowIndex2 + 2, columnIndex)) {
                    sheet.addMergedRegion(new CellRangeAddress(
                            headerRowIndex2 + 2,
                            headerRowIndex2 + 2,
                            columnIndex,
                            columnIndex + 1));
                }


                dataCell = dataRow2.createCell(columnIndex);
                Long value = statsItem.getExpiredDevicesQuantity().get(key);
                dataCell.setCellValue(value == null ? 0 : value);

                dataCell = dataRow2.createCell(columnIndex + 1);
                if (columnIndex > 1) dataCell.setCellFormula(getIfFormula(dataRowIndex2, columnIndex));

                totalCell = totalRow2.createCell(columnIndex);
                totalCell.setCellFormula(getSumFormula(2 * HEADER_ROWS_QUANTITY + inpData.size() + 2, totalRowIndex2, columnIndex));

                totalCell = totalRow2.createCell(columnIndex + 1);
                if (columnIndex > 1) totalCell.setCellFormula(getIfFormula(totalRowIndex2, columnIndex));

                columnIndex = columnIndex + 2;
            }
            dataRowIndex1++;
            dataRowIndex2++;
        }

    }

    private CellStyle getHeaderStyle3(Sheet sheet) {
        CellStyle result = sheet.getWorkbook().createCellStyle();
        DataFormat format = sheet.getWorkbook().createDataFormat();
        result.setDataFormat(format.getFormat("dd.mm.yyyy"));
        result.setAlignment(HorizontalAlignment.CENTER);
        Font styleFont = sheet.getWorkbook().createFont();
        styleFont.setFontName("Times New Roman");
        styleFont.setBold(true);
        styleFont.setFontHeight((short) (14 * 20));
        result.setFont(styleFont);
        result.setBorderTop(BorderStyle.NONE);
        result.setBorderBottom(BorderStyle.MEDIUM);
        result.setBorderLeft(BorderStyle.MEDIUM);
        result.setBorderRight(BorderStyle.MEDIUM);
        return result;
    }

    private boolean isMergedCell(Sheet sheet, int rowNumber, int columnNumber) {
        int numberOfMergedRegions = sheet.getNumMergedRegions();
        for (int i = 0; i < numberOfMergedRegions; i++) {
            CellRangeAddress mergedCell = sheet.getMergedRegion(i);

            if (mergedCell.isInRange(rowNumber, columnNumber)) {
                return true;
            }
        }
        return false;
    }

    private String getSumFormula(int startRowIndex, int endRowIndex, int columnIndex) {
        return "SUM(" +
                CellReference.convertNumToColString(columnIndex) +
                startRowIndex +
                ":" +
                CellReference.convertNumToColString(columnIndex) +
                endRowIndex +
                ")";
    }

    private String getIfFormula(int rowIndex, int columnIndex) {
        return "IF(" +
                CellReference.convertNumToColString(columnIndex) +
                (rowIndex + 1) +
                "=0,0,IF(" +
                CellReference.convertNumToColString(columnIndex) +
                (rowIndex + 1) +
                ">0," +
                CellReference.convertNumToColString(columnIndex) +
                (rowIndex + 1) +
                "-" +
                CellReference.convertNumToColString(columnIndex - 2) +
                (rowIndex + 1) +
                ",))";
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






























