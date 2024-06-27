package com.o2dent.security.bundle.authentication.access;

import com.o2dent.lib.accounts.entity.Account;
import com.o2dent.lib.accounts.entity.Business;
import com.o2dent.lib.accounts.persistence.AccountService;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class O2OidcAccountService extends OidcUserService {
    private final AccountService accountService;
    public O2OidcAccountService(AccountService accountService) {
        this.accountService = accountService;
    }
    @Override
    public O2AccountInfo loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        try {
            return processOidcUser(userRequest, oidcUser);
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private O2AccountInfo processOidcUser(OidcUserRequest userRequest, OidcUser oidcUser) {
        // Transform OidcUser into general O2AccountInfo
        O2AccountInfo accountInfo = new O2AccountInfo(oidcUser);

        // Find Account in o2dent's db to aggregate roles in the O2AccountInfo roles
        Optional<Account> account = accountService.findByEmail(accountInfo.getEmail());

        if (!account.isPresent()) {
            Account newUser = new Account();
            newUser.setAccount(true);
            newUser.setEmail(oidcUser.getEmail());
            newUser.setUsername(oidcUser.getPreferredUsername());
            newUser.setName(oidcUser.getGivenName());
            newUser.setActive(true);
            newUser.setEnabled(true);
            newUser.setSurname(oidcUser.getFamilyName());
            newUser.setRoles(null);

            account = Optional.of(accountService.createPlain(newUser));
        }else{
            if(account.get().getRoles() != null){
                var oidcAuthorities = account.get().getRoles().stream().map(role -> role.getName()).collect(Collectors.toList());

                Optional<Business> currentBusiness = account.get().getBusinesses().stream().findFirst();
                currentBusiness.ifPresent( business -> accountInfo.setCurrentBusiness(business));

                // Add oxydent db roles to oidc (oauth2userinfo) roles
                if(accountInfo.getUserInfo().hasClaim("groups") && !oidcAuthorities.isEmpty()){
                    ((Collection<String>)accountInfo.getUserInfo().getClaim("groups")).addAll(oidcAuthorities);
                }
            }
        }

        accountInfo.setAccount(account.get());

        return accountInfo;
    }
}