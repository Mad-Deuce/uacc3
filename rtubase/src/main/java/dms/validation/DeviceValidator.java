package dms.validation;

import dms.entity.DeviceEntity;
import dms.entity.LocationEntity;
import dms.exception.DeviceValidationException;
import dms.repository.DeviceRepository;
import dms.repository.LocationRepository;
import dms.standing.data.dock.val.ReplacementType;
import dms.standing.data.dock.val.Status;
import dms.standing.data.entity.DeviceTypeEntity;
import dms.standing.data.entity.FacilityEntity;
import dms.standing.data.repository.DeviceTypeRepository;
import dms.standing.data.repository.LineFacilityRepository;
import dms.standing.data.repository.RtdFacilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;

@Component
public class DeviceValidator {

    @PersistenceContext
    EntityManager em;

    private final DeviceRepository deviceRepository;
    private final DeviceTypeRepository deviceTypeRepository;
    private final RtdFacilityRepository rtdFacilityRepository;
    private final LineFacilityRepository lineFacilityRepository;
    private final LocationRepository locationRepository;

    @Autowired
    public DeviceValidator(DeviceRepository deviceRepository,
                           DeviceTypeRepository deviceTypeRepository,
                           RtdFacilityRepository rtdFacilityRepository,
                           LineFacilityRepository lineFacilityRepository,
                           LocationRepository locationRepository) {
        this.deviceRepository = deviceRepository;
        this.deviceTypeRepository = deviceTypeRepository;
        this.rtdFacilityRepository = rtdFacilityRepository;
        this.lineFacilityRepository = lineFacilityRepository;
        this.locationRepository = locationRepository;
    }


    /**
     * @param deviceEntity тип прибора есть в справочнике;
     *                     номер прибора состоит только из цифр;
     *                     год выпуска прибора - 4-х значное число от 1950 до текущего года;
     *                     дата проверки - год проверки больше или равен году выпуска;
     *                     - не слишком старая т.е. больше 01.01.1970;
     *                     - не в будущем т.е. меньше или равна текущей дате;
     *                     периодичность замены - число от 1 до 900;
     *                     РТД к которому приписывается прибор есть в справочнике;
     *                     в базе нет дубликата - прибора с такими же: тип, номер, год выпуска;
     */
    public void onCreateValidation(DeviceEntity deviceEntity) {

        DeviceValidationException exception = new DeviceValidationException();

        isTypeExist(deviceEntity.getType(), exception);
        isNumberCorrect(deviceEntity, exception);
        isYearCorrect(deviceEntity, exception);
        isTestDateCorrect(deviceEntity, exception);
        isReplacementPeriodCorrect(deviceEntity, exception);
        isRtdFacilityExist(deviceEntity.getFacility(), exception);

        isDuplicateExists(deviceEntity, exception);

        if (!exception.getErrors().isEmpty()) {
            throw exception;
        }
    }

