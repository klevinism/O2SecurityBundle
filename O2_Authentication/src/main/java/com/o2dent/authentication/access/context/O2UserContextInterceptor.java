package com.o2dent.authentication.access.context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
@O2UserContext
public class O2UserContextInterceptor extends O2AbstractContext<O2UserContext> implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        // Check if request hits a HandlerMethod
        if (handler instanceof HandlerMethod) {
            if(!this.isAuthorizedToMakeRequest((HandlerMethod) handler, O2UserContext.class)){
                // Intercept the request because we take care of it.
                response.sendError(500, "You do not have the right permissions for this request!");
                // Stop request from continuing.
                return false;
            }
        }
        return true;
    }
}
