// Gerenciamento da lista de usuários - usado em users.html
document.addEventListener('DOMContentLoaded', function() {
    loadUsers();
});

function loadUsers() {
    const tenant = localStorage.getItem('currentTenant');
    if (!tenant) {
        console.log('Tenant não definido');
        const loadingMessage = document.getElementById('loadingMessage');
        if (loadingMessage) {
            loadingMessage.style.display = 'none';
        }
        return;
    }

    const loadingMessage = document.getElementById('loadingMessage');
    const emptyMessage = document.getElementById('emptyMessage');
    const tableBody = document.getElementById('usersTableBody');

    fetch('/userService/users', {
        headers: { 
            'x-tenant': tenant
        }
    })
    .then(response => {
        if (response.status === 400) {
            throw new Error('Tenant não definido ou inválido');
        }
        if (!response.ok) {
            throw new Error(`Erro HTTP! status: ${response.status}`);
        }
        return response.json();
    })
    .then(users => {
        if (loadingMessage) {
            loadingMessage.style.display = 'none';
        }
        
        if (!users || users.length === 0) {
            if (emptyMessage) {
                emptyMessage.classList.remove('d-none');
            }
            return;
        }

        if (tableBody) {
            tableBody.innerHTML = '';
            users.forEach(user => {
                const row = tableBody.insertRow();
                
                let rolesHtml = '';
                if (user.roles && Array.isArray(user.roles)) {
                    rolesHtml = user.roles.map(role => 
                        `<span class="badge bg-secondary me-1">${escapeHtml(role)}</span>`
                    ).join('');
                } else if (user.roles) {
                    rolesHtml = `<span class="badge bg-secondary me-1">${escapeHtml(user.roles)}</span>`;
                }
                
                row.innerHTML = `
                    <td>${user.id}</td>
                    <td>${escapeHtml(user.username)}</td>
                    <td>${rolesHtml}</td>
                    <td class="table-actions">
                        <a href="/users/${user.id}/edit" class="btn btn-sm btn-outline-primary me-1">
                            <i class="bi bi-pencil"></i> Editar
                        </a>
                        <button class="btn btn-sm btn-outline-danger" onclick="deleteUser(${user.id})">
                            <i class="bi bi-trash"></i> Excluir
                        </button>
                    </td>
                `;
            });
        }
    })
    .catch(error => {
        console.error('Erro ao carregar usuários:', error);
        if (loadingMessage) {
            loadingMessage.style.display = 'none';
        }
        showAlert('error', 'Erro ao carregar usuários: ' + error.message);
    });
}

function deleteUser(userId) {
    if (!confirm('Tem certeza que deseja excluir este usuário?')) {
        return;
    }

    const tenant = localStorage.getItem('currentTenant');
    
    fetch(`/userService/users/${userId}`, {
        method: 'DELETE',
        headers: { 'x-tenant': tenant }
    })
    .then(response => {
        if (response.ok) {
            showAlert('success', 'Usuário excluído com sucesso!');
            loadUsers();
        } else {
            throw new Error('Falha ao excluir usuário');
        }
    })
    .catch(error => {
        showAlert('error', 'Erro ao excluir usuário: ' + error.message);
    });
}

function escapeHtml(unsafe) {
    if (!unsafe) return '';
    return unsafe
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

function showAlert(type, message) {
    const existingAlerts = document.querySelectorAll('.alert');
    existingAlerts.forEach(alert => {
        if (!alert.classList.contains('alert-warning') || !alert.textContent.includes('tenant')) {
            alert.remove();
        }
    });
    
    const alertClass = type === 'success' ? 'alert-success' : 'alert-danger';
    const alertHtml = `
        <div class="alert ${alertClass} alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
    
    const content = document.querySelector('main .container');
    if (content) {
        content.insertAdjacentHTML('afterbegin', alertHtml);
    }
}