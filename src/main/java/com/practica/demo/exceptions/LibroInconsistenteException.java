package com.practica.demo.exceptions;

public class LibroInconsistenteException extends RuntimeException {
    public LibroInconsistenteException(String isbn) {
        super("Ya existe un libro con el ISBN: " + isbn + ", pero con datos diferentes.");
        
        
    }
}


