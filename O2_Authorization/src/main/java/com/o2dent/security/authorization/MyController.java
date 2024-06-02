package com.o2dent.security.authorization;

import com.o2dent.security.authorization.services.Requests;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MyController {

    Requests requests;

    public MyController(Requests requests){
        this.requests = requests;
    }
    @GetMapping("/home")
    public String home(){
        System.out.println("sending rest call");
        requests.someRestCall();
        return "Hello from authorization";
    }
}
