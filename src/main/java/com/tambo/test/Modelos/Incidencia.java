package com.tambo.test.Modelos;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Incidencia {

    @Id
    private String id; // ID tipo IND001

    private String titulo;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private CategoriaIncidencia categoria;

    private String prioridad;

    private String estado = "Pendiente"; // por defecto

    private LocalDateTime fechaRegistro;

    @ManyToOne
    @JoinColumn(name = "supervisor_id")
    private Supervisor supervisor;

    // Campo nuevo para técnico asignado
    @ManyToOne
    @JoinColumn(name = "tecnico_id")
    private Tecnico tecnico;

    // Campo nuevo para evidencia (nombre de archivo o ruta de la imagen)
    private String evidenciaIncidencia;

    // Nuevo campo para el tipo (producto, maquinaria, infraestructura)
    private String tipo; // ejemplo: "producto"

    // Nuevas relaciones condicionales (solo una se usará dependiendo del tipo)
    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "maquinaria_id")
    private Maquinaria maquinaria;

    @ManyToOne
    @JoinColumn(name = "infraestructura_id")
    private Infraestructura infraestructura;



    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public CategoriaIncidencia getCategoria() {
        return this.categoria;
    }

    public void setCategoria(CategoriaIncidencia categoria) {
        this.categoria = categoria;
    }

    public String getPrioridad() {
        return this.prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getEstado() {
        return this.estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaRegistro() {
        return this.fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Supervisor getSupervisor() {
        return this.supervisor;
    }

    public void setSupervisor(Supervisor supervisor) {
        this.supervisor = supervisor;
    }

    public Tecnico getTecnico() {
        return this.tecnico;
    }

    public void setTecnico(Tecnico tecnico) {
        this.tecnico = tecnico;
    }

    public String getEvidenciaIncidencia() {
        return this.evidenciaIncidencia;
    }

    public void setEvidenciaIncidencia(String evidenciaIncidencia) {
        this.evidenciaIncidencia = evidenciaIncidencia;
    }

    public String getTipo() {
        return this.tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Producto getProducto() {
        return this.producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Maquinaria getMaquinaria() {
        return this.maquinaria;
    }

    public void setMaquinaria(Maquinaria maquinaria) {
        this.maquinaria = maquinaria;
    }

    public Infraestructura getInfraestructura() {
        return this.infraestructura;
    }

    public void setInfraestructura(Infraestructura infraestructura) {
        this.infraestructura = infraestructura;
    }
   
    

}
