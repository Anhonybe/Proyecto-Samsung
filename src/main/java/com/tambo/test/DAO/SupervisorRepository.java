package com.tambo.test.DAO;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tambo.test.Modelos.Supervisor;
import com.tambo.test.Modelos.Usuario;

@Repository
public interface SupervisorRepository extends JpaRepository<Supervisor, String> {
    Supervisor findByUsuario(Usuario usuario);
    Optional<Supervisor> findByUsuarioCorreo(String correo);



}
