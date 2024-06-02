package com.o2dent.authentication.config;

import com.o2dent.authentication.access.O2AuthoritiesMapper;
import com.o2dent.authentication.access.O2OidcAccountService;
import com.o2dent.lib.accounts.Configurations;
import com.o2dent.lib.accounts.persistence.AccountService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@Import(Configurations.class)
public class O2WebMvcConfiguration implements WebMvcConfigurer {

    private final O2OidcAccountService o2OidcAccountService;
    private final AccountService accountService;

    public O2WebMvcConfiguration(O2OidcAccountService o2OidcAccountService, AccountService accountService) {
        this.o2OidcAccountService = o2OidcAccountService;
        this.accountService = accountService;
    }

    @Bean
    public SecurityFilterChain resourceServerFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(new AntPathRequestMatcher("/customers*", HttpMethod.OPTIONS.name()))
                        .authenticated()
                        .requestMatchers(new AntPathRequestMatcher("/customers*"))
                        .hasRole("oxydent-user")
                        .requestMatchers(new AntPathRequestMatcher("/"))
                        .permitAll());
        http.oauth2ResourceServer((oauth2) -> oauth2
                .jwt(Customizer.withDefaults()));
        http.oauth2Login(oauth2 ->
                        oauth2.userInfoEndpoint(userInfoEndpointConfig ->
                                userInfoEndpointConfig
                                        .oidcUserService(this.o2OidcAccountService)
                                        .userAuthoritiesMapper(this.userAuthoritiesMapperForKeycloak())))
                .logout(logout -> logout.logoutSuccessUrl("/"));
        return http.build();
    }

    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapperForKeycloak() {
        return new O2AuthoritiesMapper();
    }

}
