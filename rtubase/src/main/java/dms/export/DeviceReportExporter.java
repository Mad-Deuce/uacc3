package dms.export;

import dms.dto.DeviceDTO;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.lang.reflect.Field;
import java.util.List;

import static org.apache.poi.ss.usermodel.BorderStyle.MEDIUM;
import static org.apache.poi.ss.usermodel.BorderStyle.THIN;
import static org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER;
import static org.apache.poi.ss.usermodel.HorizontalAlignment.LEFT;
import static org.apache.poi.ss.usermodel.VerticalAlignment.TOP;
import static org.apache.poi.ss.util.CellUtil.createCell;

public class DeviceReportExporter {

    private final XSSFWorkbook workbook;
    private int headerStartRow;
    private int bodyStartRow;
    private int footerStartRow;

    public DeviceReportExporter() {
        this.workbook = new XSSFWorkbook();
        this.headerStartRow = 0;
        this.bodyStartRow = 0;
        this.footerStartRow = 0;
    }


    public XSSFWorkbook generateWorkbook(List<DeviceDTO> devicesList, String currentDateTime, DeviceDTO filterInfo) throws IllegalAccessException {

        XSSFSheet sheet = workbook.createSheet("report");

        this.fillingHeader(sheet, devicesList.size(), currentDateTime, getStringFilterInfo(filterInfo));
        this.fillingBodyByAnn(devicesList, sheet);
        this.fillingFooter(sheet);
        this.customizing(sheet);

        return workbook;
    }

    private void customizing(XSSFSheet sheet) {
        int k = 37;
        sheet.setColumnWidth(0, k * 101);
        sheet.setColumnWidth(1, k * 50);
        sheet.setColumnWidth(2, k * 125);
        sheet.setColumnWidth(3, k * 75);
        sheet.setColumnWidth(4, k * 40);
        sheet.setColumnWidth(5, k * 90);
        sheet.setColumnWidth(6, k * 90);
        sheet.setColumnWidth(7, k * 200);
        sheet.setColumnWidth(8, k * 101);
        sheet.setColumnWidth(9, k * 101);
        sheet.setColumnWidth(10, k * 80);
        sheet.setColumnWidth(11, k * 80);
        sheet.setColumnWidth(12, k * 80);
        sheet.setColumnWidth(13, k * 80);
        sheet.setColumnWidth(14, k * 80);
        sheet.setColumnWidth(15, k * 80);
        sheet.setColumnWidth(16, k * 80);
    }

    private void fillingHeader(XSSFSheet sheet, Integer dataSize, String currentDateTime, String filterInfo) {
        fillingHeader(sheet, dataSize, currentDateTime, filterInfo, headerStartRow);
    }

    private void fillingHeader(XSSFSheet sheet, Integer dataSize, String currentDateTime, String filterInfo, int startRow) {
        headerStartRow = startRow;
        createCell(sheet.createRow(headerStartRow++), 0, "Перелік приладів", getHeaderStyle());
        createCell(sheet.createRow(headerStartRow++), 0, "згідно заданих параметрів: ", getHeaderStyle());
        createCell(sheet.createRow(headerStartRow++), 0, filterInfo, getHeaderStyle());
        createCell(sheet.createRow(headerStartRow++), 0, "станом на: " + currentDateTime, getHeaderStyle());
        createCell(sheet.createRow(headerStartRow++), 0, "кількість: " + dataSize.toString(), getHeaderStyle());
        createCell(sheet.createRow(headerStartRow++), 0, "", getHeaderStyle());
        bodyStartRow = headerStartRow;
    }

    private void fillingBodyByAnn(List<DeviceDTO> devicesList, XSSFSheet sheet) throws IllegalAccessException {
        fillingBodyByAnn(devicesList, sheet, bodyStartRow);
    }

