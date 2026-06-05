package com.agritrade.common;

import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerValidationTest {
    @Test
    void validationErrorsIncludeFieldDetails() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "quantity", "must be greater than or equal to 1"));
        bindingResult.addError(new FieldError("request", "productId", "must not be null"));

        Result<Void> result = new GlobalExceptionHandler().handleValid(new BindException(bindingResult));

        assertThat(result.getCode()).isEqualTo(1);
        assertThat(result.getMessage()).contains("参数校验失败");
        assertThat(result.getMessage()).contains("quantity: must be greater than or equal to 1");
        assertThat(result.getMessage()).contains("productId: must not be null");
    }
}
