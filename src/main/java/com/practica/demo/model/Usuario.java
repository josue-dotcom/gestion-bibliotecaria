package com.practica.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonInclude;

import org.springframework.hateoas.EntityModel;

@Entity
@Table(name = "usuario")
//@Data
@NoArgsConstructor // Crea un constructor vacío
@AllArgsConstructor // Crea un constructor con todos los campos
public class Usuario extends RepresentationModel<Usuario>  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombreUsuario es obligatorio y no puede ser null")
    private String nombreUsuario;
    
    @NotBlank(message = "La matrícula es obligatoria y no puede ser null")
    private String matricula;

    @NotNull(message = "La fecha de nacimiento no puede ser null")
    private LocalDate fechaNacimiento;

    @NotBlank(message = "El correo es obligatorio y no puede ser null")
    private String correoElectronico;

    private boolean sancionado = false;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<EntityModel<Prestamo>> prestamosActivos;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<EntityModel<Prestamo>> ultimosPrestamosDevueltos;


    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public LocalDate getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(LocalDate fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public String getCorreoElectronico() {
		return correoElectronico;
	}

	public void setCorreoElectronico(String correoElectronico) {
		this.correoElectronico = correoElectronico;
	}

	public boolean isSancionado() {
		return sancionado;
	}

	public void setSancionado(boolean sancionado) {
		this.sancionado = sancionado;
	}
	
	// Agrega sus getters y setters:
	public Set<EntityModel<Prestamo>> getPrestamosActivos() {
	    return prestamosActivos;
	}

	public void setPrestamosActivos(Set<EntityModel<Prestamo>> prestamosActivos) {
	    this.prestamosActivos = prestamosActivos;
	}

	public Set<EntityModel<Prestamo>> getUltimosPrestamosDevueltos() {
	    return ultimosPrestamosDevueltos;
	}

	public void setUltimosPrestamosDevueltos(Set<EntityModel<Prestamo>> ultimosPrestamosDevueltos) {
	    this.ultimosPrestamosDevueltos = ultimosPrestamosDevueltos;
	}

}

