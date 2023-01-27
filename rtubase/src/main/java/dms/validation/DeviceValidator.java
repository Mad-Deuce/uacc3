package dms.validation;

import dms.entity.DeviceEntity;
import dms.exception.WrongDataException;
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
        validateType(deviceEntity.getType());
        validateNumber(deviceEntity.getNumber());
        validateYear(deviceEntity.getReleaseYear());
        validateTestDate(deviceEntity.getTestDate());
        validateFacility(deviceEntity);
        validateDuplicateExistByQuery(deviceEntity);
    }


    private void validateType(DeviceTypeEntity deviceTypeEntity) {
        if (deviceTypeEntity == null) throw new WrongDataException("Device Type Is Null");
        if (deviceTypeRepository.existsById(deviceTypeEntity.getId())) {
            System.out.println("type is correct");
        } else {
            throw new WrongDataException("Device Type Is Wrong: " +
                    " - type Id: " + deviceTypeEntity.getId())
                    ;
        }
    }

    private void validateNumber(String number) {
        if (number.matches("[0-9]+")) {
            System.out.println("number is correct");
        } else {
            throw new WrongDataException("Device Number Is Wrong (not digital):  " +
                    " - number: " + number);
        }
    }

    private void validateYear(String year) {
        if (year.matches("\\d\\d\\d\\d")
                && Integer.parseInt(year) > 1950
                && Integer.parseInt(year) <= Calendar.getInstance().get(Calendar.YEAR)) {
            System.out.println("Year is correct");
        } else {
            throw new WrongDataException("Device Release Year Is Wrong: " +
                    " - number: " + year);
        }
    }

    private void validateFacility(DeviceEntity deviceEntity) {
        if (rtdFacilityRepository.existsById(deviceEntity.getFacility().getId())) {
            System.out.println("facility is correct");
        } else {
            throw new WrongDataException("Facility Is Wrong: " +
                    " - facility Id: " + deviceEntity.getFacility().getId());
        }
    }

    private void validateTestDate(Date testDate) {

    }

    private void validateDuplicateExistByQuery(DeviceEntity deviceEntity) {
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
            throw new WrongDataException("Duplicate Is Found with parameter: " +
                    " - type: " + deviceEntity.getType().getName() +
                    " - number: " + deviceEntity.getNumber() +
                    " - release year: " + deviceEntity.getReleaseYear())
                    ;
        }
    }
}
