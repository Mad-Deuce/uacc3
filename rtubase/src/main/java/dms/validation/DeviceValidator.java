package dms.validation;

import dms.entity.DeviceEntity;
import dms.standing.data.entity.DeviceTypeEntity;
import dms.standing.data.repository.DeviceTypeRepository;
import dms.standing.data.repository.RtdFacilityRepository;
import dms.validation.dto.ValidationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Component
public class DeviceValidator {

    @PersistenceContext
    EntityManager em;

    private final DeviceTypeRepository deviceTypeRepository;
    private final RtdFacilityRepository rtdFacilityRepository;

    @Autowired
    public DeviceValidator(DeviceTypeRepository deviceTypeRepository,
                           RtdFacilityRepository rtdFacilityRepository) {
        this.deviceTypeRepository = deviceTypeRepository;
        this.rtdFacilityRepository = rtdFacilityRepository;
    }

    public List<ValidationDTO> onCreateEntityValidation(DeviceEntity deviceEntity) {
        List<ValidationDTO> errors = new ArrayList<>();

        validateType(deviceEntity.getType(), errors);
        validateNumber(deviceEntity.getNumber(), errors);
        validateYear(deviceEntity.getReleaseYear(), errors);
        validateTestDate(deviceEntity, errors);
        validatePeriod(deviceEntity.getReplacementPeriod(), errors);
        validateFacility(deviceEntity, errors);
        validateDuplicateExistByQuery(deviceEntity, errors);

        return errors;
    }


    private void validateType(DeviceTypeEntity deviceTypeEntity, List<ValidationDTO> errors) {
        if (deviceTypeEntity == null) {
            errors.add(new ValidationDTO("typeId:type",
                    "Device Type Is Null"));
        } else {
            if (deviceTypeRepository.existsById(deviceTypeEntity.getId())) {
                System.out.println("type is correct");
            } else {
                errors.add(new ValidationDTO("typeId:type",
                        "Device Type Is Wrong: " +
                                " - type Id: " + deviceTypeEntity.getId()));
            }
        }


    }

    private void validateNumber(String number, List<ValidationDTO> errors) {
        if (number.matches("\\d+")) {
            System.out.println("number is correct");
        } else {
            errors.add(new ValidationDTO("number:number",
                    "Device Number Is Wrong (not digital):  " +
                            " - number: " + number));
        }
    }

    private void validateYear(String year, List<ValidationDTO> errors) {
        if (year.matches("\\d\\d\\d\\d")
                && Integer.parseInt(year) > 1950
                && Integer.parseInt(year) <= Calendar.getInstance().get(Calendar.YEAR)) {
            System.out.println("Year is correct");
        } else {
            errors.add(new ValidationDTO("releaseYear:releaseYear",
                    "releaseYear Is Wrong (must be from 1950 to current year): " +
                            " - releaseYear: " + year));
        }
    }

    private void validateFacility(DeviceEntity deviceEntity, List<ValidationDTO> errors) {
        if (rtdFacilityRepository.existsById(deviceEntity.getFacility().getId())) {
            System.out.println("facility is correct");
        } else {
            errors.add(new ValidationDTO("facilityId:facility",
                    "facility Is Wrong: " +
                            " - facility ID: " + deviceEntity.getFacility().getId()));
        }
    }

    private void validateTestDate(DeviceEntity deviceEntity, List<ValidationDTO> errors) {
        Date testDate = deviceEntity.getTestDate();
        Date nextTestDate = deviceEntity.getNextTestDate();
        if (testDate.compareTo(new Date(0L)) >= 0) {
            System.out.println("test date is correct");
        } else {
            errors.add(new ValidationDTO("testDate:testDate",
                            "testDate Is very Old: " +
                                    " - testDate: " + deviceEntity.getTestDate().toString() + "; " +
                                    " - etDate: " + new Date(0L)
                    )
            );
        }

        if (testDate.compareTo(new Date(System.currentTimeMillis())) <= 0) {
            System.out.println("test date is correct");
        } else {
            errors.add(new ValidationDTO("testDate:testDate",
                    "testDate Is Wrong: " +
                            " - testDate: " + deviceEntity.getTestDate().toString()));
        }

        if (testDate.before(nextTestDate)) {
            System.out.println("test date is correct");
        } else {
            errors.add(new ValidationDTO("testDate:testDate",
                    "testDate > nextTestDate: " +
                            " - testDate : " + deviceEntity.getTestDate().toString() + "; " +
                            " - nextTestDate : " + deviceEntity.getNextTestDate().toString()));
        }
    }

    private void validatePeriod(Integer period, List<ValidationDTO> errors) {
        if (period > 0 && period <= 900) {
            System.out.println("ReplacementPeriod is correct");
        } else {
            errors.add(new ValidationDTO("replacementPeriod:replacementPeriod",
                    "replacementPeriod Is Wrong (must be from 1 to 900): " +
                            " - replacementPeriod: " + period));
        }
    }

    private void validateDuplicateExistByQuery(DeviceEntity deviceEntity, List<ValidationDTO> errors) {
        Long size = (Long) em.createQuery(
                        "SELECT count (d) " +
                                " FROM DeviceEntity d " +
                                " WHERE 1=1 " +
                                " AND d.type =:typeParam" +
                                " AND d.number=:numberParam" +
                                " AND d.releaseYear=:rYearParam"
                )
                .setParameter("typeParam", deviceEntity.getType())
                .setParameter("numberParam", deviceEntity.getNumber())
                .setParameter("rYearParam", deviceEntity.getReleaseYear())
                .getSingleResult();

        if (size > 0) {

            errors.add(new ValidationDTO("id:id",
                    "Duplicate Is Found with parameter: " +
                            " - type: " + deviceEntity.getType().getName() + "; " +
                            " - number: " + deviceEntity.getNumber() + "; " +
                            " - release year: " + deviceEntity.getReleaseYear()));

        }
    }
}
