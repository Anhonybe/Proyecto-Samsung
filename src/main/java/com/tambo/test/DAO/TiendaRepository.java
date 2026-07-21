package com.tambo.test.DAO;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tambo.test.Modelos.Tienda;

@Repository
public interface TiendaRepository extends JpaRepository<Tienda, String> {
    Optional<Tienda> findTopByOrderByIdDesc();
}

