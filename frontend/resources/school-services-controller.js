// Updated controlador.js (await loadData in assign and remove)

const authService = 'http://localhost:8001';
const studentService = 'http://localhost:8002';
const schoolServicesService = "http://localhost:8003/servicios-escolares/api";
const humanResourcesService = "http://localhost:8004/api/v1";

// For professors, since no data, JS doesn't fill detallesProfesoresBody yet

// New controlador.js (combined and expanded from controladorGrupo and controladorAlumno)

let carreras = [];
let grupos = [];
let alumnos = [];
let profesores = [];
let currentGrupoId = null; // For modals

async function loadData() {
  try {
    const resCarreras = await fetch(`${schoolServicesService}/carrera/getAllCarreras`, {
      headers: {
        'Authorization': `Bearer ${sessionStorage.getItem('token')}`
      }
    });
    if (resCarreras.status === 401 || resCarreras.status === 403) {
      alert('Su sesión ha expirado. Por favor, inicie sesión de nuevo.');
      window.location.href = 'index.html';
      return;
    }
    carreras = await resCarreras.json();

    const resGrupos = await fetch(`${schoolServicesService}/grupo/getAllGrupos`, {
      headers: {
        'Authorization': `Bearer ${sessionStorage.getItem('token')}`
      }
    });
    if (resGrupos.status === 401 || resGrupos.status === 403) {
      alert('Su sesión ha expirado. Por favor, inicie sesión de nuevo.');
      window.location.href = 'index.html';
      return;
    }
    grupos = await resGrupos.json();

    const resAlumnos = await fetch(`${studentService}/students`, {
      headers: {
        'Authorization': `Bearer ${sessionStorage.getItem('token')}`
      }
    });
    if (resAlumnos.status === 401 || resAlumnos.status === 403) {
      alert('Su sesión ha expirado. Por favor, inicie sesión de nuevo.');
      window.location.href = 'index.html';
      return;
    }
    const data = await resAlumnos.json();
    alumnos = data.students;

    const resProfesores = await fetch(`${humanResourcesService}/profesores`, {
      headers: {
        'Authorization': `Bearer ${sessionStorage.getItem('token')}`
      }
    });
    if (resProfesores.status === 401 || resProfesores.status === 403) {
      alert('Su sesión ha expirado. Por favor, inicie sesión de nuevo.');
      window.location.href = 'index.html';
      return;
    }
    profesores = await resProfesores.json();

    populateSelects();
    renderAlumnosTable();
    renderGruposTable();
  } catch (error) {
    console.error("Error loading data:", error);
  }
}

function populateSelects() {
  // Carreras for all selects
  const addCarrera = document.getElementById('addCarrera');
  const editCarrera = document.getElementById('editCarrera');
  const addCarreraGrupo = document.getElementById('addCarreraGrupo');
  const editCarreraGrupo = document.getElementById('editCarreraGrupo');

  let options = '<option value="">Seleccione una carrera</option>';
  carreras.forEach(c => {
    options += `<option value="${c.id_carrera}">${c.nombre_carrera}</option>`;
  });

  addCarrera.innerHTML = options;
  editCarrera.innerHTML = options;
  addCarreraGrupo.innerHTML = options;
  editCarreraGrupo.innerHTML = options;

}

function renderAlumnosTable() {
  const tableBody = document.getElementById('alumnosTableBody');
  let rows = '';
  if (alumnos.length === 0) {
    rows = '<tr><td colspan="4">No hay alumnos registrados.</td></tr>';
  }
  alumnos.forEach(a => {
    rows += `
      <tr>
        <td>${a.id}</td>
        <td>${a.name}</td>
        <td>${a.major}</td>
        <td class="action-buttons">
          <button class="btn btn-sm btn-outline-primary" onclick="editAlumno(${a.id})"><i class="fas fa-edit"></i></button>
          <button class="btn btn-sm btn-outline-danger" onclick="deleteAlumno(${a.id})"><i class="fas fa-trash-alt"></i></button>
        </td>
      </tr>
    `;
  });
  tableBody.innerHTML = rows;
}

