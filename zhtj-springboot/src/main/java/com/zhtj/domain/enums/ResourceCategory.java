package com.zhtj.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 学习资源分类枚举
 */
@Getter
public enum ResourceCategory {
    
    THEORY(1, "思想理论"),
    CURRENT_AFFAIRS(2, "时政热点"),
    LEAGUE_HISTORY(3, "团史学习"),
    LEAGUE_RULES(4, "团章团规"),
    JOINING_EDUCATION(5, "入团教育"),
    OTHERS(6, "其他");
    
    @EnumValue
    @JsonValue
    private final Integer code;
    private final String name;
    
    ResourceCategory(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
    
    /**
     * 根据编码获取枚举实例
     */
    public static ResourceCategory getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        
        for (ResourceCategory category : values()) {
            if (category.getCode().equals(code)) {
                return category;
            }
        }
        return null;
    }
    
    /**
     * 获取所有分类
     */
    public static ResourceCategory[] getAllCategories() {
        return ResourceCategory.values();
    }
} 