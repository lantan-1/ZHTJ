package com.zhtj.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 组织实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("organization")
public class Organization {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;                  // 组织ID
    
    private String name;                 // 组织名称
    
    private String fullName;             // 组织全称
    
    private String orgType;              // 组织类型(团支部/团总支/团委)
    
    private Integer parentId;            // 上级组织ID
    
    private Integer level;               // 组织层级
    
    private String path;                 // 组织路径，用于快速查找
    
    private Boolean isLeaf;              // 是否为叶子节点
    
    private Boolean status;              // 状态(true-启用，false-禁用)
    
    private String secretaryName;        // 书记姓名
    
    private String secretaryPhone;       // 书记联系电话
    
    private Integer memberCount;         // 成员数量
    
    // 非数据库字段，数据库中不存在该字段
    @TableField(exist = false)
    private String description;          // 组织描述
    
    private LocalDateTime createTime;    // 创建时间
    
    private LocalDateTime updateTime;    // 更新时间
    
    // 非数据库字段
    @TableField(exist = false)
    private String parentName;           // 上级组织名称
    
    @TableField(exist = false)
    private List<Organization> children; // 子组织列表
    
    @TableField(exist = false)
    private User user;                   // 用户信息（非数据库字段，用于前端交互）
    
    /**
     * 用于前端交互的简单构造器
     * @param id 组织ID
     * @param name 组织名称
     * @param user 用户信息
     */
    public Organization(Integer id, String name, User user) {
        this.id = id;
        this.name = name;
        this.user = user;
    }
} 