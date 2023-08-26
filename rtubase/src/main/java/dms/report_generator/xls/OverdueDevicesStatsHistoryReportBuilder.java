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
import java.util.Set;
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
        setColumnWidth(sheet);
        fillSheet(sheet, values);
        return workbook;
    }

    private Map<String, OverdueDevicesStatsHistoryReportModel> convertToReportModel(List<OverdueDevsStatsEntity> inpData) {
        Map<String, OverdueDevicesStatsHistoryReportModel> result = new TreeMap<>();
        Map<LocalDate, Long> standardDateMap = new TreeMap<>();
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
            standardDateMap.put(inpItem.getStatsDate(), 0L);
            rowModel.getExpiredDevicesQuantity().put(inpItem.getStatsDate(), inpItem.getExpiredDevicesQuantity());
            rowModel.getExpiredWarrantyDevicesQuantity().put(inpItem.getStatsDate(), inpItem.getExpiredWarrantyDevicesQuantity());
        }
        checkComplete(result, standardDateMap);
        return result;
    }

    private void checkComplete(Map<String, OverdueDevicesStatsHistoryReportModel> result, Map<LocalDate, Long> standardDateMap) {
        for (OverdueDevicesStatsHistoryReportModel item : result.values()) {
            for (LocalDate key : standardDateMap.keySet()) {
                item.getExpiredDevicesQuantity().putIfAbsent(key, null);
                item.getExpiredWarrantyDevicesQuantity().putIfAbsent(key, null);
            }
        }
    }

    private void fillHeaders(Sheet sheet, Set<LocalDate> labels, int startRowIndex, String header1Label) {
        CellStyle headerStyle0 = getHeaderStyle0(sheet);
        CellStyle headerStyle1 = getHeaderStyle1(sheet);
        CellStyle headerStyle2 = getHeaderStyle2(sheet);
        CellStyle headerStyle3 = getHeaderStyle3(sheet);

        int columnIndex = 1;

        Row headerRow1 = sheet.createRow(startRowIndex);
        Row headerRow2 = sheet.createRow(startRowIndex + 1);
        Row headerRow3 = sheet.createRow(startRowIndex + 2);

        Cell headerCell;

        headerCell = headerRow1.createCell(0);
        headerCell.setCellValue("Структурний підрозділ");
        sheet.addMergedRegion(new CellRangeAddress(startRowIndex, startRowIndex + HEADER_ROWS_QUANTITY - 1,
                0, 0));
        headerCell.setCellStyle(headerStyle0);
        setAllBorderToMergedRegion(sheet, headerCell);

        for (LocalDate label : labels) {
            headerCell = headerRow1.createCell(columnIndex);
            headerCell.setCellValue(header1Label);
            headerCell.setCellStyle(headerStyle1);
            setYBorderToMergedRegion(headerCell);
            if (isNotPartOfMergedRegion(headerCell)) {
                sheet.addMergedRegion(new CellRangeAddress(startRowIndex, startRowIndex,
                        columnIndex,
                        columnIndex + (
                                (labels.size() * 2 - columnIndex) / 15 >= 1
                                        ? 15
                                        : (labels.size() * 2 - columnIndex) % 15
                        )
                ));
            }

            headerCell = headerRow2.createCell(columnIndex);
            headerCell.setCellValue("Станом на");
            headerCell.setCellStyle(headerStyle2);
            headerCell = headerRow2.createCell(columnIndex + 1);
            headerCell.setCellStyle(headerStyle2);
            if (isNotPartOfMergedRegion(headerCell)) {
                sheet.addMergedRegion(new CellRangeAddress(startRowIndex + 1, startRowIndex + 1,
                        columnIndex, columnIndex + 1));
            }

            headerCell = headerRow3.createCell(columnIndex);
            headerCell.setCellValue(label);
            headerCell.setCellStyle(headerStyle3);
            headerCell = headerRow3.createCell(columnIndex + 1);
            headerCell.setCellStyle(headerStyle3);
            if (isNotPartOfMergedRegion(headerCell)) {
                sheet.addMergedRegion(new CellRangeAddress(startRowIndex + 2, startRowIndex + 2,
                        columnIndex, columnIndex + 1));
            }
            columnIndex = columnIndex + 2;
        }
    }

    private void fillBody(Sheet sheet, Map<String, OverdueDevicesStatsHistoryReportModel> inpData, int startRowIndex, int options) {
        CellStyle dataStyle1 = getDataStyle1(sheet);
        CellStyle headerStyle0 = getHeaderStyle0(sheet);

        int dataRowIndex = startRowIndex;
        Row dataRow;
        Cell dataCell;

        for (OverdueDevicesStatsHistoryReportModel statsItem : inpData.values()) {

            dataRow = sheet.createRow(dataRowIndex);
            dataCell = dataRow.createCell(0);
            dataCell.setCellValue(statsItem.getObjectName());
            dataCell.setCellStyle(headerStyle0);

            int columnIndex = 1;
            Set<LocalDate> keySet;
            Long value;
            if (options == 0) keySet = statsItem.getExpiredDevicesQuantity().keySet();
            else keySet = statsItem.getExpiredWarrantyDevicesQuantity().keySet();

            for (LocalDate key : keySet) {

                dataCell = dataRow.createCell(columnIndex);
                dataCell.setCellStyle(headerStyle0);
                if (options == 0) value = statsItem.getExpiredDevicesQuantity().get(key);
                else value = statsItem.getExpiredWarrantyDevicesQuantity().get(key);
                if (value != null) dataCell.setCellValue(value);

                dataCell = dataRow.createCell(columnIndex + 1);
                dataCell.setCellStyle(dataStyle1);
                addConditionalFormatting(sheet, dataCell);
                if (columnIndex > 1) dataCell.setCellFormula(getIfFormula(dataRowIndex, columnIndex));

                columnIndex = columnIndex + 2;
            }
            dataRowIndex++;
        }
    }

    private void fillTotal(Sheet sheet, int totalRowIndex, int startDataRowIndex, int columnQuantity) {
        CellStyle headerStyle0 = getHeaderStyle0(sheet);
        CellStyle dataStyle1 = getDataStyle1(sheet);

        Row totalRow = sheet.createRow(totalRowIndex);
        Cell totalCell;

        totalCell = totalRow.createCell(0);
        totalCell.setCellValue("Ш");
        totalCell.setCellStyle(headerStyle0);

        for (int i = 1; i < columnQuantity; i = i + 2) {

            totalCell = totalRow.createCell(i);
            totalCell.setCellStyle(headerStyle0);
            totalCell.setCellFormula(getSumFormula(startDataRowIndex, totalRowIndex, i));

            totalCell = totalRow.createCell(i + 1);
            totalCell.setCellStyle(dataStyle1);
            addConditionalFormatting(sheet, totalCell);
            if (i > 1) totalCell.setCellFormula(getIfFormula(totalRowIndex, i));
        }
    }

    private void fillSheet(Sheet sheet, Map<String, OverdueDevicesStatsHistoryReportModel> inpData) {
        String firstKey = inpData.keySet().stream().findFirst().orElse("");
        OverdueDevicesStatsHistoryReportModel firstItem = inpData.get(firstKey);
        if (firstItem != null) {
            Set<LocalDate> labels = firstItem.getExpiredDevicesQuantity().keySet();
            fillHeaders(sheet, labels, 0,
                    "З урахуванням гарантійного терміну придатності");
            fillHeaders(sheet, labels, HEADER_ROWS_QUANTITY + inpData.size() + 1,
                    "Без урахування гарантійного терміну придатності");

            fillBody(sheet, inpData, HEADER_ROWS_QUANTITY, 0);
            fillBody(sheet, inpData, HEADER_ROWS_QUANTITY * 2 + inpData.size() + 1, 1);

            fillTotal(sheet, HEADER_ROWS_QUANTITY + inpData.size(),
                    HEADER_ROWS_QUANTITY + 1,
                    firstItem.getExpiredDevicesQuantity().size() * 2);
            fillTotal(sheet, (HEADER_ROWS_QUANTITY + inpData.size()) * 2 + 1,
                    HEADER_ROWS_QUANTITY * 2 + inpData.size() + 2,
                    firstItem.getExpiredWarrantyDevicesQuantity().size() * 2);
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
        result.setAlignment(HorizontalAlignment.LEFT);
        result.setIndention((short) 5);
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

    private boolean isNotPartOfMergedRegion(Cell cell) {
        Sheet sheet = cell.getSheet();
        for (CellRangeAddress mergedCell : sheet.getMergedRegions()) {
            if (mergedCell.isInRange(cell)) {
                return false;
            }
        }
        return true;
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

    private void setYBorderToMergedRegion(Cell cell) {
        Sheet sheet = cell.getSheet();
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






























