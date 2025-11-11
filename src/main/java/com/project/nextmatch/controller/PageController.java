package com.project.nextmatch.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

    @GetMapping("/") public String main() {return "main";}

    @GetMapping("/register-contest") public String registerContest() {return "register-contest";}

    @GetMapping("/login") public String login() {return "login";}

    @GetMapping("/signup") public String signup() {return "signup";}

    @GetMapping("/mypage") public String mypage() {return "mypage";}

    @GetMapping("/dashboard") public String dashboard() {return "dashboard";}
}
