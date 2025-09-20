const authService = 'http://localhost:8001';
const studentService = 'http://localhost:8002';
const schoolServicesService = 'http://localhost:8003/servicios-escolares/api';
const teacherService = 'https://68c88b915d8d9f514735a4f0.mockapi.io';

function showSection(sectionId) {
    const sections = document.querySelectorAll('.content-section');
    sections.forEach(section => {
        section.classList.remove('active');
    });

    const tabs = document.querySelectorAll('.nav-tab');
    tabs.forEach(tab => {
        tab.classList.remove('active');
    });

    document.getElementById(sectionId).classList.add('active');

    event.target.classList.add('active');

    // Load data when switching to a section
    if (sectionId === 'groups') {
        loadGroups();
    } else if (sectionId === 'grades') {
        loadGrades();
    }
}

function loadGroups() {
    const loadingDiv = document.getElementById('groups-loading');
    const contentDiv = document.getElementById('groups-content');
    const errorDiv = document.getElementById('groups-error');

    loadingDiv.style.display = 'block';
    contentDiv.style.display = 'none';
    errorDiv.style.display = 'none';

    fetch(`${schoolServicesService}/alumnogrupo/getGruposByMatricula`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${sessionStorage.getItem('token')}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) {
                if (response.status === 401 || response.status === 403) {
                    alert('Su sesión ha expirado. Por favor, inicie sesión de nuevo.');
                    window.location.href = 'index.html';
                    return;
                }
                throw new Error('Error loading groups');
            }
            return response.json();
        })
        .then(data => {
            loadingDiv.style.display = 'none';

            contentDiv.innerHTML = '';

            if (data && data.length > 0) {
                data.forEach(group => {
                    const groupCard = document.createElement('div');
                    groupCard.className = 'group-card';
                    groupCard.innerHTML = `
                            <div class="group-name">${group.nombreGrupo}</div>
                        `;
                    contentDiv.appendChild(groupCard);
                });
            } else {
                contentDiv.innerHTML = '<p style="text-align: center; color: #757575; padding: 40px;">No tienes grupos asignados.</p>';
            }

            contentDiv.style.display = 'grid';
        })
        .catch(error => {
            loadingDiv.style.display = 'none';
            errorDiv.style.display = 'block';
            console.error('Error loading groups:', error);
        });
}

function loadGrades() {
    const loadingDiv = document.getElementById('grades-loading');
    const contentDiv = document.getElementById('grades-content');
    const errorDiv = document.getElementById('grades-error');
    const tbody = document.getElementById('grades-tbody');

    loadingDiv.style.display = 'block';
    contentDiv.style.display = 'none';
    errorDiv.style.display = 'none';

    fetch(`${teacherService}/grades`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${sessionStorage.getItem('token')}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) {
                if (response.status === 401 || response.status === 403) {
                    alert('Su sesión ha expirado. Por favor, inicie sesión de nuevo.');
                    window.location.href = 'index.html';
                    return;
                }
                throw new Error('Error loading grades');
            }
            return response.json();
        })
        .then(data => {
            loadingDiv.style.display = 'none';

            tbody.innerHTML = '';

            if (data && data.length > 0) {
                data.forEach(grade => {
                    const row = document.createElement('tr');
                    const average = ((parseFloat(grade.partial1 || 0) + parseFloat(grade.partial2 || 0) + parseFloat(grade.partial3 || 0)) / 3).toFixed(1);

                    row.innerHTML = `
                            <td>${grade.subject}</td>
                            <td>${grade.partial1 || '-'}</td>
                            <td>${grade.partial2 || '-'}</td>
                            <td>${grade.partial3 || '-'}</td>
                            <td><strong>${average}</strong></td>
                        `;
                    tbody.appendChild(row);
                });
            } else {
                tbody.innerHTML = '<tr><td colspan="5" style="text-align: center; color: #757575; padding: 40px;">No tienes calificaciones registradas.</td></tr>';
            }

            contentDiv.style.display = 'block';
        })
        .catch(error => {
            loadingDiv.style.display = 'none';
            errorDiv.style.display = 'block';
            console.error('Error loading grades:', error);
        });
}

function loadInfo() {
    const nameElem = document.getElementById('student-name');
    const detailsElem = document.getElementById('student-details');
    const avatarElem = document.getElementById('student-avatar');

    function getAvatarInitials(name) {
        const names = name.split(' ');
        let initials = names[0].charAt(0);
        if (names.length > 1) {
            initials += names[1].charAt(0);
        }
        return initials.toUpperCase();
    }

    fetch(`${studentService}/me`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${sessionStorage.getItem('token')}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) {
                if (response.status === 401 || response.status === 403) {
                    alert('Su sesión ha expirado. Por favor, inicie sesión de nuevo.');
                    window.location.href = 'index.html';
                    return;
                } else if (response.status === 404) {
                    alert('Información del estudiante no encontrada.');
                    window.location.href = 'index.html';
                    return;
                }
                throw new Error('Error loading student info');
            }
            return response.json();
        })
        .then(data => {
            nameElem.textContent = data.name || 'Nombre no disponible';
            detailsElem.textContent = `Matrícula: ${data.id || 'N/A'} | Carrera: ${data.major || 'N/A'}`;
            avatarElem.textContent = getAvatarInitials(data.name || 'Not Name');
        })
        .catch(error => {
            throw new Error('Error loading student info');
        });
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

    }).catch(error => {
        errorAlert.style.display = 'block';
    });
}

function logout() {
  sessionStorage.removeItem('token');
  window.location.href = 'index.html';
}


function checkBackendStatus() {
    const loadingScreen = document.getElementById('loading-screen');
    const errorMessage = document.getElementById('error-message');
    const mainContent = document.querySelector('.container');

    loadingScreen.style.display = 'flex';
    mainContent.style.display = 'none';

    fetch(`${studentService}/status`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            if (data.status === 'ok') {
                // Load initial data
                loadInfo();
                loadGroups();

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

document.addEventListener('DOMContentLoaded', () => {
    checkBackendStatus();
});