    /**
     * @param oldDeviceEntity тип старого прибора совместим с типом нового;
     *                        Линейный Объект, к которому приписан старый прибор есть в справочнике;
     *                        РТД, к которому приписан новый прибор и Объект, к которому приписан старый прибор
     *                        приписаны к одному ШЧ - совместимы;
     *                        статус старого прибора - 21 (АВЗ станций)
     * @param newDeviceEntity тип нового прибора есть в справочнике;
     *                        номер нового прибора состоит только из цифр;
     *                        год выпуска нового прибора - 4-х значное число от 1950 до текущего года;
     *                        РТД, к которому приписан новый прибор есть в справочнике;
     *                        РТД, к которому приписан новый прибор и Объект, к которому приписан старый прибор
     *                        приписаны к одному ШЧ - совместимы;
     *                        периодичность замены нового прибора - число от 1 до 900;
     *                        дата проверки нового прибора - год проверки больше или равен году выпуска;
     *                        - не слишком старая т.е. больше 01.01.1970;
     *                        - не в будущем т.е. меньше или равна текущей дате;
     *                        дата следующей проверки нового прибора - больше даты проверки нового прибора;
     *                        - равна дата проверки + периодичность;
     *                        дата следующей проверки нового прибора как минимум на 1 месяц больше текущей даты;
     *                        статус нового прибора - 31 (ОБФ);
     * @param replacementType тип замены: по сроку или отказ прибора
     */
    public void onReplaceToAvzLineValidation(DeviceEntity oldDeviceEntity,
                                             DeviceEntity newDeviceEntity,
                                             ReplacementType replacementType) {
        DeviceValidationException exception = new DeviceValidationException();

        isTypeExist(newDeviceEntity.getType(), exception);
        isNumberCorrect(newDeviceEntity, exception);
        isYearCorrect(newDeviceEntity, exception);
        isReplacementPeriodCorrect(newDeviceEntity, exception);
        isTestDateCorrect(newDeviceEntity, exception);
        isNextTestDateMatchTestDay(newDeviceEntity, exception);
        isNextTestDateValid(newDeviceEntity.getNextTestDate(), exception);


        isRtdFacilityExist(newDeviceEntity.getFacility(), exception);
        isFacilitiesCompatible(oldDeviceEntity.getFacility(), newDeviceEntity.getFacility(), exception);


        isStatus31(newDeviceEntity, exception);

        isTypesCompatible(oldDeviceEntity.getType(), newDeviceEntity.getType(), exception);
        isLineFacilityExist(oldDeviceEntity.getFacility(), exception);

        isStatus21(oldDeviceEntity, exception);

        isReplacementTypeOtkZam(replacementType, exception);

        if (!exception.getErrors().isEmpty()) {
            throw exception;
        }
    }

    /**
     * @param oldDeviceEntity тип старого прибора совместим с типом нового;
     *                        Объект РТД, к которому приписан старый прибор есть в справочнике;
     *                        РТД, к которому приписан новый прибор и Объект РТД, к которому приписан старый прибор
     *                        приписаны к одному ШЧ - совместимы;
     *                        статус старого прибора - 32 (АВЗ РТД)
     * @param newDeviceEntity тип нового прибора есть в справочнике;
     *                        номер нового прибора состоит только из цифр;
     *                        год выпуска нового прибора - 4-х значное число от 1950 до текущего года;
     *                        РТД, к которому приписан новый прибор есть в справочнике;
     *                        РТД, к которому приписан новый прибор и Объект РТД, к которому приписан старый прибор
     *                        приписаны к одному ШЧ - совместимы;
     *                        периодичность замены нового прибора - число от 1 до 900;
     *                        дата проверки нового прибора - год проверки больше или равен году выпуска;
     *                        - не слишком старая т.е. больше 01.01.1970;
     *                        - не в будущем т.е. меньше или равна текущей дате;
     *                        дата следующей проверки нового прибора - больше даты проверки нового прибора;
     *                        - равна дата проверки + периодичность;
     *                        дата следующей проверки нового прибора как минимум на 1 месяц больше текущей даты;
     *                        статус нового прибора - 31 (ОБФ);
     * @param replacementType тип замены: по сроку или отказ прибора
     */
    public void onReplaceToAvzRtdValidation(DeviceEntity oldDeviceEntity,
                                            DeviceEntity newDeviceEntity,
                                            ReplacementType replacementType) {
        DeviceValidationException exception = new DeviceValidationException();

        isTypeExist(newDeviceEntity.getType(), exception);
        isNumberCorrect(newDeviceEntity, exception);
        isYearCorrect(newDeviceEntity, exception);
        isReplacementPeriodCorrect(newDeviceEntity, exception);
        isTestDateCorrect(newDeviceEntity, exception);
        isNextTestDateMatchTestDay(newDeviceEntity, exception);
        isNextTestDateValid(newDeviceEntity.getNextTestDate(), exception);

        isRtdFacilityExist(newDeviceEntity.getFacility(), exception);
        isRtdFacilityExist(oldDeviceEntity.getFacility(), exception);
        isFacilitiesCompatible(oldDeviceEntity.getFacility(), newDeviceEntity.getFacility(), exception);


        isStatus31(newDeviceEntity, exception);

        isTypesCompatible(oldDeviceEntity.getType(), newDeviceEntity.getType(), exception);

        isStatus32(oldDeviceEntity, exception);

        isReplacementTypeOtkZam(replacementType, exception);

        if (!exception.getErrors().isEmpty()) {
            throw exception;
        }
    }

