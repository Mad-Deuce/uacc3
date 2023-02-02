package dms.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class NoEntityException extends RuntimeException {

    public NoEntityException(String message) {
        super(message);
    }

    @JsonIgnore
    @Override
    public StackTraceElement[] getStackTrace(){
        return super.getStackTrace();
    }
}
