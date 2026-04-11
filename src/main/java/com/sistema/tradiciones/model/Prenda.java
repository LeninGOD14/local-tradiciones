package com.sistema.tradiciones.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "prendas")
public class Prenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioAlquiler;

    // Constructor vacío (Obligatorio para JPA)
    public Prenda() {
    }

    // Constructor para facilitar la creación de objetos
    public Prenda(String nombre, BigDecimal precioAlquiler) {
        this.nombre = nombre;
        this.precioAlquiler = precioAlquiler;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecioAlquiler() {
        return precioAlquiler;
    }

    public void setPrecioAlquiler(BigDecimal precioAlquiler) {
        this.precioAlquiler = precioAlquiler;
    }
}