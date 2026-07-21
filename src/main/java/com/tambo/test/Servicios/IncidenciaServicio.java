package com.tambo.test.Servicios;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tambo.test.DAO.IncidenciaRepository;
import com.tambo.test.DAO.InfraestructuraRepository;
import com.tambo.test.DAO.MaquinariaRepository;
import com.tambo.test.DAO.ProductoRepository;
import com.tambo.test.Modelos.Incidencia;
import com.tambo.test.Modelos.Infraestructura;
import com.tambo.test.Modelos.Maquinaria;
import com.tambo.test.Modelos.Producto;
import com.tambo.test.Modelos.Supervisor;
import com.tambo.test.Modelos.Tecnico;

@Service
public class IncidenciaServicio {

    @Autowired
    private IncidenciaRepository incidenciaRepository;

    @Autowired
    private MaquinariaRepository maquinariaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private InfraestructuraRepository infraestructuraRepository;

    public String generarId() {
        Optional<Incidencia> ultima = incidenciaRepository.findTopByOrderByIdDesc();
        if (ultima.isPresent()) {
            String id = ultima.get().getId(); // IND001
            int num = Integer.parseInt(id.substring(3)) + 1;
            return String.format("IND%03d", num);
        } else {
            return "IND001";
        }
    }

    public Incidencia guardar(Incidencia inc, Supervisor supervisor) {
        if (inc.getId() == null || inc.getId().isEmpty()) {
            inc.setId(generarId());
        }

        inc.setSupervisor(supervisor); // Asignar supervisor autenticado
        inc.setEstado("Pendiente"); // Estado por defecto

        // Limpieza de relaciones no necesarias
        switch (inc.getTipo()) {
            case "producto" -> {
                inc.setMaquinaria(null);
                inc.setInfraestructura(null);
                if (inc.getProducto() != null && inc.getProducto().getId() != null) {
                    Producto product = productoRepository.findById(inc.getProducto().getId()).orElse(null);
                    if (product != null) {
                        product.setActivo(false);
                        productoRepository.save(product);
                    }
                }
            }
            case "maquinaria" -> {
                inc.setProducto(null);
                inc.setInfraestructura(null);
                // Aquí se actualiza el estado de la maquinaria
                if (inc.getMaquinaria() != null && inc.getMaquinaria().getId() != null) {
                    Maquinaria maquinaria = maquinariaRepository.findById(inc.getMaquinaria().getId()).orElse(null);
                    if (maquinaria != null) {
                        maquinaria.setEstado("En revisión");
                        maquinariaRepository.save(maquinaria);
                    }
                }
            }
            case "infraestructura" -> {
                inc.setProducto(null);
                inc.setMaquinaria(null);
                if (inc.getInfraestructura() != null && inc.getInfraestructura().getId() != null) {
                    Infraestructura infraestructura = infraestructuraRepository.findById(inc.getInfraestructura().getId()).orElse(null);
                    if (infraestructura != null) {
                        infraestructura.setEnBuenEstado(false);
                        infraestructuraRepository.save(infraestructura);
                    }
                }
            }
            default -> {
                inc.setProducto(null);
                inc.setMaquinaria(null);
                inc.setInfraestructura(null);
            }
        }

        return incidenciaRepository.save(inc);
    }

    public Incidencia actulizar(Incidencia inc) {
        return incidenciaRepository.save(inc);
    }

    public List<Incidencia> obtenerPorSupervisor(Supervisor supervisor) {
        return incidenciaRepository.findBySupervisor(supervisor);
    }

    public List<Incidencia> listarTodas() {
        return incidenciaRepository.findAll();
    }

    public Incidencia buscarPorId(String id) {
        return incidenciaRepository.findById(id).orElse(null);
    }

    public void guardaraasignando(Incidencia incidencia) {
        incidenciaRepository.save(incidencia);
    }

    public List<Incidencia> listarPorTecnicoEnProceso(Tecnico tecnico) {
        return incidenciaRepository.findByTecnicoAndEstado(tecnico, "En proceso");
    }

    public Incidencia buscarPorMaquinariaId(String maquinariaId) {
        return incidenciaRepository.findByMaquinariaId(maquinariaId);
    }

    public Map<String, Integer> obtenerCantidadPorTurno(List<Incidencia> incidencias) {
        int manana = 0, tarde = 0, noche = 0;

        for (Incidencia incidencia : incidencias) {
            LocalTime hora = incidencia.getFechaRegistro().toLocalTime();

            if (!hora.isBefore(LocalTime.of(5, 0)) && hora.isBefore(LocalTime.of(12, 0))) {
                manana++;
            } else if (!hora.isBefore(LocalTime.of(12, 0)) && hora.isBefore(LocalTime.of(18, 0))) {
                tarde++;
            } else {
                noche++;
            }
        }

        Map<String, Integer> resultado = new HashMap<>();
        resultado.put("Mañana", manana);
        resultado.put("Tarde", tarde);
        resultado.put("Noche", noche);

        return resultado;
    }

    public int contarIncidenciasAsignadas() {
        return (int) incidenciaRepository.countByTecnicoIsNotNull(); // <--- NO uses "ccount"
    }

    public int contarIncidenciasNoAsignadas() {
        return (int) incidenciaRepository.countByTecnicoIsNull();
    }

    public List<Map<String, Object>> obtenerIncidenciasMasRapidas() {
        List<Object[]> resultados = incidenciaRepository.obtenerTop5IncidenciasResueltasRapidamente();
        List<Map<String, Object>> respuesta = new ArrayList<>();

        for (Object[] obj : resultados) {
            Map<String, Object> fila = new HashMap<>();
            fila.put("id", obj[0].toString()); // ← No hacer Long.valueOf
            fila.put("titulo", obj[1].toString());

            // Validar si el valor de días es nulo antes de convertir
            Object diasObj = obj[2];
            fila.put("dias", diasObj != null ? ((Number) diasObj).intValue() : 0);

            respuesta.add(fila);
        }

        return respuesta;
    }

    public List<Map<String, Object>> obtenerCantidadPorEstado() {
        List<Object[]> resultados = incidenciaRepository.contarIncidenciasPorEstado();
        List<Map<String, Object>> respuesta = new ArrayList<>();

        for (Object[] obj : resultados) {
            Map<String, Object> fila = new HashMap<>();
            fila.put("estado", obj[0].toString());
            fila.put("cantidad", ((Number) obj[1]).intValue());
            respuesta.add(fila);
        }

        return respuesta;
    }

    public Map<String, Long> contarIncidenciasPorEstado() {
    List<Incidencia> incidencias = incidenciaRepository.findAll();

    return incidencias.stream()
            .collect(Collectors.groupingBy(
                    Incidencia::getEstado,
                    Collectors.counting()
            ));
}


}
