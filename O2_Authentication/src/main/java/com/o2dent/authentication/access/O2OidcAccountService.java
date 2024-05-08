package com.o2dent.authentication.access;

import com.o2dent.lib.accounts.Account;
import com.o2dent.lib.accounts.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import java.util.Optional;
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

        Optional<Account> account = accountService.findByEmail("klevindelimeta@hotmail.com");

        Optional<Object> userOptional = Optional.empty();//userRepository.findByEmail(accountInfo.getEmail());

        if (!userOptional.isPresent()) {
            Object user = new Object();
//            user.setEmail(googleUserInfo.getEmail());
//            user.setName(googleUserInfo.getName());

            // set other needed data

            //userRepository.save(user);
        }

        return accountInfo;
    }
}