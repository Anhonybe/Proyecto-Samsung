const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');



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

//Cuando asigne un tecnico a la incidencia
function asignarTecnico(selectElement) {
    const tecnicoId = selectElement.value;
    const incidenciaId = selectElement.getAttribute('data-id');

    fetch('/mantenimiento/asignar-tecnico', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]')?.getAttribute('content') || ''
        },
        body: JSON.stringify({
            tecnicoId: tecnicoId,
            incidenciaId: incidenciaId
        })
    })
        .then(async response => {
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText);
            }
            return response.json();
        })
        .then(data => {
            // Actualizar UI
            const estadoTd = document.getElementById('estado-' + incidenciaId);
            if (estadoTd) {
                estadoTd.textContent = 'En proceso';
            }

            selectElement.disabled = true;
            selectElement.classList.add('asignado');

            alert('✅ Técnico asignado con éxito.');
        })
        .catch(error => {
            console.error("Error:", error);
            alert("❌ Error al asignar técnico: " + error.message);
        });
}


//Actulizar el estado en la toma de decision
let maquinariaIdSeleccionada = null;
let estadoNuevoSeleccionado = null;

// Función que se llama al hacer clic en Reparar o Reemplazar
function mostrarModalConfirmacion(id, nuevoEstado) {
    maquinariaIdSeleccionada = id;
    estadoNuevoSeleccionado = nuevoEstado;

    // Actualiza el texto dentro del modal
    document.getElementById('estadoConfirmar').textContent = nuevoEstado;

    // Abre el modal
    const modal = new bootstrap.Modal(document.getElementById('modalConfirmacion'));
    modal.show();
}

// Acción cuando el usuario confirma en el modal
document.getElementById('btnConfirmarCambio').addEventListener('click', function () {
    // Obtener los valores del formulario
    const descripcion = document.querySelector('textarea[name="descripcion"]').value.trim();
    const fechaRegistro = document.querySelector('input[name="fechaRegistro"]').value;
    const imagenReporte = document.querySelector('input[name="imagenReporte"]').files[0];

    // Validación
    if (!descripcion || !fechaRegistro || !imagenReporte) {
        alert('⚠️ Por favor, completa todos los campos del formulario.');
        return;
    }

    // Crear FormData para enviar como multipart/form-data
    const formData = new FormData();
    formData.append('descripcion', descripcion);
    formData.append('fechaRegistro', fechaRegistro);
    formData.append('imagenReporte', imagenReporte);
    formData.append('maquinariaId', maquinariaIdSeleccionada);
    formData.append('nuevoEstado', estadoNuevoSeleccionado);

    // Enviar datos al backend
    fetch('/mantenimiento/incidencia/actualizar-estado', {
        method: 'POST',
        headers: {
        [csrfHeader]: csrfToken // agrega el token al encabezado
    },
        body: formData
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => { throw new Error(text); });
            }
            return response.json();
        })
        .then(data => {
            alert('✅ Estado actualizado correctamente a: ' + estadoNuevoSeleccionado);

            // Cambia el contenido del botón
            const accionesTd = document.getElementById('acciones-' + maquinariaIdSeleccionada);
            if (accionesTd) {
                accionesTd.innerHTML = '<span class="text-success fw-bold">✔ Decisión tomada</span>';
            }

            // Actualiza el estado si hay una celda con id correspondiente
            const estadoTd = document.getElementById('estado-' + maquinariaIdSeleccionada);
            if (estadoTd) {
                estadoTd.textContent = estadoNuevoSeleccionado;
            }

            // Cierra el modal
            bootstrap.Modal.getInstance(document.getElementById('modalConfirmacion')).hide();

            // Limpia campos
            document.querySelector('textarea[name="descripcion"]').value = '';
            document.querySelector('input[name="fechaRegistro"]').value = '';
            document.querySelector('input[name="imagenReporte"]').value = '';

            maquinariaIdSeleccionada = null;
            estadoNuevoSeleccionado = null;
        })
        .catch(error => {
            console.error("❌ Error al actualizar estado:", error);
            alert("❌ Error: " + error.message);
        });
});




