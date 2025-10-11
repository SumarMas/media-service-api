package com.platform.media_service.context;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
/**
 * A filter that extracts request context information from HTTP headers
 * and stores it in a ThreadLocal variable for the duration of the request.
 */
@Component
@AllArgsConstructor
public class ContextPropagationFilter extends OncePerRequestFilter {

    /** Extracts context information from
     * HTTP headers and sets it in RequestContext. */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String userId = request.getHeader("X-User-Id");
        String roles = request.getHeader("X-Roles");
        String requestId = request.getHeader("X-Request-Id");
        String authorizationToken = request.getHeader("Authorization");
        RequestContext context = new RequestContext(
                userId,
                roles,
                requestId,
                authorizationToken
        );

        RequestContext.setCurrentContext(context);
        if (requestId != null) {
            MDC.put("X-Request-Id", requestId);
        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            RequestContext.clear();
            MDC.clear();
        }
    }
}
