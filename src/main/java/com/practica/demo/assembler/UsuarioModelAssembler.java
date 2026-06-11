package com.practica.demo.assembler;



import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import com.practica.demo.controller.UsuarioController;
import com.practica.demo.model.Usuario;

@Component
public class UsuarioModelAssembler extends RepresentationModelAssemblerSupport<Usuario, Usuario> {

    public UsuarioModelAssembler() {
        super(UsuarioController.class, Usuario.class);
    }

    @Override
    public Usuario toModel(Usuario usuario) {
        usuario.add(linkTo(methodOn(UsuarioController.class).obtenerUsuario(usuario.getId())).withSelfRel());
        return usuario;
    }
}