    /**
     * @param oldDeviceEntity тип старого прибора совместим с типом нового;
     *                        Линейный Объект, к которому приписан старый прибор есть в справочнике;
     *                        РТД, к которому приписан новый прибор и Объект, к которому приписан старый прибор
     *                        приписаны к одному ШЧ - совместимы;
     *                        статус старого прибора - 11 (Линия)
     *                        расположение старого прибора существует в базе;
     *                        Объект, к которому приписан старый прибор, соответствует Объекту,
     *                        к которому приписано расположение;
     * @param newDeviceEntity тип нового прибора есть в справочнике;
     *                        номер нового прибора состоит только из цифр;
     *                        год выпуска нового прибора - 4-х значное число от 1950 до текущего года;
     *                        РТД, к которому приписан новый прибор есть в справочнике;
     *                        РТД, к которому приписан новый прибор и Объект, к которому приписан старый прибор
     *                        приписаны к одному ШЧ - совместимы;
     *                        периодичность замены нового прибора - число от 1 до 900;
     *                        дата проверки нового прибора - год проверки больше или равен году выпуска;
     *                        - не слишком старая т.е. больше 01.01.1970;
     *                        - не в будущем т.е. меньше или равна текущей дате;
     *                        дата следующей проверки нового прибора - больше даты проверки нового прибора;
     *                        - равна дата проверки + периодичность;
     *                        дата следующей проверки нового прибора как минимум на 1 месяц больше текущей даты;
     *                        статус нового прибора - 31 (ОБФ);
     * @param replacementType тип замены: по сроку или отказ прибора
     */
    public void onReplaceToLineValidation(DeviceEntity oldDeviceEntity,
                                          DeviceEntity newDeviceEntity,
                                          ReplacementType replacementType) {
        DeviceValidationException exception = new DeviceValidationException();

        isTypeExist(newDeviceEntity.getType(), exception);
        isNumberCorrect(newDeviceEntity, exception);
        isYearCorrect(newDeviceEntity, exception);
        isReplacementPeriodCorrect(newDeviceEntity, exception);
        isTestDateCorrect(newDeviceEntity, exception);
        isNextTestDateMatchTestDay(newDeviceEntity, exception);
        isNextTestDateValid(newDeviceEntity.getNextTestDate(), exception);

        isRtdFacilityExist(newDeviceEntity.getFacility(), exception);
        isFacilitiesCompatible(oldDeviceEntity.getFacility(), newDeviceEntity.getFacility(), exception);

        isStatus31(newDeviceEntity, exception);

        isTypesCompatible(oldDeviceEntity.getType(), newDeviceEntity.getType(), exception);
        isLineFacilityExist(oldDeviceEntity.getFacility(), exception);
        isStatus11(oldDeviceEntity, exception);
        isLocationExist(oldDeviceEntity.getLocation(), exception);
        isLocationMatch(oldDeviceEntity, exception);

        isReplacementTypeOtkZam(replacementType, exception);

        if (!exception.getErrors().isEmpty()) {
            throw exception;
        }
    }


