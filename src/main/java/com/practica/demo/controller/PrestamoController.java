package com.practica.demo.controller;

import com.practica.demo.exceptions.PrestamoException;
import com.practica.demo.model.Prestamo;
import com.practica.demo.model.PrestamoDTO;
import com.practica.demo.model.PrestamoUpdateDTO;
import com.practica.demo.service.PrestamoService;

//import jakarta.validation.Valid;

//import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/prestamos")
public class PrestamoController {

    @Autowired
    private PrestamoService prestamoService;

    // Endpoint para crear un nuevo préstamo
    @PostMapping
    public ResponseEntity<Void> crearPrestamo(@RequestBody PrestamoDTO dto) {
        Prestamo nuevoPrestamo = prestamoService.crearPrestamoDesdeDTO(dto);
        return ResponseEntity
                .created(linkTo(methodOn(PrestamoController.class).obtenerPrestamo(nuevoPrestamo.getId())).toUri())
                .build();
    }

    // Endpoint para obtener un préstamo por ID
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Prestamo>> obtenerPrestamo(@PathVariable Long id) {
        Optional<Prestamo> prestamoOpt = prestamoService.obtenerPrestamo(id);
        if (prestamoOpt.isEmpty()) {
            throw new PrestamoException("Préstamo con ID " + id + " no encontrado.");
        }

        EntityModel<Prestamo> recurso = EntityModel.of(prestamoOpt.get());
        recurso.add(linkTo(methodOn(PrestamoController.class).obtenerPrestamo(id)).withSelfRel());
        return ResponseEntity.ok(recurso);
    }

    // Endpoint para actualizar un préstamo (por ejemplo, marcarlo como devuelto o ampliado)
    
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPrestamo(@PathVariable Long id, @RequestBody PrestamoUpdateDTO dto) {
        Optional<Prestamo> actualizado = prestamoService.actualizarPrestamo(id, dto);

        if (actualizado.isPresent()) {
            return ResponseEntity.ok("Préstamo actualizado correctamente.");
        } else {
            throw new PrestamoException("No se pudo actualizar el préstamo con ID " + id + ".");
        }
    }


    // Endpoint para eliminar un préstamo (por ejemplo, en caso de error o cancelación)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPrestamo(@PathVariable Long id) {
        boolean eliminado = prestamoService.eliminarPrestamo(id);
        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
