//백송렬 작성
package com.project.nextmatch.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

public class GongBackValidation implements ConstraintValidator<GongBack, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        // null 검사는 @NotBlank가 담당하도록 책임을 분리합니다.
        // (만약 @NotBlank 없이 @GongBack만 쓴다면 null 체크를 해야 함)
        if (value == null) {
            return true;
        }


        //공백(스페이스, 탭, 줄바꿈 등)이 "포함되어 있으면" true를 반환
        //공백이 "전혀 없으면" false를 반환
        return !StringUtils.containsWhitespace(value);
    }
}
