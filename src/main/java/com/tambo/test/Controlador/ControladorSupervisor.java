package com.tambo.test.Controlador;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.tambo.test.Modelos.CategoriaIncidencia;
import com.tambo.test.Modelos.Incidencia;
import com.tambo.test.Modelos.Maquinaria;
import com.tambo.test.Modelos.Reporte;
import com.tambo.test.Modelos.Supervisor;
import com.tambo.test.Modelos.Usuario;
import com.tambo.test.Servicios.CategoriaIncidenciaServicio;
import com.tambo.test.Servicios.IncidenciaServicio;
import com.tambo.test.Servicios.InfraestructuraServicio;
import com.tambo.test.Servicios.MaquinariaServicio;
import com.tambo.test.Servicios.ProductoServicio;
import com.tambo.test.Servicios.ReporteServicio;
import com.tambo.test.Servicios.SupervisorServicio;
import com.tambo.test.Servicios.UsuarioServicios;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class ControladorSupervisor {

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private CategoriaIncidenciaServicio categoriaIncidenciaService;

    @Autowired
    UsuarioServicios usuarioService;

    @Autowired
    SupervisorServicio supervisorServicio;

    @Autowired
    ProductoServicio productoServicio;

    @Autowired
    MaquinariaServicio maquinariaServicio;

    @Autowired
    InfraestructuraServicio infraestructuraServicio;

    @Autowired
    IncidenciaServicio incidenciaServicio;

    @Autowired
    ReporteServicio reporteServicio;

    @GetMapping("/supervisor/vista")
    public String adminDashboard(Model model, Principal principal) {
        String correo = principal.getName();
        Usuario usuario = usuarioService.findByCorreoOrThrow(correo);
        Supervisor supervisor = supervisorServicio.findByUsuario(usuario);
        List<Incidencia> incidencias = incidenciaServicio.obtenerPorSupervisor(supervisor);
        List<Maquinaria> maquinarias = maquinariaServicio.listarPorEstado("Operativa");
        List<Reporte> reportes = reporteServicio.listarPorSupervisor(supervisor.getId());
        Map<String, Integer> conteo = incidenciaServicio.obtenerCantidadPorTurno(incidencias);
        int asignadas = incidenciaServicio.contarIncidenciasAsignadas();
        int noAsignadas = incidenciaServicio.contarIncidenciasNoAsignadas();
         List<Map<String, Object>> incidenciasRapidas = incidenciaServicio.obtenerIncidenciasMasRapidas();
          Map<String, Long> incidenciaPorEstado = incidenciaServicio.contarIncidenciasPorEstado();
        model.addAttribute("usuario", usuario);
        model.addAttribute("supervisor", supervisor);
        model.addAttribute("categorias", categoriaIncidenciaService.listar());
        model.addAttribute("productos", productoServicio.listarActivos());
        model.addAttribute("maquinarias", maquinarias);
        model.addAttribute("infraestructuras", infraestructuraServicio.obtenerInfraestructurasEnBuenEstado());
        model.addAttribute("incidencias", incidencias);
        model.addAttribute("reportes", reportes);
        model.addAttribute("conteoTurnos", conteo);
        model.addAttribute("asignadas", asignadas);
        model.addAttribute("noAsignadas", noAsignadas);
        model.addAttribute("incidenciasRapidas", incidenciasRapidas);
        model.addAttribute("incidenciaPorEstado", incidenciaPorEstado);

        return "supervisor";
    }

    @PostMapping("/supervisor/categorias-incidencia/guardar")
    public String guardarCategoriaDesdeSupervisor(@ModelAttribute("categoria") CategoriaIncidencia categoria,
            RedirectAttributes redirectAttributes) {
        try {
            categoriaIncidenciaService.guardar(categoria);
            redirectAttributes.addFlashAttribute("exitoRegistroCatIncidencia", "✅ Categoría registrada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorRegistroCatIncidencia", "❌ Error al registrar la categoría.");
        }
        return "redirect:/supervisor/vista";
    }

    @PostMapping("/supervisor/categorias-incidencia/eliminar")
    public String eliminarCategoria(@ModelAttribute("id") String id, RedirectAttributes redirectAttributes) {
        try {
            categoriaIncidenciaService.eliminarPorId(id);
            redirectAttributes.addFlashAttribute("exitoEliminarCatIncidencia", "✅ Categoría eliminada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorEliminarCatIncidencia", "❌ Error al eliminar la categoría debe estar relacionado con una incidencia.");
        }
        return "redirect:/supervisor/vista";
    }

    @PostMapping("/supervisor/incidencias/guardar")
    public String guardarIncidencia(
            @ModelAttribute Incidencia incidencia,
            @RequestParam("evidencia") MultipartFile evidencia,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {
            Supervisor supervisor = supervisorServicio.buscarPorCorreo(principal.getName());

            if (!evidencia.isEmpty()) {
                // Obtener ruta base
                String baseRuta = System.getProperty("user.dir") + "/src/main/resources/static/evidencias/";

                // Crear un nombre único (puedes usar UUID o timestamp)
                String nombreArchivo = "Evidencia" + "_" + evidencia.getOriginalFilename();

                // Concatenar ruta final
                Path rutaFinal = Paths.get(baseRuta + nombreArchivo);
                Files.createDirectories(rutaFinal.getParent());
                evidencia.transferTo(rutaFinal.toFile());

                // Setea el nombre o ruta del archivo en el atributo evidenciaIncidencia
                incidencia.setEvidenciaIncidencia(nombreArchivo);
            }

            incidenciaServicio.guardar(incidencia, supervisor);
            redirectAttributes.addFlashAttribute("exitoRegistroIncidencia", "✅ Incidencia registrada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorRegistroIncidencia", "❌ Ocurrió un error al registrar la incidencia." + e);
        }

        return "redirect:/supervisor/vista";
    }

    @GetMapping("/supervisor/reporte/pdf/{id}")
    public <T> void generarPdf(@PathVariable("id") String id, HttpServletResponse response) throws IOException {
        // Establecer tipo de contenido
        response.setContentType("application/pdf");

        // Establecer cabeceras
        response.setHeader("Content-Disposition", "inline; filename=reporte_" + id + ".pdf");

        try {
            // Obtener los datos del reporte
            Reporte reporte = reporteServicio.findById(id); // asegúrate que no sea null

            // Crear documento PDF
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            InputStream imagenStream = getClass().getResourceAsStream("/static/imagenes/logo-samsung.png");
            if (imagenStream == null) {
                throw new IOException("No se encontró la imagen en /static/imagenes/logo-samsung.png");
            }
            byte[] imagenBytes = imagenStream.readAllBytes();
            ImageData imageData = ImageDataFactory.create(imagenBytes);
            Image imagen = new Image(imageData);

// Escalar la imagen (ej. 60x35 px)
            imagen.scaleAbsolute(60, 35);

// Crear una tabla con 2 columnas: imagen izquierda / texto derecha
            float[] anchoColumnas = {1, 3}; // proporción entre columnas
            Table cabecera = new Table(anchoColumnas).useAllAvailableWidth();

// Celda 1: Imagen (alineada arriba a la izquierda)
            Cell celdaImagen = new Cell().add(imagen)
                    .setBorder(Border.NO_BORDER)
                    .setVerticalAlignment(VerticalAlignment.TOP);
            cabecera.addCell(celdaImagen);

// Crear textos a la derecha
            Paragraph idReporte = new Paragraph("ID Reporte: " + reporte.getId())
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(12);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String fechaHora = LocalDateTime.now().format(formatter);
            Paragraph fechaActual = new Paragraph("Fecha: " + fechaHora)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(10);

            Cell celdaTexto = new Cell()
                    .add(idReporte)
                    .add(fechaActual)
                    .setBorder(Border.NO_BORDER)
                    .setVerticalAlignment(VerticalAlignment.TOP);
            cabecera.addCell(celdaTexto);

// Agregar cabecera al documento
            document.add(cabecera);

// Espaciado posterior
            document.add(new Paragraph("\n"));

            // Título centrado "Reporte de Incidencia"
            Paragraph titulo = new Paragraph("Reporte de Incidencia")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(16)
                    .setBold();
            document.add(titulo);

// Espacio debajo del título
            document.add(new Paragraph("\n"));

// Crear tabla de datos básicos (Supervisor, Tienda, Tipo, Nombre, Prioridad)
            Table tablaInfo = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1, 1, 1, 1}))
                    .useAllAvailableWidth()
                    .setMarginTop(10f);

