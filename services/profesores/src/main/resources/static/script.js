const API_URL = "http://localhost:8083"; // Cambia si usas otro puerto

const DOMINIOS_PERMITIDOS = ["gmail.com", "hotmail.com", "outlook.com", "edu.mx", "yahoo.com"];

function limpiarCampos(ids) {
    ids.forEach(id => document.getElementById(id).value = '');
}

function esCorreoValido(correo) {
    if (!correo.includes("@")) return false;
    const dominio = correo.split("@")[1];
    return DOMINIOS_PERMITIDOS.includes(dominio);
}

function registrarProfesor() {
    const numEmpleado = document.getElementById("prof-num")?.value;
    const nombre = document.getElementById("prof-nombre").value.trim();
    const puesto = document.getElementById("prof-puesto").value.trim();
    const contra = document.getElementById("prof-contra").value;
    const correo = document.getElementById("prof-correo").value.trim();

    if (!nombre || !puesto || !contra || !correo) {
        alert("❌ Todos los campos son obligatorios.");
        return;
    }

    if (!esCorreoValido(correo)) {
        alert("❌ Ingresa un correo con dominio válido: " + DOMINIOS_PERMITIDOS.join(", "));
        return;
    }

    const data = { nombre, puesto, contrasena: contra, correo };

    fetch(`${API_URL}/profesores`, {
        method: "POST",
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    })
        .then(r => r.json())
        .then(ok => {
            alert(ok ? "✅ Profesor registrado" : "❌ Ya existe");
            limpiarCampos(["prof-nombre", "prof-puesto", "prof-contra", "prof-correo"]);
        })
        .catch(error => {
            alert("❌ Error de conexión.");
            console.error(error);
        });
}

function actualizarProfesor() {
    const numEmpleado = document.getElementById("prof-dto-num").value.trim();
    const contra = document.getElementById("prof-dto-contra").value;
    const correo = document.getElementById("prof-dto-correo").value.trim();

    if (!numEmpleado || !contra || !correo) {
        alert("❌ Todos los campos son obligatorios.");
        return;
    }

    const numeroEmpleado = parseInt(numEmpleado);
    if (isNaN(numeroEmpleado)) {
        alert("❌ El número de empleado debe ser numérico.");
        return;
    }

    if (!esCorreoValido(correo)) {
        alert("❌ Ingresa un correo con dominio válido: " + DOMINIOS_PERMITIDOS.join(", "));
        return;
    }

    const data = { numeroEmpleado, contrasena: contra, correo };

    fetch(`${API_URL}/profesores`, {
        method: "PUT",
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    })
        .then(r => r.json())
        .then(ok => {
            alert(ok ? "✅ Actualizado" : "❌ Error al actualizar");
            limpiarCampos(["prof-dto-num", "prof-dto-contra", "prof-dto-correo"]);
        })
        .catch(error => {
            alert("❌ Error de conexión.");
            console.error(error);
        });
}

function registrarAlumno() {
    const calif = document.getElementById("alu-calif").value.trim();

    if (!calif) {
        alert("❌ Todos los campos son obligatorios.");
        return;
    }

    const calificacion = parseFloat(calif);

    if (isNaN(calificacion)) {
        alert("❌ La calificación debe ser numérica.");
        return;
    }

    const data = { calificacion };

    fetch(`${API_URL}/alumnos`, {
        method: "POST",
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    })
        .then(r => r.json())
        .then(ok => {
            alert(ok ? "✅ Alumno registrado" : "❌ Ya existe");
            limpiarCampos(["alu-calif"]);
        })
        .catch(error => {
            alert("❌ Error de conexión.");
            console.error(error);
        });
}

function calificarAlumno() {
    const id = document.getElementById("alu-c-id").value.trim();
    const calif = document.getElementById("alu-c-calif").value.trim();

    if (!id || !calif) {
        alert("❌ Todos los campos son obligatorios.");
        return;
    }

    const idAlumno = parseInt(id);
    const calificacion = parseFloat(calif);

    if (isNaN(idAlumno) || isNaN(calificacion)) {
        alert("❌ ID y calificación deben ser numéricos.");
        return;
    }

    const data = { idAlumno, calificacion };

    fetch(`${API_URL}/profesores/calificar`, {
        method: "PUT",
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    })
        .then(r => r.json())
        .then(ok => {
            alert(ok ? "✅ Calificado con éxito" : "❌ Error al calificar");
            limpiarCampos(["alu-c-id", "alu-c-calif"]);
        })
        .catch(error => {
            alert("❌ Error de conexión.");
            console.error(error);
        });
}

function buscarAlumno() {
    const id = document.getElementById("alu-b-id").value.trim();
    const resultado = document.getElementById("resultado");

    if (!id) {
        alert("❌ Ingresa un ID de alumno.");
        return;
    }

    const idAlumno = parseInt(id);
    if (isNaN(idAlumno)) {
        alert("❌ El ID debe ser numérico.");
        return;
    }

    fetch(`${API_URL}/alumnos/${idAlumno}`)
        .then(r => {
            if (!r.ok) throw new Error("Alumno no encontrado");
            return r.json();
        })
        .then(alumno => {
            mostrarAlumnoEnTabla(alumno);
            alert("✅ Alumno encontrado.");
        })
        .catch(() => {
            resultado.innerHTML = "<p style='color:red;'>❌ Alumno no encontrado.</p>";
            alert("❌ Alumno no encontrado.");
        })
        .finally(() => {
            limpiarCampos(["alu-b-id"]);
        });
}

function mostrarAlumnoEnTabla(alumno) {
    const resultado = document.getElementById("resultado");
    resultado.innerHTML = `
        <table border="1" cellpadding="5" cellspacing="0">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Calificación</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>${alumno.idAlumno}</td>
                    <td>${alumno.calificacion}</td>
                </tr>
            </tbody>
        </table>
    `;
}
