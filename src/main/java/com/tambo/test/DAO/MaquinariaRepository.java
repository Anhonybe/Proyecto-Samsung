package com.tambo.test.DAO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tambo.test.Modelos.Maquinaria;


@Repository
public interface MaquinariaRepository extends JpaRepository<Maquinaria, String> {
    List<Maquinaria> findByEstado(String estado);

}