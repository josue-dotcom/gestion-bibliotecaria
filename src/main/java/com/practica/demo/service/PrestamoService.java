package com.practica.demo.service;

import com.practica.demo.exceptions.LibroNotFoundException;
import com.practica.demo.exceptions.PrestamoBadRequestException;
//import com.practica.demo.exceptions.LibroNotFoundException;
//import com.practica.demo.exceptions.UsuarioNotFoundException;
import com.practica.demo.exceptions.PrestamoException;
import com.practica.demo.exceptions.UsuarioNotFoundException;
import com.practica.demo.model.Libro;
import com.practica.demo.model.Prestamo;
import com.practica.demo.model.PrestamoDTO;
import com.practica.demo.model.PrestamoUpdateDTO;
import com.practica.demo.model.Usuario;
import com.practica.demo.repository.LibroRepository;
import com.practica.demo.repository.PrestamoRepository;
import com.practica.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class PrestamoService {

    @Autowired
    private PrestamoRepository prestamoRepository;
    
    @Autowired
    private LibroRepository libroRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Crea un préstamo: se verifica que el libro existe y tiene copias disponibles,
     * se descuenta una copia y se crea el registro del préstamo.
     */
    public Prestamo crearPrestamoDesdeDTO(PrestamoDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
            .orElseThrow(() -> new UsuarioNotFoundException( dto.getUsuarioId() ));
        Libro libro = libroRepository.findById(dto.getLibroId())
            .orElseThrow(() -> new LibroNotFoundException( dto.getLibroId()));

        if (usuario.isSancionado()) {
            throw new PrestamoException("El usuario '" + usuario.getNombreUsuario() + "' está sancionado y no puede realizar préstamos.");
        }

        if (libro.getCopiasDisponibles() <= 0) {
            throw new PrestamoException("No hay copias disponibles para el libro '" + libro.getTitulo() + "'.");
        }

        libro.setCopiasDisponibles(libro.getCopiasDisponibles() - 1);
        libroRepository.save(libro);

        Prestamo prestamo = new Prestamo();
        prestamo.setUsuario(usuario);
        prestamo.setLibro(libro);
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setFechaDevolucion(LocalDate.now().plusWeeks(2));
        prestamo.setDevuelto(false);
        prestamo.setAmpliado(false);

        return prestamoRepository.save(prestamo);
    }

    
    
    /**
     * Obtiene un préstamo por su ID.
     */
    public Optional<Prestamo> obtenerPrestamo(Long id) {
        return prestamoRepository.findById(id);
    }

    public Page<Prestamo> obtenerPrestamosActivosFiltrados(Long idUsuario, LocalDate fechaPrestamo, LocalDate fechaDevolucion, Pageable pageable) {
        if (fechaPrestamo != null && fechaDevolucion != null) {
            return prestamoRepository.findByUsuarioIdAndDevueltoFalseAndFechaPrestamoGreaterThanEqualAndFechaDevolucionLessThanEqual(
                idUsuario, fechaPrestamo, fechaDevolucion, pageable);
        } else {
            return prestamoRepository.findByUsuarioIdAndDevueltoFalse(idUsuario, pageable);
        }
    }
    
//    //obtener los 5 últimos del historial
//    public Page<Prestamo> obtenerUltimos5PrestamosDevueltos(Long idUsuario) {
//        Pageable pageable = PageRequest.of(0, 5, Sort.by("fechaDevolucion").descending());
//        return prestamoRepository.findByUsuarioIdAndDevueltoTrue(idUsuario, pageable);
//    }

    
    
    //prestamo historial
    public Page<Prestamo> obtenerHistoricoPrestamos(Long idUsuario, Pageable pageable) {
        return prestamoRepository.findByUsuarioIdAndDevueltoTrue(idUsuario, pageable);
    }

    //prestamo con actividad
    public Page<Prestamo> obtenerPrestamosActivosOrdenados(Long idUsuario, Pageable pageable) {
        return prestamoRepository.findByUsuarioIdAndDevueltoFalseOrderByFechaPrestamoAsc(idUsuario, pageable);
    }

    public Page<Prestamo> obtenerHistoricoOrdenado(Long idUsuario, Pageable pageable) {
        return prestamoRepository.findByUsuarioIdAndDevueltoTrueOrderByFechaDevolucionDesc(idUsuario, pageable);
    }

        
    /**
     * Actualiza un préstamo. Por ejemplo, se puede usar para marcar un préstamo como devuelto.
     * Si se marca como devuelto, se debe aumentar las copias disponibles del libro.
     */
    public Optional<Prestamo> actualizarPrestamo(Long id, PrestamoUpdateDTO dto) {
        Optional<Prestamo> prestamoOpt = prestamoRepository.findById(id);
        if (prestamoOpt.isEmpty()) {
            return Optional.empty();
        }

        Prestamo prestamo = prestamoOpt.get();

        if (!dto.isDevuelto() && !dto.isAmpliado()) {
            throw new PrestamoBadRequestException("Debe marcarse como devuelto o ampliado. No se puede dejar ambos en falso.");
        }

        if (dto.isDevuelto() && dto.isAmpliado()) {
            throw new PrestamoBadRequestException("No puedes marcar un préstamo como devuelto y ampliado al mismo tiempo.");
        }

        if (prestamo.isDevuelto()) {
            throw new PrestamoException("Este préstamo ya fue devuelto y no puede modificarse.");
        }

        if (dto.isDevuelto() && !prestamo.isDevuelto()) {
            prestamo.setDevuelto(true);
            Libro libro = prestamo.getLibro();
            libro.setCopiasDisponibles(libro.getCopiasDisponibles() + 1);
            libroRepository.save(libro);

            // Sancionar si se devuelve tarde
            if (LocalDate.now().isAfter(prestamo.getFechaDevolucion())) {
                Usuario usuario = prestamo.getUsuario();
                usuario.setSancionado(true);
                usuarioRepository.save(usuario);
            }
        }

        if (dto.isAmpliado() && !prestamo.isAmpliado() && !prestamo.isDevuelto()) {
            prestamo.setFechaDevolucion(prestamo.getFechaDevolucion().plusWeeks(1));
            prestamo.setAmpliado(true);
        }

        return Optional.of(prestamoRepository.save(prestamo));
    }


    /**
     * Elimina un préstamo por su ID (opcional: normalmente se mantiene el historial de préstamos).
     */
    public boolean eliminarPrestamo(Long id) {
        if (prestamoRepository.existsById(id)) {
            prestamoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}

