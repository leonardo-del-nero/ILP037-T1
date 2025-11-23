package br.com.project.userService.tenant;

import java.util.Map;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

// Removemos o @Setter do Lombok pois faremos manualmente para o ThreadLocal
@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver<String>, HibernatePropertiesCustomizer {

    // MUDANÇA PRINCIPAL: Usamos ThreadLocal ao invés de String simples
    // Isso garante que cada requisição HTTP tenha seu próprio valor de tenant isolado
    private final ThreadLocal<String> currentTenant = ThreadLocal.withInitial(() -> "unknown");

    // Método manual para definir o tenant no ThreadLocal (usado pelo TenantFilter)
    public void setCurrentTenant(String tenant) {
        this.currentTenant.set(tenant);
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, this);
    }

    @Override
    public @NonNull String resolveCurrentTenantIdentifier() {
        // Retorna o valor isolado da thread atual
        return currentTenant.get();
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}