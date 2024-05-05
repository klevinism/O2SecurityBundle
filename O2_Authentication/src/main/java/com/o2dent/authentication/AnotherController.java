package com.o2dent.authentication;

import com.o2dent.authentication.access.O2UserContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@O2UserContext
@RestController
@RequestMapping("/api")
public class AnotherController {

    private HttpServletRequest context;

    @GetMapping("/test_call")
    public String home(){

        System.out.println(" hello fron authentication");
        System.out.println(context);

        return "Hello from authentication";
    }
}
