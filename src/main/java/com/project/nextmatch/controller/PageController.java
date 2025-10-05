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


    @GetMapping("/main") public String main() {return "main";}

    @GetMapping("/login") public String login() {return "login";}

    @GetMapping("/signup") public String signup() {return "signup";}

    @GetMapping("/mypage") public String mypage() {return "mypage";}
}
