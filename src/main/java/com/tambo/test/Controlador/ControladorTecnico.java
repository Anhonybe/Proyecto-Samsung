package com.tambo.test.Controlador;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tambo.test.Modelos.Incidencia;
import com.tambo.test.Modelos.Infraestructura;
import com.tambo.test.Modelos.Inventario;
import com.tambo.test.Modelos.Maquinaria;
import com.tambo.test.Modelos.Producto;
import com.tambo.test.Modelos.Reporte;
import com.tambo.test.Modelos.Tecnico;
import com.tambo.test.Modelos.Usuario;
import com.tambo.test.Servicios.IncidenciaServicio;
import com.tambo.test.Servicios.InventarioServicio;
import com.tambo.test.Servicios.MaquinariaServicio;
import com.tambo.test.Servicios.ProductoServicio;
import com.tambo.test.Servicios.ReporteServicio;
import com.tambo.test.Servicios.TecnicoServicio;
import com.tambo.test.Servicios.UsuarioServicios;

@Controller
public class ControladorTecnico {

    @Autowired
    UsuarioServicios usuarioService;
    @Autowired
    TecnicoServicio tecnicoServicio;
    @Autowired
    MaquinariaServicio maquinariaServicio;
    @Autowired
    IncidenciaServicio incidenciaServicio;
    @Autowired
    ReporteServicio reporteServicio;
    @Autowired
    ProductoServicio productoServicio;
    @Autowired
    InventarioServicio inventarioServicio;

    @GetMapping("/tecnico/vista")
    public String empleadoDashboard(Model model, Principal principal) {
        String correo = principal.getName();
        Usuario usuario = usuarioService.findByCorreoOrThrow(correo);
        Tecnico tecnico = tecnicoServicio.findByUsuario(usuario);
        List<Inventario> inventarios = inventarioServicio.listarTodos();
        List<Incidencia> incidencias = incidenciaServicio.listarPorTecnicoEnProceso(tecnico);
        model.addAttribute("incidencias", incidencias);
        model.addAttribute("usuario", usuario);
        model.addAttribute("tecnico", tecnico);
        model.addAttribute("inventarios", inventarios);
        return "tecnico";
    }

    @PostMapping("/tecnico/incidencia/solucionar")
    public String solucionarIncidencia(
            @ModelAttribute Reporte reporte,
            @RequestParam(value = "cantidadReemplazo", required = false) Integer cantidadReemplazo,
            @RequestParam(value = "imagenReporte", required = false) MultipartFile imagen,
            @RequestParam(value = "nuevoEstado", required = false) String nuevoEstado,
            @RequestParam(value = "estado", required = false) String estado,
            Principal principal,
            @RequestParam(value = "tipo", required = false) String tipo,
            RedirectAttributes redirect) throws IOException {

        String correo = principal.getName();
        Usuario usuario = usuarioService.findByCorreoOrThrow(correo);
        Tecnico tecnico = tecnicoServicio.findByUsuario(usuario);

        if ("mantenimiento".equals(estado)) {
            Incidencia incidencia = incidenciaServicio.buscarPorId(reporte.getIncidencia().getId());
            Maquinaria maquinaria = incidencia.getMaquinaria();
            System.out.println(maquinaria.getNombre());
            maquinaria.setEstado("En mantenimiento");
            incidencia.setEstado("En observacion");
            incidenciaServicio.actulizar(incidencia);
            maquinariaServicio.guardar(maquinaria);
            return "redirect:/tecnico/vista";
        } else if ("solucionado".equals(estado)) {
            if ("maquinaria".equals(tipo)) {
                Incidencia incidencia = incidenciaServicio.buscarPorId(reporte.getIncidencia().getId());
                Maquinaria maquinaria = incidencia.getMaquinaria();
                maquinaria.setEstado("Operativa");
                maquinariaServicio.guardar(maquinaria);
            }

        }

        // Si se quiere registrar un reporte (con solución)
        if (imagen != null && !imagen.isEmpty()) {
            String baseRuta = System.getProperty("user.dir") + "/src/main/resources/static/reporte/";
            String nombreArchivo = "Reporte_" + imagen.getOriginalFilename();
            Path rutaFinal = Paths.get(baseRuta + nombreArchivo);
            Files.createDirectories(rutaFinal.getParent());
            reporte.setImagen(nombreArchivo);
            imagen.transferTo(rutaFinal.toFile());
        }

        if ("producto".equals(tipo)) {
            Incidencia incidencia = incidenciaServicio.buscarPorId(reporte.getIncidencia().getId());
            Producto producto = incidencia.getProducto();
            Inventario inventario = inventarioServicio.buscarPorProducto(producto);
            // Verificar si hay suficiente stock
            if (inventario.getStockEnInventario() < cantidadReemplazo) {
                redirect.addFlashAttribute("errorReemplazarProducto", "No hay suficiente cantidad del producto. Consulte el inventario.");
                return "redirect:/tecnico/vista";
            }

            producto.setActivo(true);

            inventario.setStockEnInventario(inventario.getStockEnInventario() - cantidadReemplazo);
            inventarioServicio.guardar(inventario); // O el método correspondiente para actualizar

        } else if ("infraestructura".equals(tipo)) {
            Incidencia incidencia = incidenciaServicio.buscarPorId(reporte.getIncidencia().getId());
            Infraestructura infraestructura = incidencia.getInfraestructura();
            infraestructura.setEnBuenEstado(true);
            System.out.println("Es infraestructura");
            System.out.println("");
            System.out.println("");
            System.out.println("");
            System.out.println("");
            System.out.println("");
            System.out.println("");
            System.out.println("");
        }

        reporteServicio.guardar(reporte);
        redirect.addFlashAttribute("exito", "Incidencia solucionada y reporte registrado.");

        return "redirect:/tecnico/vista";
    }

    @PostMapping("/tecnico/inventario/solicitar")
    public String solicitarInventario(
            @RequestParam("productoId") String productoId,
            @RequestParam("cantidadSolicitada") int cantidadSolicitada,
            RedirectAttributes redirect) {

        Producto producto = productoServicio.buscarPorId(productoId);
        if (producto == null) {
            redirect.addFlashAttribute("errorInventario", "Producto no encontrado.");
            return "redirect:/tecnico/inventario";
        }

        Inventario inventario = inventarioServicio.buscarPorProducto(producto);

        if (inventario == null) {
            // Si no existe inventario, lo inicializamos
            inventario = new Inventario();
            inventario.setProducto(producto);
            inventario.setStockEnInventario(0);
        }

        // Aumentamos el stock
        inventario.setStockEnInventario(inventario.getStockEnInventario() + cantidadSolicitada);
        inventarioServicio.guardar(inventario);

        redirect.addFlashAttribute("exitoInventario", "Solicitud realizada. El inventario ha sido actualizado.");
        return "redirect:/tecnico/vista";
    }

}
