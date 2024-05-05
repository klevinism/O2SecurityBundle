package com.o2dent.authentication.access;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Arrays;
@Component
@O2UserContext
public class O2UserContextFilter implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            O2UserContext methodAnnotation = handlerMethod.getBeanType().getAnnotation(O2UserContext.class);
            O2UserContext classAnnotation = handlerMethod.getMethodAnnotation(O2UserContext.class);
            if (methodAnnotation != null || classAnnotation != null) {
               var permissions = methodAnnotation.permissions();
               System.out.println(permissions + " << Permissions");

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (!(authentication instanceof AnonymousAuthenticationToken) && authentication != null) {
                    var p = authentication.getPrincipal();
                    var r = authentication.getAuthorities().stream().map( authority -> authority.getAuthority()).toList();

                    var perm = Arrays.stream(permissions).toList();

                    if(r.containsAll(perm)){
                        System.out.println("HEUREKA");
                    }
                    System.out.println(p + "<<<<<");
                    // do whatever is necessary
                }
            }
        }

        return true;
    }

//    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken) && authentication != null) {
            var p = authentication.getPrincipal();
            var r = authentication.getAuthorities();

            var a = RequestContextHolder.getRequestAttributes();

            if(r.stream().allMatch( x -> x.equals("ROLE_oxydent-user"))){

            }
            System.out.println(p + "<<<<<");
            // do whatever is necessary
        }

        filterChain.doFilter(request, response);
    }


}
