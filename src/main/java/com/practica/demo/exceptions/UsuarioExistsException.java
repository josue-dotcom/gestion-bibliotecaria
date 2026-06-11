package com.practica.demo.exceptions;

public class UsuarioExistsException extends RuntimeException {
    public UsuarioExistsException(String nombreUsuario) {
        super("El usuario con nombre '" + nombreUsuario + "' ya existe.");
    }
}

