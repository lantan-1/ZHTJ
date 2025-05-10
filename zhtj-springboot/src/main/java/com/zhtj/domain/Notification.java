package com.zhtj.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 消息通知实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("notification")
public class Notification {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;                     // 通知ID
    
    private String title;                   // 通知标题
    
    private String content;                 // 通知内容
    
    private String senderName;              // 发送者名称(系统/用户)
    
    private Integer senderId;               // 发送者ID(0表示系统)
    
    private Integer recipientId;            // 接收者ID
    
    private String notificationType;        // 通知类型(转接申请/评议通知/荣誉通知/其他)
    
    private Integer referenceId;            // 关联业务ID
    
    private String referenceType;           // 关联业务类型
    
    private Integer isRead;                 // 是否已读(0-未读，1-已读)
    
    private LocalDateTime readTime;         // 阅读时间
    
    private Integer priority;               // 优先级(0-普通，1-重要，2-紧急)
    
    private LocalDateTime expireTime;       // 过期时间
    @TableField(exist = false)
    private String actionUrl;               // 操作链接
    
    private LocalDateTime createTime;       // 创建时间
    
    private LocalDateTime updateTime;       // 更新时间
    
    // 非数据库字段
    @TableField(exist = false)
    private String recipientName;           // 接收者姓名
} 