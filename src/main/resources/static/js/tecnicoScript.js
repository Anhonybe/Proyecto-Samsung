// Función para mostrar secciones
function showSection(sectionId) {
    // Ocultar todas las secciones
    const sections = document.querySelectorAll('.section');
    sections.forEach(section => section.classList.remove('active'));

    // Mostrar la sección seleccionada
    document.getElementById(sectionId).classList.add('active');

    // Actualizar botones de navegación
    const navButtons = document.querySelectorAll('.nav-btn');
    navButtons.forEach(btn => btn.classList.remove('active'));
    event.target.classList.add('active');
}

//Para el detalle de incidencia
document.addEventListener("DOMContentLoaded", () => {
    const modalDetalle = document.getElementById("modal-detalle");

    modalDetalle.addEventListener("show.bs.modal", function (event) {
        const button = event.relatedTarget;

        document.getElementById("detalle-id").textContent = button.getAttribute("data-id");
        document.getElementById("detalle-titulo").textContent = button.getAttribute("data-titulo");
        document.getElementById("detalle-categoria").textContent = button.getAttribute("data-categoria");
        document.getElementById("detalle-prioridad").textContent = button.getAttribute("data-prioridad");
        document.getElementById("detalle-estado").textContent = button.getAttribute("data-estado");
        document.getElementById("detalle-fecha").textContent = button.getAttribute("data-fecha");
        document.getElementById("detalle-supervisor").textContent = button.getAttribute("data-supervisor");
        document.getElementById("detalle-tipo").textContent = button.getAttribute("data-tipo");
        document.getElementById("detalle-objeto").textContent = button.getAttribute("data-objeto");

        const evidencia = button.getAttribute("data-evidencia");
        const evidenciaImg = document.getElementById("detalle-evidencia");
        if (evidencia) {
            evidenciaImg.src = `/evidencias/${evidencia}`; // ajusta la ruta real
            evidenciaImg.style.display = "block";
        } else {
            evidenciaImg.style.display = "none";
        }
    });
});




function mostrarFormularioSegunTipo(btn) {
    const tipo = btn.dataset.tipo;
    const id = btn.dataset.id;
    const nombproduct = btn.dataset.nombproducto;

    console.log(nombproduct); // Puedes quitar esto si ya no lo necesitas

    const contenido = document.getElementById("contenidoSolucion");
    document.getElementById("idIncidenciaInput").value = id;

    let html = '';

    if (tipo === 'maquinaria') {
        html = `
            <input type="hidden" name="incidencia.id" value="${id}" />
            <label>¿Qué desea hacer?</label>
            <select class="form-control mb-3" name="estado" onchange="toggleFormularioMaquinaria(this.value)">
        <option value="">Seleccione</option>
        <option value="mantenimiento">Enviar al área de mantenimiento</option>
        <option value="solucionado">Se solucionó correctamente</option>
    </select>
            <div id="extraMaquinaria"></div>
        `;
    } else if (tipo === 'producto') {
        html = `
            <input type="hidden" name="incidencia.id" value="${id}" />
            <div class="mb-2">
                <label>Solución:</label>
                <textarea name="descripcion" class="form-control" required></textarea>
            </div>
            <div class="mb-2">
                <label>Fecha de solución:</label>
                <input type="date" name="fechaRegistro" class="form-control" required>
            </div>
            <div class="mb-2">
                <label>Producto a reemplazar:</label>
                <input type="text" value="${nombproduct}" class="form-control" readonly>
            </div>
            <div class="mb-2">
                <label>Cantidad reemplazada:</label>
                <input type="number" name="cantidadReemplazo" class="form-control" required>
            </div>
            <div class="mb-2">
                <label>Imagen de reporte:</label>
                <input type="file" name="imagenReporte" class="form-control" required>
            </div>
            <input type="hidden" name="tipo" value="producto" />
        `;
    } else if (tipo === 'infraestructura') {
        html = `
            <input type="hidden" name="incidencia.id" value="${id}" />
            <div class="mb-2">
                <label>Solución:</label>
                <textarea name="descripcion" class="form-control" required></textarea>
            </div>
            <div class="mb-2">
                <label>Fecha de solución:</label>
                <input type="date" name="fechaRegistro" class="form-control" required>
            </div>
            <div class="mb-2">
                <label>Imagen de reporte:</label>
                <input type="file" name="imagenReporte" class="form-control" required>
            </div>
            <input type="hidden" name="tipo" value="infraestructura" />
        `;
    }

    contenido.innerHTML = html;
}

function toggleFormularioMaquinaria(valor) {
    const contenedor = document.getElementById("extraMaquinaria");

    if (valor === 'mantenimiento') {
        // Solo cambiamos el estado, no se necesita solución ni imagen
        contenedor.innerHTML = `
            <input type="hidden" name="nuevoEstado" value="En mantenimiento">
        `;
    } else if (valor === 'solucionado') {
        // Mostrar los campos para solucionar
        contenedor.innerHTML = `
            <div class="mb-2">
                <label>Solución:</label>
                <textarea name="descripcion" class="form-control" required></textarea>
            </div>
            <div class="mb-2">
                <label>Fecha de solución:</label>
                <input type="date" name="fechaRegistro" class="form-control" required>
            </div>
            <div class="mb-2">
                <label>Imagen de reporte:</label>
                <input type="file" name="imagenReporte" class="form-control" required>
            </div>
            <input type="hidden" name="tipo" value="maquinaria" />
        `;
    } else {
        contenedor.innerHTML = "";
    }
}

