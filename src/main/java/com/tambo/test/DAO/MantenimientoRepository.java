package com.tambo.test.DAO;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tambo.test.Modelos.Mantenimiento;
import com.tambo.test.Modelos.Usuario;

@Repository
public interface MantenimientoRepository extends JpaRepository<Mantenimiento, String> {
    Mantenimiento findByUsuario(Usuario usuario);
    Optional<Mantenimiento> findByUsuarioCorreo(String correo);
}
