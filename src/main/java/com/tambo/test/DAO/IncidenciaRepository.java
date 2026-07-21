package com.tambo.test.DAO;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tambo.test.Modelos.Incidencia;
import com.tambo.test.Modelos.Supervisor;
import com.tambo.test.Modelos.Tecnico;

@Repository
public interface IncidenciaRepository extends JpaRepository<Incidencia, String> {

    Optional<Incidencia> findTopByOrderByIdDesc();

    List<Incidencia> findBySupervisor(Supervisor supervisor);

    List<Incidencia> findByTecnicoAndEstado(Tecnico tecnico, String estado);

    Incidencia findByMaquinariaId(String maquinariaId);

    long countByTecnicoIsNotNull();

    long countByTecnicoIsNull();

    @Query(value = """
    SELECT 
        i.id,
        i.titulo,
       r.fecha_registro - CAST(i.fecha_registro AS DATE) AS dias
    FROM 
        incidencia i
    JOIN 
        reporte r ON r.id_incidencia = i.id
    ORDER BY 
        dias ASC
    LIMIT 5
    """, nativeQuery = true)
    List<Object[]> obtenerTop5IncidenciasResueltasRapidamente();

    @Query(value = """
    SELECT i.estado, COUNT(*) 
    FROM incidencia i 
    GROUP BY i.estado
    """, nativeQuery = true)
    List<Object[]> contarIncidenciasPorEstado();

}
