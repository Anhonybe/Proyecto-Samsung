package com.tambo.test.Servicios;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tambo.test.DAO.MaquinariaRepository;
import com.tambo.test.Modelos.Maquinaria;

@Service
public class MaquinariaServicio {

    @Autowired
    private MaquinariaRepository maquinariaRepository;

    public List<Maquinaria> listar() {
        return maquinariaRepository.findAll();
    }

    public List<Maquinaria> listarPorEstado(String estado) {
        return maquinariaRepository.findByEstado(estado);
    }

    public void actualizarEstado(String id, String nuevoEstado) {
        Maquinaria maquinaria = maquinariaRepository.findById(id).orElse(null);
        if (maquinaria != null) {
            maquinaria.setEstado(nuevoEstado);
            maquinariaRepository.save(maquinaria);
        }
    }

    public void guardar(Maquinaria maquinaria) {
        maquinariaRepository.save(maquinaria);
    }

}