    /**
     * @param deviceEntity   тип прибора есть в справочнике;
     *                       номер прибора состоит только из цифр;
     *                       год выпуска прибора - 4-х значное число от 1950 до текущего года;
     *                       РТД, к которому приписан прибор есть в справочнике;
     *                       периодичность замены прибора - число от 1 до 900;
     *                       дата проверки прибора - год проверки больше или равен году выпуска;
     *                       - не слишком старая т.е. больше 01.01.1970;
     *                       - не в будущем т.е. меньше или равна текущей дате;
     *                       дата следующей проверки прибора - больше даты проверки  прибора;
     *                       - равна дата проверки + периодичность;
     *                       дата следующей проверки нового прибора как минимум на 1 месяц больше текущей даты;
     *                       статус нового прибора - 31 (ОБФ);
     * @param locationEntity расположение на котором будет новый прибор существует в базе;
     *                       расположение на котором будет новый прибор не занято;
     * @param facilityEntity Объект (Линейный), к которому будет приписан прибор есть в справочнике;
     *                       РТД, к которому приписан прибор и Объект, к которому будет приписан прибор
     *                       приписаны к одному ШЧ - совместимы;
     *                       !!!!!!!!!!!!!!!   ЧТО ЕЩЕ необходимо проверить?????????
     */
    public void onSetDeviceToLineValidation(DeviceEntity deviceEntity,
                                            FacilityEntity facilityEntity,
                                            LocationEntity locationEntity) {

        DeviceValidationException exception = new DeviceValidationException();

        isTypeExist(deviceEntity.getType(), exception);
        isNumberCorrect(deviceEntity, exception);
        isYearCorrect(deviceEntity, exception);
        isRtdFacilityExist(deviceEntity.getFacility(), exception);

        isReplacementPeriodCorrect(deviceEntity, exception);
        isTestDateCorrect(deviceEntity, exception);
        isNextTestDateMatchTestDay(deviceEntity, exception);
        isNextTestDateValid(deviceEntity.getNextTestDate(), exception);

        isStatus31(deviceEntity, exception);

        isLocationExist(locationEntity, exception);
        isLocationEmpty(locationEntity, exception);

        isLineFacilityExist(facilityEntity, exception);
        isFacilitiesCompatible(facilityEntity, deviceEntity.getFacility(), exception);

        if (!exception.getErrors().isEmpty()) {
            throw exception;
        }
    }

    /**
     * @param deviceEntity   тип прибора есть в справочнике;
     *                       номер прибора состоит только из цифр;
     *                       год выпуска прибора - 4-х значное число от 1950 до текущего года;
     *                       РТД, к которому приписан прибор есть в справочнике;
     *                       периодичность замены прибора - число от 1 до 900;
     *                       дата проверки прибора - год проверки больше или равен году выпуска;
     *                       - не слишком старая т.е. больше 01.01.1970;
     *                       - не в будущем т.е. меньше или равна текущей дате;
     *                       дата следующей проверки прибора - больше даты проверки  прибора;
     *                       - равна дата проверки + периодичность;
     *                       дата следующей проверки нового прибора как минимум на 1 месяц больше текущей даты;
     *                       статус прибора - 31 (ОБФ);
     * @param facilityEntity Объект (АВЗ РТД), к которому будет приписан прибор есть в справочнике;
     *                       РТД, к которому приписан прибор и Объект, к которому будет приписан прибор
     *                       приписаны к одному ШЧ - совместимы;
     *                       !!!!!!!!!!!!!!!   ЧТО ЕЩЕ необходимо проверить?????????
     */
    public void onSetDeviceToAvzRtdValidation(DeviceEntity deviceEntity,
                                              FacilityEntity facilityEntity) {
        DeviceValidationException exception = new DeviceValidationException();

        isTypeExist(deviceEntity.getType(), exception);
        isNumberCorrect(deviceEntity, exception);
        isRtdFacilityExist(deviceEntity.getFacility(), exception);

        isReplacementPeriodCorrect(deviceEntity, exception);
        isTestDateCorrect(deviceEntity, exception);
        isNextTestDateMatchTestDay(deviceEntity, exception);
        isNextTestDateValid(deviceEntity.getNextTestDate(), exception);

        isStatus31(deviceEntity, exception);

        isRtdFacilityExist(facilityEntity, exception);
        isFacilitiesCompatible(facilityEntity, deviceEntity.getFacility(), exception);

        if (!exception.getErrors().isEmpty()) {
            throw exception;
        }
    }

