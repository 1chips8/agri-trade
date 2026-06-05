package com.agritrade.category;

import com.agritrade.common.BizException;
import org.junit.jupiter.api.Test;

import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

class CategoryRequestValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void categoryRequestRequiresNameAndValidSortOrderAndStatus() throws Exception {
        ProductCategory category = new ProductCategory();
        category.setCategoryName(" ");
        category.setSortOrder(-1);
        category.setStatus("UNKNOWN");

        assertThat(validator.validate(category)).hasSize(3);
        assertThat(ProductCategory.class.getDeclaredField("categoryName").isAnnotationPresent(NotBlank.class)).isTrue();
        assertThat(ProductCategory.class.getDeclaredField("sortOrder").isAnnotationPresent(Min.class)).isTrue();
        assertThat(ProductCategory.class.getDeclaredField("status").isAnnotationPresent(Pattern.class)).isTrue();

        Method create = CategoryController.class.getDeclaredMethod("create", ProductCategory.class);
        Method update = CategoryController.class.getDeclaredMethod("update", Long.class, ProductCategory.class);
        assertThat(create.getParameters()[0].isAnnotationPresent(Valid.class)).isTrue();
        assertThat(update.getParameters()[1].isAnnotationPresent(Valid.class)).isTrue();
    }

    @Test
    void statusRequestRejectsUnsupportedStatus() {
        CategoryController controller = new CategoryController(null, null);

        assertThatThrownBy(() -> controller.status(1L, "UNKNOWN"))
                .isInstanceOf(BizException.class)
                .hasMessage("类目状态无效");
    }

    @Test
    void categoryOperationsRejectNonPositiveCategoryId() {
        CategoryController controller = new CategoryController(null, null);

        assertThatThrownBy(() -> controller.update(0L, new ProductCategory()))
                .isInstanceOf(BizException.class)
                .hasMessage("类目ID必须为正数");
        assertThatThrownBy(() -> controller.status(-1L, "ENABLED"))
                .isInstanceOf(BizException.class)
                .hasMessage("类目ID必须为正数");
        assertThatThrownBy(() -> controller.status(null, "DISABLED"))
                .isInstanceOf(BizException.class)
                .hasMessage("类目ID必须为正数");
    }
}
