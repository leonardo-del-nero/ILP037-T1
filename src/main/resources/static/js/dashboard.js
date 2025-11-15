// Dashboard - usado em index-completo.html
document.addEventListener('DOMContentLoaded', function() {
    const tenant = localStorage.getItem('currentTenant');
    if (tenant) {
        updateUserStats(tenant);
    }
});

function updateUserStats(tenant) {
    fetch('/userService/users', {
        headers: { 
            'x-tenant': tenant
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(users => {
        const totalUsersElement = document.getElementById('totalUsers');
        if (totalUsersElement) {
            totalUsersElement.textContent = users.length;
        }
    })
    .catch(error => {
        console.error('Erro ao carregar estatísticas:', error);
        const totalUsersElement = document.getElementById('totalUsers');
        if (totalUsersElement) {
            totalUsersElement.textContent = 'Erro';
        }
    });
}