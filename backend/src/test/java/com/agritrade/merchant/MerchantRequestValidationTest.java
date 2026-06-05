package com.agritrade.merchant;

import com.agritrade.common.BizException;
import org.junit.jupiter.api.Test;

import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MerchantRequestValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void merchantApplyRequiresCoreContactFields() throws Exception {
        MerchantApply request = new MerchantApply();
        request.setMerchantName(" ");
        request.setContactName("");
        request.setContactPhone(null);
        request.setOriginAddress(" ");

        assertThat(validator.validate(request)).hasSize(4);
        assertThat(MerchantApply.class.getDeclaredField("merchantName").isAnnotationPresent(NotBlank.class)).isTrue();
        assertThat(MerchantApply.class.getDeclaredField("contactName").isAnnotationPresent(NotBlank.class)).isTrue();
        assertThat(MerchantApply.class.getDeclaredField("contactPhone").isAnnotationPresent(NotBlank.class)).isTrue();
        assertThat(MerchantApply.class.getDeclaredField("originAddress").isAnnotationPresent(NotBlank.class)).isTrue();

        Method method = MerchantController.class.getDeclaredMethod("apply", MerchantApply.class);
        assertThat(method.getParameters()[0].isAnnotationPresent(Valid.class)).isTrue();
    }

    @Test
    void rejectRequestRequiresReason() throws Exception {
        MerchantController.RejectRequest request = new MerchantController.RejectRequest();
        request.setRejectReason(" ");

        assertThat(validator.validate(request)).hasSize(1);
        assertThat(MerchantController.RejectRequest.class.getDeclaredField("rejectReason").isAnnotationPresent(NotBlank.class)).isTrue();

        Method method = MerchantController.class.getDeclaredMethod("reject", Long.class, MerchantController.RejectRequest.class);
        assertThat(method.getParameters()[1].isAnnotationPresent(Valid.class)).isTrue();
    }

    @Test
    void merchantApplicationOperationsRejectNonPositiveApplyId() {
        MerchantController controller = new MerchantController(null);

        assertThatThrownBy(() -> controller.detail(0L))
                .isInstanceOf(BizException.class)
                .hasMessage("商家申请ID必须为正数");
        assertThatThrownBy(() -> controller.approve(-1L))
                .isInstanceOf(BizException.class)
                .hasMessage("商家申请ID必须为正数");
        assertThatThrownBy(() -> controller.reject(null, new MerchantController.RejectRequest()))
                .isInstanceOf(BizException.class)
                .hasMessage("商家申请ID必须为正数");
    }
}
