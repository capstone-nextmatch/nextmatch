//1006 백송렬 작성
package com.project.nextmatch.controller;

import com.project.nextmatch.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // 이 클래스를 RESTful API의 컨트롤러로 선언합니다.
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 회원가입 요청을 처리하는 DTO(Data Transfer Object)
    public static class SignupRequest {
        @NotBlank(message = "아이디는 비워둘 수 없습니다.")
        public String userId;

        @NotBlank(message = "이름은 비워둘 수 없습니다.")
        public String name;

        @NotBlank
        @Size(min = 4, message = "비밀번호는 4자 이상이어야 합니다.")
        public String password;

        @NotBlank
        @Size(min = 4, message = "비밀번호는 4자 이상이어야 합니다.")
        public String passwordConfirm;

        @NotBlank(message = "전화번호는 비워둘 수 없습니다.")
        public String phone;

        @NotBlank
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        public String email;

    }


    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequest request) {
        try{
            userService.signup(request.userId, request.name, request.password,
                    request.passwordConfirm, request.phone, request.email);
            return ResponseEntity.ok("회원가입이 성공적으로 완료되었습니다.");
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 이 예제에서는 로그인 성공/실패만 간단히 반환합니다.
    // 실제로는 로그인 성공 시 JWT 토큰을 반환하거나 세션을 생성해야 합니다.
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody SignupRequest request) {
        try{
            userService.login(request.userId, request.password);
            return ResponseEntity.ok("로그인 성공");
        }catch (IllegalArgumentException e){
            //실패 시, 400 Bad Request 상태 코드와 에러 메시지 반환 (실패 보고)
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
}
