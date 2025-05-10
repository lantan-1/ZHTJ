package com.zhtj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhtj.domain.Activity;

import java.util.List;
import java.util.Map;

public interface ActivityService extends IService<Activity> {
    /**
     * 保存活动
     * @param activity 活动对象
     * @return 是否成功
     */
    boolean save(Activity activity);

    /**
     * 删除活动
     * @param id 活动ID
     * @return 是否成功
     */
    boolean delete(Integer id);

    /**
     * 更新活动
     * @param activity 活动对象
     * @return 是否成功
     */
    boolean update(Activity activity);

    /**
     * 获取组织的所有活动
     * @param organization 组织ID
     * @return 活动列表
     */
    List<Activity> getAllByOrg(Integer organization);

    /**
     * 根据日期获取组织的活动
     * @param organization 组织ID
     * @param date 日期
     * @return 活动列表
     */
    List<Activity> getAllByDate(Integer organization, String date);
    
    /**
     * 分页查询活动
     * @param page 分页对象
     * @param organizationId 组织ID
     * @param category 活动类别
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 分页结果
     */
    IPage<Activity> getActivityPage(Page<Activity> page, Integer organizationId, String category, String startDate, String endDate);
    
    /**
     * 获取活动详情
     * @param id 活动ID
     * @return 活动详情
     */
    Activity getActivityDetail(Integer id);
    
    /**
     * 根据组织统计活动数量
     * @param organizationId 组织ID
     * @return 活动数量
     */
    int countByOrganization(Integer organizationId);
    
    /**
     * 按类别统计活动数量
     * @param organizationId 组织ID
     * @return 类别和数量的映射
     */
    Map<String, Integer> countByCategory(Integer organizationId);
} 