function renderGruposTable() {
  const tableBody = document.getElementById('gruposTableBody');
  let rows = '';
  if (grupos.length === 0) {
    rows = '<tr><td colspan="4">No hay grupos registrados.</td></tr>';
    tableBody.innerHTML = rows;
    return;
  }
  const promises = grupos.map(g => (
    fetch(`${schoolServicesService}/alumnogrupo/getNumeroAlumnosByGrupoId/${g.id_grupo}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${sessionStorage.getItem('token')}`
      }
    })
  ));
  Promise.all(promises)
    .then(responses => Promise.all(responses.map(r => r.json())))
    .then(data => {
      const numAlumnos = data.map(d => d.total);
      grupos.forEach((g, i) => {
        rows += `
          <tr>
            <td>${g.nombre_grupo}</td>
            <td>${g.carrera ? g.carrera.nombre_carrera : 'N/A'}</td>
            <td>${numAlumnos[i] || 0}</td>
            <td class="action-buttons">
              <button class="btn btn-sm btn-outline-info" onclick="viewGrupoDetails(${g.id_grupo})"><i class="fas fa-eye"></i></button>
              <button class="btn btn-sm btn-outline-primary" onclick="editGrupo(${g.id_grupo})"><i class="fas fa-edit"></i></button>
              <button class="btn btn-sm btn-outline-danger" onclick="deleteGrupo(${g.id_grupo})"><i class="fas fa-trash-alt"></i></button>
            </td>
          </tr>
        `;
        tableBody.innerHTML = rows;
      });
    })
    .catch(error => {
      console.error("Error fetching alumno counts:", error);
    });
}

async function saveAlumno() {
  const nombre = document.getElementById('addNombre').value;
  const carreraInput = document.getElementById('addCarrera');
  const carrera = carreraInput.options[carreraInput.selectedIndex].text;

  try {
    const res = await fetch(`${studentService}/students`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('token')}`
      },
      body: JSON.stringify({ name: nombre, major: carrera })
    });
    if (res.status === 401 || res.status === 403) {
      alert('Su sesión ha expirado. Por favor, inicie sesión de nuevo.');
      window.location.href = 'index.html';
      return;
    }
    if (res.ok) {
      loadData();
      bootstrap.Modal.getInstance(document.getElementById('modalAgregarAlumno')).hide();
    }
  } catch (error) {
    console.error("Error saving alumno:", error);
  }
}

function editAlumno(matricula) {
  const alumno = alumnos.find(a => a.id === matricula);
  document.getElementById('editMatricula').value = alumno.id;
  document.getElementById('editNombre').value = alumno.name;
  const carreraInput = document.getElementById('editCarrera');
  for (let i = 0; i < carreraInput.options.length; i++) {
    if (carreraInput.options[i].text === alumno.major) {
      carreraInput.selectedIndex = i;
      break;
    }
  }

  bootstrap.Modal.getOrCreateInstance(document.getElementById('modalEditarAlumno')).show();
}

async function updateAlumno() {
  const matricula = parseInt(document.getElementById('editMatricula').value);
  const nombre = document.getElementById('editNombre').value;
  const carreraInput = document.getElementById('editCarrera');
  const carrera = carreraInput.options[carreraInput.selectedIndex].text;

  try {
    const res = await fetch(`${studentService}/students/${matricula}`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('token')}`
      },
      body: JSON.stringify({ name: nombre, major: carrera })
    });
    if (res.status === 401 || res.status === 403) {
      alert('Su sesión ha expirado. Por favor, inicie sesión de nuevo.');
      window.location.href = 'index.html';
      return;
    }
    if (res.ok) {
      loadData();
      bootstrap.Modal.getInstance(document.getElementById('modalEditarAlumno')).hide();
    }
  } catch (error) {
    console.error("Error updating alumno:", error);
  }
}

async function deleteAlumno(matricula) {
  if (confirm("¿Eliminar alumno?")) {
    try {
      const res = await fetch(`${studentService}/students/${matricula}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${sessionStorage.getItem('token')}`
        }
      });
      if (res.status === 401 || res.status === 403) {
        alert('Su sesión ha expirado. Por favor, inicie sesión de nuevo.');
        window.location.href = 'index.html';
        return;
      }
      if (res.ok) {
        loadData();
      }
    } catch (error) {
      console.error("Error deleting alumno:", error);
    }
  }
}

