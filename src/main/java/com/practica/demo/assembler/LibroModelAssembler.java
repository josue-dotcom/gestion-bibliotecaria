package com.practica.demo.assembler;

import com.practica.demo.controller.LibroController;
import com.practica.demo.model.Libro;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class LibroModelAssembler extends RepresentationModelAssemblerSupport<Libro, Libro> {

    public LibroModelAssembler() {
        super(LibroController.class, Libro.class);
    }

    @Override
    public Libro toModel(Libro libro) {
        libro.add(linkTo(methodOn(LibroController.class).obtenerLibro(libro.getId())).withSelfRel());
        return libro;
    }
}

