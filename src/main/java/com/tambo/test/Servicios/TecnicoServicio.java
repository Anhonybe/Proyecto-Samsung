package com.tambo.test.Servicios;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tambo.test.DAO.TecnicoRepository;
import com.tambo.test.Modelos.Tecnico;
import com.tambo.test.Modelos.Usuario;

@Service
public class TecnicoServicio {

    @Autowired
    private TecnicoRepository tecnicoRepository;

    public List<Tecnico> listarTodos() {
        return tecnicoRepository.findAll();
    }

    public Tecnico buscarPorId(String id) {
        return tecnicoRepository.findById(id).orElse(null);
    }

    public Tecnico findByUsuario(Usuario usuario) {
        return tecnicoRepository.findByUsuario(usuario);
    }

}
