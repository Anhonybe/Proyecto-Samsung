package com.tambo.test.Servicios;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tambo.test.DAO.InfraestructuraRepository;
import com.tambo.test.Modelos.Infraestructura;

@Service
public class InfraestructuraServicio {
    @Autowired
    private InfraestructuraRepository infraestructuraRepository;

    public List<Infraestructura> listar() {
        return infraestructuraRepository.findAll();
    }
    public List<Infraestructura> obtenerInfraestructurasEnBuenEstado() {
        return infraestructuraRepository.findByEnBuenEstadoTrue();
    }
}