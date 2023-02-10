package dms.validation;

import dms.entity.DeviceEntity;
import dms.exception.DeviceValidationException;
import dms.repository.LocationRepository;
import dms.standing.data.dock.val.Status;
import dms.standing.data.repository.DeviceTypeRepository;
import dms.standing.data.repository.LineFacilityRepository;
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
    private final LineFacilityRepository lineFacilityRepository;
    private final LocationRepository locationRepository;

    @Autowired
    public DeviceValidator(DeviceTypeRepository deviceTypeRepository,
                           RtdFacilityRepository rtdFacilityRepository,
                           LineFacilityRepository lineFacilityRepository,
                           LocationRepository locationRepository) {
        this.deviceTypeRepository = deviceTypeRepository;
        this.rtdFacilityRepository = rtdFacilityRepository;
        this.lineFacilityRepository = lineFacilityRepository;
        this.locationRepository = locationRepository;
    }

    public void onCreateEntityValidation(DeviceEntity deviceEntity) {

        DeviceValidationException exception = new DeviceValidationException();

        isTypeExist(deviceEntity, exception);
        isNumberCorrect(deviceEntity, exception);
        isYearCorrect(deviceEntity, exception);
        isTestDateCorrect(deviceEntity, exception);
        isReplacementPeriodCorrect(deviceEntity, exception);
        isRtdFacilityExist(deviceEntity, exception);

        isDuplicateExists(deviceEntity, exception);

        if (!exception.getErrors().isEmpty()) {
            throw exception;
        }
    }

    public void onReplaceEntityValidation(DeviceEntity oldDeviceEntity, DeviceEntity newDeviceEntity) {
        DeviceValidationException exception = new DeviceValidationException();

        isTypeExist(newDeviceEntity, exception);
        isTypesMatch(oldDeviceEntity, newDeviceEntity, exception);

        isNumberCorrect(newDeviceEntity, exception);

        isYearCorrect(newDeviceEntity, exception);

        isRtdFacilityExist(newDeviceEntity, exception);
        isLineFacilityExist(oldDeviceEntity, exception);
        isFacilityMatch(oldDeviceEntity, newDeviceEntity, exception);

        isReplacementPeriodCorrect(newDeviceEntity, exception);
        isTestDateCorrect(newDeviceEntity, exception);
        isNextTestDateMatchTestDay(newDeviceEntity, exception);
        isNextTestDateValid(newDeviceEntity, exception);

        isStatus31(newDeviceEntity, exception);
        isStatus1121(oldDeviceEntity, exception);

        isLocationExist(oldDeviceEntity, exception);
        isLocationMatch(oldDeviceEntity, exception);

        if (!exception.getErrors().isEmpty()) {
            throw exception;
        }
    }

    private void isTypeExist(DeviceEntity deviceEntity, DeviceValidationException exception) {
        if (deviceEntity.getType() == null) {
            exception.addError("typeId:type",
                    "Device Type Is Null");
        } else {
            if (deviceTypeRepository.existsById(deviceEntity.getType().getId())) {
                System.out.println("type is correct");
            } else {
                exception.addError("typeId:type",
                        "Device Type Is Wrong: " +
                                " - type Id: " + deviceEntity.getType().getId());
            }
        }
    }


    private void isTypesMatch(DeviceEntity oldDeviceEntity, DeviceEntity newDeviceEntity,
                              DeviceValidationException exception) {
        if (newDeviceEntity.getType() == null) {
            exception.addError("typeId:type",
                    "Device Type Is Null");
        } else {
            if (newDeviceEntity.getType().equals(oldDeviceEntity.getType())) {
//                todo: check types match
                System.out.println("type is correct");
            } else {
                exception.addError("typeId:type",
                        "Device Type Is Wrong: " +
                                " - type Id: " + newDeviceEntity.getType().getId());
            }
        }
    }

    private void isNumberCorrect(DeviceEntity deviceEntity, DeviceValidationException exception) {
        if (deviceEntity.getNumber().matches("\\d+")) {
            System.out.println("number is correct");
        } else {
            exception.addError("number:number",
                    "Device Number Is Wrong (not digital):  " +
                            " - number: " + deviceEntity.getNumber());
        }
    }

    private void isYearCorrect(DeviceEntity deviceEntity, DeviceValidationException exception) {
        if (deviceEntity.getReleaseYear().matches("\\d\\d\\d\\d")
                && Integer.parseInt(deviceEntity.getReleaseYear()) > 1950
                && Integer.parseInt(deviceEntity.getReleaseYear()) <= Calendar.getInstance().get(Calendar.YEAR)) {
            System.out.println("Year is correct");
        } else {
            exception.addError("releaseYear:releaseYear",
                    "releaseYear Is Wrong (must be from 1950 to current year): " +
                            " - releaseYear: " + deviceEntity.getReleaseYear());
        }
    }

    private void isRtdFacilityExist(DeviceEntity deviceEntity, DeviceValidationException exception) {
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

    private void isLineFacilityExist(DeviceEntity deviceEntity, DeviceValidationException exception) {
        if (deviceEntity.getFacility() == null) {
            exception.addError("facilityId:facility",
                    "Facility Is Null");
        } else {
            if (lineFacilityRepository.existsById(deviceEntity.getFacility().getId())) {
                System.out.println("facility is correct");
            } else {
                exception.addError("facilityId:facility",
                        "facility Is Wrong: " +
                                " - facility ID: " + deviceEntity.getFacility().getId());
            }
        }
    }

    private void isFacilityMatch(DeviceEntity oldDeviceEntity, DeviceEntity newDeviceEntity,
                                 DeviceValidationException exception) {
        if (newDeviceEntity.getFacility() == null) {
            exception.addError("facilityId:facility",
                    "Facility Is Null");
        } else {
            if (newDeviceEntity.getFacility().getSubdivision().equals(oldDeviceEntity.getFacility().getSubdivision())) {
                System.out.println("facility is correct");
            } else {
                exception.addError("facilityId:facility",
                        "Facilities Not Match (new Device must be from RTD which servicing Line Object): " +
                                " -new Device facility ID: " + newDeviceEntity.getFacility().getId() +
                                " -old Device facility ID: " + oldDeviceEntity.getFacility().getId()
                );
            }
        }
    }

    private void isTestDateCorrect(DeviceEntity deviceEntity, DeviceValidationException exception) {
        Date testDate = deviceEntity.getTestDate();

        if (testDate.toLocalDate().getYear() >= Integer.parseInt(deviceEntity.getReleaseYear())) {
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
                    "testDate Is In Future: " +
                            " - testDate: " + deviceEntity.getTestDate().toString());
        }

    }

    private void isNextTestDateMatchTestDay(DeviceEntity deviceEntity, DeviceValidationException exception) {
        Date testDate = deviceEntity.getTestDate();
        Date nextTestDate = deviceEntity.getNextTestDate();
        Date expectedNextTestDate = Date.valueOf(testDate.toLocalDate().plusMonths(deviceEntity.getReplacementPeriod()));

        if (testDate.before(nextTestDate)) {
            System.out.println("test date is correct");
        } else {
            exception.addError("testDate:testDate",
                    "testDate > nextTestDate: " +
                            " - testDate : " + deviceEntity.getTestDate().toString() + "; " +
                            " - nextTestDate : " + deviceEntity.getNextTestDate().toString());
        }

        if (nextTestDate.equals(expectedNextTestDate)) {
            System.out.println("Next test date is correct");
        } else {
            exception.addError("nextTestDate:nextTestDate",
                    "testDate + getReplacementPeriod != nextTestDate: " +
                            " - testDate : " + deviceEntity.getTestDate().toString() + "; " +
                            " - getReplacementPeriod : " + deviceEntity.getReplacementPeriod() + "; " +
                            " - nextTestDate : " + deviceEntity.getNextTestDate().toString());
        }
    }

    private void isNextTestDateValid(DeviceEntity deviceEntity, DeviceValidationException exception) {
        Date nowDate = new Date(System.currentTimeMillis());
        Date nextTestDate = deviceEntity.getNextTestDate();
        Date expectedNextTestDate = Date.valueOf(nowDate.toLocalDate().plusMonths(1));


        if (nextTestDate.compareTo(expectedNextTestDate) > 0) {
            System.out.println("test date is correct");
        } else {
            exception.addError("testDate:testDate",
                    "nextTestDate terms Ends within a month: " +
                            " - nextTestDate: " + deviceEntity.getTestDate().toString());
        }

    }

    private void isReplacementPeriodCorrect(DeviceEntity deviceEntity, DeviceValidationException exception) {
        Integer period = deviceEntity.getReplacementPeriod();
        if (period > 0 && period <= 900) {
            System.out.println("ReplacementPeriod is correct");
        } else {
            exception.addError("replacementPeriod:replacementPeriod",
                    "replacementPeriod Is Wrong (must be from 1 to 900): " +
                            " - replacementPeriod: " + period);
        }
    }

    private void isStatus31(DeviceEntity deviceEntity, DeviceValidationException exception) {
        if (deviceEntity.getStatus().equals(Status.PS31)) {
            System.out.println("Status is 31 - OBF");
        } else {
            exception.addError("status:status",
                    "Status Is Wrong (must be 31): " +
                            " - Status: " + deviceEntity.getStatus().getName() +
                            "--" + deviceEntity.getStatus().getComment());
        }
    }

    private void isStatus1121(DeviceEntity deviceEntity, DeviceValidationException exception) {
        if (deviceEntity.getStatus().equals(Status.PS11) || deviceEntity.getStatus().equals(Status.PS21)) {
            System.out.println("Status is: " + deviceEntity.getStatus().getName() +
                    "--" + deviceEntity.getStatus().getComment());
        } else {
            exception.addError("status:status",
                    "Status Is Wrong (must be 11 or 21): " +
                            " - Status: " + deviceEntity.getStatus().getName() +
                            "--" + deviceEntity.getStatus().getComment());
        }
    }

    private void isLocationExist(DeviceEntity deviceEntity, DeviceValidationException exception) {
        if (deviceEntity.getLocation() == null) {
            exception.addError("locationId:location",
                    "Location Is Null");
        } else {
            if (locationRepository.existsById(deviceEntity.getLocation().getId())) {
                System.out.println("Location is correct");
            } else {
                exception.addError("locationId:location",
                        "Location Is Wrong: " +
                                " - facility ID: " + deviceEntity.getLocation().getId());
            }
        }
    }

    private void isLocationMatch(DeviceEntity deviceEntity, DeviceValidationException exception) {
        if (deviceEntity.getLocation() == null) {
            exception.addError("locationId:location",
                    "Location Is Null");
        } else {
            if (deviceEntity.getLocation().getFacility().equals(deviceEntity.getFacility())) {
                System.out.println("Location is correct");
            } else {
                exception.addError("locationId:location",
                        "Location Is not match to line object: " +
                                " - Location line object ID: " + deviceEntity.getLocation().getFacility().getId() +
                                " - Entity object ID: " + deviceEntity.getFacility().getId()
                );
            }
        }
    }

    private void isDuplicateExists(DeviceEntity deviceEntity, DeviceValidationException exception) {
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
