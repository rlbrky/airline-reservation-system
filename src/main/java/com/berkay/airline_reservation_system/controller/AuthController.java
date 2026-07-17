package com.berkay.airline_reservation_system.controller;

import com.berkay.airline_reservation_system.model.AirlineUser;
import com.berkay.airline_reservation_system.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
         model.addAttribute("registerForm", new RegisterForm()); // add fresh form

        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerForm") RegisterForm registerForm,
                           BindingResult bindingResult,
                           RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            ra.addFlashAttribute("error", "Your form has errors.");
            return "register";
        }

        try {
            userService.register(
                    registerForm.getUsername(),
                    registerForm.getPassword(),
                    registerForm.getFullName()
            );

            ra.addFlashAttribute("message", "Account created -- Please login.");
        } catch (IllegalArgumentException e){
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }

        return "redirect:/login";
    }

    @GetMapping("/")
    public String home(Principal principal, Model model) {
        // Needs to be done this way to show full name instead of username.
        AirlineUser user = userService.getByUsername(principal.getName());
        model.addAttribute("fullName", user.getFullName());

        return "home";
    }
}
