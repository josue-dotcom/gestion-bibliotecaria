package com.practica.demo.service;

import com.practica.demo.exceptions.UsuarioExistsException;
import com.practica.demo.exceptions.UsuarioNotFoundException;
//import com.practica.demo.model.Prestamo;
import com.practica.demo.model.Usuario;
//import com.practica.demo.repository.PrestamoRepository;
import com.practica.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

//import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
   // @Autowired
    //private PrestamoRepository prestamoRepository;


    public Usuario crearUsuario(Usuario usuario) {
        if (usuarioRepository.existsByNombreUsuario(usuario.getNombreUsuario())) {
            throw new UsuarioExistsException(usuario.getNombreUsuario());
        }
        return usuarioRepository.save(usuario);
    }


    public Optional<Usuario> buscarUsuarioById(Long id) {
        return  usuarioRepository.findById(id);
    }

    public Page<Usuario> obtenerUsuariosPaginados(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return usuarioRepository.findAll(pageable); 
    }


    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }


    public Usuario actualizarUsuario(Long id, Usuario datosActualizados) {
    	Usuario usuario = usuarioRepository.findById(id)
    	        .orElseThrow(() -> new UsuarioNotFoundException(id));

        // Conflicto: otro usuario ya tiene ese nombre
        Optional<Usuario> otroConMismoNombre = usuarioRepository.findByNombreUsuario(datosActualizados.getNombreUsuario());
        if (otroConMismoNombre.isPresent() && !otroConMismoNombre.get().getId().equals(id)) {
            throw new UsuarioExistsException(datosActualizados.getNombreUsuario());
        }

        usuario.setNombreUsuario(datosActualizados.getNombreUsuario());
        usuario.setCorreoElectronico(datosActualizados.getCorreoElectronico());
        usuario.setFechaNacimiento(datosActualizados.getFechaNacimiento());
        usuario.setMatricula(datosActualizados.getMatricula());

        return usuarioRepository.save(usuario);
    }


}