    /**
     * @param deviceEntity   тип прибора есть в справочнике;
     *                       номер прибора состоит только из цифр;
     *                       год выпуска прибора - 4-х значное число от 1950 до текущего года;
     *                       РТД, к которому приписан прибор есть в справочнике;
     *                       периодичность замены прибора - число от 1 до 900;
     *                       дата проверки прибора - год проверки больше или равен году выпуска;
     *                       - не слишком старая т.е. больше 01.01.1970;
     *                       - не в будущем т.е. меньше или равна текущей дате;
     *                       дата следующей проверки прибора - больше даты проверки  прибора;
     *                       - равна дата проверки + периодичность;
     *                       дата следующей проверки нового прибора как минимум на 1 месяц больше текущей даты;
     *                       статус прибора - 31 (ОБФ);
     * @param facilityEntity Объект (Линия), к которому будет приписан прибор есть в справочнике;
     *                       РТД, к которому приписан прибор и Объект, к которому будет приписан прибор
     *                       приписаны к одному ШЧ - совместимы;
     *                       !!!!!!!!!!!!!!!   ЧТО ЕЩЕ необходимо проверить?????????
     */
    public void onSetDeviceToAvzLineValidation(DeviceEntity deviceEntity,
                                               FacilityEntity facilityEntity) {
        DeviceValidationException exception = new DeviceValidationException();

        isTypeExist(deviceEntity.getType(), exception);
        isNumberCorrect(deviceEntity, exception);
        isRtdFacilityExist(deviceEntity.getFacility(), exception);

        isReplacementPeriodCorrect(deviceEntity, exception);
        isTestDateCorrect(deviceEntity, exception);
        isNextTestDateMatchTestDay(deviceEntity, exception);
        isNextTestDateValid(deviceEntity.getNextTestDate(), exception);

        isStatus31(deviceEntity, exception);

        isLineFacilityExist(facilityEntity, exception);
        isFacilitiesCompatible(facilityEntity, deviceEntity.getFacility(), exception);

        if (!exception.getErrors().isEmpty()) {
            throw exception;
        }
    }

    /**
     * @param deviceEntity   статус прибора - 11 (линия) или 21 (АВЗ Ст.) или 32 (АВЗ РТД);
     * @param facilityEntity Объект (РТД), к которому будет приписан прибор есть в справочнике;
     *                       РТД, к которому приписан прибор и Объект, к которому будет приписан прибор
     *                       приписаны к одному ШЧ - совместимы;
     *                       !!!!!!!!!!!!!!!   ЧТО ЕЩЕ необходимо проверить?????????
     */
    public void onUnsetDeviceValidation(DeviceEntity deviceEntity, FacilityEntity facilityEntity) {
        DeviceValidationException exception = new DeviceValidationException();

        isStatus112132(deviceEntity, exception);

        isRtdFacilityExist(facilityEntity, exception);
        isFacilitiesCompatible(facilityEntity, deviceEntity.getFacility(), exception);

        if (!exception.getErrors().isEmpty()) {
            throw exception;
        }
    }

    /**
     * @param deviceEntity статус прибора - 31 (ОБФ);
     *                     Объект (РТД), к которому приписан прибор есть в справочнике;
     *                     !!!!!!!!!!!!!!!   ЧТО ЕЩЕ необходимо проверить?????????
     */
    public void onDecommissionDeviceValidation(DeviceEntity deviceEntity) {
        DeviceValidationException exception = new DeviceValidationException();

        isStatus31(deviceEntity, exception);

        isRtdFacilityExist(deviceEntity.getFacility(), exception);

        if (!exception.getErrors().isEmpty()) {
            throw exception;
        }
    }

