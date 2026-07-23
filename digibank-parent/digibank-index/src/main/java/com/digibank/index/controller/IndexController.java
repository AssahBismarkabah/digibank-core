package com.digibank.index.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("appName", "Digi Bank Core");
        model.addAttribute("version", "1.0.0");
        model.addAttribute("framework", "Spring Boot 4.1.0");
        return "index";
    }
}
