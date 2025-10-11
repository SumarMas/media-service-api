package com.platform.media_service.context;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
/**
 * An interceptor that propagates headers from the RequestContext
 * to outgoing HTTP requests made by RestTemplate or other HTTP clients.
 */
@Component
@AllArgsConstructor
public class HeaderPropagationInterceptor implements ClientHttpRequestInterceptor {

    /** Intercepts HTTP requests to propagate headers from the RequestContext. */
    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution
    ) throws IOException {

        RequestContext ctx = RequestContext.getCurrentContext();

        if (ctx != null) {
            if (ctx.getAuthorizationToken() != null) {
                request.getHeaders().add("Authorization", ctx.getAuthorizationToken());
            }
            if (ctx.getUserId() != null) {
                request.getHeaders().add("X-User-Id", ctx.getUserId());
            }
            if (ctx.getRoles() != null) {
                request.getHeaders().add("X-Roles", ctx.getRoles());
            }
            if (ctx.getRequestId() != null) {
                request.getHeaders().add("X-Request-Id", ctx.getRequestId());
            }
        }

        return execution.execute(request, body);
    }
}
