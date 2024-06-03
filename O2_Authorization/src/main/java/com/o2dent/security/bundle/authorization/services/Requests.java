package com.o2dent.security.bundle.authorization.services;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class Requests {
    private WebClient webClient;

    public Requests(WebClient webClient) {
        this.webClient = webClient;
    }

    public Object someRestCall() {
        var result = webClient.get().uri("/api/test_call")
                .retrieve().bodyToMono(String.class).block();

        System.out.println(" RESULT of call = " + result.toString());
        return result;
    }
}
