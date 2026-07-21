package com.tambo.test.Servicios;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tambo.test.DAO.InventarioRepository;
import com.tambo.test.Modelos.Inventario;
import com.tambo.test.Modelos.Producto;

@Service
public class InventarioServicio {

    @Autowired
    private InventarioRepository inventarioRepository;

    public List<Inventario> listarTodos() {
        return inventarioRepository.findAll();
    }

    public Inventario buscarPorProducto(Producto producto) {
        return inventarioRepository.findByProducto(producto)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado para el producto"));
    }

    public Inventario guardar(Inventario inventario) {
        return inventarioRepository.save(inventario);
    }
   

}
