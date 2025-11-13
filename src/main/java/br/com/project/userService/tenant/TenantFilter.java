package br.com.project.userService.tenant;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TenantFilter extends OncePerRequestFilter  {

    private final TenantIdentifierResolver tenantIdentifierResolver;
    
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        
        String tenant = request.getHeader("x-tenant");
        
        if (request.getRequestURI().startsWith("/userService/")) {
            if(!StringUtils.hasText(tenant)){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Header x-tenant é obrigatório para a API");
                return;
            }
        } else {
            if(!StringUtils.hasText(tenant)){
                tenant = "bradev";
            }
        }
        
        tenantIdentifierResolver.setCurrentTenant(tenant);

        // Proceed with the next filter in the chain
        filterChain.doFilter(request, response);
    }
}