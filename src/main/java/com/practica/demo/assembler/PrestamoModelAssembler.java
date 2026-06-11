package com.practica.demo.assembler;

import com.practica.demo.controller.UsuarioPrestamoController;
import com.practica.demo.model.Libro;
import com.practica.demo.model.Prestamo;
import com.practica.demo.model.Usuario;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.beans.factory.annotation.Autowired;

@Component
public class PrestamoModelAssembler extends RepresentationModelAssemblerSupport<Prestamo, Prestamo> {

	@Autowired
	private UsuarioModelAssembler usuarioModelAssembler;

	@Autowired
	private LibroModelAssembler libroModelAssembler;
    public PrestamoModelAssembler() {
        super(UsuarioPrestamoController.class, Prestamo.class);
    }

   
    public Prestamo toModel(Prestamo prestamo) {
        if (prestamo.isDevuelto()) {
            // Si el préstamo fue devuelto, añade enlace al HISTORIAL
            prestamo.add(linkTo(methodOn(UsuarioPrestamoController.class)
                    .obtenerHistoricoPrestamos(prestamo.getUsuario().getId(), 0, 2))
                    .withRel("historial"));
        } else {
            // Si está activo, añade enlace a los ACTIVOS
            prestamo.add(linkTo(methodOn(UsuarioPrestamoController.class)
                    .obtenerPrestamosActivos(prestamo.getUsuario().getId(), null, null, 0, 2))
                    .withRel("activos"));
        }
        
     // Limpia links previos del usuario antes de añadir uno nuevo
        Usuario usuario = prestamo.getUsuario();
        usuario.removeLinks(); // 🔥 Evita duplicados
        usuarioModelAssembler.toModel(usuario);


        // Limpia links del libro también si lo haces igual
        Libro libro = prestamo.getLibro();
        libro.removeLinks();
        libroModelAssembler.toModel(libro);
        
        return prestamo;
    }




    
}

