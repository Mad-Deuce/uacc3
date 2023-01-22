package dms.validation;

import dms.entity.DeviceEntity;
import dms.exception.WrongDataException;
import dms.repository.DeviceRepository;
import dms.standing.data.repository.DeviceTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
public class DeviceValidator {

    @PersistenceContext
    EntityManager em;

    private final DeviceTypeRepository deviceTypeRepository;
    private final DeviceRepository deviceRepository;

    @Autowired
    public DeviceValidator(DeviceTypeRepository deviceTypeRepository, DeviceRepository deviceRepository) {
        this.deviceTypeRepository = deviceTypeRepository;
        this.deviceRepository = deviceRepository;
    }

    public void onCreateEntityValidation(DeviceEntity deviceEntity) {
        validateType(deviceEntity);
        validateFacility(deviceEntity);
        validateDuplicateExistByQuery(deviceEntity);
    }

    private void validateType(DeviceEntity deviceEntity) {
        if (!deviceTypeRepository.existsById(deviceEntity.getType().getId())) {
            throw new WrongDataException("Device Type Is Wrong: " +
                    " - type Id: " + deviceEntity.getType().getId())
                    ;
        }
    }

    private  void validateFacility(DeviceEntity deviceEntity){

    }

    private void validateDuplicateExistByQuery(DeviceEntity deviceEntity) {
        Long size = (Long) em.createQuery(
                "SELECT count (d.id) " +
                        " FROM DeviceEntity d " +
                        " WHERE 1=1 " +
                        " AND d.type = " + deviceEntity.getType() +
                        " AND d.number = " + deviceEntity.getNumber() +
                        " AND d.releaseYear = " + deviceEntity.getReleaseYear()
        ).getSingleResult();

        if (size > 0) {
            throw new WrongDataException("Duplicate Is Found with parameter: " +
                    " - type: " + deviceEntity.getType().getName() +
                    " - number: " + deviceEntity.getNumber() +
                    " - release year: " + deviceEntity.getReleaseYear())
                    ;
        }
    }
}
