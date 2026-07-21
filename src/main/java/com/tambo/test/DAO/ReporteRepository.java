package com.tambo.test.DAO;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tambo.test.Modelos.Reporte;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, String> {
     Optional<Reporte> findTopByOrderByIdDesc();
     Optional<Reporte> findById(String id);
}
