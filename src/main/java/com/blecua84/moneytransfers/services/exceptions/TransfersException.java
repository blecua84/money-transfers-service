package com.blecua84.moneytransfers.services.exceptions;

public class TransfersException extends Exception {

    public TransfersException(String message) {
        super(message);
    }

    public TransfersException(String message, Throwable cause) {
        super(message, cause);
    }
}