    private void isReplacementTypeNew(ReplacementType replacementType, DeviceValidationException exception) {
        if (replacementType == null) {
            exception.addError("replacementType:",
                    "ReplacementType Type Is Null");
        } else {
            if (replacementType.equals(ReplacementType.NEW)) {
                System.out.println("ReplacementType is correct");
            } else {
                exception.addError("replacementType:type",
                        "ReplacementType Is Wrong: " +
                                " - ReplacementType Id: " + replacementType.getComment());
            }
        }
    }

    private void isReplacementTypeOtkZam(ReplacementType replacementType, DeviceValidationException exception) {
        if (replacementType == null) {
            exception.addError("replacementType:",
                    "ReplacementType Type Is Null");
        } else {
            if (replacementType.equals(ReplacementType.ZAM) || replacementType.equals(ReplacementType.OTK)) {
                System.out.println("ReplacementType is correct");
            } else {
                exception.addError("replacementType:type",
                        "ReplacementType Is Wrong: " +
                                " - ReplacementType Id: " + replacementType.getComment());
            }
        }
    }

    private void isLocationEmpty(LocationEntity locationEntity, DeviceValidationException exception) {
        List<DeviceEntity> deviceEntityList = deviceRepository.findAllByLocation(locationEntity);

        if (deviceEntityList.size() > 0) {
            exception.addError("location:location",
                    "In Location already assembled Device: " +
                            " - Location Id: " + locationEntity.getId() + "; " +
                            " - Device Id: " + deviceEntityList.get(0).getId() + "; "
            );
        }
    }

