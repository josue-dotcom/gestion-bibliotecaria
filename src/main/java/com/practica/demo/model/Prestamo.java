package com.practica.demo.model;

import jakarta.persistence.*;
//import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@Entity
@Table(name = "prestamo")
@NoArgsConstructor
@AllArgsConstructor
public class Prestamo extends RepresentationModel<Prestamo>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Usuario usuario;


    //@ManyToOne
    @ManyToOne(fetch = FetchType.EAGER)
    private Libro libro;

    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;

    private boolean devuelto = false;
    private boolean ampliado = false;

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public boolean isDevuelto() {
        return devuelto;
    }

    public void setDevuelto(boolean devuelto) {
        this.devuelto = devuelto;
    }

    public boolean isAmpliado() {
        return ampliado;
    }

    public void setAmpliado(boolean ampliado) {
        this.ampliado = ampliado;
    }
    
    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String tituloLibro;

    public String getTituloLibro() {
        return tituloLibro;
    }

    public void setTituloLibro(String tituloLibro) {
        this.tituloLibro = tituloLibro;
    }

}

