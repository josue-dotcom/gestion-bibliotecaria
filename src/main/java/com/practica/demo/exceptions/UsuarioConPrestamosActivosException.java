package com.practica.demo.exceptions;

public class UsuarioConPrestamosActivosException extends RuntimeException {
    public UsuarioConPrestamosActivosException(Long id) {
        super("El usuario con ID " + id + " no puede ser eliminado porque tiene préstamos activos.");
    }
}

