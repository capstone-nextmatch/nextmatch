package com.project.nextmatch.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/dashboard") public String dashboard() {return "dashboard";}

    @GetMapping("/signin") public String signin() {return "signin";}

    @GetMapping("/detail") public String detail() {return "detail";}
}
