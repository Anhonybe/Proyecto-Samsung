package com.tambo.test.DAO;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tambo.test.Modelos.CategoriaIncidencia;

@Repository
public interface CategoriaIncidenciaRepository extends JpaRepository<CategoriaIncidencia, String> {
    Optional<CategoriaIncidencia> findTopByOrderByIdDesc();
}
