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
        
        // Get tenant from header "x-tenant"
        String tenant = request.getHeader("x-tenant");
        if(!StringUtils.hasText(tenant)){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or unknown tenant");
            return;
        }
        tenantIdentifierResolver.setCurrentTenant(tenant);

        // Proceed with the next filter in the chain
        filterChain.doFilter(request, response);
    }

}
