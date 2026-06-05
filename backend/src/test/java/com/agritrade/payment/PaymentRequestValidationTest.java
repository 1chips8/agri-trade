package com.agritrade.payment;

import com.agritrade.common.BizException;
import org.junit.jupiter.api.Test;

import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentRequestValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void mockPayRequestRequiresOrderId() throws Exception {
        PaymentController.MockPayRequest request = new PaymentController.MockPayRequest();

        assertThat(validator.validate(request)).hasSize(1);
        assertThat(PaymentController.MockPayRequest.class.getDeclaredField("orderId").isAnnotationPresent(NotNull.class)).isTrue();
        assertThat(PaymentController.MockPayRequest.class.getDeclaredField("orderId").isAnnotationPresent(Min.class)).isTrue();

        Method method = PaymentController.class.getDeclaredMethod("mockPay", PaymentController.MockPayRequest.class);
        assertThat(method.getParameters()[0].isAnnotationPresent(Valid.class)).isTrue();
    }

    @Test
    void mockPayRequestRequiresPositiveOrderId() {
        PaymentController.MockPayRequest request = new PaymentController.MockPayRequest();
        request.setOrderId(0L);

        assertThat(validator.validate(request)).hasSize(1);
    }

    @Test
    void paymentOrderQueryRejectsNonPositiveOrderId() {
        PaymentController controller = new PaymentController(null);

        assertThatThrownBy(() -> controller.byOrder(0L))
                .isInstanceOf(BizException.class)
                .hasMessage("订单ID必须为正数");
        assertThatThrownBy(() -> controller.byOrder(-1L))
                .isInstanceOf(BizException.class)
                .hasMessage("订单ID必须为正数");
        assertThatThrownBy(() -> controller.byOrder(null))
                .isInstanceOf(BizException.class)
                .hasMessage("订单ID必须为正数");
    }
}
