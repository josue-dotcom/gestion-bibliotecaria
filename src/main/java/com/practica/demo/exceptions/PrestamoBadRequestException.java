package com.practica.demo.exceptions;

public class PrestamoBadRequestException extends RuntimeException {
    public PrestamoBadRequestException(String message) {
        super(message);
    }
}
