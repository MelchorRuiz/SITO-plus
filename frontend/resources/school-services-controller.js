// Updated controlador.js (await loadData in assign and remove)

const URL = "http://localhost:8003/servicios-escolares/api";
const authService = 'http://localhost:8001';

async function assignAlumnoToGroup() {
  const matricula = parseInt(document.getElementById('asignarAlumnoSelect').value);
  if (matricula) {
    try {
      const res = await fetch(`${URL}/alumno/assignToGroup/${matricula}/${currentGrupoId}`, {
        method: 'PUT',
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
        bootstrap.Modal.getInstance(document.getElementById('modalAsignarAlumno')).hide();
        viewGrupoDetails(currentGrupoId); // Refresh details
      }
    } catch (error) {
      console.error("Error assigning:", error);
    }
  }
}

async function removeAlumnoFromGroup(matricula) {
  if (confirm("¿Remover alumno del grupo?")) {
    try {
      const res = await fetch(`${URL}/alumno/removeFromGroup/${matricula}`, {
        method: 'PUT',
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

// For professors, since no data, JS doesn't fill detallesProfesoresBody yet

// New controlador.js (combined and expanded from controladorGrupo and controladorAlumno)

let carreras = [];
let grupos = [];
let alumnos = [];
let currentGrupoId = null; // For modals

async function loadData() {
  try {
    const resCarreras = await fetch(`${URL}/carrera/getAllCarreras`, {
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

    const resGrupos = await fetch(`${URL}/grupo/getAllGrupos`, {
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

    const resAlumnos = await fetch(`${URL}/alumno/getAllAlumnos`, {
      headers: {
        'Authorization': `Bearer ${sessionStorage.getItem('token')}`
      }
    });
    if (resAlumnos.status === 401 || resAlumnos.status === 403) {
      alert('Su sesión ha expirado. Por favor, inicie sesión de nuevo.');
      window.location.href = 'index.html';
      return;
    }
    alumnos = await resAlumnos.json();

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

  // Grupos for editAlumno
  const editGrupo = document.getElementById('editGrupo');
  let grupoOptions = '<option value="0">Sin grupo</option>';
  grupos.forEach(g => {
    grupoOptions += `<option value="${g.id_grupo}">${g.nombre_grupo}</option>`;
  });
  editGrupo.innerHTML = grupoOptions;
}

function renderAlumnosTable() {
  const tableBody = document.getElementById('alumnosTableBody');
  let rows = '';
  alumnos.forEach(a => {
    rows += `
      <tr>
        <td>${a.matricula}</td>
        <td>${a.nombre}</td>
        <td>${a.usuario}</td>
        <td>${a.contrasenia}</td>
        <td>${a.carrera ? a.carrera.nombre_carrera : 'N/A'}</td>
        <td>${a.grupo ? a.grupo.nombre_grupo : 'No tiene grupo'}</td>
        <td class="action-buttons">
          <button class="btn btn-sm btn-outline-primary" onclick="editAlumno(${a.matricula})"><i class="fas fa-edit"></i></button>
          <button class="btn btn-sm btn-outline-danger" onclick="deleteAlumno(${a.matricula})"><i class="fas fa-trash-alt"></i></button>
        </td>
      </tr>
    `;
  });
  tableBody.innerHTML = rows;
}

function renderGruposTable() {
  const tableBody = document.getElementById('gruposTableBody');
  let rows = '';
  grupos.forEach(g => {
    const numAlumnos = alumnos.filter(a => a.grupo && a.grupo.id_grupo === g.id_grupo).length;
    rows += `
      <tr>
        <td>${g.nombre_grupo}</td>
        <td>${g.carrera ? g.carrera.nombre_carrera : 'N/A'}</td>
        <td>${numAlumnos}</td>
        <td class="action-buttons">
          <button class="btn btn-sm btn-outline-info" onclick="viewGrupoDetails(${g.id_grupo})"><i class="fas fa-eye"></i></button>
          <button class="btn btn-sm btn-outline-primary" onclick="editGrupo(${g.id_grupo})"><i class="fas fa-edit"></i></button>
          <button class="btn btn-sm btn-outline-danger" onclick="deleteGrupo(${g.id_grupo})"><i class="fas fa-trash-alt"></i></button>
        </td>
      </tr>
    `;
  });
  tableBody.innerHTML = rows;
}

async function saveAlumno() {
  const matricula = parseInt(document.getElementById('addMatricula').value);
  const nombre = document.getElementById('addNombre').value;
  const usuario = parseInt(document.getElementById('addUsuario').value);
  const contrasenia = document.getElementById('addContrasenia').value;
  const carreraId = parseInt(document.getElementById('addCarrera').value);

  const alumno = {
    matricula,
    nombre,
    usuario,
    contrasenia,
    estatus: 1,
    carrera: { id_carrera: carreraId },
    grupo: null
  };

  try {
    const res = await fetch(`${URL}/alumno/addAlumno`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('token')}`
      },
      body: JSON.stringify(alumno)
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
  const alumno = alumnos.find(a => a.matricula === matricula);
  document.getElementById('editMatricula').value = alumno.matricula;
  document.getElementById('editNombre').value = alumno.nombre;
  document.getElementById('editUsuario').value = alumno.usuario;
  document.getElementById('editContrasenia').value = alumno.contrasenia;
  document.getElementById('editCarrera').value = alumno.carrera ? alumno.carrera.id_carrera : '';
  document.getElementById('editGrupo').value = alumno.grupo ? alumno.grupo.id_grupo : 0;

  bootstrap.Modal.getOrCreateInstance(document.getElementById('modalEditarAlumno')).show();
}

async function updateAlumno() {
  const matricula = parseInt(document.getElementById('editMatricula').value);
  const nombre = document.getElementById('editNombre').value;
  const usuario = parseInt(document.getElementById('editUsuario').value);
  const contrasenia = document.getElementById('editContrasenia').value;
  const carreraId = parseInt(document.getElementById('editCarrera').value);
  const grupoId = parseInt(document.getElementById('editGrupo').value);

  const alumno = {
    matricula,
    nombre,
    usuario,
    contrasenia,
    estatus: 1,
    carrera: { id_carrera: carreraId },
    grupo: grupoId > 0 ? { id_grupo: grupoId } : null
  };

  try {
    const res = await fetch(`${URL}/alumno/updateAlumno`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('token')}`
      },
      body: JSON.stringify(alumno)
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
      const res = await fetch(`${URL}/alumno/deleteAlumno/${matricula}`, {
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
    const res = await fetch(`${URL}/grupo/addGrupo`, {
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
    const res = await fetch(`${URL}/grupo/updateGrupo`, {
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
      const res = await fetch(`${URL}/grupo/deleteGrupo/${id}`, {
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

  const alumnosEnGrupo = alumnos.filter(a => a.grupo && a.grupo.id_grupo === id);
  const tableBody = document.getElementById('detallesAlumnosBody');
  let rows = '';
  if (alumnosEnGrupo.length === 0) {
    rows = '<tr><td colspan="3">No hay ningún alumno en el grupo.</td></tr>';
  } else {
    alumnosEnGrupo.forEach(a => {
      rows += `
        <tr>
          <td>${a.matricula}</td>
          <td>${a.nombre}</td>
          <td>
            <button class="btn btn-sm btn-outline-danger" onclick="removeAlumnoFromGroup(${a.matricula})"><i class="fas fa-user-minus"></i> Remover</button>
          </td>
        </tr>
      `;
    });
  }
  tableBody.innerHTML = rows;

  // Populate assign select with alumnos sin grupo
  const asignarSelect = document.getElementById('asignarAlumnoSelect');
  let options = '<option value="">Seleccione un alumno</option>';
  const alumnosSinGrupo = alumnos.filter(a => !a.grupo);
  alumnosSinGrupo.forEach(a => {
    options += `<option value="${a.matricula}">${a.nombre} (${a.matricula})</option>`;
  });
  asignarSelect.innerHTML = options;

  bootstrap.Modal.getOrCreateInstance(document.getElementById('modalDetallesGrupo')).show();
}

async function assignAlumnoToGroup() {
  const matricula = parseInt(document.getElementById('asignarAlumnoSelect').value);
  if (matricula) {
    try {
      const res = await fetch(`${URL}/alumno/assignToGroup/${matricula}/${currentGrupoId}`, {
        method: 'PUT',
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
        bootstrap.Modal.getInstance(document.getElementById('modalAsignarAlumno')).hide();
        viewGrupoDetails(currentGrupoId); // Refresh details
      }
    } catch (error) {
      console.error("Error assigning:", error);
    }
  }
}

async function removeAlumnoFromGroup(matricula) {
  if (confirm("¿Remover alumno del grupo?")) {
    try {
      const res = await fetch(`${URL}/alumno/removeFromGroup/${matricula}`, {
        method: 'PUT',
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
        viewGrupoDetails(currentGrupoId); // Refresh details
      }
    } catch (error) {
      console.error("Error removing:", error);
    }
  }
}

function checkBackendStatus() {
  const loadingScreen = document.getElementById('loading-screen');
  const errorMessage = document.getElementById('error-message');
  const mainContent = document.querySelector('.container');

  loadingScreen.style.display = 'flex';
  mainContent.style.display = 'none';

  fetch(`${URL}/status`)
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