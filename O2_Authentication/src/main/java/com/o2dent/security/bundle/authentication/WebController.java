package com.o2dent.security.bundle.authentication;

import com.o2dent.security.bundle.authentication.access.context.O2UserContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class WebController {

//    @GetMapping(path = "/")
//    public String index() {
//        return "external";
//    }
//
//    @GetMapping("/logout")
//    public String logout(HttpServletRequest request) throws Exception {
//        request.logout();
//        return "redirect:/";
//    }

    @O2UserContext(roles = "ROLE_CUSTOMER")
    @GetMapping(path = "/customers_test")
    public String customersTest(Principal principal, Model model) {
        model.addAttribute("customers", "customerDAO.stream().map(x -> x.getName()).toArray()");
        model.addAttribute("username", principal.getName());
        return "customers";
    }
}