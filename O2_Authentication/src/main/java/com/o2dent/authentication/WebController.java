package com.o2dent.authentication;

import com.o2dent.authentication.access.context.O2UserContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class WebController {

    @GetMapping(path = "/")
    public String index() {
        return "external";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) throws Exception {
        request.logout();
        return "redirect:/";
    }

    @O2UserContext(roles = "ROLE_CUSTOMER")
    @GetMapping(path = "/customers")
    public String customers(Principal principal, Model model) {
        model.addAttribute("customers", "customerDAO.stream().map(x -> x.getName()).toArray()");
        model.addAttribute("username", principal.getName());
        return "customers";
    }
}