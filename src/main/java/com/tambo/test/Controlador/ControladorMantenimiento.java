package com.tambo.test.Controlador;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.tambo.test.Modelos.Incidencia;
import com.tambo.test.Modelos.Mantenimiento;
import com.tambo.test.Modelos.Maquinaria;
import com.tambo.test.Modelos.Reporte;
import com.tambo.test.Modelos.Tecnico;
import com.tambo.test.Modelos.Usuario;
import com.tambo.test.Servicios.IncidenciaServicio;
import com.tambo.test.Servicios.MantenimientoServicio;
import com.tambo.test.Servicios.MaquinariaServicio;
import com.tambo.test.Servicios.ReporteServicio;
import com.tambo.test.Servicios.TecnicoServicio;
import com.tambo.test.Servicios.UsuarioServicios;

@Controller
public class ControladorMantenimiento {
    @Autowired
    ReporteServicio reporteServicio;

    @Autowired
    private UsuarioServicios usuarioServicios;
    @Autowired
    private MantenimientoServicio mantenimientoServicio;
    @Autowired
    private IncidenciaServicio incidenciaServicio;
    @Autowired
    private TecnicoServicio tecnicoServicio;
    @Autowired
    private MaquinariaServicio maquinariaServicio;

    @GetMapping("/mantenimiento/vista")
    public String empleadoDashboard(Model model, Principal principal) {
        String correo = principal.getName();
        Usuario usuario = usuarioServicios.findByCorreoOrThrow(correo);
        Mantenimiento mantenimiento = mantenimientoServicio.findByUsuario(usuario);
        List<Incidencia> incidencias = incidenciaServicio.listarTodas();
        List<Tecnico> tecnicos = tecnicoServicio.listarTodos();
        List<Maquinaria> maquinarias = maquinariaServicio.listarPorEstado("En revision");
        model.addAttribute("maquinarias", maquinarias);
        model.addAttribute("usuario", usuario);
        model.addAttribute("mantenimiento", mantenimiento);
        model.addAttribute("incidencias", incidencias);
        model.addAttribute("tecnicos", tecnicos);
        return "mantenimiento";
    }

    @PostMapping("/mantenimiento/asignar-tecnico")
    @ResponseBody
    public ResponseEntity<?> asignarTecnico(@RequestBody Map<String, String> datos) {
        String incidenciaId = datos.get("incidenciaId");
        String tecnicoId = datos.get("tecnicoId");

        Incidencia incidencia = incidenciaServicio.buscarPorId(incidenciaId);
        Tecnico tecnico = tecnicoServicio.buscarPorId(tecnicoId);

        if (incidencia == null || tecnico == null) {
            return ResponseEntity.badRequest().body("Datos inválidos");
        }

        incidencia.setTecnico(tecnico);
        incidencia.setEstado("En proceso");
        incidenciaServicio.guardaraasignando(incidencia);

        return ResponseEntity.ok().body(Map.of("mensaje", "Asignado"));
    }

    @PostMapping("/mantenimiento/incidencia/actualizar-estado")
@ResponseBody
public ResponseEntity<Map<String, String>> actualizarEstado(
    @ModelAttribute Reporte reporte,
    @RequestParam("imagenReporte") MultipartFile imagen,
    @RequestParam("maquinariaId") String maquinariaId,
    @RequestParam("nuevoEstado") String nuevoEstado
) throws IOException {

    maquinariaServicio.actualizarEstado(maquinariaId, nuevoEstado);
    
    Incidencia incidencia = incidenciaServicio.buscarPorMaquinariaId(maquinariaId);
    incidencia.setEstado("Solucionado");
    incidenciaServicio.actulizar(incidencia);

    if (imagen != null && !imagen.isEmpty()) {
        String baseRuta = System.getProperty("user.dir") + "/src/main/resources/static/reporte/";
        String nombreArchivo = "Reporte_" + imagen.getOriginalFilename();
        Path rutaFinal = Paths.get(baseRuta + nombreArchivo);
        Files.createDirectories(rutaFinal.getParent());
        reporte.setImagen(nombreArchivo);
        imagen.transferTo(rutaFinal.toFile());
    }

    reporteServicio.guardar(reporte);

    Map<String, String> respuesta = new HashMap<>();
    respuesta.put("mensaje", "Estado actualizado correctamente");
    return ResponseEntity.ok(respuesta);
}


}
