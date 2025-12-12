//백송렬 작성
package com.project.nextmatch;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.util.StringUtils;

import java.io.IOException;

//"-"와 " "를 모두 제거합니다.
public class PhoneDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        //JSON에서 날것의 문자열 값을 가져옵니다. (예: "010-1234-5678")
        String value = p.getText();

        //만약 값이 비어있으면, 굳이 변환하지 않고 그대로 둡니다.
        //(빈 값 검사는 @NotBlank가 알아서 할 겁니다)
        if (!StringUtils.hasText(value)) {
            return value;
        }

        //일꾼의 핵심 로직: "-"와 " " (공백)을 모두 제거합니다.
        String normalizedPhone = value.replaceAll("-", "").replaceAll(" ", "");

        //깨끗한 값을 DTO에 최종적으로 반환합니다. (예: "01012345678")
        return normalizedPhone;
    }
}