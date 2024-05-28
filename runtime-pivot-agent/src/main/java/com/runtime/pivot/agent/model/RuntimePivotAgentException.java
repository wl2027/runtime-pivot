package com.runtime.pivot.agent.model;

public class RuntimePivotAgentException extends RuntimeException{
    public RuntimePivotAgentException() {
    }

    public RuntimePivotAgentException(String message) {
        super(message);
    }

    public RuntimePivotAgentException(String message, Throwable cause) {
        super(message, cause);
    }

    public RuntimePivotAgentException(Throwable cause) {
        super(cause);
    }

    public RuntimePivotAgentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
