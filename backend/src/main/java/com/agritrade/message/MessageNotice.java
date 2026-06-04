package com.agritrade.message;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("message_notice")
public class MessageNotice {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String noticeType;
    private String readStatus;
    private Long relatedId;
    private String relatedType;
    private LocalDateTime createTime;
}