// Fila 1: Supervisor, Tienda, Tipo
            tablaInfo.addCell(new Cell(1, 2).add(
                    new Paragraph()
                            .add(new Text("Supervisor: ").setBold())
                            .add(reporte.getIncidencia().getSupervisor().getUsuario().getNombreCompleto()))
                    .setBorder(Border.NO_BORDER));

            tablaInfo.addCell(new Cell(1, 2).add(
                    new Paragraph()
                            .add(new Text("Tienda: ").setBold())
                            .add(reporte.getIncidencia().getSupervisor().getTienda().getNombre()))
                    .setBorder(Border.NO_BORDER));

            tablaInfo.addCell(new Cell(1, 2).add(
                    new Paragraph()
                            .add(new Text("Tipo: ").setBold())
                            .add(reporte.getIncidencia().getTipo()))
                    .setBorder(Border.NO_BORDER));

            if (reporte.getIncidencia().getTipo().equalsIgnoreCase("producto")) {
                tablaInfo.addCell(new Cell(1, 3).add(
                        new Paragraph()
                                .add(new Text("Nombre: ").setBold())
                                .add(reporte.getIncidencia().getProducto().getNombre()))
                        .setBorder(Border.NO_BORDER));
                tablaInfo.addCell(new Cell(1, 3).add(
                        new Paragraph()
                                .add(new Text("Estado: ").setBold())
                                .add(reporte.getIncidencia().getProducto().getActivo() ? "El producto esta activo" : "El producto no esta activo"))
                        .setBorder(Border.NO_BORDER));

            } else if (reporte.getIncidencia().getTipo().equalsIgnoreCase("maquinaria")) {
                tablaInfo.addCell(new Cell(1, 3).add(
                        new Paragraph()
                                .add(new Text("Nombre: ").setBold())
                                .add(reporte.getIncidencia().getMaquinaria().getNombre()))
                        .setBorder(Border.NO_BORDER));
                tablaInfo.addCell(new Cell(1, 3).add(
                        new Paragraph()
                                .add(new Text("Estado: ").setBold())
                                .add(reporte.getIncidencia().getMaquinaria().getEstado()))
                        .setBorder(Border.NO_BORDER));
            } else if (reporte.getIncidencia().getTipo().equalsIgnoreCase("infraestructura")) {
                tablaInfo.addCell(new Cell(1, 3).add(
                        new Paragraph()
                                .add(new Text("Nombre: ").setBold())
                                .add(reporte.getIncidencia().getInfraestructura().getDescripcion()))
                        .setBorder(Border.NO_BORDER));
                tablaInfo.addCell(new Cell(1, 3).add(
                        new Paragraph()
                                .add(new Text("Estado: ").setBold())
                                .add(reporte.getIncidencia().getInfraestructura().getEnBuenEstado() ? "La infraestructura esta en buen estado" : "La infraestructura no esta en buen estado"))
                        .setBorder(Border.NO_BORDER));
            }
            tablaInfo.addCell(new Cell(1, 3).add(
                    new Paragraph()
                            .add(new Text("Fecha de la incidencia: ").setBold())
                            .add(reporte.getIncidencia().getFechaRegistro()
                                    .toLocalDate()
                                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            ).setBorder(Border.NO_BORDER));
            tablaInfo.addCell(new Cell(1, 3).add(
                    new Paragraph()
                            .add(new Text("Fecha de la solucion: ").setBold())
                            .add(reporte.getFechaRegistro()
                                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            ).setBorder(Border.NO_BORDER));

            tablaInfo.addCell(new Cell(1, 3).add(
                    new Paragraph()
                            .add(new Text("El problema: ").setBold())
                            .add(reporte.getIncidencia().getTitulo()))
                    .setBorder(Border.NO_BORDER));
            tablaInfo.addCell(new Cell(1, 3).add(
                    new Paragraph()
                            .add(new Text("La solucion: ").setBold())
                            .add(reporte.getDescripcion()))
                    .setBorder(Border.NO_BORDER));

            tablaInfo.addCell(new Cell(1, 3)
                    .add(new Paragraph("Imagen de evidencia:").setBold())
                    .setBorder(Border.NO_BORDER));
            tablaInfo.addCell(new Cell(1, 3)
                    .add(new Paragraph("Imagen de la solucion:").setBold())
                    .setBorder(Border.NO_BORDER));

            InputStream is = getClass().getResourceAsStream("/static/evidencias/" + reporte.getIncidencia().getEvidenciaIncidencia());
            if (is != null) {
                byte[] bytes = is.readAllBytes();
                ImageData data = ImageDataFactory.create(bytes);
                Image imagenreport = new Image(data).setAutoScale(true);

                tablaInfo.addCell(new Cell(1, 3)
                        .add(imagenreport)
                        .setBorder(Border.NO_BORDER));
            }

            InputStream ret = getClass().getResourceAsStream("/static/reporte/" + reporte.getImagen());
            if (ret != null) {
                byte[] bytes = ret.readAllBytes();
                ImageData data = ImageDataFactory.create(bytes);
                Image imagenresolu = new Image(data).setAutoScale(true);

                tablaInfo.addCell(new Cell(1, 3)
                        .add(imagenresolu)
                        .setBorder(Border.NO_BORDER));
            }

            document.add(tablaInfo);

            document.close();

            // Escribir PDF al response
            response.getOutputStream().write(out.toByteArray());
            response.getOutputStream().flush();
        } catch (Exception e) {
            // Enviar error legible al navegador
            response.setContentType("text/plain");
            response.getWriter().write("Error al generar PDF: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private Cell celdaSinBorde(String texto, boolean enNegrita) {
        Paragraph p = new Paragraph(texto);
        if (enNegrita) {
            p.setBold();
        }
        return new Cell().add(p).setBorder(Border.NO_BORDER);
    }

}
