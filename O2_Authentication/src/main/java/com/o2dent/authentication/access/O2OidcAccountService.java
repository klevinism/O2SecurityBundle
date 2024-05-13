package com.o2dent.authentication.access;

import com.o2dent.lib.accounts.Account;
import com.o2dent.lib.accounts.AccountService;
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

    private
    O2AccountInfo processOidcUser(OidcUserRequest userRequest, OidcUser oidcUser) {
        O2AccountInfo accountInfo = new O2AccountInfo(oidcUser);
        // see what other data from userRequest or oidcUser you need
        Optional<Account> account = accountService.findByEmail(accountInfo.getEmail());

        if (!account.isPresent()) {
            Account newUser = new Account();
            newUser.setAccount(true);
            newUser.setEmail(oidcUser.getEmail());
            newUser.setName(oidcUser.getGivenName());
            newUser.setActive(true);
            newUser.setEnabled(true);
            newUser.setSurname(oidcUser.getFamilyName());
            newUser.setRoles(null);
//            newUser.setUsername(oidcUser.getPreferredUsername());
//            user.setEmail(googleUserInfo.getEmail());
//            user.setName(googleUserInfo.getName());

            // set other needed data

            account = Optional.of(accountService.createPlain(newUser));
        }

        if(account.isPresent() && account.get().getRoles() != null){
            var oidcAuthorities = account.get().getRoles().stream().map(role -> role.getName()).collect(Collectors.toList());

            // Add oxydent db roles to oidc (oauth2userinfo) roles
            if(accountInfo.getUserInfo().hasClaim("groups") && !oidcAuthorities.isEmpty()){
                ((Collection<String>)accountInfo.getUserInfo().getClaim("groups")).addAll(oidcAuthorities);
            }
        }

        return accountInfo;
    }
}