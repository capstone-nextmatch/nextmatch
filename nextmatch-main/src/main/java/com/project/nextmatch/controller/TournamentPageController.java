// src/main/java/com/project/nextmatch/controller/TournamentPageController.java
package com.project.nextmatch.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tournaments")
public class TournamentPageController {

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        // 지금은 목데이터/타이틀만
        model.addAttribute("id", id);
        model.addAttribute("title", "예시 대회 " + id);
        model.addAttribute("date", "2025-10-20");
        model.addAttribute("location", "서울");
        model.addAttribute("desc", "이곳에 대회 상세 설명이 들어갑니다.");
        return "tournament-detail";
    }
}
