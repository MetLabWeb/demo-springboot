package com.met.metcamp.web.demospringboot.exceptions;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class ApiException extends RuntimeException {
    private int status;

    public ApiException(int status, String message) {
        super(message);
        this.status = status;
    }

    public ApiException(int status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
}
