package com.agritrade.message;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageNoticeMapper messageMapper;

    public void create(Long userId, String title, String content, String type, Long relatedId, String relatedType) {
        MessageNotice message = new MessageNotice();
        message.setUserId(userId);
        message.setTitle(title);
        message.setContent(content);
        message.setNoticeType(type);
        message.setReadStatus("UNREAD");
        message.setRelatedId(relatedId);
        message.setRelatedType(relatedType);
        message.setCreateTime(LocalDateTime.now());
        messageMapper.insert(message);
    }

    public List<MessageNotice> list(Long userId) {
        return messageMapper.selectList(Wrappers.<MessageNotice>lambdaQuery()
                .eq(MessageNotice::getUserId, userId)
                .orderByDesc(MessageNotice::getCreateTime));
    }
}
