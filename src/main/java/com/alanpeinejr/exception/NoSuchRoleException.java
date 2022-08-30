package com.alanpeinejr.exception;

public class NoSuchRoleException extends Exception {
    public NoSuchRoleException(String errorMessage) {
        super((errorMessage));
    }
}
