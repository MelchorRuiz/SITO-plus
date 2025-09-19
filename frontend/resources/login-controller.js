const apiUrl = 'http://localhost:8001';

const loginForm = document.getElementById('login-form');
const usernameInput = document.getElementById('username');
const passwordInput = document.getElementById('password');
const notification = document.getElementById('notification');
const notificationMessage = document.getElementById('notification-message');

function showNotification(message, type) {
    notification.classList.remove('notification-success', 'notification-error');

    if (type === 'success') {
        notification.classList.add('notification-success');
    } else if (type === 'error') {
        notification.classList.add('notification-error');
    }

    notificationMessage.textContent = message;
    notification.classList.remove('hidden');
}

loginForm.addEventListener('submit', function (event) {
    event.preventDefault();

    const username = usernameInput.value.trim();
    const password = passwordInput.value.trim();

    if (username === '' || password === '') {
        showNotification('Por favor, completa todos los campos.', 'error');
        return;
    }

    fetch(`${apiUrl}/login`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ username, password })
    }).then(async response => {
        if (response.ok) {
            const data = await response.json();
            showNotification('Inicio de sesión exitoso.', 'success');
            sessionStorage.setItem('token', data.token);
            window.location.href = `${data.role}.html`;
        } else {
            const error = await response.json();
            if (error.detail && error.detail === 'Invalid credentials') {
                showNotification('Credenciales inválidas.', 'error');
            } else {
                showNotification('Error al iniciar sesión.', 'error');
            }
        }
    }).catch(() => {
        showNotification('No se pudo conectar con el servidor.', 'error');
    });
});