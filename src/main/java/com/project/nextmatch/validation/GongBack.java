//백송렬 작성
package com.project.nextmatch.validation;

import com.project.nextmatch.validation.GongBackValidation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 사용자 정의 유효성 검사 어노테이션 (일명 'GongBack')
 * 이 필드에 공백(whitespace)이 포함되어 있는지 검사합니다.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER}) // 이 어노테이션을 필드와 파라미터에 쓸 수 있게 함
@Retention(RetentionPolicy.RUNTIME) // 런타임(실행 중)에도 이 어노테이션 정보를 유지함
@Constraint(validatedBy = GongBackValidation.class) // 이 어노테이션의 검사 로직은 GongBackValidation 클래스가 담당
public @interface GongBack {

    //기본 메시지
    String message() default "공백을 포함할 수 없습니다.";

    //(이하 2개는 유효성 검사 어노테이션의 필수 상용구)
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
