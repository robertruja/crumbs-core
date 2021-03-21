package com.gigi.crumbs.exception;

public class CrumbsInitException extends RuntimeException {
    public CrumbsInitException(String message) {
        super(message);
    }
    public CrumbsInitException(String message, Throwable cause) {
        super(message, cause);
    }
}
