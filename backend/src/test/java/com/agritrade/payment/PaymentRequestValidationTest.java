package com.agritrade.payment;

import org.junit.jupiter.api.Test;

import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentRequestValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void mockPayRequestRequiresOrderId() throws Exception {
        PaymentController.MockPayRequest request = new PaymentController.MockPayRequest();

        assertThat(validator.validate(request)).hasSize(1);
        assertThat(PaymentController.MockPayRequest.class.getDeclaredField("orderId").isAnnotationPresent(NotNull.class)).isTrue();

        Method method = PaymentController.class.getDeclaredMethod("mockPay", PaymentController.MockPayRequest.class);
        assertThat(method.getParameters()[0].isAnnotationPresent(Valid.class)).isTrue();
    }
}
