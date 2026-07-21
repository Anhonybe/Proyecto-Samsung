package com.tambo.test.Modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "inventario")
public class Inventario {

    @Id
    private String id;
    @OneToOne
    @JoinColumn(name = "producto_id", referencedColumnName = "id")
    private Producto producto;

    @Min(0)
    @Column(name = "stock_inventario")
    private int stockEnInventario;

    public Inventario() {
    }


    public Inventario(String id, Producto producto, int stockEnInventario) {
        this.id = id;
        this.producto = producto;
        this.stockEnInventario = stockEnInventario;
    }
    


    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Producto getProducto() {
        return this.producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getStockEnInventario() {
        return this.stockEnInventario;
    }

    public void setStockEnInventario(int stockEnInventario) {
        this.stockEnInventario = stockEnInventario;
    }
    
}