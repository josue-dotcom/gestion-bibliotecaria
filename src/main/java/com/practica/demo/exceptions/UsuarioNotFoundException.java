package com.practica.demo.exceptions;

public class UsuarioNotFoundException extends RuntimeException {
    public UsuarioNotFoundException(Long id) {
        super("Usuario con ID " + id + " no encontrado.");
    }
    
    
}


