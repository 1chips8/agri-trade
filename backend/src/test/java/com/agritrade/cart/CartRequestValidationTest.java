package com.agritrade.cart;

import com.agritrade.common.BizException;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.Valid;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CartRequestValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void addCartRequestRequiresProductAndPositiveQuantity() throws Exception {
        CartController.AddCartRequest request = new CartController.AddCartRequest();
        request.setQuantity(0);

        assertThat(validator.validate(request)).hasSize(2);
        assertRequestBodyIsValid("add", CartController.AddCartRequest.class);
        assertThat(CartController.AddCartRequest.class.getDeclaredField("productId").isAnnotationPresent(NotNull.class)).isTrue();
        assertThat(CartController.AddCartRequest.class.getDeclaredField("quantity").isAnnotationPresent(Min.class)).isTrue();
    }

    @Test
    void quantityRequestRequiresPositiveQuantity() throws Exception {
        CartController.QuantityRequest request = new CartController.QuantityRequest();
        request.setQuantity(0);

        assertThat(validator.validate(request)).hasSize(1);
        assertRequestBodyIsValid("updateQuantity", Long.class, CartController.QuantityRequest.class);
    }

    @Test
    void selectedRequestRequiresZeroOrOne() throws Exception {
        CartController.SelectedRequest request = new CartController.SelectedRequest();
        request.setSelected(2);

        assertThat(validator.validate(request)).hasSize(1);
        assertRequestBodyIsValid("selected", Long.class, CartController.SelectedRequest.class);
        assertRequestBodyIsValid("selectAll", CartController.SelectedRequest.class);
        assertThat(CartController.SelectedRequest.class.getDeclaredField("selected").isAnnotationPresent(Max.class)).isTrue();
    }

    @Test
    void cartItemOperationsRejectNonPositiveCartItemId() {
        CartController controller = new CartController(null, null);

        assertThatThrownBy(() -> controller.updateQuantity(0L, new CartController.QuantityRequest()))
                .isInstanceOf(BizException.class)
                .hasMessage("购物车项ID必须为正数");
        assertThatThrownBy(() -> controller.delete(-1L))
                .isInstanceOf(BizException.class)
                .hasMessage("购物车项ID必须为正数");
        assertThatThrownBy(() -> controller.selected(null, new CartController.SelectedRequest()))
                .isInstanceOf(BizException.class)
                .hasMessage("购物车项ID必须为正数");
    }

    private void assertRequestBodyIsValid(String methodName, Class<?>... parameterTypes) throws Exception {
        Method method = CartController.class.getDeclaredMethod(methodName, parameterTypes);
        assertThat(method.getParameters()[method.getParameterCount() - 1].isAnnotationPresent(Valid.class)).isTrue();
    }
}
