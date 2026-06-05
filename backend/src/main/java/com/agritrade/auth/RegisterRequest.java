package com.agritrade.auth;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class RegisterRequest {
    @NotBlank
    @Size(min = 3, max = 32)
    private String username;
    @NotBlank
    @Size(min = 6, max = 64)
    private String password;
    @NotBlank
    @Pattern(regexp = "1[3-9]\\d{9}")
    private String phone;
    @Size(max = 32)
    private String nickname;
}
