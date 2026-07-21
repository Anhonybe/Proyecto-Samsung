package com.tambo.test.Servicios;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tambo.test.DAO.IncidenciaRepository;
import com.tambo.test.DAO.ReporteRepository;
import com.tambo.test.Modelos.Incidencia;
import com.tambo.test.Modelos.Reporte;

@Service
public class ReporteServicio {

    @Autowired
    private ReporteRepository reporteRepostory;

    @Autowired
    private IncidenciaRepository incidenciaRepostory;

    public String generarId() {
        Optional<Reporte> ultima = reporteRepostory.findTopByOrderByIdDesc();
        if (ultima.isPresent()) {
            String id = ultima.get().getId(); // RPT001
            int num = Integer.parseInt(id.substring(3)) + 1;
            return String.format("RPT%03d", num);
        } else {
            return "RPT001";
        }
    }

    public Reporte guardar(Reporte reporte) {
        // Generar ID
        if (reporte.getId() == null || reporte.getId().isEmpty()) {
            reporte.setId(generarId());
        }

        // Actualizar estado de la incidencia
        Incidencia inc = reporte.getIncidencia();
        if (inc != null && inc.getId() != null) {
            Incidencia incidenciaDB = incidenciaRepostory.findById(inc.getId()).orElse(null);
            if (incidenciaDB != null) {
                incidenciaDB.setEstado("Solucionado");
                incidenciaRepostory.save(incidenciaDB);
                reporte.setIncidencia(incidenciaDB);
            }
        }

        return reporteRepostory.save(reporte);
    }

    public List<Reporte> listarPorSupervisor(String idSupervisor) {
        return reporteRepostory.findAll().stream()
                .filter(r -> r.getIncidencia() != null
                && r.getIncidencia().getSupervisor() != null
                && r.getIncidencia().getSupervisor().getId().equals(idSupervisor))
                .collect(Collectors.toList());
    }

    public Reporte findById(String id) {
        return reporteRepostory.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado con ID: " + id));
    }
}
