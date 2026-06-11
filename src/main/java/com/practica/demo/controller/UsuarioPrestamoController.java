package com.practica.demo.controller;

import com.practica.demo.assembler.PrestamoModelAssembler;
import com.practica.demo.exceptions.PrestamoBadRequestException;
import com.practica.demo.exceptions.PrestamoNotFoundException;
import com.practica.demo.model.Prestamo;
import com.practica.demo.model.Usuario;
import com.practica.demo.service.PrestamoService;
import com.practica.demo.service.UsuarioService;
import com.practica.demo.exceptions.UsuarioNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;


@RestController
@RequestMapping("/usuarios/{idUsuario}/prestamos")
public class UsuarioPrestamoController {

    @Autowired
    private PrestamoService prestamoService;

    @Autowired
    private PrestamoModelAssembler prestamoAssembler;

    @Autowired
    private PagedResourcesAssembler<Prestamo> pagedAssembler;

    @Autowired
    private UsuarioService usuarioService;

    
    //Obtener préstamos activos filtrados
    @GetMapping
    public ResponseEntity<PagedModel<Prestamo>> obtenerPrestamosActivos(
            @PathVariable Long idUsuario,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaPrestamo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDevolucion,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size) {

        if (fechaPrestamo != null && fechaDevolucion != null && fechaPrestamo.isAfter(fechaDevolucion)) {
            throw new PrestamoBadRequestException("La fecha de préstamo no puede ser posterior a la fecha de devolución.");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Prestamo> prestamos = prestamoService
                .obtenerPrestamosActivosFiltrados(idUsuario, fechaPrestamo, fechaDevolucion, pageable);

        if (prestamos.isEmpty()) {
            throw new PrestamoNotFoundException("No se encontraron préstamos activos para el usuario con ID " + idUsuario);
        }

        return ResponseEntity.ok(pagedAssembler.toModel(prestamos, prestamoAssembler));
    }

    //  Historial de préstamos devueltos
    @GetMapping("/historial")
    public ResponseEntity<PagedModel<Prestamo>> obtenerHistoricoPrestamos(
            @PathVariable Long idUsuario,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size) {

    	   
    	if (page < 0 || size <= 0) {
    	     throw new PrestamoBadRequestException("Paginación inválida: 'page' debe ser >= 0 y 'size' > 0.");
    	}
    	   
        Pageable pageable = PageRequest.of(page, size);
        Page<Prestamo> prestamos = prestamoService.obtenerHistoricoPrestamos(idUsuario, pageable);

        //Obtener páginas completas
        
        Page<Prestamo> devueltosPage = prestamoService.obtenerHistoricoPrestamos(idUsuario, pageable);
        //Setear título para ambos antes de ensamblar HATEOAS
        devueltosPage.forEach(p -> p.setTituloLibro(p.getLibro().getTitulo()));
        
        if (prestamos.isEmpty()) {
            throw new PrestamoNotFoundException("No se encontraron préstamos devueltos para el usuario con ID " + idUsuario);
        }
        
        return ResponseEntity.ok(pagedAssembler.toModel(prestamos, prestamoAssembler));
    }
    
    //  Actividad: préstamos activos + últimos devueltos
//    @GetMapping("/actividad")
//    public ResponseEntity<Usuario> obtenerActividadUsuario(
//    		@PathVariable Long idUsuario,  
//    		@RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "5") int size) {
//    	
//        Usuario usuario = usuarioService.buscarUsuarioById(idUsuario)
//                .orElseThrow(() -> new UsuarioNotFoundException(idUsuario));
//
//        Set<EntityModel<Prestamo>> prestamosActivos = new HashSet<>();
//        Set<EntityModel<Prestamo>> prestamosDevueltos = new HashSet<>();
//
//        Pageable pageable = PageRequest.of(page, size);
//        List<Prestamo> activos = prestamoService.obtenerPrestamosActivosOrdenados(idUsuario, pageable).getContent();
//
//        List<Prestamo> ultimosDevueltos = prestamoService.obtenerUltimos5PrestamosDevueltos(idUsuario).getContent();
//
//        for (Prestamo prestamo : activos) {
//        	prestamo.setTituloLibro(prestamo.getLibro().getTitulo());
//            prestamosActivos.add(EntityModel.of(prestamo,
//                    linkTo(methodOn(PrestamoController.class).obtenerPrestamo(prestamo.getId())).withSelfRel()));
//        }
//
//        for (Prestamo prestamo : ultimosDevueltos) {
//        	prestamo.setTituloLibro(prestamo.getLibro().getTitulo());
//            prestamosDevueltos.add(EntityModel.of(prestamo,
//                    linkTo(methodOn(PrestamoController.class).obtenerPrestamo(prestamo.getId())).withSelfRel()));
//        }
//
//        usuario.setPrestamosActivos(prestamosActivos);
//        usuario.setUltimosPrestamosDevueltos(prestamosDevueltos);
//
//        usuario.add(linkTo(methodOn(UsuarioPrestamoController.class)
//                .obtenerActividadUsuario(idUsuario,0,5)).withSelfRel());
//
//        return ResponseEntity.ok(usuario);
//    }
    
    @GetMapping("/actividad")
    public ResponseEntity<Map<String, Object>> obtenerActividadUsuario(
            @PathVariable Long idUsuario,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

    	  if (page < 0 || size <= 0) {
  	        throw new PrestamoBadRequestException("Paginación inválida: 'page' debe ser >= 0 y 'size' > 0.");
  	    }
    	
    	Usuario usuario = usuarioService.buscarUsuarioById(idUsuario)
                .orElseThrow(() -> new UsuarioNotFoundException(idUsuario));

        Pageable pageable = PageRequest.of(page, size);

        //Obtener páginas completas
        Page<Prestamo> activosPage = prestamoService.obtenerPrestamosActivosOrdenados(idUsuario, pageable);
        Page<Prestamo> devueltosPage = prestamoService.obtenerHistoricoOrdenado(idUsuario, pageable);


        //Usar pagedAssembler
        PagedModel<Prestamo> pagedActivos = pagedAssembler.toModel(activosPage, prestamoAssembler);
        PagedModel<Prestamo> pagedDevueltos = pagedAssembler.toModel(devueltosPage, prestamoAssembler);

        usuario.add(linkTo(methodOn(UsuarioPrestamoController.class)
                .obtenerActividadUsuario(idUsuario, page, size)).withSelfRel());

        //Construir la respuesta incluyendo paginación completa
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("usuario", usuario);
        respuesta.put("prestamosActivos", pagedActivos);
        respuesta.put("ultimosPrestamosDevueltos", pagedDevueltos);

        return ResponseEntity.ok(respuesta);
    }

   


}
