package com.practica.demo.repository;

import com.practica.demo.model.Usuario;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Aquí puedes añadir búsquedas personalizadas si las necesitas
	 Page<Usuario> findByNombreUsuarioStartingWith(String nombre, Pageable paginable);
	 boolean existsByNombreUsuario(String nombreUsuario);
	 Optional<Usuario> findByNombreUsuario(String nombreUsuario);


}

