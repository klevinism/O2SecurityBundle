package com.o2dent.authentication.access.context;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Abstract class which provides the required reflection functionality and constraint functionality to find out
 * if the user has the right authorities to continue with the request.
 * By extending this class and by adding it as a constraint to an annotation class, it will initialize
 * a constraint validator for that annotation.
 * You can then use this annotation on whichever class/method you want to validate the right authorities.
 *
 * A generic constraint to be used to generate annotations that check the authenticated user's roles.
 *
 * @param <C> annotation type AnnotationClass
 */
abstract class O2AbstractContextConstraint<C extends java.lang.annotation.Annotation> implements ConstraintValidator<C,Object> {
    private final Log logger = LogFactory.getLog(O2AbstractContextConstraint.class);

    private String[] requiredRoles = null;

    /**
     * Initializer method of constraint. It's called when validation starts.
     *
     * @param constraintAnnotation C annotation type of constraint
     */
    @Override
    public void initialize(C constraintAnnotation) {
        try {
            requiredRoles = findAllRolesInAnnotation(constraintAnnotation);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method to validate weather user has the required authority provided by the annotation.
     *
     * @param o Object
     * @param context ConstraintValidatorContext
     * @return boolean weather validation succeeded or not
     */
    @Override
    public boolean isValid(Object o, ConstraintValidatorContext context) {
        if(requiredRoles == null || requiredRoles.length < 1){
            return false;
        }
        return isAuthorizedToMakeRequest(requiredRoles, SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * Checks if a user has the right authorities to continue with the request.
     * @param neededRoles String[]
     * @param authentication Authentication
     * @return boolean isAuthorized to make request
     */
    protected boolean isAuthorizedToMakeRequest(String[] neededRoles, Authentication authentication){
            // If user is authenticated
            if (!(authentication instanceof AnonymousAuthenticationToken)
                    && authentication != null && authentication.isAuthenticated()) {
                // If required roles don't match user's role
                if(!hasRequiredRoles(neededRoles, authentication.getAuthorities())){
                    // We will handle processing and prevent next handlers in chain from processing.
                    return false;
                }
            }
        return true;
    }

    /**
     * Checks if current user's roles match the ones provided from the handler's annotation
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
    private String[] findAllRolesInAnnotation(C annotation) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            var rolesMethod = annotation.annotationType().getDeclaredMethod("roles",null);
            var roles = rolesMethod.invoke(annotation);
            return (String[])roles;
    }

}
