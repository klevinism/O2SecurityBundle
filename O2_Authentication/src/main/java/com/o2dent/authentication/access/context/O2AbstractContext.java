package com.o2dent.authentication.access.context;

import com.o2dent.authentication.access.O2AccountInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * An abstract context class which provides required reflection functionality to its subclass.
 * By creating an annotation interface and having the handler method of a http/rest request we can
 * extract the handler method's, or it's responsible class' annotation. We can then check the roles provided
 * in this annotation against the authenticated user's roles.
 *
 * @param <T> annotation type
 */
abstract class O2AbstractContext<T extends java.lang.annotation.Annotation>{
    private final Log logger = LogFactory.getLog(O2AbstractContext.class);

    /**
     * Checks if a user has the right authorities to continue with the request.
     * @param handler HandlerMethod
     * @param annotationClass Class<T>
     * @return boolean
     */
    protected boolean isAuthorizedToMakeRequest(HandlerMethod handler, Class<T> annotationClass){
        // Check if request hits a HandlerMethod
            var annotation = this.getAnnotationFromClassOrMethod(handler, annotationClass);
            // If HandlerMethod class/method has O2 custom context annotation
            if (annotation != null) {
                var roles = this.findAllRolesInAnnotation(annotation);
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                // If user is authenticated
                if (!(authentication instanceof AnonymousAuthenticationToken)
                        && authentication != null && authentication.isAuthenticated()) {
                    O2AccountInfo account = (O2AccountInfo) authentication.getPrincipal();
                    // If required roles don't match user's role
                    if(!hasRequiredRoles(roles, account.getUserRoles())){
                        // We will handle processing and prevent next handlers in chain from processing.
                        return false;
                    }
                }
            }
        return true;
    }

    /**
     * Check if current user's roles match the ones provided from the handler's annotation
     * @param roles String[]
     * @param authorities Collection<? extends GrantedAuthority>
     * @return boolean hasRequiredRoles
     */
    private boolean hasRequiredRoles(String[] roles, Collection<? extends GrantedAuthority> authorities){
        var requiredRoles = List.of(roles);
        if(requiredRoles.isEmpty()) return false;
        if(requiredRoles.stream().anyMatch(role -> role.isEmpty() || role.isBlank())) return false;

        var oidcRoles = authorities.stream().map(GrantedAuthority::getAuthority).toList();
        return new HashSet<>(oidcRoles).containsAll(requiredRoles);
    }

    /**
     * Finds the 'roles' method in a context class via reflection and returns the method's value.
     * @return String[]
     */
    private String[] findAllRolesInAnnotation(T annotation) {
        var allAnnotationMethods = List.of(annotation.annotationType().getDeclaredMethods());
        var getRoleMethodIndex = allAnnotationMethods.contains("roles") ? allAnnotationMethods.indexOf("roles") : 0;
        var rolesMethod = allAnnotationMethods.get(getRoleMethodIndex);

        if(rolesMethod != null){
            try {
                var roles = rolesMethod.invoke(annotation);
                return (String[])roles;
            } catch (RuntimeException | IllegalAccessException | InvocationTargetException e) {
                logger.error(e.getCause() + " " +e.getMessage());
                logger.error("Continue...");
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    /**
     * Grabs the current annotation found in the handler's method, or it's member class.
     * @param methodOrClassHandler Object
     * @param o2ClassToFind Class<T>
     * @return T
     */
    private T getAnnotationFromClassOrMethod(Object methodOrClassHandler, Class<T> o2ClassToFind){
        HandlerMethod handlerMethod = (HandlerMethod) methodOrClassHandler;
        if(handlerMethod.getBeanType().getAnnotation(o2ClassToFind) != null){
            return handlerMethod.getBeanType().getAnnotation(o2ClassToFind);
        }else if(handlerMethod.getMethodAnnotation(o2ClassToFind) != null){
            return handlerMethod.getMethodAnnotation(o2ClassToFind);
        }
        return null;
    }
}
