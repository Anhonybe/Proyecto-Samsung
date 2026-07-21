package com.tambo.test.Servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tambo.test.DAO.SupervisorRepository;
import com.tambo.test.Modelos.Supervisor;
import com.tambo.test.Modelos.Usuario;

@Service
public class SupervisorServicio {

    @Autowired
    private SupervisorRepository supervisorRepository;

    public Supervisor findByUsuario(Usuario usuario) {
        return supervisorRepository.findByUsuario(usuario);
    }

    public Supervisor buscarPorCorreo(String correo) {
        return supervisorRepository.findByUsuarioCorreo(correo)
                .orElseThrow(() -> new RuntimeException("No se encontró al supervisor con ese correo"));
    }

}