async function saveGrupo() {
  const nombreGrupo = document.getElementById('addNombreGrupo').value;
  const carreraId = parseInt(document.getElementById('addCarreraGrupo').value);

  const grupo = {
    nombre_grupo: nombreGrupo,
    estatus: 1,
    carrera: { id_carrera: carreraId }
  };

  try {
    const res = await fetch(`${schoolServicesService}/grupo/addGrupo`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('token')}`
      },
      body: JSON.stringify(grupo)
    });
    if (res.status === 401 || res.status === 403) {
      alert('Su sesión ha expirado. Por favor, inicie sesión de nuevo.');
      window.location.href = 'index.html';
      return;
    }
    if (res.ok) {
      loadData();
      bootstrap.Modal.getInstance(document.getElementById('modalAgregarGrupo')).hide();
    }
  } catch (error) {
    console.error("Error saving grupo:", error);
  }
}

function editGrupo(id) {
  const grupo = grupos.find(g => g.id_grupo === id);
  document.getElementById('editIdGrupo').value = grupo.id_grupo;
  document.getElementById('editNombreGrupo').value = grupo.nombre_grupo;
  document.getElementById('editCarreraGrupo').value = grupo.carrera ? grupo.carrera.id_carrera : '';

  bootstrap.Modal.getOrCreateInstance(document.getElementById('modalEditarGrupo')).show();
}

async function updateGrupo() {
  const id = parseInt(document.getElementById('editIdGrupo').value);
  const nombreGrupo = document.getElementById('editNombreGrupo').value;
  const carreraId = parseInt(document.getElementById('editCarreraGrupo').value);

  const grupo = {
    id_grupo: id,
    nombre_grupo: nombreGrupo,
    estatus: 1,
    carrera: { id_carrera: carreraId }
  };

  try {
    const res = await fetch(`${schoolServicesService}/grupo/updateGrupo`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('token')}`
      },
      body: JSON.stringify(grupo)
    });
    if (res.status === 401 || res.status === 403) {
      alert('Su sesión ha expirado. Por favor, inicie sesión de nuevo.');
      window.location.href = 'index.html';
      return;
    }
    if (res.ok) {
      loadData();
      bootstrap.Modal.getInstance(document.getElementById('modalEditarGrupo')).hide();
    }
  } catch (error) {
    console.error("Error updating grupo:", error);
  }
}

async function deleteGrupo(id) {
  if (confirm("¿Eliminar grupo?")) {
    try {
      const res = await fetch(`${schoolServicesService}/grupo/deleteGrupo/${id}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${sessionStorage.getItem('token')}`
        }
      });
      if (res.status === 401 || res.status === 403) {
        alert('Su sesión ha expirado. Por favor, inicie sesión de nuevo.');
        window.location.href = 'index.html';
        return;
      }
      if (res.ok) {
        loadData();
      }
    } catch (error) {
      console.error("Error deleting grupo:", error);
    }
  }
}

