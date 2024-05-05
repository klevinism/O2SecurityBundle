package com.o2dent.authentication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.SpringServletContainerInitializer;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class Auth {

    /**
     * @param args
     */
    public static void main(String[] args) {
        new SpringApplication().run(Auth.class, args);
    }

}
