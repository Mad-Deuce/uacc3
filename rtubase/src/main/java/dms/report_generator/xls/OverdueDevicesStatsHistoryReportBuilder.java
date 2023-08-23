package dms.report_generator.xls;

import dms.entity.OverdueDevsStatsEntity;
import dms.report_generator.model.OverdueDevicesStatsHistoryReportModel;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.RegionUtil;
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
        CellStyle headerStyle1 = getHeaderStyle1(sheet);
        CellStyle headerStyle2 = getHeaderStyle2(sheet);
        CellStyle headerStyle3 = getHeaderStyle3(sheet);
        CellStyle headerStyle0 = getHeaderStyle0(sheet);
        CellStyle dataStyle1 = getDataStyle1(sheet);

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
        headerCell.setCellStyle(headerStyle0);
        setAllBorderToMergedRegion(sheet, headerCell);
        headerCell = headerRow21.createCell(0);
        headerCell.setCellValue("Структурний підрозділ");
        sheet.addMergedRegion(new CellRangeAddress(headerRowIndex2, dataRowIndex2 - 1, 0, 0));
        headerCell.setCellStyle(headerStyle0);
        setAllBorderToMergedRegion(sheet, headerCell);

        totalCell = totalRow1.createCell(0);
        totalCell.setCellValue("Ш");
        totalCell.setCellStyle(headerStyle0);
        totalCell = totalRow2.createCell(0);
        totalCell.setCellValue("Ш");
        totalCell.setCellStyle(headerStyle0);

        for (OverdueDevicesStatsHistoryReportModel statsItem : inpData.values()) {

            dataRow1 = sheet.createRow(dataRowIndex1);
            headerCell = dataRow1.createCell(0);
            headerCell.setCellValue(statsItem.getObjectName());
            headerCell.setCellStyle(headerStyle0);

            int columnIndex = 1;
            for (LocalDate key : statsItem.getExpiredWarrantyDevicesQuantity().keySet()) {

                headerCell = headerRow11.createCell(columnIndex);
                headerCell.setCellValue("З урахуванням гарантійного терміну придатності");
                headerCell.setCellStyle(headerStyle1);
                setHorizontalBorderToMergedRegion(sheet, headerCell);
                if (!isMergedCell(sheet, headerRowIndex1, columnIndex)) {
                    sheet.addMergedRegion(new CellRangeAddress(
                            headerRowIndex1,
                            headerRowIndex1,
                            columnIndex,
                            columnIndex + 15));
                }

                headerCell = headerRow12.createCell(columnIndex);
                headerCell.setCellValue("Станом на");
                headerCell.setCellStyle(headerStyle2);
                headerCell = headerRow12.createCell(columnIndex + 1);
                headerCell.setCellStyle(headerStyle2);
                if (!isMergedCell(sheet, headerRowIndex1 + 1, columnIndex)) {
                    sheet.addMergedRegion(new CellRangeAddress(
                            headerRowIndex1 + 1,
                            headerRowIndex1 + 1,
                            columnIndex,
                            columnIndex + 1));
                }

                headerCell = headerRow13.createCell(columnIndex);
                headerCell.setCellValue(key);
                headerCell.setCellStyle(headerStyle3);
                headerCell = headerRow13.createCell(columnIndex + 1);
                headerCell.setCellStyle(headerStyle3);
                if (!isMergedCell(sheet, headerRowIndex1 + 2, columnIndex)) {
                    sheet.addMergedRegion(new CellRangeAddress(
                            headerRowIndex1 + 2,
                            headerRowIndex1 + 2,
                            columnIndex,
                            columnIndex + 1));
                }

                dataCell = dataRow1.createCell(columnIndex);
                dataCell.setCellStyle(headerStyle0);
                Long value = statsItem.getExpiredWarrantyDevicesQuantity().get(key);
                if (value != null) dataCell.setCellValue(value);
//                dataCell.setCellValue(value == null ? 0 : value);


                dataCell = dataRow1.createCell(columnIndex + 1);
                dataCell.setCellStyle(dataStyle1);
                addConditionalFormatting(sheet, dataCell);
                if (columnIndex > 1) dataCell.setCellFormula(getIfFormula(dataRowIndex1, columnIndex));

                totalCell = totalRow1.createCell(columnIndex);
                totalCell.setCellStyle(headerStyle0);
                totalCell.setCellFormula(getSumFormula(HEADER_ROWS_QUANTITY + 1, totalRowIndex1, columnIndex));


                totalCell = totalRow1.createCell(columnIndex + 1);
                totalCell.setCellStyle(dataStyle1);
                addConditionalFormatting(sheet, totalCell);
                if (columnIndex > 1) totalCell.setCellFormula(getIfFormula(totalRowIndex1, columnIndex));

                columnIndex = columnIndex + 2;
            }


            dataRow2 = sheet.createRow(dataRowIndex2);
            headerCell = dataRow2.createCell(0);
            headerCell.setCellValue(statsItem.getObjectName());
            headerCell.setCellStyle(headerStyle0);

            columnIndex = 1;
            for (LocalDate key : statsItem.getExpiredDevicesQuantity().keySet()) {

                headerCell = headerRow21.createCell(columnIndex);
                headerCell.setCellValue("Без урахуванням гарантійного терміну придатності");
                headerCell.setCellStyle(headerStyle1);
                setHorizontalBorderToMergedRegion(sheet, headerCell);
                if (!isMergedCell(sheet, headerRowIndex2, columnIndex)) {
                    sheet.addMergedRegion(new CellRangeAddress(
                            headerRowIndex2,
                            headerRowIndex2,
                            columnIndex,
                            columnIndex + 15));
                }

                headerCell = headerRow22.createCell(columnIndex);
                headerCell.setCellValue("Станом на");
                headerCell.setCellStyle(headerStyle2);
                headerCell = headerRow22.createCell(columnIndex + 1);
                headerCell.setCellStyle(headerStyle2);
                if (!isMergedCell(sheet, headerRowIndex2 + 1, columnIndex)) {
                    sheet.addMergedRegion(new CellRangeAddress(
                            headerRowIndex2 + 1,
                            headerRowIndex2 + 1,
                            columnIndex,
                            columnIndex + 1));
                }


                headerCell = headerRow23.createCell(columnIndex);
                headerCell.setCellValue(key);
                headerCell.setCellStyle(headerStyle3);
                headerCell = headerRow23.createCell(columnIndex + 1);
                headerCell.setCellStyle(headerStyle3);
                if (!isMergedCell(sheet, headerRowIndex2 + 2, columnIndex)) {
                    sheet.addMergedRegion(new CellRangeAddress(
                            headerRowIndex2 + 2,
                            headerRowIndex2 + 2,
                            columnIndex,
                            columnIndex + 1));
                }


                dataCell = dataRow2.createCell(columnIndex);
                dataCell.setCellStyle(headerStyle0);
                Long value = statsItem.getExpiredDevicesQuantity().get(key);
                if (value != null) dataCell.setCellValue(value);
//                dataCell.setCellValue(value == null ? 0 : value);


                dataCell = dataRow2.createCell(columnIndex + 1);
                dataCell.setCellStyle(dataStyle1);
                addConditionalFormatting(sheet, dataCell);
                if (columnIndex > 1) dataCell.setCellFormula(getIfFormula(dataRowIndex2, columnIndex));

                totalCell = totalRow2.createCell(columnIndex);
                totalCell.setCellStyle(headerStyle0);
                totalCell.setCellFormula(getSumFormula(2 * HEADER_ROWS_QUANTITY + inpData.size() + 2, totalRowIndex2, columnIndex));

                totalCell = totalRow2.createCell(columnIndex + 1);
                totalCell.setCellStyle(dataStyle1);
                addConditionalFormatting(sheet, totalCell);
                if (columnIndex > 1) totalCell.setCellFormula(getIfFormula(totalRowIndex2, columnIndex));

                columnIndex = columnIndex + 2;
            }
            dataRowIndex1++;
            dataRowIndex2++;
        }

    }

    private void addConditionalFormatting(Sheet sheet, Cell cell) {
        SheetConditionalFormatting sheetCF = sheet.getSheetConditionalFormatting();

        ConditionalFormattingRule ruleGreen = sheetCF.createConditionalFormattingRule(
                ComparisonOperator.BETWEEN,
                "-1",
                "-9999999"
        );
        PatternFormatting fillGreen = ruleGreen.createPatternFormatting();
        fillGreen.setFillBackgroundColor(HSSFColor.HSSFColorPredefined.LIGHT_GREEN.getIndex());

        ConditionalFormattingRule ruleRed = sheetCF.createConditionalFormattingRule(
                ComparisonOperator.BETWEEN,
                "1",
                "9999999"
        );
        PatternFormatting fillRed = ruleRed.createPatternFormatting();
        fillRed.setFillBackgroundColor(HSSFColor.HSSFColorPredefined.CORAL.getIndex());

        ConditionalFormattingRule[] cfRules = new ConditionalFormattingRule[]{ruleGreen, ruleRed};
        CellRangeAddress[] regions = new CellRangeAddress[]{CellRangeAddress.valueOf(cell.getAddress().formatAsString())};

        sheetCF.addConditionalFormatting(regions, cfRules);


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

    private CellStyle getHeaderStyle2(Sheet sheet) {
        CellStyle result = sheet.getWorkbook().createCellStyle();
        result.setAlignment(HorizontalAlignment.CENTER);
        Font styleFont = sheet.getWorkbook().createFont();
        styleFont.setFontName("Times New Roman");
        styleFont.setBold(true);
        styleFont.setFontHeight((short) (14 * 20));
        styleFont.setColor(HSSFColor.HSSFColorPredefined.GREY_40_PERCENT.getIndex());
        result.setFont(styleFont);
        result.setBorderTop(BorderStyle.MEDIUM);
        result.setBorderBottom(BorderStyle.NONE);
        result.setBorderLeft(BorderStyle.MEDIUM);
        result.setBorderRight(BorderStyle.MEDIUM);
        return result;
    }

    private CellStyle getHeaderStyle1(Sheet sheet) {
        CellStyle result = sheet.getWorkbook().createCellStyle();
        result.setAlignment(HorizontalAlignment.CENTER);
        Font styleFont = sheet.getWorkbook().createFont();
        styleFont.setFontName("Times New Roman");
        styleFont.setBold(true);
        styleFont.setFontHeight((short) (20 * 20));
        styleFont.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        result.setFont(styleFont);
        result.setBorderTop(BorderStyle.MEDIUM);
        result.setBorderBottom(BorderStyle.MEDIUM);
        result.setBorderLeft(BorderStyle.NONE);
        result.setBorderRight(BorderStyle.NONE);
        return result;
    }

    private CellStyle getHeaderStyle0(Sheet sheet) {
        CellStyle result = sheet.getWorkbook().createCellStyle();
        result.setAlignment(HorizontalAlignment.CENTER);
        result.setVerticalAlignment(VerticalAlignment.CENTER);
        result.setWrapText(true);
        Font styleFont = sheet.getWorkbook().createFont();
        styleFont.setFontName("Times New Roman");
        styleFont.setBold(true);
        styleFont.setFontHeight((short) (14 * 20));
        styleFont.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        result.setFont(styleFont);
        result.setBorderTop(BorderStyle.MEDIUM);
        result.setBorderBottom(BorderStyle.MEDIUM);
        result.setBorderLeft(BorderStyle.MEDIUM);
        result.setBorderRight(BorderStyle.MEDIUM);
        return result;
    }

    private CellStyle getDataStyle1(Sheet sheet) {
        CellStyle result = sheet.getWorkbook().createCellStyle();
        result.setAlignment(HorizontalAlignment.CENTER);
        result.setVerticalAlignment(VerticalAlignment.CENTER);
        result.setWrapText(true);
        Font styleFont = sheet.getWorkbook().createFont();
        styleFont.setFontName("Times New Roman");
        styleFont.setBold(false);
        styleFont.setItalic(true);
        styleFont.setFontHeight((short) (14 * 20));
        styleFont.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        result.setFont(styleFont);
        result.setBorderTop(BorderStyle.MEDIUM);
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

    private void setAllBorderToMergedRegion(Sheet sheet, Cell cell) {
        int numberOfMergedRegions = sheet.getNumMergedRegions();
        for (int i = 0; i < numberOfMergedRegions; i++) {
            CellRangeAddress mergedCell = sheet.getMergedRegion(i);
            if (mergedCell.isInRange(cell)) {
                RegionUtil.setBorderTop(BorderStyle.MEDIUM, mergedCell, sheet);
                RegionUtil.setBorderBottom(BorderStyle.MEDIUM, mergedCell, sheet);
                RegionUtil.setBorderLeft(BorderStyle.MEDIUM, mergedCell, sheet);
                RegionUtil.setBorderRight(BorderStyle.MEDIUM, mergedCell, sheet);
            }
        }
    }

    private void setHorizontalBorderToMergedRegion(Sheet sheet, Cell cell) {
        int numberOfMergedRegions = sheet.getNumMergedRegions();
        for (int i = 0; i < numberOfMergedRegions; i++) {
            CellRangeAddress mergedCell = sheet.getMergedRegion(i);
            if (mergedCell.isInRange(cell)) {
                RegionUtil.setBorderTop(BorderStyle.MEDIUM, mergedCell, sheet);
                RegionUtil.setBorderBottom(BorderStyle.MEDIUM, mergedCell, sheet);
                RegionUtil.setBorderLeft(BorderStyle.NONE, mergedCell, sheet);
                RegionUtil.setBorderRight(BorderStyle.NONE, mergedCell, sheet);
            }
        }
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






