function viewGrupoDetails(id) {
  currentGrupoId = id;
  const grupo = grupos.find(g => g.id_grupo === id);
  document.getElementById('detallesTitle').textContent = `Detalles de ${grupo.nombre_grupo}`;

  fetch(`${schoolServicesService}/alumnogrupo/getAlumnosByGrupoId/${id}`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${sessionStorage.getItem('token')}`
    }
  }).then(response => {
    if (!response.ok) {
      throw new Error('Error fetching alumnos');
    }
    return response.json();
  }).then(data => {
    const alumnosEnGrupo = data;
    const tableBody = document.getElementById('detallesAlumnosBody');
    let rows = '';
    if (alumnosEnGrupo.length === 0) {
      rows = '<tr><td colspan="3">No hay ningún alumno en el grupo.</td></tr>';
    } else {
      alumnosEnGrupo.forEach(a => {
        rows += `
          <tr>
            <td>${a.matricula}</td>
            <td>${a.alumnoNombre}</td>
            <td>
              <button class="btn btn-sm btn-outline-danger" onclick="removeAlumnoFromGroup(${a.id})"><i class="fas fa-user-minus"></i> Remover</button>
            </td>
          </tr>
        `;
      });
    }
    tableBody.innerHTML = rows;

    // Populate assign select with alumnos sin grupo
    const asignarSelect = document.getElementById('asignarAlumnoSelect');
    let options = '<option value="">Seleccione un alumno</option>';
    const alumnosSinGrupo = alumnos.filter(a => !alumnosEnGrupo.some(ag => ag.matricula === a.id));
    alumnosSinGrupo.forEach(a => {
      options += `<option value="${a.id}">${a.name} (${a.id})</option>`;
    });
    asignarSelect.innerHTML = options;

    fetch(`${schoolServicesService}/grupoprofesor/getProfesoresByGrupoId/${id}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${sessionStorage.getItem('token')}`
      }
    }).then(response => {
      if (!response.ok) {
        throw new Error('Error fetching grupos');
      }
      return response.json();
    }).then(data => {
      const profesoresEnGrupo = data;
      const tableBodyProf = document.getElementById('detallesProfesoresBody');
      let rowsProf = '';
      if (profesoresEnGrupo.length === 0) {
        rowsProf = '<tr><td colspan="3">No hay ningún profesor asignado al grupo.</td></tr>';
      } else {
        profesoresEnGrupo.forEach(p => {
          rowsProf += `
            <tr>
              <td>${p.profesorId}</td>
              <td>${p.profesorNombre}</td>
              <td>
                <button class="btn btn-sm btn-outline-danger" onclick="removeProfesorFromGroup(${p.id})"><i class="fas fa-user-minus"></i> Remover</button>
              </td>
            </tr>
          `;
        });
      }
      tableBodyProf.innerHTML = rowsProf;

      const asignarProfesorSelect = document.getElementById('asignarProfesorSelect');
      let optionsProf = '<option value="">Seleccione un profesor</option>';
      const profesoresSinGrupo = profesores.filter(p => !profesoresEnGrupo.some(pg => pg.profesorId === p.numeroEmpleado));
      profesoresSinGrupo.forEach(p => {
        optionsProf += `<option value="${p.numeroEmpleado}">${p.nombre} ${p.apellido} (${p.numeroEmpleado})</option>`;
      });
      asignarProfesorSelect.innerHTML = optionsProf;

      bootstrap.Modal.getOrCreateInstance(document.getElementById('modalDetallesGrupo')).show();
    }).catch(error => {
      console.error("Error fetching alumnos:", error);
    });
  }).catch(error => {
    console.error("Error fetching alumnos:", error);
  });

}

async function assignAlumnoToGroup() {
  const select = document.getElementById('asignarAlumnoSelect');
  const matricula = parseInt(select.value);
  const alumnoNombre = select.options[select.selectedIndex].text.split(' (')[0];
  if (matricula) {
    try {
      const res = await fetch(`${schoolServicesService}/alumnogrupo/addAlumnoGrupo`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${sessionStorage.getItem('token')}`
        },
        body: JSON.stringify({ matricula, alumnoNombre, grupoId: currentGrupoId })
      });
      if (res.status === 401 || res.status === 403) {
        alert('Su sesión ha expirado. Por favor, inicie sesión de nuevo.');
        window.location.href = 'index.html';
        return;
      }
      if (res.ok) {
        loadData();
        bootstrap.Modal.getInstance(document.getElementById('modalAsignarAlumno')).hide();
        viewGrupoDetails(currentGrupoId); // Refresh details
      }
    } catch (error) {
      console.error("Error assigning:", error);
    }
  }
}

async function removeAlumnoFromGroup(id) {
  if (confirm("¿Remover alumno del grupo?")) {
    try {
      const res = await fetch(`${schoolServicesService}/alumnogrupo/deleteAlumnoGrupo/${id}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${sessionStorage.getItem('token')}`
        }
      });
      if (res.status === 401 || res.status === 403) {
        alert('Su sesión ha expirado. Por favor, inicie sesión de nuevo.');
        window.location.href = 'index.html';
        return;
      }
      if (res.ok) {
        await loadData();
        viewGrupoDetails(currentGrupoId); // Refresh details
      }
    } catch (error) {
      console.error("Error removing:", error);
    }
  }
}


