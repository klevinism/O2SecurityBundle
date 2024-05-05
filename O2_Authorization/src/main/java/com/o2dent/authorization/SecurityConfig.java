package com.o2dent.authorization;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.*;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

//    @Bean
//    ClientRegistrationRepository clientRegistrations() {
//        ClientRegistration clientRegistration = ClientRegistrations
//                .fromOidcIssuerLocation("http://localhost:8080/realms/master")
//                .clientId("oxydent-module-authorization")
//                .clientSecret("FUNUnK94fpvhvrvi1aXYygEPVLmnf0W6")
//                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
//                .registrationId("keycloak")
//                .build();
//        return new InMemoryClientRegistrationRepository(clientRegistration);
//    }
    @Bean
    OAuth2AuthorizedClientManager authorizedClientManager
            (ClientRegistrationRepository clientRegistrationRepository) {
        OAuth2AuthorizedClientService service =
                new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
        AuthorizedClientServiceOAuth2AuthorizedClientManager manager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository, service);
        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .clientCredentials()
                        .build();
        manager.setAuthorizedClientProvider(authorizedClientProvider);
        return manager;
    }

    @Bean
    WebClient webClient(OAuth2AuthorizedClientManager authorizedClientManager) {
        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
                new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth2Client.setDefaultOAuth2AuthorizedClient(true);
        oauth2Client.setDefaultClientRegistrationId("keycloak");

        return WebClient.builder()
                .baseUrl("http://localhost:8081/")
                .exchangeStrategies(ExchangeStrategies.builder().codecs(c ->
                        c.defaultCodecs().enableLoggingRequestDetails(true)).build()
                )
                .filter(oauth2Client)
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(httpRequest -> httpRequest.anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer.jwt(Customizer.withDefaults()))
                .build();
    }

}
