package com.agritrade.auth;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RegisterRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String phone;
    private String nickname;
}