    private void isTypeExist(DeviceTypeEntity deviceTypeEntity, DeviceValidationException exception) {
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


    private void isTypesCompatible(DeviceTypeEntity oldDeviceTypeEntity, DeviceTypeEntity newDeviceTypeEntity,
                                   DeviceValidationException exception) {
        if (newDeviceTypeEntity == null) {
            exception.addError("typeId:type",
                    "Device Type Is Null");
        } else {
            if (newDeviceTypeEntity.equals(oldDeviceTypeEntity)) {
//                todo: check types match
                System.out.println("type is correct");
            } else {
                exception.addError("typeId:type",
                        "Device Type Is Wrong: " +
                                " - type Id: " + newDeviceTypeEntity.getId());
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

    private void isRtdFacilityExist(FacilityEntity rtdFacilityEntity, DeviceValidationException exception) {
        if (rtdFacilityEntity == null) {
            exception.addError("facilityId:facility",
                    "Facility Is Null");
        } else {
            if (rtdFacilityRepository.existsById(rtdFacilityEntity.getId())) {
                System.out.println("facility is correct");
            } else {
                exception.addError("facilityId:facility",
                        "facility Is Wrong: " +
                                " - facility ID: " + rtdFacilityEntity.getId());
            }
        }
    }

    private void isLineFacilityExist(FacilityEntity facilityEntity, DeviceValidationException exception) {
        if (facilityEntity == null) {
            exception.addError("facilityId:facility",
                    "Facility Is Null");
        } else {
            if (lineFacilityRepository.existsById(facilityEntity.getId())) {
                System.out.println("facility is correct");
            } else {
                exception.addError("facilityId:facility",
                        "facility Is Wrong: " +
                                " - facility ID: " + facilityEntity.getId());
            }
        }
    }

    private void isFacilitiesCompatible(FacilityEntity oldDeviceFacilityEntity, FacilityEntity newDeviceFacilityEntity,
                                        DeviceValidationException exception) {
        if (newDeviceFacilityEntity == null) {
            exception.addError("facilityId:facility",
                    "Facility Is Null");
        } else {
            if (newDeviceFacilityEntity.getSubdivision().equals(oldDeviceFacilityEntity.getSubdivision())) {
                System.out.println("facility is correct");
            } else {
                exception.addError("facilityId:facility",
                        "Facilities Not Match (new Device must be from RTD which servicing Line Object): " +
                                " -new Device facility ID: " + newDeviceFacilityEntity.getId() +
                                " -old Device facility ID: " + oldDeviceFacilityEntity.getId()
                );
            }
        }
    }

    private void isFacilitiesEquals(FacilityEntity deviceFacilityEntity, FacilityEntity locationFacilityEntity,
                                    DeviceValidationException exception) {
        if (deviceFacilityEntity == null) {
            exception.addError("facilityId:facility",
                    "Facility Is Null");
        } else {
            if (deviceFacilityEntity.equals(locationFacilityEntity)) {
                System.out.println("facility is correct");
            } else {
                exception.addError("facilityId:facility",
                        "Facilities Not Match (new Device must be from RTD which servicing Line Object): " +
                                " - Device facility ID: " + deviceFacilityEntity.getId() +
                                " - Location facility ID: " + locationFacilityEntity.getId()
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

    private void isNextTestDateValid(Date nextTestDate, DeviceValidationException exception) {
        Date nowDate = new Date(System.currentTimeMillis());
        Date expectedNextTestDate = Date.valueOf(nowDate.toLocalDate().plusMonths(1));

        if (nextTestDate.compareTo(expectedNextTestDate) > 0) {
            System.out.println("test date is correct");
        } else {
            exception.addError("testDate:testDate",
                    "nextTestDate terms Ends within a month: " +
                            " - nextTestDate: " + nextTestDate);
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

    private void isStatus112132(DeviceEntity deviceEntity, DeviceValidationException exception) {
        if (deviceEntity.getStatus().equals(Status.PS11) ||
                deviceEntity.getStatus().equals(Status.PS21) ||
                deviceEntity.getStatus().equals(Status.PS32)) {
            System.out.println("Status is: " + deviceEntity.getStatus().getName() +
                    "--" + deviceEntity.getStatus().getComment());
        } else {
            exception.addError("status:status",
                    "Status Is Wrong (must be 11 or 21 or 32): " +
                            " - Status: " + deviceEntity.getStatus().getName() +
                            "--" + deviceEntity.getStatus().getComment());
        }
    }

    private void isStatus11(DeviceEntity deviceEntity, DeviceValidationException exception) {
        if (deviceEntity.getStatus().equals(Status.PS11)) {
            System.out.println("Status is: " + deviceEntity.getStatus().getName() +
                    "--" + deviceEntity.getStatus().getComment());
        } else {
            exception.addError("status:status",
                    "Status Is Wrong (must be 11): " +
                            " - Status: " + deviceEntity.getStatus().getName() +
                            "--" + deviceEntity.getStatus().getComment());
        }
    }

    private void isStatus21(DeviceEntity deviceEntity, DeviceValidationException exception) {
        if (deviceEntity.getStatus().equals(Status.PS21)) {
            System.out.println("Status is: " + deviceEntity.getStatus().getName() +
                    "--" + deviceEntity.getStatus().getComment());
        } else {
            exception.addError("status:status",
                    "Status Is Wrong (must be 21): " +
                            " - Status: " + deviceEntity.getStatus().getName() +
                            "--" + deviceEntity.getStatus().getComment());
        }
    }

    private void isStatus32(DeviceEntity deviceEntity, DeviceValidationException exception) {
        if (deviceEntity.getStatus().equals(Status.PS32)) {
            System.out.println("Status is: " + deviceEntity.getStatus().getName() +
                    "--" + deviceEntity.getStatus().getComment());
        } else {
            exception.addError("status:status",
                    "Status Is Wrong (must be 32): " +
                            " - Status: " + deviceEntity.getStatus().getName() +
                            "--" + deviceEntity.getStatus().getComment());
        }
    }

    private void isLocationExist(LocationEntity locationEntity, DeviceValidationException exception) {
        if (locationEntity == null) {
            exception.addError("locationId:location",
                    "Location Is Null");
        } else {
            if (locationRepository.existsById(locationEntity.getId())) {
                System.out.println("Location is correct");
            } else {
                exception.addError("locationId:location",
                        "Location Is Wrong: " +
                                " - facility ID: " + locationEntity.getId());
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
