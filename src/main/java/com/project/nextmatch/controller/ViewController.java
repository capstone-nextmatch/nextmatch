//1006 백송렬 작성
package com.project.nextmatch.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // @RestController가 아님!
public class ViewController {

    @GetMapping("/main")
    public String mainPage() {
        return "main"; // "main.html" 템플릿 파일을 찾아서 보여줘! 라는 뜻
    }

    // 추가로, 로그인과 회원가입 페이지를 보여주는 것도 만들 수 있습니다.
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // login.html을 보여줌
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup"; // signup.html을 보여줌
    }
}