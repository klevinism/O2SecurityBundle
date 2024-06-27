package com.o2dent.security.bundle.authentication.access;

import com.o2dent.lib.accounts.entity.Account;
import com.o2dent.lib.accounts.entity.Business;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;
import java.util.Map;

public class O2AccountInfo implements OidcUser {
    private Account account;
    private Business currentBusiness;
    private final OidcUser oidcUser;
    private final Map<String, Object> attributes;
    private final Collection<? extends GrantedAuthority> userRoles;
    /**
     *
     * Since the introduction of keycloak oauth2 authentication
     * and because of uncoupling of Account,Business and Roles
     * we need to check and set global settings to business.
     * @return string
     */
    private Object currentBusinessSettings;
    private boolean isPersonnel;

    public O2AccountInfo(OidcUser oidcUser) {
        this.oidcUser = oidcUser;
        this.attributes = oidcUser.getClaims();
        this.userRoles = oidcUser.getAuthorities();
    }
    public OidcUser getOidcUser() {return oidcUser;}
    public Account getAccount() {return account;}
    public void setAccount(Account account) {this.account = account;}
    public Business getCurrentBusiness() {return currentBusiness;}
    public void setCurrentBusiness(Business currentBusiness) {this.currentBusiness = currentBusiness;}
    public boolean isPersonnel() {
        return isPersonnel;
    }
    public void setPersonnel(boolean isPersonnel) {
        this.isPersonnel = isPersonnel;
    }
    // remove when business becomes it's own service;
    public Object getCurrentBusinessSettings() {return currentBusinessSettings;}
    public void setCurrentBusinessSettings(Object currentBusinessSettings) {this.currentBusinessSettings = currentBusinessSettings;}
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
