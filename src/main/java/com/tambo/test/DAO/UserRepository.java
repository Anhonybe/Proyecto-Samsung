package com.tambo.test.DAO;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tambo.test.Modelos.Usuario;

@Repository
public interface UserRepository extends JpaRepository<Usuario, String> {
    
    Optional<Usuario> findByCorreo(String correo);
    
    boolean existsByCorreo(String correo);
    
    boolean existsByTelefono(String telefono);

    // ✅ Esta línea es necesaria para generar el próximo ID tipo US00X
    Optional<Usuario> findTopByOrderByIdDesc();
}
