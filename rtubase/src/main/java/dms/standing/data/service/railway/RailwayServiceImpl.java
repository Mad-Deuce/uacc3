package dms.standing.data.service.railway;

import dms.RtubaseAuthService;
import dms.standing.data.entity.RailwayEntity;
import dms.standing.data.repository.RailwayRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class RailwayServiceImpl implements RailwayService {

    private final RtubaseAuthService rtubaseAuthService;

    private final RailwayRepository railwayRepository;

    @Autowired
    public RailwayServiceImpl(RtubaseAuthService rtubaseAuthService, RailwayRepository railwayRepository) {
        this.rtubaseAuthService = rtubaseAuthService;
        this.railwayRepository = railwayRepository;
    }

    @Override
    public List<RailwayEntity> getAll() {

        String principalPermitCode = rtubaseAuthService.getPrincipalPermitCode();
        List<RailwayEntity> result;

        if (principalPermitCode.equals("")){
            result = railwayRepository.findByIdStartsWith(principalPermitCode);
        } else {
            result = railwayRepository.findByIdStartsWith(principalPermitCode.substring(0, 1));
        }

        return result;
    }

    @Override
    public void importFromExcel(MultipartFile files) throws IOException {
        String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        String[] HEADERs = {"Id", "Name", "Code"};
        String SHEET = "rails";

        InputStream inputStream = files.getInputStream();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheet(SHEET);
        Iterator<Row> rows = sheet.iterator();
        int rowNumber = 0;

        List<RailwayEntity> rails = new ArrayList<RailwayEntity>();

        while (rows.hasNext()) {
            Row currentRow = rows.next();
            // skip header
            if (rowNumber == 0) {
                rowNumber++;
                continue;
            }
            RailwayEntity rail = new RailwayEntity();
            rail.setId(currentRow.getCell(0).getStringCellValue());
            rail.setName(currentRow.getCell(1).getStringCellValue());
            rail.setCode(currentRow.getCell(2).getStringCellValue());
            rails.add(rail);

        }
        railwayRepository.saveAll(rails);
    }
}
