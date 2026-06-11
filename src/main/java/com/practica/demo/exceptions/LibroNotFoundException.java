package com.practica.demo.exceptions;

public class LibroNotFoundException extends RuntimeException {
    public LibroNotFoundException(Long id) {
        super("Libro con ID " + id + " no encontrado.");
    }
    
    public LibroNotFoundException(String mensaje) {
        super(mensaje);
    }
}
