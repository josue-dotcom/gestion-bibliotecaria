package com.practica.demo.exceptions;

public class LibroConPrestamosActivosException extends RuntimeException {
    public LibroConPrestamosActivosException(Long id) {
        super("No se puede eliminar el libro con ID " + id + " porque tiene préstamos activos.");
    }
}
