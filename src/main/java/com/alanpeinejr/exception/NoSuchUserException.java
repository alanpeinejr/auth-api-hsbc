package com.alanpeinejr.exception;

public class NoSuchUserException extends Exception {
    public NoSuchUserException(String errorMessage) {
        super((errorMessage));
    }
}
