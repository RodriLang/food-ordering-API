package com.group_three.food_ordering.security;

import com.group_three.food_ordering.configs.ApiPaths;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * Filtro para leer el token JWT desde una query param
 * para la conexión SSE (EventSource), ya que no puede
 * enviar cabeceras de autorización.
 */
@Component
public class SseAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain)
            throws ServletException, IOException {

        // Solo actuamos sobre el endpoint de suscripción SSE
        if (request.getRequestURI().startsWith(ApiPaths.EVENTS_SUBSCRIPTIONS)) {
            String token = request.getParameter("token");

            if (token != null && !token.isEmpty()) {
                // Creamos un wrapper del request para poder añadir la cabecera
                HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request) {
                    @Override
                    public String getHeader(String name) {
                        if ("Authorization".equals(name)) {
                            return "Bearer " + token;
                        }
                        return super.getHeader(name);
                    }
                };
                // Pasamos el request modificado al siguiente filtro
                filterChain.doFilter(wrapper, response);
                return;
            }
        }

        // Para cualquier otra petición, no hacemos nada
        filterChain.doFilter(request, response);
    }
}
