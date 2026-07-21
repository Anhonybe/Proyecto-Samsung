package com.tambo.test.Security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.tambo.test.Modelos.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                        HttpServletResponse response,
                                        Authentication authentication) 
                                        throws IOException, ServletException {
        // Obtenemos el usuario autenticado
        Usuario usuario = (Usuario) authentication.getPrincipal();

        // Redirección basada en rol
        switch (usuario.getRole()) {
            case SUPERVISOR:
                response.sendRedirect("/supervisor/vista");
                break;
            case TECNICO:
                response.sendRedirect("/tecnico/vista");
                break;
            case MANTENIMIENTO:
                response.sendRedirect("/mantenimiento/vista");
                break;
            default:
                response.sendRedirect("/login?error=rol"); // fallback
                break;
        }
    }
}
