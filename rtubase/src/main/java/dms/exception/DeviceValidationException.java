package dms.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DeviceValidationException extends RuntimeException {

    private final List<Error> errors;

    public DeviceValidationException(){
        errors = new ArrayList<>();
    }

    @AllArgsConstructor
    @Getter
    private static class Error {
        private String fieldName;
        private String message;
    }

    public void addError(String fieldName, String message){
        errors.add(new Error(fieldName, message));
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
