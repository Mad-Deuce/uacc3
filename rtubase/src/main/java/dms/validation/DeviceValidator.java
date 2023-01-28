package dms.validation;

import dms.entity.DeviceEntity;
import dms.exception.DeviceValidationException;
import dms.standing.data.entity.DeviceTypeEntity;
import dms.standing.data.repository.DeviceTypeRepository;
import dms.standing.data.repository.RtdFacilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Date;
import java.util.Calendar;

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

    public void onCreateEntityValidation(DeviceEntity deviceEntity) {

        DeviceValidationException exception = new DeviceValidationException();

        validateType(deviceEntity.getType(), exception);
        validateNumber(deviceEntity.getNumber(), exception);
        validateYear(deviceEntity.getReleaseYear(), exception);
        validateTestDate(deviceEntity, exception);
        validatePeriod(deviceEntity.getReplacementPeriod(), exception);
        validateFacility(deviceEntity, exception);
        validateDuplicateExistByQuery(deviceEntity, exception);

        if (!exception.getErrors().isEmpty()) {
            throw exception;
        }
    }

    private void validateType(DeviceTypeEntity deviceTypeEntity, DeviceValidationException exception) {
        if (deviceTypeEntity == null) {
            exception.addError("typeId:type",
                    "Device Type Is Null");
        } else {
            if (deviceTypeRepository.existsById(deviceTypeEntity.getId())) {
                System.out.println("type is correct");
            } else {
                exception.addError("typeId:type",
                        "Device Type Is Wrong: " +
                                " - type Id: " + deviceTypeEntity.getId());
            }
        }
    }

    private void validateNumber(String number, DeviceValidationException exception) {
        if (number.matches("\\d+")) {
            System.out.println("number is correct");
        } else {
            exception.addError("number:number",
                    "Device Number Is Wrong (not digital):  " +
                            " - number: " + number);
        }
    }

    private void validateYear(String year, DeviceValidationException exception) {
        if (year.matches("\\d\\d\\d\\d")
                && Integer.parseInt(year) > 1950
                && Integer.parseInt(year) <= Calendar.getInstance().get(Calendar.YEAR)) {
            System.out.println("Year is correct");
        } else {
            exception.addError("releaseYear:releaseYear",
                    "releaseYear Is Wrong (must be from 1950 to current year): " +
                            " - releaseYear: " + year);
        }
    }

    private void validateFacility(DeviceEntity deviceEntity, DeviceValidationException exception) {
        if (deviceEntity.getFacility() == null) {
            exception.addError("facilityId:facility",
                    "Facility Is Null");
        } else {
            if (rtdFacilityRepository.existsById(deviceEntity.getFacility().getId())) {
                System.out.println("facility is correct");
            } else {
                exception.addError("facilityId:facility",
                        "facility Is Wrong: " +
                                " - facility ID: " + deviceEntity.getFacility().getId());
            }
        }
    }

    private void validateTestDate(DeviceEntity deviceEntity, DeviceValidationException exception) {
        Date testDate = deviceEntity.getTestDate();
        Date nextTestDate = deviceEntity.getNextTestDate();

        if (testDate.toLocalDate().getYear() > Integer.parseInt(deviceEntity.getReleaseYear())) {
            System.out.println("test date is correct");
        } else {
            exception.addError("testDate:testDate",
                    "testDate(year) < ReleaseYear: " +
                            " - testDate(year): " + testDate.toLocalDate().getYear() + "; " +
                            " - ReleaseYear: " + deviceEntity.getReleaseYear());
        }

        if (testDate.compareTo(new Date(0L)) >= 0) {
            System.out.println("test date is correct");
        } else {
            exception.addError("testDate:testDate",
                    "testDate Is very Old: " +
                            " - testDate: " + deviceEntity.getTestDate().toString() + "; " +
                            " - etDate: " + new Date(0L));
        }

        if (testDate.compareTo(new Date(System.currentTimeMillis())) <= 0) {
            System.out.println("test date is correct");
        } else {
            exception.addError("testDate:testDate",
                    "testDate Is Wrong: " +
                            " - testDate: " + deviceEntity.getTestDate().toString());
        }

        if (testDate.before(nextTestDate)) {
            System.out.println("test date is correct");
        } else {
            exception.addError("testDate:testDate",
                    "testDate > nextTestDate: " +
                            " - testDate : " + deviceEntity.getTestDate().toString() + "; " +
                            " - nextTestDate : " + deviceEntity.getNextTestDate().toString());
        }
    }

    private void validatePeriod(Integer period, DeviceValidationException exception) {
        if (period > 0 && period <= 900) {
            System.out.println("ReplacementPeriod is correct");
        } else {
            exception.addError("replacementPeriod:replacementPeriod",
                    "replacementPeriod Is Wrong (must be from 1 to 900): " +
                            " - replacementPeriod: " + period);
        }
    }

    private void validateDuplicateExistByQuery(DeviceEntity deviceEntity, DeviceValidationException exception) {
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
            exception.addError("id:id",
                    "Duplicate Is Found with parameter: " +
                            " - type: " + deviceEntity.getType().getName() + "; " +
                            " - number: " + deviceEntity.getNumber() + "; " +
                            " - release year: " + deviceEntity.getReleaseYear());
        }
    }
}
