package dms.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dms.validation.dto.ValidationDTO;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DeviceValidationException extends RuntimeException {

    private final List<ValidationDTO> errors;

    public DeviceValidationException(String message) {
        super(message);
        errors = new ArrayList<>();
    }

    public DeviceValidationException(String message, List<ValidationDTO> errorsInfo) {
        super(message);
        this.errors = errorsInfo;
    }

    @JsonIgnore
    @Override
    public StackTraceElement[] getStackTrace(){
        return super.getStackTrace();
    }

    @JsonIgnore
    @Override
    public synchronized Throwable getCause(){
        return super.getCause();
    }

    @JsonIgnore
    @Override
    public String getLocalizedMessage(){
        return super.getLocalizedMessage();
    }
}
