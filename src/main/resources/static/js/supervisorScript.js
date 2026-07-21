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

    // Redibujar gráficos si se muestra la sección de gráficos
    if (sectionId === 'graficos') {
        setTimeout(drawCharts, 100);
    }
}

function abrirModalCatIncidencia() {
    document.getElementById('modalCategoria').style.display = 'block';
    document.getElementById("nombreCategoria").value = "";
    document.getElementById("descripcionCategoria").value = "";
    document.body.style.overflow = 'hidden'; // ❌ desactiva scroll fondo
}

function cerrarModalCatIncidencia() {
    document.getElementById('modalCategoria').style.display = 'none';
    document.body.style.overflow = 'auto'; // ✅ reactiva scroll fondo
}

window.onclick = function (event) {
    const modal = document.getElementById('modalCategoria');
    if (event.target === modal) {
        cerrarModalCatIncidencia();
    }
}

function abrirModalIncidencia() {
    document.getElementById('modalIncidencia').style.display = 'block';
    document.body.style.overflow = 'hidden'; // ❌ desactiva scroll fondo
}

function cerrarModalIncidencia() {
    document.getElementById('modalIncidencia').style.display = 'none';
    document.body.style.overflow = 'auto'; // ✅ reactiva scroll fondo
}

window.onclick = function (event) {
    const modal = document.getElementById('modalIncidencia');
    if (event.target === modal) {
        cerrarModalIncidencia();
    }
}


google.charts.load("current", { packages: ["corechart"] });
google.charts.setOnLoadCallback(drawChart);

function drawChart() {
    const data = google.visualization.arrayToDataTable([
        ['Estado', 'Cantidad'],
        ['Asignado', 30],
        ['No Asignado', 15]
    ]);

    const options = {
        pieHole: 0, // Si lo quieres tipo dona usa 0.4
        colors: ['#4CAF50', '#FFC107'],
        backgroundColor: 'transparent',
        titleTextStyle: { color: '#C2185B', fontSize: 16 },
        legend: { position: 'right', textStyle: { color: '#333' } }
    };

    const chart = new google.visualization.PieChart(document.getElementById('columnchart_values'));
    chart.draw(data, options);
}



function abrirModalEditarCategoria(btn) {
    const id = btn.getAttribute("data-id");
    const nombre = btn.getAttribute("data-nombre");
    const descripcion = btn.getAttribute("data-descripcion");

    document.getElementById("categoriaId").value = id;
    document.getElementById("nombreCategoria").value = nombre;
    document.getElementById("descripcionCategoria").value = descripcion;

    document.getElementById("modalCategoria").style.display = "block";
}


/*Datos para el select*/
function mostrarSelectorRelacionado() {
    const tipo = document.getElementById("tipo").value;

    document.getElementById("selectProducto").style.display = "none";
    document.getElementById("selectMaquinaria").style.display = "none";
    document.getElementById("selectInfraestructura").style.display = "none";

    if (tipo === "producto") {
        document.getElementById("selectProducto").style.display = "block";
    } else if (tipo === "maquinaria") {
        document.getElementById("selectMaquinaria").style.display = "block";
    } else if (tipo === "infraestructura") {
        document.getElementById("selectInfraestructura").style.display = "block";
    }
}


//filtros a la tabla incidencia
function aplicarFiltros() {
    const tituloFiltro = document.getElementById('buscar-reporte').value.toLowerCase();
    const categoriaFiltro = document.getElementById('filtro-categoria').value.toLowerCase();
    const estadoFiltro = document.getElementById('filtro-estado').value.toLowerCase();
    const fechaDesde = document.getElementById('fecha-desde').value;
    const fechaHasta = document.getElementById('fecha-hasta').value;

    const filas = document.querySelectorAll('#tabla-incidencias tr');

    filas.forEach(fila => {
        const titulo = fila.getAttribute('data-titulo');
        const categoria = fila.getAttribute('data-categoria');
        const estado = fila.getAttribute('data-estado');
        const fecha = fila.getAttribute('data-fecha');

        let mostrar = true;

        if (tituloFiltro && !titulo.includes(tituloFiltro)) mostrar = false;
        if (categoriaFiltro && categoria !== categoriaFiltro) mostrar = false;
        if (estadoFiltro && estado !== estadoFiltro) mostrar = false;
        if (fechaDesde && fecha < fechaDesde) mostrar = false;
        if (fechaHasta && fecha > fechaHasta) mostrar = false;

        fila.style.display = mostrar ? '' : 'none';
    });
}



