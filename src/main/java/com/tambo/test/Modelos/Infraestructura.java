package com.tambo.test.Modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "infraestructuras")
public class Infraestructura {

    @Id
    @Column(length = 10)
    private String id;

    @NotBlank
    private String tipo; // ej: Techo, Pared, Baño, Piso, etc.

    private String descripcion;

    private boolean enBuenEstado = true;

    private String ubicacion; // ej: zona de carga, caja, almacén



    public Infraestructura() {
    }


    public Infraestructura(String id, String tipo, String descripcion, boolean enBuenEstado, String ubicacion) {
        this.id = id;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.enBuenEstado = enBuenEstado;
        this.ubicacion = ubicacion;
    }


    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipo() {
        return this.tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isEnBuenEstado() {
        return this.enBuenEstado;
    }

    public boolean getEnBuenEstado() {
        return this.enBuenEstado;
    }

    public void setEnBuenEstado(boolean enBuenEstado) {
        this.enBuenEstado = enBuenEstado;
    }

    public String getUbicacion() {
        return this.ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    
}
