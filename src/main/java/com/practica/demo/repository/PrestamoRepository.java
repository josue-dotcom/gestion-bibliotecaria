package com.practica.demo.repository;

import com.practica.demo.model.Libro;
import com.practica.demo.model.Prestamo;
import com.practica.demo.model.Usuario;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

	// Devuelve una lista de préstamos activos (no devueltos) de un usuario
    List<Prestamo> findByUsuarioAndDevueltoFalse(Usuario usuario);
    
    // Si necesitas buscar un préstamo específico de un usuario, puedes agregar:
    Optional<Prestamo> findByIdAndUsuario(Long id, Usuario usuario);
    
    List<Prestamo> findByUsuario(Usuario usuario);
    List<Prestamo> findByLibro(Libro libro);
    
    Page<Prestamo> findByUsuarioIdAndDevueltoFalse(Long idUsuario, Pageable pageable);
    Page<Prestamo> findByUsuarioIdAndDevueltoFalseAndFechaPrestamoGreaterThanEqualAndFechaDevolucionLessThanEqual(
        Long idUsuario, 
        LocalDate fechaInicio, 
        LocalDate fechaFin, 
        Pageable pageable
    );
    
    Page<Prestamo> findByUsuarioIdAndDevueltoTrue(Long idUsuario, Pageable pageable);

    Page<Prestamo> findByUsuarioIdAndDevueltoFalseOrderByFechaPrestamoAsc(Long idUsuario, Pageable pageable);

    Page<Prestamo> findByUsuarioIdAndDevueltoTrueOrderByFechaDevolucionDesc(Long idUsuario, Pageable pageable);

	
}
