package com.o2dent.security.bundle.authentication;

import com.o2dent.security.bundle.authentication.access.context.O2UserContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@O2UserContext
@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/test_call")
    public String home(){
        System.out.println(" hello fron authentication");

        return "Hello from authentication";
    }
}
