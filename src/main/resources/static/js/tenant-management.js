// Gerenciamento de tenants - usado em todas as páginas
const CUSTOM_TENANTS_KEY = 'customTenants';
let currentTenant = '';

function getCustomTenants() {
    const stored = localStorage.getItem(CUSTOM_TENANTS_KEY);
    return stored ? JSON.parse(stored) : [];
}

function saveCustomTenant(tenant) {
    if (!tenant || tenant.trim() === '') return;
    
    const customTenants = getCustomTenants();
    const normalizedTenant = tenant.trim();
    
    if (!customTenants.includes(normalizedTenant)) {
        customTenants.push(normalizedTenant);
        localStorage.setItem(CUSTOM_TENANTS_KEY, JSON.stringify(customTenants));
    }
}

function loadTenantsDropdown() {
    const customTenants = getCustomTenants();
    const dropdownMenu = document.querySelector('#tenantDropdown + .dropdown-menu');
    
    if (!dropdownMenu) return;
    
    // Limpar itens customizados existentes
    const existingCustomItems = dropdownMenu.querySelectorAll('.custom-tenant-item');
    existingCustomItems.forEach(item => item.remove());
    
    // Adicionar tenants customizados
    if (customTenants.length > 0) {
        // Encontrar o item do form
        const formItem = Array.from(dropdownMenu.children).find(child => 
            child.querySelector && child.querySelector('.px-3.py-2')
        );
        
        customTenants.forEach(tenant => {
            const listItem = document.createElement('li');
            listItem.className = 'custom-tenant-item';
            listItem.innerHTML = `
                <a class="dropdown-item" href="#" onclick="setTenant('${tenant}')">
                    <i class="bi bi-building me-2"></i>${tenant}
                </a>
            `;
            
            if (formItem) {
                dropdownMenu.insertBefore(listItem, formItem);
            } else {
                dropdownMenu.appendChild(listItem);
            }
        });
    }
}

function setTenant(tenant) {
    if (tenant && tenant.trim() !== '') {
        currentTenant = tenant.trim();
        localStorage.setItem('currentTenant', currentTenant);
        
        // Salvar tenants customizados (exceto os padrão)
        const defaultTenants = ['tenant1', 'tenant2', 'tenant3', 'bradev'];
        if (!defaultTenants.includes(currentTenant)) {
            saveCustomTenant(currentTenant);
        }
        
        // Atualizar visualmente
        const tenantElement = document.getElementById('currentTenant');
        if (tenantElement) {
            tenantElement.textContent = currentTenant;
        }
        
        fetch('/set-tenant', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `tenant=${encodeURIComponent(currentTenant)}`
        })
        .then(response => {
            if (response.ok) {
                setTimeout(() => {
                    window.location.reload();
                }, 300);
            } else {
                throw new Error('Falha ao definir tenant');
            }
        })
        .catch(error => {
            console.error('Erro ao definir tenant:', error);
            setTimeout(() => {
                window.location.reload();
            }, 300);
        });
    } else {
        alert('Por favor, insira um tenant válido.');
    }
}

function setCustomTenant() {
    const customTenantInput = document.getElementById('customTenant');
    if (customTenantInput && customTenantInput.value.trim() !== '') {
        const tenant = customTenantInput.value.trim();
        
        saveCustomTenant(tenant);
        loadTenantsDropdown();
        
        const dropdown = bootstrap.Dropdown.getInstance(document.getElementById('tenantDropdown'));
        if (dropdown) {
            dropdown.hide();
        }
        
        customTenantInput.value = '';
        setTenant(tenant);
    } else {
        alert('Por favor, digite um nome para o tenant customizado.');
    }
}

// Inicialização do tenant
document.addEventListener('DOMContentLoaded', function() {
    const savedTenant = localStorage.getItem('currentTenant');
    if (savedTenant) {
        currentTenant = savedTenant;
        const tenantElement = document.getElementById('currentTenant');
        if (tenantElement) {
            tenantElement.textContent = savedTenant;
        }
        
        fetch('/set-tenant', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `tenant=${encodeURIComponent(savedTenant)}`
        }).catch(error => console.error('Erro ao sincronizar tenant:', error));
    }
    
    loadTenantsDropdown();
});