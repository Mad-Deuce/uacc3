package com.dms_uz.auth.exception;

public class NotUniqueUsernameException extends RuntimeException{
    public NotUniqueUsernameException(String message) {
        super(message);
    }
}
