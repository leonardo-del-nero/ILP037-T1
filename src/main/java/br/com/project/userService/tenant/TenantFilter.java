package br.com.project.userService.tenant;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TenantFilter extends OncePerRequestFilter  {

    private final TenantIdentifierResolver tenantIdentifierResolver;
    
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        
        String tenant = request.getHeader("x-tenant");
        String path = request.getRequestURI();
        
        System.out.println("=== TENANT FILTER ===");
        System.out.println("Path: " + path);
        System.out.println("Tenant header: " + tenant);
        
        // Para requisições da API, exigir o header x-tenant
        if (path.startsWith("/userService/")) {
            if(!StringUtils.hasText(tenant)){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Header x-tenant é obrigatório para a API");
                return;
            }
        } else {
            // Para requisições do frontend, tentar pegar da sessão primeiro
            String sessionTenant = (String) request.getSession().getAttribute("currentTenant");
            System.out.println("Tenant da sessão: " + sessionTenant);
            
            if (StringUtils.hasText(sessionTenant)) {
                // Usar o tenant da sessão se existir
                tenant = sessionTenant;
            } else if (!StringUtils.hasText(tenant)) {
                // Fallback apenas se não houver tenant nem na sessão nem no header
                tenant = "bradev";
            }
        }
        
        System.out.println("Tenant final: " + tenant);
        tenantIdentifierResolver.setCurrentTenant(tenant);

        filterChain.doFilter(request, response);
    }
}