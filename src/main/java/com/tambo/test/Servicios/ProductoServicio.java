package com.tambo.test.Servicios;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tambo.test.DAO.ProductoRepository;
import com.tambo.test.Modelos.Producto;

@Service
public class ProductoServicio {
    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    public List<Producto> listarActivos() {
        return productoRepository.findByActivoTrue();
    }

    public Producto buscarPorId(String id) {
        return productoRepository.findById(id)
                .orElse(null); 
    }
}