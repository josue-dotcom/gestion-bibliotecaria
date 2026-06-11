package com.practica.demo.controller;

import com.practica.demo.assembler.LibroModelAssembler;
import com.practica.demo.exceptions.LibroNotFoundException;
//import com.practica.demo.exceptions.UsuarioNotFoundException;
import com.practica.demo.model.Libro;
//import com.practica.demo.model.Usuario;
import com.practica.demo.service.LibroService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/libros")
@AllArgsConstructor
public class LibroController {

    @Autowired
    private LibroService libroService;
    @Autowired
    private LibroModelAssembler assembler;
    @Autowired
    private PagedResourcesAssembler<Libro> pagedAssembler;

    // POST /libros
    @PostMapping
    public ResponseEntity<Void> crearLibro(@Valid @RequestBody Libro libro) {
    	libro.setId(null);
    	Libro nuevo = libroService.crearLibro(libro);
        return ResponseEntity
                .created(linkTo(methodOn(LibroController.class).obtenerLibro(nuevo.getId())).toUri())
                .build();
    }

    // GET /libros/{id}
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Libro>> obtenerLibro(@PathVariable Long id) {
    	Libro libro = libroService.buscarLibroId(id)        
    	.orElseThrow(() -> new LibroNotFoundException(id));

        return ResponseEntity.ok(EntityModel.of(libro,
                linkTo(methodOn(LibroController.class).obtenerLibro(id)).withSelfRel()));
    }

    // GET /libros
    @GetMapping
    public ResponseEntity<PagedModel<Libro>> getLibros(
        @RequestParam(defaultValue = "") String titulo,
        @RequestParam(defaultValue = "false") boolean soloDisponibles,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "2") int size
    ) {
      
        Page<Libro> librosPage;

        //  Filtrado según título y disponibilidad
        if (!titulo.isEmpty() && soloDisponibles) {
            librosPage = libroService.buscarLibrosPorTituloYDisponibles(titulo, page, size);
        } else if (!titulo.isEmpty()) {
            librosPage = libroService.buscarLibrosPorTitulo(titulo, page, size);
        } else if (soloDisponibles) {
            librosPage = libroService.obtenerLibrosDisponibles(page, size);
        } else {
            librosPage = libroService.obtenerLibrosPaginados(page, size);
        }

        if (librosPage.getTotalElements() == 0) {
            throw new LibroNotFoundException("No se encontró ningún libro con los filtros aplicados.");
        }

        PagedModel<Libro> pagedModel = pagedAssembler.toModel(librosPage, assembler);
        System.out.println("Contenido de la página actual: " + librosPage.getContent().size());
        return ResponseEntity.ok(pagedModel);
    }


    
    // PUT /libros/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Libro> actualizarLibro(@PathVariable Long id, @Valid @RequestBody Libro datosActualizados) {
        Libro actualizado = libroService.actualizarLibro(id, datosActualizados);
        return ResponseEntity.ok(actualizado);
    }


    // DELETE /libros/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarLibro(@PathVariable Long id) {
        libroService.eliminarLibro(id);
        return ResponseEntity.noContent().build(); // 204 si se elimina correctamente
    }



}

