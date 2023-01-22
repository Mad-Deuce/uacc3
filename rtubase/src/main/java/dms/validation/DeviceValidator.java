package dms.validation;

import dms.entity.DeviceEntity;
import org.springframework.stereotype.Component;

@Component
public class DeviceValidator {


    public void onCreateEntityValidation (DeviceEntity deviceEntity){
        this.isValidType();

    }

    private boolean isValidType () {
        return true;
    }

}
