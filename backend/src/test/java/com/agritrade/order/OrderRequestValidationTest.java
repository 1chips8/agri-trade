package com.agritrade.order;

import com.agritrade.common.BizException;
import org.junit.jupiter.api.Test;

import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Test
    void orderOperationsRejectNonPositiveOrderId() {
        OrderController controller = new OrderController(null);

        assertThatThrownBy(() -> controller.detail(0L))
                .isInstanceOf(BizException.class)
                .hasMessage("订单ID必须为正数");
        assertThatThrownBy(() -> controller.cancel(-1L, null))
                .isInstanceOf(BizException.class)
                .hasMessage("订单ID必须为正数");
        assertThatThrownBy(() -> controller.ship(null))
                .isInstanceOf(BizException.class)
                .hasMessage("订单ID必须为正数");
        assertThatThrownBy(() -> controller.receive(0L))
                .isInstanceOf(BizException.class)
                .hasMessage("订单ID必须为正数");
    }
}
