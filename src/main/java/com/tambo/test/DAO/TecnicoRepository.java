package com.tambo.test.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tambo.test.Modelos.Tecnico;
import com.tambo.test.Modelos.Usuario;

@Repository
public interface TecnicoRepository extends JpaRepository<Tecnico, String> {
    Tecnico findByUsuario(Usuario usuario);
}
