package com.agritrade.auth;

import org.junit.jupiter.api.Test;
import org.springframework.validation.annotation.Validated;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class AuthRequestValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void registerRequestRequiresCredentialShape() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("ab");
        request.setPassword("12345");
        request.setPhone("123");
        request.setNickname("123456789012345678901234567890123");

        assertThat(validator.validate(request)).hasSize(4);
        assertThat(RegisterRequest.class.getDeclaredField("username").isAnnotationPresent(Size.class)).isTrue();
        assertThat(RegisterRequest.class.getDeclaredField("password").isAnnotationPresent(Size.class)).isTrue();
        assertThat(RegisterRequest.class.getDeclaredField("phone").isAnnotationPresent(Pattern.class)).isTrue();
        assertThat(RegisterRequest.class.getDeclaredField("nickname").isAnnotationPresent(Size.class)).isTrue();

        Method method = AuthController.class.getDeclaredMethod("register", RegisterRequest.class);
        assertThat(method.getParameters()[0].isAnnotationPresent(Validated.class)).isTrue();
    }

    @Test
    void loginRequestRequiresCredentialShape() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("ab");
        request.setPassword("12345");

        assertThat(validator.validate(request)).hasSize(2);
        assertThat(LoginRequest.class.getDeclaredField("username").isAnnotationPresent(Size.class)).isTrue();
        assertThat(LoginRequest.class.getDeclaredField("password").isAnnotationPresent(Size.class)).isTrue();

        Method method = AuthController.class.getDeclaredMethod("login", LoginRequest.class);
        assertThat(method.getParameters()[0].isAnnotationPresent(Validated.class)).isTrue();
    }
}
