package com.platform.media_service.context;

import lombok.AllArgsConstructor;
import lombok.Getter;
/**
 * A class to hold request-specific context information using ThreadLocal storage.
 */

@Getter
@AllArgsConstructor
public class RequestContext {
    /** ThreadLocal to hold the RequestContext for each thread. */
    private static final ThreadLocal<RequestContext> CONTEXT = new ThreadLocal<>();
    /** The user ID associated with the request. */
    private final String userId;
    /** The roles associated with the user making the request. */
    private final String roles;
    /** The unique request ID for tracking the request. */
    private final String requestId;
    /** The authorization token from the request header. */
    private final String authorizationToken;

    /** Static methods to manage the RequestContext in ThreadLocal storage.
     * @return the current RequestContext, or null if none is set.
     * */
    public static RequestContext getCurrentContext() {
        return CONTEXT.get();
    }

    /**
     * Sets the current RequestContext for the thread.
     * @param context the RequestContext to set.
     */
    public static void setCurrentContext(RequestContext context) {
        CONTEXT.set(context);
    }

    /**
     * Clears the current RequestContext from the thread.
     */
    public static void clear() {
        CONTEXT.remove();
    }
}