    private void fillingBodyByAnn(List<DeviceDTO> devicesList, XSSFSheet sheet, int startRow) throws IllegalAccessException {
        boolean isExportInfoAnnPresent = false;

//        Create TITLE Row
        Row row = sheet.createRow(startRow++);

        Class<?> clazz = devicesList.get(0).getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(ExportInfo.class)) {
                createCell(row,
                        field.getAnnotation(ExportInfo.class).position(),
                        field.getAnnotation(ExportInfo.class).title(),
                        getBodyTitleStyle()
                );

                isExportInfoAnnPresent = true;
            }
        }

        if (!isExportInfoAnnPresent) this.fillingBody(devicesList, sheet);

//        Create Data Rows
        for (DeviceDTO item : devicesList) {

            row = sheet.createRow(startRow++);

            for (Field field : fields) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(ExportInfo.class)) {
                    createCell(row,
                            field.getAnnotation(ExportInfo.class).position(),
                            field.get(item) != null ? field.get(item).toString() : "",
                            getBodyStringStyle());

                }
            }
        }
        footerStartRow = startRow + 1;

    }

    private void fillingBody(List<DeviceDTO> devicesList, XSSFSheet sheet) {
        fillingBody(devicesList, sheet, bodyStartRow);
    }

    private void fillingBody(List<DeviceDTO> devicesList, XSSFSheet sheet, int startRow) {
        int columnIdx = 0;
        Row row = sheet.createRow(startRow++);

        createCell(row, columnIdx++, "Залізниця", getBodyTitleStyle());
        createCell(row, columnIdx++, "Підрозділ", getBodyTitleStyle());
        createCell(row, columnIdx++, "Тип", getBodyTitleStyle());
        createCell(row, columnIdx++, "Номер", getBodyTitleStyle());
        createCell(row, columnIdx++, "Рік виготовлення", getBodyTitleStyle());
        createCell(row, columnIdx++, "Дата перевірки", getBodyTitleStyle());
        createCell(row, columnIdx++, "Дата наступної перевірки", getBodyTitleStyle());
        createCell(row, columnIdx++, "Об'єкт", getBodyTitleStyle());
        createCell(row, columnIdx++, "Статус", getBodyTitleStyle());
        createCell(row, columnIdx++, "Коментар", getBodyTitleStyle());
        createCell(row, columnIdx++, "Категорія розташування", getBodyTitleStyle());
        createCell(row, columnIdx++, "Розташування", getBodyTitleStyle());
        createCell(row, columnIdx++, "Категорія розташування 2", getBodyTitleStyle());
        createCell(row, columnIdx++, "Розташування 2", getBodyTitleStyle());
        createCell(row, columnIdx++, "Місце", getBodyTitleStyle());
        createCell(row, columnIdx++, "Найменування по схемі", getBodyTitleStyle());
        createCell(row, columnIdx, "Коментар до місця", getBodyTitleStyle());

        for (DeviceDTO item : devicesList) {
            columnIdx = 0;
            row = sheet.createRow(startRow++);

            createCell(row, columnIdx++, nullToEmpty(item.getRailwayName()), getBodyStringStyle());
            createCell(row, columnIdx++, nullToEmpty(item.getSubdivisionShortName()), getBodyStringStyle());
            createCell(row, columnIdx++, nullToEmpty(item.getTypeName()), getBodyStringStyle());
            createCell(row, columnIdx++, nullToEmpty(item.getNumber()), getBodyStringStyle());
            createCell(row, columnIdx++, nullToEmpty(item.getReleaseYear()), getBodyStringStyle());
            createCell(row, columnIdx++, nullToEmpty(item.getTestDate().toString()), getBodyDateStyle());
            createCell(row, columnIdx++, nullToEmpty(item.getNextTestDate().toString()), getBodyDateStyle());
            createCell(row, columnIdx++, nullToEmpty(item.getFacilityName()), getBodyStringStyle());
            createCell(row, columnIdx++, nullToEmpty(item.getStatusComment()), getBodyStringStyle());
            createCell(row, columnIdx++, nullToEmpty(item.getDetail()), getBodyStringStyle());
            createCell(row, columnIdx++, nullToEmpty(item.getRegionTypeComment()), getBodyStringStyle());
            createCell(row, columnIdx++, nullToEmpty(item.getRegion()), getBodyStringStyle());
            createCell(row, columnIdx++, nullToEmpty(item.getLocateTypeComment()), getBodyStringStyle());
            createCell(row, columnIdx++, nullToEmpty(item.getLocate()), getBodyStringStyle());
            createCell(row, columnIdx++, nullToEmpty(item.getPlaceNumber()), getBodyStringStyle());
            createCell(row, columnIdx++, nullToEmpty(item.getDescription()), getBodyStringStyle());
            createCell(row, columnIdx, nullToEmpty(item.getLocationDetail()), getBodyStringStyle());
        }
        footerStartRow = startRow + 1;
    }

    private void fillingFooter(XSSFSheet sheet) {
        fillingFooter(sheet, footerStartRow);
    }

    private void fillingFooter(XSSFSheet sheet, int startRow) {
        footerStartRow = startRow;

        createCell(sheet.createRow(footerStartRow++), 0, "какая-то хрень внизу - 1", getFooterStyle());
        createCell(sheet.createRow(footerStartRow++), 0, "какая-то хрень внизу - 2", getFooterStyle());
        createCell(sheet.createRow(footerStartRow++), 0, "какая-то хрень внизу - 3", getFooterStyle());
        createCell(sheet.createRow(footerStartRow++), 0, "какая-то хрень внизу - 4", getFooterStyle());
        createCell(sheet.createRow(footerStartRow++), 0, "какая-то хрень внизу - 5", getFooterStyle());

        headerStartRow = footerStartRow;
    }

    private CellStyle getHeaderStyle() {
        CellStyle style = workbook.createCellStyle();

        XSSFFont font = workbook.createFont();
        font.setBold(false);
        font.setFontHeight(12);
        style.setFont(font);

        return style;
    }

    private CellStyle getBodyTitleStyle() {
        CellStyle style = workbook.createCellStyle();

        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(12);
        style.setFont(font);
        style.setWrapText(true);
        style.setAlignment(CENTER);
        style.setVerticalAlignment(TOP);
        style.setBorderBottom(MEDIUM);
        style.setBorderLeft(MEDIUM);
        style.setBorderRight(MEDIUM);
        style.setBorderTop(MEDIUM);
        return style;
    }

    private CellStyle getBodyDateStyle() {
        CellStyle style = workbook.createCellStyle();

        XSSFFont font = workbook.createFont();
        font.setBold(false);
        font.setFontHeight(12);
        style.setFont(font);
        style.setDataFormat(workbook.createDataFormat().getFormat("yyyy-MM-dd"));
        style.setAlignment(LEFT);
        style.setBorderBottom(THIN);
        style.setBorderLeft(THIN);
        style.setBorderRight(THIN);
        style.setBorderTop(THIN);

        return style;
    }

    private CellStyle getBodyStringStyle() {
        CellStyle style = workbook.createCellStyle();

        XSSFFont font = workbook.createFont();
        font.setBold(false);
        font.setFontHeight(12);
        style.setFont(font);
        style.setAlignment(LEFT);
        style.setBorderBottom(THIN);
        style.setBorderLeft(THIN);
        style.setBorderRight(THIN);
        style.setBorderTop(THIN);

        return style;
    }

    private CellStyle getFooterStyle() {
        CellStyle style = workbook.createCellStyle();

        XSSFFont font = workbook.createFont();
        font.setBold(false);
        font.setFontHeight(12);
        style.setFont(font);

        return style;
    }

    private String nullToEmpty(String input) {
        return (input != null ? input : "");
    }

    private String getStringFilterInfo(DeviceDTO deviceDTO) throws IllegalAccessException {
        StringBuilder result = new StringBuilder();
        Field[] fields = deviceDTO.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.get(deviceDTO) != null) {
                result.append("(")
                        .append(field.getName())
                        .append("==")
                        .append(field.get(deviceDTO).toString())
                        .append("); ");
            }
        }

        return result.toString();
    }
}
