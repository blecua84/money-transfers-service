package com.blecua84.moneytransfers.persistence.exceptions;

public class DataManagerException extends Exception {

    public DataManagerException(String message) {
        super(message);
    }

    public DataManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
