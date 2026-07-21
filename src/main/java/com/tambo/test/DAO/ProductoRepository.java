package com.tambo.test.DAO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tambo.test.Modelos.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, String> {
    List<Producto> findByActivoTrue();
}