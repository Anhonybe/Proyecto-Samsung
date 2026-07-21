package com.tambo.test.Servicios;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tambo.test.DAO.CategoriaIncidenciaRepository;
import com.tambo.test.Modelos.CategoriaIncidencia;

@Service
public class CategoriaIncidenciaServicio {

    @Autowired
    private CategoriaIncidenciaRepository repo;

    public String generarId() {
        Optional<CategoriaIncidencia> ultima = repo.findTopByOrderByIdDesc();
        if (ultima.isPresent()) {
            String id = ultima.get().getId(); // CATI001
            int num = Integer.parseInt(id.substring(4)) + 1;
            return String.format("CATI%03d", num);
        } else {
            return "CATI001";
        }
    }

    public CategoriaIncidencia guardar(CategoriaIncidencia categoria) {
        if (categoria.getId() == null || categoria.getId().isEmpty()) {
            // Nueva categoría
            String nuevoId = generarId(); // eg. "CATI001"
            categoria.setId(nuevoId);
            categoria.setFechaCreacion(LocalDateTime.now());
        } else {
            // Ya tiene ID: se está editando → no se cambia la fecha
            Optional<CategoriaIncidencia> existente = repo.findById(categoria.getId());
            if (existente.isPresent()) {
                categoria.setFechaCreacion(existente.get().getFechaCreacion());
            }
        }

        return repo.save(categoria);
    }

    public List<CategoriaIncidencia> listar() {
        return repo.findAll();
    }

    public void eliminarPorId(String id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
        }
    }

}
