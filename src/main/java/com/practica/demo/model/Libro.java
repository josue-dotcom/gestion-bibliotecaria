package com.practica.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import lombok.*;


@Entity
@Table(name = "libro")
//@Data
@NoArgsConstructor
@AllArgsConstructor
@Relation(collectionRelation = "libros")
public class Libro extends RepresentationModel<Libro> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message ="El titulo es obligatorio y no puede ser null")
    private String titulo;

    @ElementCollection
    @NotEmpty(message = "Debe haber al menos un autor")
    private List<@NotBlank(message = "El nombre del autor no puede estar vacío") String> autores;

    @NotNull(message = "La edición no puede ser null")
    private String edicion;

    @NotBlank(message = "El isbn es obligatorio y no puede ser null")
    private String isbn;

    @NotBlank(message = "La edición no puede ser null")
    private String editorial;

    //@Min(value = 1, message = "Debe haber al menos 1 copia disponible")
    private int copiasDisponibles = 1;

    // 🔽 Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<String> getAutores() {
        return autores;
    }

    public void setAutores(List<String> autores) {
        this.autores = autores;
    }

    public String getEdicion() {
        return edicion;
    }

    public void setEdicion(String edicion) {
        this.edicion = edicion;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public int getCopiasDisponibles() {
        return copiasDisponibles;
    }

    public void setCopiasDisponibles(int copiasDisponibles) {
        this.copiasDisponibles = copiasDisponibles;
    }
}

