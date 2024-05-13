package com.o2dent.authentication.access;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;
import java.util.Map;
public class O2AccountInfo implements OidcUser {
    private final OidcUser oidcUser;
    private final Map<String, Object> attributes;
    private final Collection<? extends GrantedAuthority> userRoles;

    public O2AccountInfo(OidcUser oidcUser) {
        this.oidcUser = oidcUser;
        this.attributes = oidcUser.getClaims();
        this.userRoles = oidcUser.getAuthorities();
    }

    public String getId() {
        return (String) attributes.get("sub");
    }

    public String getName() {
        return (String) attributes.get("name");
    }

    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public Map<String, Object> getClaims() {
        return oidcUser.getClaims();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return oidcUser.getUserInfo();
    }

    @Override
    public OidcIdToken getIdToken() {
        return oidcUser.getIdToken();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oidcUser.getAttributes();
    }

    public Collection<String> getGroups(){ return (Collection<String>)oidcUser.getClaim("groups");}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oidcUser.getAuthorities();
    }

    public Collection<? extends GrantedAuthority> getUserRoles() {
        return this.userRoles;
    }
}
