package com.rungenius.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;

@Controller
public class ProgramController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("today", LocalDate.now());
        return "index";
    }

    @GetMapping("/editor")
    public String editor(Model model) {
        model.addAttribute("today", LocalDate.now());
        return "editor";
    }
}