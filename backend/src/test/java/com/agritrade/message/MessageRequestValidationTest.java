package com.agritrade.message;

import com.agritrade.common.BizException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MessageRequestValidationTest {
    private final MessageController controller = new MessageController(null, null);

    @Test
    void batchReadRejectsEmptyIds() {
        assertThatThrownBy(() -> controller.batchRead(Collections.emptyList()))
                .isInstanceOf(BizException.class)
                .hasMessage("消息ID不能为空");
    }

    @Test
    void batchReadRejectsNullOrNonPositiveIds() {
        assertThatThrownBy(() -> controller.batchRead(Arrays.asList(1L, null)))
                .isInstanceOf(BizException.class)
                .hasMessage("消息ID必须为正数");

        assertThatThrownBy(() -> controller.batchRead(Arrays.asList(1L, 0L)))
                .isInstanceOf(BizException.class)
                .hasMessage("消息ID必须为正数");
    }
}
