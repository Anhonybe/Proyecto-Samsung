package com.tambo.test.DAO;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tambo.test.Modelos.Inventario;
import com.tambo.test.Modelos.Producto;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, String> {
    Optional<Inventario> findByProducto(Producto producto);

}
