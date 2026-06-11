package com.practica.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

@RequestMapping(produces = "application/json")
@RestControllerAdvice
public class GlobalExceptionHandler {

	//Usuario
	@ExceptionHandler(UsuarioExistsException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorMessage handleUsuarioExists(UsuarioExistsException ex) {
	    return new ErrorMessage(ex.getMessage());
	}
	
    @ExceptionHandler(UsuarioNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleUsuarioNotFound(UsuarioNotFoundException ex) {
        return new ErrorMessage(ex.getMessage());
    }

    @ExceptionHandler(UsuarioConPrestamosActivosException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleUsuarioConPrestamosActivos(UsuarioConPrestamosActivosException ex) {
        return new ErrorMessage(ex.getMessage());
    }
    
    @ExceptionHandler(UsuarioBadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) 
    public ErrorMessage handleUsuarioBadRequest(UsuarioBadRequestException ex) {
        return new ErrorMessage(ex.getMessage());
    }


    //Libro
    @ExceptionHandler(LibroNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleLibroNotFound(LibroNotFoundException ex) {
        return new ErrorMessage(ex.getMessage());
    }
    
    
    @ExceptionHandler(LibroInconsistenteException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorMessage handleLibroInconsistente(LibroInconsistenteException ex) {
        return new ErrorMessage(ex.getMessage());
    }

    @ExceptionHandler(LibroConPrestamosActivosException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleLibroConPrestamosActivos(LibroConPrestamosActivosException ex) {
        return new ErrorMessage(ex.getMessage());
    }
    
    //Prestamo
    @ExceptionHandler(PrestamoException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorMessage handlePrestamoException(PrestamoException ex) {
    	ex.printStackTrace(); // Para que se vea en consola
    	return new ErrorMessage(ex.getMessage());
    }
    
    @ExceptionHandler(PrestamoBadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handlePrestamoBadRequest(PrestamoBadRequestException ex) {
        return new ErrorMessage(ex.getMessage());
    }
    
    @ExceptionHandler(PrestamoNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handlePrestamoNotFound(PrestamoNotFoundException ex) {
        return new ErrorMessage(ex.getMessage());
    }

    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ErrorMessage(errors.toString());
    }

}

