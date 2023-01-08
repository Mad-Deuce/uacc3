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

    private List<DeviceDTO> dataList;
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
        font.setFontHeight(16);
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
            Row row = sheet.createRow(rowIdx++);

            row.createCell(0).setCellValue(item.getId());
            row.createCell(1).setCellValue(item.getTypeName());
            row.createCell(2).setCellValue(item.getNumber());
            row.createCell(3).setCellValue(item.getReleaseYear());
            row.createCell(4).setCellValue(item.getTestDate());
            row.createCell(5).setCellValue(item.getNextTestDate());
            row.createCell(6).setCellValue(item.getStatusComment());
        }
    }

    private void createFooter(){

    }
}