function assignProfesorToGroup() {
  const select = document.getElementById('asignarProfesorSelect');
  const profesorId = select.value;
  const profesorNombre = select.options[select.selectedIndex].text.split(' (')[0];
  if (profesorId) {
    fetch(`${schoolServicesService}/grupoprofesor/addGrupoProfesor`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('token')}`
      },
      body: JSON.stringify({ profesorId, profesorNombre, grupoId: currentGrupoId })
    }).then(async response => {
      if (response.ok) {
        loadData();
        bootstrap.Modal.getInstance(document.getElementById('modalAsignarProfesor')).hide();
        viewGrupoDetails(currentGrupoId); // Refresh details
      } else {
        if (response.status === 401 || response.status === 403) {
          alert('Su sesión ha expirado. Por favor, inicie sesión de nuevo.');
          window.location.href = 'index.html';
          return;
        }
        throw new Error('Error assigning profesor');
      }
    }).catch(error => {
      console.error("Error assigning profesor:", error);
    });
  }
}

async function removeProfesorFromGroup(id) {
  if (confirm("¿Remover profesor del grupo?")) {
    try {
      const res = await fetch(`${schoolServicesService}/grupoprofesor/deleteGrupoProfesor/${id}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${sessionStorage.getItem('token')}`
        }
      });
      if (res.status === 401 || res.status === 403) {
        alert('Su sesión ha expirado. Por favor, inicie sesión de nuevo.');
        window.location.href = 'index.html';
        return;
      }
      if (res.ok) {
        await loadData();
        viewGrupoDetails(currentGrupoId); // Refresh details
      }
    } catch (error) {
      console.error("Error removing profesor:", error);
    }
  }
}

function checkBackendStatus() {
  const loadingScreen = document.getElementById('loading-screen');
  const errorMessage = document.getElementById('error-message');
  const mainContent = document.querySelector('.container');

  loadingScreen.style.display = 'flex';
  mainContent.style.display = 'none';

  fetch(`${schoolServicesService}/status`)
    .then(response => {
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      return response.json();
    })
    .then(data => {
      if (data.status === 'ok') {
        // Load initial data
        initializeApp();

        loadingScreen.style.display = 'none';
        mainContent.style.display = 'block';
      } else {
        throw new Error('Service unavailable');
      }
    })
    .catch(_ => {
      loadingScreen.style.display = 'none';
      errorMessage.style.display = 'block';
    });
}

function logout() {
  sessionStorage.removeItem('token');
  window.location.href = 'index.html';
}

function changePassword(event) {
  event.preventDefault();

  const currentPassword = document.getElementById('current-password').value;
  const newPassword = document.getElementById('new-password').value;
  const confirmPassword = document.getElementById('confirm-password').value;

  const successAlert = document.getElementById('success-alert');
  const errorAlert = document.getElementById('error-alert');

  successAlert.style.display = 'none';
  errorAlert.style.display = 'none';

  if (newPassword !== confirmPassword) {
    errorAlert.style.display = 'block';
    return;
  }

  fetch(`${authService}/change-password`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${sessionStorage.getItem('token')}`
    },
    body: JSON.stringify({
      current_password: currentPassword,
      new_password: newPassword
    })
  }).then(async response => {
    const data = await response.json();
    if (response.ok) {
      successAlert.style.display = 'block';
      document.getElementById('current-password').value = '';
      document.getElementById('new-password').value = '';
      document.getElementById('confirm-password').value = '';

      setTimeout(() => {
        successAlert.style.display = 'none';
      }, 3000);

    } else {
      if (response.status === 401 || response.status === 403) {
        alert('Su sesión ha expirado. Por favor, inicie sesión de nuevo.');
        window.location.href = 'index.html';
        return;
      }
      throw new Error('Error updating password');
    }

  }).catch(_ => {
    errorAlert.style.display = 'block';
  });
}

function initializeApp() {
  loadData();
  const navLinks = document.querySelectorAll('.nav-link[data-section]');
  const sections = document.querySelectorAll('.section');

  navLinks.forEach(link => {
    link.addEventListener('click', function (e) {
      e.preventDefault();
      navLinks.forEach(navLink => navLink.classList.remove('active'));
      this.classList.add('active');
      sections.forEach(section => section.classList.remove('active'));
      const sectionId = this.getAttribute('data-section');
      document.getElementById(sectionId).classList.add('active');
    });
  });

  // Handle modal chaining
  document.querySelectorAll('[data-bs-target="#modalAsignarAlumno"]').forEach(btn => {
    btn.addEventListener('click', () => {
      bootstrap.Modal.getInstance(document.getElementById('modalDetallesGrupo')).hide();
    });
  });

}

// Section switching (from original)
document.addEventListener('DOMContentLoaded', () => {
  checkBackendStatus();
});