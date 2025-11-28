//1006  백송렬 작성
package com.project.nextmatch.controller;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.project.nextmatch.PhoneDeserializer;
import com.project.nextmatch.service.MemberService;
import com.project.nextmatch.validation.GongBack;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

import java.security.AuthProvider;

@RestController // 이 클래스를 RESTful API의 컨트롤러로 선언합니다.
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    //회원가입 요청을 처리하는 DTO(Data Transfer Object)
    public static class SignupRequest {
        @NotBlank(message = "아이디는 비워둘 수 없습니다.")
        @GongBack(message = "아이디에는 공백을 포함할 수 없습니다.")
        public String username;

        @NotBlank(message = "이름은 비워둘 수 없습니다.")
        public String name;

        @NotBlank
        @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
        @GongBack(message = "비밀번호에는 공백을 포함할 수 없습니다.")
        @Pattern(regexp = ".*[^a-zA-Z0-9].*", message = "비밀번호에는 특수문자가 1개 이상 포함되어야 합니다.")
        public String password;

        @NotBlank
        @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
        @GongBack(message = "비밀번호 확인에는 공백을 포함할 수 없습니다.")
        @Pattern(regexp = ".*[^a-zA-Z0-9].*", message = "비밀번호 확인에는 특수문자가 1개 이상 포함되어야 합니다.")
        public String passwordConfirm;

        @NotBlank(message = "전화번호는 비워둘 수 없습니다.")
        @JsonDeserialize(using = PhoneDeserializer.class)
        public String phone;

        @NotBlank
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        public String email;

    }
    public static class LoginRequest {
        @NotBlank(message = "아이디는 비워둘 수 없습니다.")
        public String username;

        @NotBlank(message = "비밀번호는 비워둘 수 없습니다.")
        public String password;
    }


    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequest request) {
        try{
            memberService.signup(request.username, request.name, request.password,
                    request.passwordConfirm, request.phone, request.email);
            return ResponseEntity.ok("회원가입이 성공적으로 완료되었습니다.");
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 이 예제에서는 로그인 성공/실패만 간단히 반환합니다.
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try{
            memberService.login(request.username, request.password);
            httpRequest.login(request.username, request.password);
            return ResponseEntity.ok("로그인 성공");
        }catch (IllegalArgumentException e){
            //실패 시, 400 Bad Request 상태 코드와 에러 메시지 반환 (실패 보고)
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (ServletException e){
            return ResponseEntity.badRequest().body("로그인 처리에 실패했습니다.");
        }

    }

    public static class MeResponse {
        public String username;

        public MeResponse(String username) {
            this.username = username;
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("로그인되어 있지 않습니다.");
        }
        return ResponseEntity.ok(new MeResponse(principal.getName()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getAllErrors()
                .get(0)
                .getDefaultMessage();

        return ResponseEntity.badRequest().body(errorMessage);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        String errorMessage = ex.getMessage();
        return ResponseEntity.badRequest().body(errorMessage);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        // (이게 '동시성' 테스트에서 패자 스레드가 500 에러 대신 뱉을 메시지입니다)
        return ResponseEntity.badRequest().body("이미 사용 중인 아이디, 이메일 또는 전화번호입니다.");
    }
}
