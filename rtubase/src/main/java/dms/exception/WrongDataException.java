package dms.exception;

import dms.validation.dto.ValidationDTO;

import java.util.List;

public class WrongDataException extends RuntimeException{
    public WrongDataException (String message, List<ValidationDTO> errors){
        super(message);
    }
}
