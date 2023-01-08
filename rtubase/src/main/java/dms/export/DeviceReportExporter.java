package dms.export;

import dms.dto.DeviceDTO;
import dms.mapper.ExplicitDeviceMatcher;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static org.apache.poi.ss.util.CellUtil.createCell;

public class DeviceReportExporter {

    private final List<DeviceDTO> dataList;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private final CellStyle globalStyle;

    public DeviceReportExporter(List<DeviceDTO> dataList){
        this.dataList = dataList;
        this.workbook = new XSSFWorkbook();
        this.globalStyle = workbook.createCellStyle();
    }

    public void exportToXlsx(HttpServletResponse response) throws IOException {
        this.setGlobalStyleDefault();
        this.createHeader();
        this.createBody();
        this.createFooter();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    private void setGlobalStyleDefault(){
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(12);
        globalStyle.setFont(font);
    }

    private void createHeader(){
        sheet = workbook.createSheet("report");
        Row row = sheet.createRow(0);
        createCell(row, 0, "какой-то заголовок", globalStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
    }

    private void createBody(){
        int rowIdx = 2;
        for (DeviceDTO item : dataList) {
            int columnIdx=0;
            Row row = sheet.createRow(rowIdx++);

            row.createCell(columnIdx++).setCellValue(item.getId());
            row.createCell(columnIdx++).setCellValue(item.getTypeName());
            row.createCell(columnIdx++).setCellValue(item.getNumber());
            row.createCell(columnIdx++).setCellValue(item.getReleaseYear());
            row.createCell(columnIdx++).setCellValue(item.getTestDate());
            row.createCell(columnIdx++).setCellValue(item.getNextTestDate());
            row.createCell(columnIdx++).setCellValue(item.getFacilityName());
            row.createCell(columnIdx++).setCellValue(item.getStatusComment());
            row.createCell(columnIdx++).setCellValue(item.getDetail());
            row.createCell(columnIdx++).setCellValue(item.getRegionTypeComment());
            row.createCell(columnIdx++).setCellValue(item.getRegion());
            row.createCell(columnIdx++).setCellValue(item.getLocateTypeComment());
            row.createCell(columnIdx++).setCellValue(item.getLocate());
            row.createCell(columnIdx++).setCellValue(item.getPlaceNumber());
            row.createCell(columnIdx++).setCellValue(item.getDescription());
            row.createCell(columnIdx++).setCellValue(item.getLocationDetail());
        }
    }

    private void createFooter(){

    }
}
