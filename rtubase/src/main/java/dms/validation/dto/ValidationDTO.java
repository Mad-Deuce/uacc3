package dms.validation.dto;


import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationDTO {
    private List<ErrorInfo> errors;

    public ValidationDTO() {
        errors = new ArrayList<ErrorInfo>();
    }

    private record ErrorInfo(String fieldName, String message) {
    }

    public void addErrorInfo(String fieldName, String message) {
        errors.add(new ErrorInfo(fieldName, message));
    }

}