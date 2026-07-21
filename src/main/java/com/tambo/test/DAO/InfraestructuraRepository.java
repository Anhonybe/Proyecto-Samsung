package com.tambo.test.DAO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tambo.test.Modelos.Infraestructura;

@Repository
public interface InfraestructuraRepository extends JpaRepository<Infraestructura, String> {
    List<Infraestructura> findByEnBuenEstadoTrue();
}
