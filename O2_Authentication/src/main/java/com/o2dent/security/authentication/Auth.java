package com.o2dent.security.authentication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication()
public class Auth {

    /**
     * @param args
     */
    public static void main(String[] args) {
        new SpringApplication().run(Auth.class, args);
    }

}
