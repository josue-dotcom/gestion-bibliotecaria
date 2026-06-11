package com.practica.demo.controller;

import com.practica.demo.assembler.UsuarioModelAssembler;
import com.practica.demo.exceptions.UsuarioBadRequestException;
import com.practica.demo.exceptions.UsuarioConPrestamosActivosException;
import com.practica.demo.exceptions.UsuarioNotFoundException;
import com.practica.demo.model.Prestamo;
import com.practica.demo.model.Usuario;
import com.practica.demo.repository.PrestamoRepository;
import com.practica.demo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import lombok.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
@AllArgsConstructor
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private UsuarioModelAssembler assembler;
    @Autowired
    private PagedResourcesAssembler<Usuario> pagedAssembler;
    @Autowired
    private PrestamoRepository prestamoRepository;

    
    // Crear usuario
    @PostMapping
    public ResponseEntity<Void> crearUsuario(@Valid @RequestBody Usuario usuario) {
        usuario.setId(null);
    	Usuario nuevo = usuarioService.crearUsuario(usuario);
        return ResponseEntity
                .created(linkTo(methodOn(UsuarioController.class).obtenerUsuario(nuevo.getId())).toUri())
                .build();
    }

    // Obtener usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> obtenerUsuario(@PathVariable Long id) {
    	Usuario usuario = usuarioService.buscarUsuarioById(id)
    	        .orElseThrow(() -> new UsuarioNotFoundException(id));

        EntityModel<Usuario> recurso = EntityModel.of(usuario);
        recurso.add(linkTo(methodOn(UsuarioController.class).obtenerUsuario(id)).withSelfRel());
        return ResponseEntity.ok(recurso);
    }

    // Obtener todos los usuarios
    @GetMapping
    public ResponseEntity<PagedModel<Usuario>> obtenerUsuarios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size
    ) {
    	
        if (page < 0 || size <= 0) {
            throw new UsuarioBadRequestException("Los valores de 'page' y 'size' deben ser positivos.");
        }
        
        Page<Usuario> usuariosPage = usuarioService.obtenerUsuariosPaginados(page, size);
        PagedModel<Usuario> pagedModel = pagedAssembler.toModel(usuariosPage, assembler);
        return ResponseEntity.ok(pagedModel);
    }


    // Actualizar usuario
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Long id, @Valid @RequestBody Usuario datosActualizados) {
       Usuario usuario = usuarioService.actualizarUsuario(id, datosActualizados);
        return ResponseEntity.ok(usuario); // → 204 No Content
    }


    // Eliminar usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable Long id) {
        Usuario usuario = usuarioService.buscarUsuarioById(id)
                .orElseThrow(() -> new UsuarioNotFoundException(id));

        List<Prestamo> prestamosActivos = prestamoRepository.findByUsuarioAndDevueltoFalse(usuario);
        if (!prestamosActivos.isEmpty()) {
            throw new UsuarioConPrestamosActivosException(id); // puedes definir esta excepción
        }

        usuarioService.eliminarUsuario(id);
        return ResponseEntity.ok("Usuario eliminado correctamente.");
    }

}


