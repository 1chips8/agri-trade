package com.agritrade.order;

import org.junit.jupiter.api.Test;

import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class OrderRequestValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void createOrderRequestRequiresReceiverFields() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setReceiverName(" ");
        request.setReceiverPhone("");
        request.setReceiverAddress(null);

        assertThat(validator.validate(request)).hasSize(3);
        assertThat(CreateOrderRequest.class.getDeclaredField("receiverName").isAnnotationPresent(NotBlank.class)).isTrue();
        assertThat(CreateOrderRequest.class.getDeclaredField("receiverPhone").isAnnotationPresent(NotBlank.class)).isTrue();
        assertThat(CreateOrderRequest.class.getDeclaredField("receiverAddress").isAnnotationPresent(NotBlank.class)).isTrue();

        Method method = OrderController.class.getDeclaredMethod("create", CreateOrderRequest.class);
        assertThat(method.getParameters()[0].isAnnotationPresent(Valid.class)).isTrue();
    }
}
