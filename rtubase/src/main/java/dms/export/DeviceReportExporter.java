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
import java.util.Date;
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
        sheet = workbook.createSheet("report");
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


        int k=37;
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(12);
        globalStyle.setFont(font);
        sheet.setColumnWidth(0, k*101);
        sheet.setColumnWidth(1, k*50);
        sheet.setColumnWidth(2, k*125);
        sheet.setColumnWidth(3, k*75);
        sheet.setColumnWidth(4, k*40);
        sheet.setColumnWidth(5, k*77);
        sheet.setColumnWidth(6, k*77);
        sheet.setColumnWidth(7, k*200);
        sheet.setColumnWidth(8, k*101);
        sheet.setColumnWidth(9, k*101);
        sheet.setColumnWidth(10, k*80);
        sheet.setColumnWidth(11, k*80);
        sheet.setColumnWidth(12, k*80);
        sheet.setColumnWidth(13, k*80);
        sheet.setColumnWidth(14, k*80);
        sheet.setColumnWidth(15, k*80);
        sheet.setColumnWidth(16, k*80);

    }

    private void createHeader(){
        Row row = sheet.createRow(0);
        createCell(row, 0, "какой-то заголовок", globalStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 16));
    }

    private CellStyle getBodyStyle(){
        CellStyle style = workbook.createCellStyle();
        return style;
    }
    private void createBody(){

        int rowIdx = 2;
        for (DeviceDTO item : dataList) {
            int columnIdx=0;
            Row row = sheet.createRow(rowIdx++);


            row.createCell(columnIdx++).setCellValue(item.getRailwayName());
            row.createCell(columnIdx++).setCellValue(item.getSubdivisionShortName());
            row.createCell(columnIdx++).setCellValue(item.getTypeName());
            row.createCell(columnIdx++).setCellValue(item.getNumber());
            row.createCell(columnIdx++).setCellValue(item.getReleaseYear());


            CellStyle dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(workbook.createDataFormat().getFormat("yyyy-MM-dd"));

            row.createCell(columnIdx).setCellValue(item.getTestDate());
            row.getCell(columnIdx++).setCellStyle(dateCellStyle);
            row.createCell(columnIdx).setCellValue(item.getNextTestDate());
            row.getCell(columnIdx++).setCellStyle(dateCellStyle);

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
