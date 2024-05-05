package com.o2dent.authentication.access;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class O2AuthoritiesMapper implements GrantedAuthoritiesMapper {
    private static final String GROUPS = "groups";
    private static final String REALM_ACCESS_CLAIM = "realm_access";
    private static final String ROLES_CLAIM = "roles";

    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
        var authority = authorities.iterator().next();
        boolean isOidc = authority instanceof OidcUserAuthority;

        if (isOidc) {
            var oidcUserAuthority = (OidcUserAuthority) authority;
            var userInfo = oidcUserAuthority.getUserInfo();

            // Tokens can be configured to return roles under
            // Groups or REALM ACCESS hence have to check both
            if (userInfo.hasClaim(REALM_ACCESS_CLAIM)) {
                var realmAccess = userInfo.getClaimAsMap(REALM_ACCESS_CLAIM);
                var roles = (Collection<String>) realmAccess.get(ROLES_CLAIM);
                mappedAuthorities.addAll(generateAuthoritiesFromClaim(roles));
            } else if (userInfo.hasClaim(GROUPS)) {
                Collection<String> roles = (Collection<String>) userInfo.getClaim(
                        GROUPS);
                mappedAuthorities.addAll(generateAuthoritiesFromClaim(roles));
            }
        } else {
            var oauth2UserAuthority = (OAuth2UserAuthority) authority;
            Map<String, Object> userAttributes = oauth2UserAuthority.getAttributes();

            if (userAttributes.containsKey(REALM_ACCESS_CLAIM)) {
                Map<String, Object> realmAccess = (Map<String, Object>) userAttributes.get(
                        REALM_ACCESS_CLAIM);
                Collection<String> roles = (Collection<String>) realmAccess.get(ROLES_CLAIM);
                mappedAuthorities.addAll(generateAuthoritiesFromClaim(roles));
            }
        }
        return mappedAuthorities;
    }
    private Collection generateAuthoritiesFromClaim(Collection roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList();
    }

}
