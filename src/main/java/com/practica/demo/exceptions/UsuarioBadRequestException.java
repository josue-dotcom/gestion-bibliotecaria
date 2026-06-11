package com.practica.demo.exceptions;

public class UsuarioBadRequestException extends RuntimeException{
	
	public UsuarioBadRequestException(String message) {
        super(message);
    }
}
