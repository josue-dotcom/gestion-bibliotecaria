package com.practica.demo.repository;

import com.practica.demo.model.Libro;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibroRepository extends JpaRepository<Libro, Long> {

    Optional<Libro> findByIsbn(String isbn);
    
    Page<Libro> findByCopiasDisponiblesGreaterThan(int cantidad, Pageable pageable);
    Page<Libro> findByTituloContaining(String titulo, Pageable paginable);
    Page<Libro> findByTituloContainingAndCopiasDisponiblesGreaterThan(String titulo, int cantidad, Pageable pageable);

}

