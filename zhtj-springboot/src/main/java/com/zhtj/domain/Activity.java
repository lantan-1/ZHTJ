package com.zhtj.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("activity")
public class Activity {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;             // 序号
    
    private Integer organization;   // 团组织序号
    
    private String category;        // 类别(支部大会/团课/主题团日/入团仪式/组织生活会/其他)
    
    private String requiredTopic;   // 必学专题
    
    private String optionalTopics;  // 自学专题(多选,逗号分隔)
    
    private String participants;    // 参加的团员
    
    private String date;            // 开展时间
    
    private String place;           // 地点
    
    private String content;         // 内容
    
    private String host;            // 活动主持人
    
    private LocalDateTime createTime; // 创建时间
    
    private LocalDateTime updateTime; // 更新时间
    
    @TableField(exist = false)
    private String organizationName; // 组织名称，非数据库字段
} 