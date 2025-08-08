package com.simba.project01.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class RegisterRequest
{
    @Size(min = 4, max = 15, message = "ID는 4자 이상 15자 이하여야 합니다.")
    @NotBlank(message = "아이디는 필수 입력 항목입니다.")
    private String username;

    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    private String password;

    @Email(message = "올바른 이메일 형식을 입력하세요.")
    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    private String email;
}
