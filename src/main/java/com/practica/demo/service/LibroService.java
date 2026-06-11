package com.practica.demo.service;

import com.practica.demo.exceptions.LibroConPrestamosActivosException;
import com.practica.demo.exceptions.LibroInconsistenteException;
import com.practica.demo.exceptions.LibroNotFoundException;
import com.practica.demo.model.Libro;
import com.practica.demo.model.Prestamo;
import com.practica.demo.repository.LibroRepository;
import com.practica.demo.repository.PrestamoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class LibroService {

    @Autowired
    private LibroRepository libroRepository;
    @Autowired
    private PrestamoRepository prestamoRepository;
    
    public Libro crearLibro(Libro nuevoLibro) {
        
    	if (nuevoLibro.getCopiasDisponibles() < 1) {
            throw new LibroInconsistenteException("Debe haber al menos 1 copia disponible al crear un libro.");
        }
    	
    	Optional<Libro> libroExistente = libroRepository.findByIsbn(nuevoLibro.getIsbn());

        if (libroExistente.isPresent()) {
            Libro existente = libroExistente.get();

            if (!coincidenDatos(nuevoLibro, existente)) {
                throw new LibroInconsistenteException(nuevoLibro.getIsbn());
            }

            // Datos coinciden → sumamos copias
            int total = existente.getCopiasDisponibles() + nuevoLibro.getCopiasDisponibles();
            existente.setCopiasDisponibles(total);

            return libroRepository.save(existente);
        }

        // Libro nuevo
        if (nuevoLibro.getCopiasDisponibles() <= 0) {
            nuevoLibro.setCopiasDisponibles(1);
        }

        return libroRepository.save(nuevoLibro);
    }



    //Método auxliar
    private boolean coincidenDatos(Libro nuevo, Libro existente) {
        return nuevo.getTitulo().equalsIgnoreCase(existente.getTitulo()) &&
               nuevo.getEditorial().equalsIgnoreCase(existente.getEditorial()) &&
               nuevo.getAutores().equals(existente.getAutores()) &&
               nuevo.getEdicion().equalsIgnoreCase(existente.getEdicion());
    }

    public Optional<Libro> buscarLibroId(Long id) {
        return libroRepository.findById(id);
    }
    
    public Page<Libro> obtenerLibrosDisponibles(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return libroRepository.findByCopiasDisponiblesGreaterThan(0, pageable);
    }


    public Page<Libro> obtenerLibrosPaginados(int page, int size) {
        Pageable paginable = PageRequest.of(page, size);
        return libroRepository.findAll(paginable); 
    }
    
    public Page<Libro> buscarLibrosPorTitulo(String titulo, int page, int size) {
        return libroRepository.findByTituloContaining(titulo, PageRequest.of(page, size));
    }
    
    public Page<Libro> buscarLibrosPorTituloYDisponibles(String titulo, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return libroRepository.findByTituloContainingAndCopiasDisponiblesGreaterThan(titulo, 0, pageable);
    }

    public Libro actualizarLibro(Long id, Libro datosActualizados) {
        Libro libro = libroRepository.findById(id)
            .orElseThrow(() -> new LibroNotFoundException(id)); // Ahora lanza la excepción directamente

        // Conflicto: otro libro ya tiene ese ISBN
        Optional<Libro> otroConMismoIsbn = libroRepository.findByIsbn(datosActualizados.getIsbn());
        if (otroConMismoIsbn.isPresent() && !otroConMismoIsbn.get().getId().equals(id)) {
            throw new LibroInconsistenteException(datosActualizados.getIsbn());
        }

        libro.setTitulo(datosActualizados.getTitulo());
        libro.setAutores(datosActualizados.getAutores());
        libro.setEdicion(datosActualizados.getEdicion());
        libro.setIsbn(datosActualizados.getIsbn());
        libro.setEditorial(datosActualizados.getEditorial());
        libro.setCopiasDisponibles(datosActualizados.getCopiasDisponibles());

        return libroRepository.save(libro);
    }


    public void eliminarLibro(Long id) {
        Libro libro = libroRepository.findById(id)
            .orElseThrow(() -> new LibroNotFoundException(id));

        List<Prestamo> prestamos = prestamoRepository.findByLibro(libro);
        for (Prestamo p : prestamos) {
            if (!p.isDevuelto()) {
                throw new LibroConPrestamosActivosException(id);
            }
        }

        libroRepository.deleteById(id);
    }

 
}

