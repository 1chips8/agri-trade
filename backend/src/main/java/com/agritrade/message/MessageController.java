package com.agritrade.message;

import cn.dev33.satoken.stp.StpUtil;
import com.agritrade.common.Result;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageNoticeMapper messageMapper;
    private final MessageService messageService;

    @GetMapping
    public Result<List<MessageNotice>> list() {
        return Result.ok(messageService.list(StpUtil.getLoginIdAsLong()));
    }

    @GetMapping("/unread-count")
    public Result<Map<String, Long>> unreadCount() {
        Long count = messageMapper.selectCount(Wrappers.<MessageNotice>lambdaQuery()
                .eq(MessageNotice::getUserId, StpUtil.getLoginIdAsLong())
                .eq(MessageNotice::getReadStatus, "UNREAD"));
        return Result.ok(Map.of("count", count));
    }

    @PostMapping("/{messageId}/read")
    public Result<Void> read(@PathVariable Long messageId) {
        MessageNotice message = messageMapper.selectById(messageId);
        if (message != null && message.getUserId().equals(StpUtil.getLoginIdAsLong())) {
            message.setReadStatus("READ");
            messageMapper.updateById(message);
        }
        return Result.ok();
    }

    @PostMapping("/read")
    public Result<Void> batchRead(@RequestBody List<Long> ids) {
        for (Long id : ids) {
            read(id);
        }
        return Result.ok();
    }

    @PostMapping("/read-all")
    public Result<Void> readAll() {
        for (MessageNotice message : messageService.list(StpUtil.getLoginIdAsLong())) {
            message.setReadStatus("READ");
            messageMapper.updateById(message);
        }
        return Result.ok();
    }

    @DeleteMapping("/{messageId}")
    public Result<Void> delete(@PathVariable Long messageId) {
        MessageNotice message = messageMapper.selectById(messageId);
        if (message != null && message.getUserId().equals(StpUtil.getLoginIdAsLong())) {
            messageMapper.deleteById(messageId);
        }
        return Result.ok();
    }
}
