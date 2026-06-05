package com.agritrade.product;

import com.agritrade.common.BizException;
import org.junit.jupiter.api.Test;

import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Method;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

class ProductRequestValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void productRequestRequiresCorePublishFields() throws Exception {
        Product product = new Product();
        product.setCategoryId(null);
        product.setProductName(" ");
        product.setPrice(BigDecimal.ZERO);
        product.setStock(-1);
        product.setUnit("");

        assertThat(validator.validate(product)).hasSize(5);
        assertThat(Product.class.getDeclaredField("categoryId").isAnnotationPresent(NotNull.class)).isTrue();
        assertThat(Product.class.getDeclaredField("productName").isAnnotationPresent(NotBlank.class)).isTrue();
        assertThat(Product.class.getDeclaredField("price").isAnnotationPresent(DecimalMin.class)).isTrue();
        assertThat(Product.class.getDeclaredField("stock").isAnnotationPresent(Min.class)).isTrue();
        assertThat(Product.class.getDeclaredField("unit").isAnnotationPresent(NotBlank.class)).isTrue();

        Method create = ProductController.class.getDeclaredMethod("create", Product.class);
        Method update = ProductController.class.getDeclaredMethod("update", Long.class, Product.class);
        assertThat(create.getParameters()[0].isAnnotationPresent(Valid.class)).isTrue();
        assertThat(update.getParameters()[1].isAnnotationPresent(Valid.class)).isTrue();
    }

    @Test
    void productOperationsRejectNonPositiveProductId() {
        ProductController controller = new ProductController(null);

        assertThatThrownBy(() -> controller.update(0L, new Product()))
                .isInstanceOf(BizException.class)
                .hasMessage("商品ID必须为正数");
        assertThatThrownBy(() -> controller.onSale(-1L))
                .isInstanceOf(BizException.class)
                .hasMessage("商品ID必须为正数");
        assertThatThrownBy(() -> controller.offSale(0L))
                .isInstanceOf(BizException.class)
                .hasMessage("商品ID必须为正数");
        assertThatThrownBy(() -> controller.approve(null))
                .isInstanceOf(BizException.class)
                .hasMessage("商品ID必须为正数");
        assertThatThrownBy(() -> controller.reject(0L))
                .isInstanceOf(BizException.class)
                .hasMessage("商品ID必须为正数");
        assertThatThrownBy(() -> controller.detail(-1L))
                .isInstanceOf(BizException.class)
                .hasMessage("商品ID必须为正数");
    }
}
