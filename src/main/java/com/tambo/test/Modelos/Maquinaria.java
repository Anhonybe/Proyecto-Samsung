package com.tambo.test.Modelos;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;


@Entity
@Table(name = "maquinarias")
public class Maquinaria {

    @Id
    @Column(length = 10)
    private String id;

    @NotBlank
    private String nombre;

    private String descripcion;

    private String estado; // ej: Operativa, En revisión, Fuera de servicio

    private String modelo;

    private int anioFabricacion;

    private LocalDate fechaUltimoMantenimiento;


    public Maquinaria() {
    }



    public Maquinaria(String id, String nombre, String descripcion, String estado, String modelo, int anioFabricacion, LocalDate fechaUltimoMantenimiento) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
        this.modelo = modelo;
        this.anioFabricacion = anioFabricacion;
        this.fechaUltimoMantenimiento = fechaUltimoMantenimiento;
    }


    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return this.estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getModelo() {
        return this.modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getAnioFabricacion() {
        return this.anioFabricacion;
    }

    public void setAnioFabricacion(int anioFabricacion) {
        this.anioFabricacion = anioFabricacion;
    }

    public LocalDate getFechaUltimoMantenimiento() {
        return this.fechaUltimoMantenimiento;
    }

    public void setFechaUltimoMantenimiento(LocalDate fechaUltimoMantenimiento) {
        this.fechaUltimoMantenimiento = fechaUltimoMantenimiento;
    }
      
}

