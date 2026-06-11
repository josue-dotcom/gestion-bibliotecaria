package com.practica.demo.exceptions;

public class PrestamoNotFoundException extends RuntimeException {
    public PrestamoNotFoundException(Long idUsuario) {
        super("No se encontraron préstamos para el usuario con ID " + idUsuario);
    }

    public PrestamoNotFoundException(String mensaje) {
        super(mensaje);
    }
}
