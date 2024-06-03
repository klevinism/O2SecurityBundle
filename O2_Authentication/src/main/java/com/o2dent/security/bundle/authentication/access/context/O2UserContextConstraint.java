package com.o2dent.security.bundle.authentication.access.context;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@O2UserContext
public class O2UserContextConstraint extends O2AbstractContextConstraint<O2UserContext>
        implements HandlerInterceptor {
    @Override
    public void initialize(O2UserContext constraintAnnotation) {
        super.initialize(constraintAnnotation);
    }
}
