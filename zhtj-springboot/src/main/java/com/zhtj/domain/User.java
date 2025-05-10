package com.zhtj.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class User {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;                     // 用户ID
    
    private String name;                    // 用户姓名
    
    private String card;                    // 身份证号
    
    @JsonIgnore
    private String pwd;                     // 密码(加密存储)
    
    private String gender;                  // 性别(男/女)
    
    private String ethnic;                  // 民族
    
    private String occupation;              // 职业
    
    private String educationStatus;         // 教育状态(在读/毕业)
    
    private String educationLevel;          // 教育水平(本科/硕士/博士等)
    
    private String politicalStatus;         // 政治面貌(共青团员/中共预备党员/中共党员等)
    
    private LocalDate joinLeagueDate;       // 入团日期
    
    private String joinPartyDate;           // 入党时间
    
    private String workUnit;                // 工作单位
    
    private String address;                 // 户籍地址
    
    private String qq;                      // QQ号码
    
    private String wechat;                  // 微信
    
    private String weibo;                   // 微博
    
    private String leaguePosition;          // 团内职务(团员/团支书/副书记等)
    
    private String phone;                   // 手机号码
    
    private String email;                   // 电子邮箱
    
    private Integer organization;           // 所属组织ID
    
    private Integer previousOrganization;   // 原组织ID
    
    private LocalDateTime transferDate;     // 转移日期
    
    private Integer transferCount;          // 转移次数
    
    private LocalDateTime createTime;       // 创建时间
    
    private LocalDateTime updateTime;       // 更新时间
    
    // 非数据库字段
    @TableField(exist = false) 
    private Integer status;                 // 状态(1-正常，0-禁用)
    
    // 非数据库字段
    @TableField(exist = false)   
    private String avatar;                  // 头像URL，前端都默认头像

    // 非数据库字段
    @TableField(exist = false)
    private LocalDateTime lastLoginTime;    // 最后登录时间
    
    // 非数据库字段
    @TableField(exist = false)
    private String organizationName;        // 组织名称

    /**
     * 是否已激活
     */
    private Boolean isActivated;

    public Boolean getIsActivated() {
        return isActivated;
    }

    public void setIsActivated(Boolean isActivated) {
        this.isActivated = isActivated;
    }
} 