const authService = "http://localhost:8001";
const schoolServicesService = "http://localhost:8003/servicios-escolares/api";
const humanResourcesService = 'http://localhost:8004/api/v1';
const teacherService = "http://localhost:8005";

let currentGradeId = null;

function checkBackendStatus() {
    const loadingScreen = document.getElementById('loading-screen');
    const errorMessage = document.getElementById('error-message');
    const mainContent = document.querySelector('.container');

    loadingScreen.style.display = 'flex';
    mainContent.style.display = 'none';

    fetch(`${teacherService}/status`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            if (data.status === 'ok') {
                fillGroups();
                fillTeacherName();

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

document.addEventListener('DOMContentLoaded', () => {
    checkBackendStatus();
});

function updatePassword(event) {
    event.preventDefault();

    const currentPassword = document.getElementById('current-password').value;
    const newPassword = document.getElementById('new-password').value;
    const confirmPassword = document.getElementById('confirm-password').value;

    if (newPassword !== confirmPassword) {
        alert('Las nuevas contraseñas no coinciden.');
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
    }).then(response => {
        if (response.ok) {
            event.target.reset();
            alert('Contraseña actualizada con éxito.');
        } else {
            if (response.status === 401 || response.status === 403) {
                alert('Su sesión ha expirado. Por favor, inicie sesión de nuevo.');
                window.location.href = 'index.html';
                return;
            }
            throw new Error('Error updating password');
        }

    }).catch(_ => {
        alert('Error al actualizar la contraseña. La contraseña actual puede ser incorrecta.');
    });
}

function addGrade(event) {
    event.preventDefault();

    const studentField = document.getElementById('students');
    const groupField = document.getElementById('groups');
    const partial1Field = document.getElementById('grade1');
    const partial2Field = document.getElementById('grade2');
    const partial3Field = document.getElementById('grade3');

    const gradeData = {
        id_student: studentField.value,
        student_name: studentField.options[studentField.selectedIndex].text,
        id_group: groupField.value,
        group_name: groupField.options[groupField.selectedIndex].text,
        partial_1: parseFloat(partial1Field.value),
        partial_2: parseFloat(partial2Field.value),
        partial_3: parseFloat(partial3Field.value)
    };

    const method = currentGradeId ? 'PUT' : 'POST';
    const url = currentGradeId ? `${teacherService}/update-grades/${currentGradeId}` : `${teacherService}/register-grade`;

    fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${sessionStorage.getItem('token')}`
        },
        body: JSON.stringify(gradeData)
    }).then(response => {
        if (response.ok) {
            event.target.reset();
            currentGradeId = null;
            document.getElementById('add-grade-button').textContent = 'Agregar Calificación';
            alert('Calificaciones guardadas con éxito.');
        } else {
            if (response.status === 401 || response.status === 403) {
                alert('Su sesión ha expirado. Por favor, inicie sesión de nuevo.');
                window.location.href = 'index.html';
                return;
            }
            throw new Error('Error saving grades');
        }
    }).catch(_ => {
        console.log('Error al guardar las calificaciones.');
    });
}

function fillTeacherName() {
    fetch(`${humanResourcesService}/profesores/yo`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${sessionStorage.getItem('token')}`
        }
    }).then(response => {
        if (response.status === 401 || response.status === 403) {
            alert('Su sesión ha expirado. Por favor, inicie sesión de nuevo.');
            window.location.href = 'index.html';
            return;
        }
        if (!response.ok) {
            throw new Error('Error fetching teacher name');
        }
        return response.json();
    }).then(data => {
        document.getElementById('teacher-name').textContent = `${data.nombre} ${data.apellido}`;
    }).catch(_ => {
        console.log('No existing teacher found or error occurred.');
    });
}

function fillGroups() {
    fetch(`${schoolServicesService}/grupoprofesor/getGruposByProfesor`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${sessionStorage.getItem('token')}`
        }
    }).then(response => {
        if (response.status === 401 || response.status === 403) {
            alert('Su sesión ha expirado. Por favor, inicie sesión de nuevo.');
            window.location.href = 'index.html';
            return;
        }
        if (!response.ok) {
            throw new Error('Error fetching groups');
        }
        return response.json();
    }).then(data => {
        const groupSelect = document.getElementById('groups');
        data.forEach(group => {
            const option = document.createElement('option');
            option.value = group.id_grupo;
            option.textContent = group.nombre_grupo;
            groupSelect.appendChild(option);
        });
    }).catch(_ => {
        console.log('No existing groups found or error occurred.');
    });
}

function fillStudents(event) {
    const groupId = event.target.value;
    const studentSelect = document.getElementById('students');

    fetch(`${schoolServicesService}/alumnogrupo/getAlumnosByGrupoId/${groupId}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${sessionStorage.getItem('token')}`
        }
    }).then(response => {
        if (response.status === 401 || response.status === 403) {
            alert('Su sesión ha expirado. Por favor, inicie sesión de nuevo.');
            window.location.href = 'index.html';
            return;
        }
        if (!response.ok) {
            throw new Error('Error fetching students');
        }
        return response.json();
    }).then(data => {
        studentSelect.innerHTML = '<option value="" disabled selected>Seleccione un alumno</option>';
        data.forEach(student => {
            const option = document.createElement('option');
            option.value = student.matricula;
            option.textContent = student.alumnoNombre;
            studentSelect.appendChild(option);
        });
    }).catch(_ => {
        console.log('No existing students found or error occurred.');
    });
}

function fillGrades(event) {
    const studentId = event.target.value;
    const groupId = document.getElementById('groups').value;

    if (!studentId || !groupId) {
        return;
    }

    fetch(`${teacherService}/check-grades/${studentId}/${groupId}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${sessionStorage.getItem('token')}`
        }
    }).then(response => {
        if (response.status === 401 || response.status === 403) {
            alert('Su sesión ha expirado. Por favor, inicie sesión de nuevo.');
            window.location.href = 'index.html';
            return;
        }
        if (!response.ok) {
            throw new Error('Error checking grades');
        }
        return response.json();
    }).then(data => {
        document.getElementById('grade1').value = data.exists ? data.partial_1 : 0;
        document.getElementById('grade2').value = data.exists ? data.partial_2 : 0;
        document.getElementById('grade3').value = data.exists ? data.partial_3 : 0;
        document.getElementById('add-grade-button').textContent = data.exists ? 'Actualizar Calificación' : 'Agregar Calificación';
        currentGradeId = data.exists ? data.grade_id : null;
    }).catch(_ => {
        console.log('No existing grades found or error occurred.');
    });
}

function logout(event) {
    event.preventDefault();
    sessionStorage.removeItem('token');
    window.location.href = 'index.html';
}