package com.runtime.pivot.agent.model;

public class RuntimePivotException extends RuntimeException{
    public RuntimePivotException() {
    }

    public RuntimePivotException(String message) {
        super(message);
    }

    public RuntimePivotException(String message, Throwable cause) {
        super(message, cause);
    }

    public RuntimePivotException(Throwable cause) {
        super(cause);
    }

    public RuntimePivotException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
