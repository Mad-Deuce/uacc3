package dms.validation.dto;


import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationDTO {
    private List<Error> errors;

    public ValidationDTO() {
        errors = new ArrayList<>();
    }

    private record Error(String name, String message) {
    }

    public void add(String name, String message) {
        errors.add(new Error(name, message));
    }

}
