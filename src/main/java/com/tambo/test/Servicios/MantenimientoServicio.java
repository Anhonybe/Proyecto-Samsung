package com.tambo.test.Servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tambo.test.DAO.MantenimientoRepository;
import com.tambo.test.Modelos.Mantenimiento;
import com.tambo.test.Modelos.Usuario;

@Service
public class MantenimientoServicio {

    @Autowired
    MantenimientoRepository mantenimientoRepository;

    public Mantenimiento findByUsuario(Usuario usuario) {
        return mantenimientoRepository.findByUsuario(usuario);
    }

    public Mantenimiento buscarPorCorreo(String correo) {
        return mantenimientoRepository.findByUsuarioCorreo(correo)
                .orElseThrow(() -> new RuntimeException("No se encontró al mantenimiento con ese correo"));
    }

}
