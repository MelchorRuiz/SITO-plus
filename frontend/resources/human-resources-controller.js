const authService = 'http://localhost:8001';
const humanResourcesService = 'http://localhost:8004/api/v1';

document.addEventListener('DOMContentLoaded', () => {
    checkBackendStatus();
});

function checkBackendStatus() {
    const loadingScreen = document.getElementById('loading-screen');
    const errorMessage = document.getElementById('error-message');
    const mainContent = document.querySelector('.container');

    loadingScreen.style.display = 'flex';
    mainContent.style.display = 'none';

    fetch(`${humanResourcesService}/status`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            if (data.status === 'ok') {
                main();

                loadingScreen.style.display = 'none';
                mainContent.style.display = 'block';
            } else {
                throw new Error('Service unavailable');
            }
        })
        .catch(error => {
            loadingScreen.style.display = 'none';
            errorMessage.style.display = 'block';
        });
}

function main() {
    const teachersBtn = document.getElementById('teachers-btn');
    const passwordBtn = document.getElementById('password-btn');
    const logoutBtn = document.getElementById('logout-btn');
    const teachersSection = document.getElementById('teachers-section');
    const passwordSection = document.getElementById('password-section');

    const teacherForm = document.getElementById('teacher-form');
    const teachersTableBody = document.getElementById('teachers-table-body');
    const formSubmitBtn = document.getElementById('form-submit-btn');

    const passwordForm = document.getElementById('password-form');
    const messageBox = document.getElementById('message-box');
    const messageText = document.getElementById('message-text');
    const closeMessageBoxBtn = document.getElementById('close-message-box');

    let teachers = [];
    let isEditing = false;
    let currentTeacherId = null;

    // --- Functionality for displaying sections ---
    function showSection(section) {
        document.querySelectorAll('.content-section').forEach(sec => {
            sec.classList.remove('active');
        });
        section.classList.add('active');
    }

    teachersBtn.addEventListener('click', () => showSection(teachersSection));
    passwordBtn.addEventListener('click', () => showSection(passwordSection));
    logoutBtn.addEventListener('click', logout);

    fetch(`${humanResourcesService}/profesores`, {
        headers: {
            'Authorization': `Bearer ${sessionStorage.getItem('token')}`
        }
    }).then(async response => {
        const data = await response.json();
        if (response.ok) {
            teachers = data;
            renderTeachers();
        } else {
            if (response.status === 401 || response.status === 403) {
                alert('Su sesión ha expirado. Por favor, inicie sesión de nuevo.');
                window.location.href = 'index.html';
                return;
            }
            throw new Error('Error fetching teachers');
        }
    }).catch(_ => {
        showMessage('Error al cargar los profesores. Por favor, recargue la página.');
    });

    // --- Functionality for managing professors ---
    function renderTeachers() {
        console.log(teachers);
        teachersTableBody.innerHTML = '';
        teachers.forEach(teacher => {
            const row = document.createElement('tr');
            row.innerHTML = `
                        <td>${teacher.numeroEmpleado}</td>
                        <td>${teacher.nombre}</td>
                        <td>${teacher.apellido}</td>
                        <td>${teacher.salario}</td>
                        <td>${teacher.puesto}</td>
                        <td class="actions">
                            <button onclick="editTeacher(${teacher.id})">Editar</button>
                            <button onclick="deleteTeacher(${teacher.id})">Eliminar</button>
                        </td>
                    `;
            teachersTableBody.appendChild(row);
        });
    }

    teacherForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const name = document.getElementById('name').value;
        const lastName = document.getElementById('lastName').value;
        const salary = document.getElementById('salary').value;
        const position = document.getElementById('position').value;

        if (isEditing) {
            fetch(`${humanResourcesService}/profesores/${currentTeacherId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${sessionStorage.getItem('token')}`
                },
                body: JSON.stringify({
                    nombre: name,
                    apellido: lastName,
                    salario: salary,
                    puesto: position
                })
            }).then(async response => {
                const data = await response.json();
                if (response.ok) {
                    const index = teachers.findIndex(t => t.id === currentTeacherId);
                    if (index !== -1) {
                        teachers[index] = data;
                        renderTeachers();
                        teacherForm.reset();
                        isEditing = false;
                        currentTeacherId = null;
                        formSubmitBtn.textContent = 'Crear Profesor';
                        showMessage('Profesor actualizado exitosamente.');
                    }
                } else {
                    if (response.status === 401 || response.status === 403) {
                        alert('Su sesión ha expirado. Por favor, inicie sesión de nuevo.');
                        window.location.href = 'index.html';
                        return;
                    }
                    throw new Error('Error updating teacher');
                }
            }).catch(_ => {
                showMessage('Error al actualizar el profesor. Por favor, inténtelo de nuevo.');
            });
        } else {
            fetch(`${humanResourcesService}/profesores`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${sessionStorage.getItem('token')}`
                },
                body: JSON.stringify({
                    nombre: name,
                    apellido: lastName,
                    salario: salary,
                    puesto: position
                })
            }).then(async response => {
                const data = await response.json();
                if (response.ok) {
                    teachers.push(data);
                    teacherForm.reset();
                    renderTeachers();
                    showMessage('Profesor creado exitosamente.');
                } else {
                    if (response.status === 401 || response.status === 403) {
                        alert('Su sesión ha expirado. Por favor, inicie sesión de nuevo.');
                        window.location.href = 'index.html';
                        return;
                    }
                    throw new Error('Error creando profesor');
                }
            }).catch(_ => {
                showMessage('Error al crear el profesor. Por favor, inténtelo de nuevo.');
            });
        }

    });

    window.editTeacher = (id) => {
        const teacher = teachers.find(t => t.id === id);
        if (teacher) {
            document.getElementById('name').value = teacher.nombre;
            document.getElementById('lastName').value = teacher.apellido;
            document.getElementById('salary').value = teacher.salario;
            document.getElementById('position').value = teacher.puesto;
            isEditing = true;
            currentTeacherId = id;
            formSubmitBtn.textContent = 'Actualizar Profesor';
        }
    };

    window.deleteTeacher = (id) => {
        if (confirm('¿Está seguro de que desea eliminar este profesor?')) {
            fetch(`${humanResourcesService}/profesores/${id}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${sessionStorage.getItem('token')}`
                }
            }).then(response => {
                if (response.ok) {
                    teachers = teachers.filter(t => t.id !== id);
                    renderTeachers();
                    showMessage('Profesor eliminado exitosamente.');
                } else {
                    if (response.status === 401 || response.status === 403) {
                        alert('Su sesión ha expirado. Por favor, inicie sesión de nuevo.');
                        window.location.href = 'index.html';
                        return;
                    }
                    throw new Error('Error deleting teacher');
                }
            }).catch(_ => {
                showMessage('Error al eliminar el profesor. Por favor, inténtelo de nuevo.');
            });
        }
    };

    // --- Functionality for changing password ---
    passwordForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const currentPassword = document.getElementById('current-password').value;
        const newPassword = document.getElementById('new-password').value;
        const confirmPassword = document.getElementById('confirm-password').value;

        if (newPassword !== confirmPassword) {
            showMessage('Las nuevas contraseñas no coinciden.');
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
            if (response.ok) {
                showMessage('Contraseña cambiada exitosamente.');
                passwordForm.reset();
            } else {
                if (response.status === 401 || response.status === 403) {
                    alert('Su sesión ha expirado. Por favor, inicie sesión de nuevo.');
                    window.location.href = 'index.html';
                    return;
                }
                throw new Error('Error updating password');
            }

        }).catch(_ => {
            showMessage('Error al cambiar la contraseña. Verifique que su contraseña actual sea correcta.');
        });
    });

    // --- General message box functionality ---
    function showMessage(message) {
        messageText.textContent = message;
        messageBox.style.display = 'flex';
    }

    closeMessageBoxBtn.addEventListener('click', () => {
        messageBox.style.display = 'none';
    });
    
    function logout() {
      sessionStorage.removeItem('token');
      window.location.href = 'index.html';
    }
}
