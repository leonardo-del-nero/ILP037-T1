// Gerenciamento de formulários de usuário - usado em user-create.html e user-edit.html
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('userForm');
    if (form) {
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        });

        form.addEventListener('submit', function(event) {
            event.preventDefault();
            
            const tenant = localStorage.getItem('currentTenant');
            
            if (!tenant) {
                alert('Tenant não definido. Selecione um tenant primeiro.');
                return;
            }

            const formData = new FormData(form);
            const data = {
                username: formData.get('username'),
                password: formData.get('password'),
                rolesInput: formData.get('rolesInput')
            };

            fetch(form.action, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'x-tenant': tenant
                },
                body: new URLSearchParams({
                    'username': data.username,
                    'password': data.password,
                    'rolesInput': data.rolesInput
                })
            })
            .then(response => {
                if (response.redirected) {
                    window.location.href = response.url;
                } else if (response.ok) {
                    window.location.href = '/users';
                } else {
                    throw new Error('Erro ao criar usuário');
                }
            })
            .catch(error => {
                console.error('Erro ao criar usuário:', error);
                alert('Erro ao criar usuário: ' + error.message);
            });
        });
    }
});