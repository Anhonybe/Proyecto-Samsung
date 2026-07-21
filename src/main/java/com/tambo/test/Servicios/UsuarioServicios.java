package com.tambo.test.Servicios;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy; 
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tambo.test.DAO.MantenimientoRepository;
import com.tambo.test.DAO.SupervisorRepository;
import com.tambo.test.DAO.TecnicoRepository;
import com.tambo.test.DAO.TiendaRepository;
import com.tambo.test.DAO.UserRepository;
import com.tambo.test.Modelos.Mantenimiento;
import com.tambo.test.Modelos.Role;
import com.tambo.test.Modelos.Supervisor;
import com.tambo.test.Modelos.Tecnico;
import com.tambo.test.Modelos.Tienda;
import com.tambo.test.Modelos.Usuario;

@Service
public class UsuarioServicios implements UserDetailsService {
    @Autowired
    private MantenimientoRepository MantenimientoRepository;

    @Autowired
    private TecnicoRepository TecnicoRepository;

    @Autowired
    private SupervisorRepository SupervisorRepository;

    @Autowired
    private TiendaRepository tiendaRepository;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioServicios(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Usuario> user = userRepository.findByCorreo(username);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }
    }

    public Usuario registerUsuario(Usuario usuario, Role role) {
        if (userRepository.existsByCorreo(usuario.getCorreo())) {
            System.out.print("El correo ya está registrado");
        }

        if (userRepository.existsByTelefono(usuario.getTelefono())) {
            System.out.print("El teléfono ya está registrado");
        }

        Usuario user = new Usuario();
        user.setId(generarIdUsuario());
        user.setNombre(usuario.getNombre());
        user.setApellidos(usuario.getApellidos());
        user.setCorreo(usuario.getCorreo());
        user.setTelefono(usuario.getTelefono());
        user.setPassword(passwordEncoder.encode(usuario.getPassword()));
        user.setRole(role);
        user.setEnabled(true);

        return userRepository.save(user);
    }

    public Optional<Usuario> findByCorreo(String correo) {
        return userRepository.findByCorreo(correo);
    }

    public boolean existsByCorreo(String correo) {
        return userRepository.existsByCorreo(correo);
    }

    public boolean existsByTelefono(String telefono) {
        return userRepository.existsByTelefono(telefono);
    }

    private String generarIdUsuario() {
        Optional<Usuario> ultimo = userRepository.findTopByOrderByIdDesc();
        if (ultimo.isPresent()) {
            String ultimoId = ultimo.get().getId(); // ejemplo: "US005"
            int numero = Integer.parseInt(ultimoId.substring(2)) + 1;
            return String.format("US%03d", numero);
        } else {
            return "US001";
        }
    }

    private String generarIdConPrefijo(String prefijo, long total) {
        return String.format("%s%03d", prefijo, total);
    }

    public Mantenimiento registrarMantenimiento(Usuario usuario, String areaAsignada) {
        String id = generarIdConPrefijo("USM", MantenimientoRepository.count() + 1);
        Mantenimiento mant = new Mantenimiento();
        mant.setId(id);
        mant.setUsuario(usuario);
        mant.setAreaAsignada(areaAsignada);
        return MantenimientoRepository.save(mant);
    }

    public Tecnico registrarTecnico(Usuario usuario, String especialidad) {
        String id = generarIdConPrefijo("UST", TecnicoRepository.count() + 1);
        Tecnico tecnico = new Tecnico();
        tecnico.setId(id);
        tecnico.setUsuario(usuario);
        tecnico.setEspecialidad(especialidad);
        return TecnicoRepository.save(tecnico);
    }

    public Supervisor registrarSupervisor(Usuario usuario, Tienda tienda, String cargo) {
        String id = generarIdConPrefijo("USS", SupervisorRepository.count() + 1);
        Supervisor sup = new Supervisor();
        sup.setId(id);
        sup.setUsuario(usuario);
        sup.setTienda(tienda);
        sup.setCargo(cargo);
        return SupervisorRepository.save(sup);
    }

    public Tienda registrarTienda(String nombre, String direccion) {
        String id = tiendaRepository.findTopByOrderByIdDesc()
                .map(t -> {
                    int num = Integer.parseInt(t.getId().substring(2)) + 1;
                    return String.format("TD%03d", num);
                }).orElse("TD001");

        Tienda tienda = new Tienda(id, nombre, direccion);
        return tiendaRepository.save(tienda);
    }

    
    public TiendaRepository getTiendaRepository() {
        return tiendaRepository;
    }

    public void registrarTiendaConId(String id, String nombre, String direccion) {
    Tienda tienda = new Tienda();
    tienda.setId(id);
    tienda.setNombre(nombre);
    tienda.setDireccion(direccion);
    tiendaRepository.save(tienda);
}


public Usuario findByCorreoOrThrow(String correo) {
    return userRepository.findByCorreo(correo)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
}


}
