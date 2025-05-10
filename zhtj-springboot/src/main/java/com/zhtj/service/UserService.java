package com.zhtj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhtj.domain.User;
import com.zhtj.model.twosystem.MemberRegister;
import com.zhtj.model.twosystem.RegisterBatch;
import com.zhtj.model.twosystem.MemberEvaluation;
import com.zhtj.model.twosystem.EvaluationResult;

import java.util.List;
import java.util.Map;
import java.util.Set;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    /**
     * 保存用户
     * @param user 用户信息
     * @return 是否成功
     */
    boolean save(User user);

    /**
     * 根据身份证号获取用户
     * @param card 身份证号
     * @return 用户信息
     */
    User getByCard(String card);

    /**
     * 根据身份证号和密码获取用户ID
     * @param user 用户信息（包含证件号和密码）
     * @return 用户ID
     */
    Integer getId(User user);

    /**
     * 更新用户密码
     * @param user 用户信息（包含证件号和密码）
     * @return 是否成功
     */
    boolean updatePwd(User user);

    /**
     * 根据ID删除用户
     * @param id 用户ID
     * @return 是否成功
     */
    boolean delete(Integer id);

    /**
     * 根据证件号删除用户
     * @param card 证件号
     * @return 是否成功
     */
    boolean delete(String card);

    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 是否成功
     */
    boolean update(User user);

    /**
     * 获取组织下的所有用户
     * @param organization 组织ID
     * @return 用户列表
     */
    List<User> getOrg(Integer organization);

    /**
     * 用户登录
     * @param card 证件号
     * @param pwd 密码
     * @return 用户信息
     */
    User login(String card, String pwd);

    /**
     * 分页查询用户
     * @param page 分页参数
     * @param name 用户姓名（可选）
     * @param organizationId 组织ID（可选）
     * @return 分页结果
     */
    IPage<User> getUserPage(Page<User> page, String name, Integer organizationId);

    /**
     * 获取用户详情（包含组织信息）
     * @param id 用户ID
     * @return 用户详情
     */
    User getUserDetail(Integer id);

    /**
     * 更新用户密码
     * @param id 用户ID
     * @param oldPwd 旧密码
     * @param newPwd 新密码
     * @return 是否成功
     */
    boolean updatePassword(Integer id, String oldPwd, String newPwd);

    /**
     * 获取组织下的所有用户
     * @param organizationId 组织ID
     * @return 用户列表
     */
    List<User> getUsersByOrganization(Integer organizationId);

    /**
     * 更新用户所属组织
     * @param userId 用户ID
     * @param newOrganizationId 新组织ID
     * @return 是否成功
     */
    boolean updateUserOrganization(Integer userId, Integer newOrganizationId);

    /**
     * 按政治面貌统计用户人数
     * @param organizationId 组织ID
     * @return 统计结果
     */
    List<Map<String, Object>> countByPoliticalStatus(Integer organizationId);

    /**
     * 根据ID查询用户详情
     * 
     * @param id 用户ID
     * @return 用户详情
     */
    User getUserById(Integer id);

    /**
     * 创建用户
     * 
     * @param user 用户信息
     * @return 是否成功
     */
    boolean createUser(User user);

    /**
     * 更新用户
     * 
     * @param user 用户信息
     * @return 是否成功
     */
    boolean updateUser(User user);

    /**
     * 删除用户
     * 
     * @param id 用户ID
     * @return 是否成功
     */
    boolean deleteUser(Integer id);

    /**
     * 用户登录
     * 
     * @param card 身份证号
     * @param password 密码
     * @param captcha 验证码
     * @return 登录结果(token和用户信息)
     */
    Map<String, Object> login(String card, String password, String captcha);

    /**
     * 刷新登录令牌
     * 
     * @param oldToken 旧令牌
     * @return 刷新结果(新token)
     */
    Map<String, Object> refreshToken(String oldToken);

    /**
     * 退出登录
     * 
     * @param token 令牌
     * @return 是否成功
     */
    boolean logout(String token);

    /**
     * 从令牌中获取用户ID
     * 
     * @param token JWT令牌
     * @return 用户ID
     */
    Integer getUserIdFromToken(String token);

    /**
     * 验证令牌是否有效
     * 
     * @param token JWT令牌
     * @return 是否有效
     */
    boolean validateToken(String token);

    /**
     * 根据ID获取用户信息
     * 
     * @param id 用户ID
     * @return 用户信息
     */
    User getById(Integer id);
    
    /**
     * 获取用户角色
     * 
     * @param userId 用户ID
     * @return 用户角色集合
     */
    Set<String> getUserRoles(Integer userId);
    
    /**
     * 检查用户是否拥有指定权限
     * 
     * @param userId 用户ID
     * @param permission 权限名称
     * @return 是否拥有权限
     */
    boolean hasPermission(Integer userId, String permission);

    /**
     * 获取注册批次列表
     */
    Map<String, Object> getBatchList(Integer page, Integer size, String batchName, String registerYear, String status, HttpServletRequest request);
    
    /**
     * 获取批次详情
     */
    RegisterBatch getBatchDetail(Integer id);
    
    /**
     * 创建注册批次
     */
    boolean createBatch(RegisterBatch batch);
    
    /**
     * 更新注册批次
     */
    boolean updateBatch(RegisterBatch batch);
    
    /**
     * 删除注册批次
     */
    boolean deleteBatch(Integer id);
    
    /**
     * 开始注册批次
     */
    boolean startBatch(Integer id);
    
    /**
     * 结束注册批次
     */
    boolean endBatch(Integer id);
    
    /**
     * 获取当前批次
     */
    RegisterBatch getCurrentBatch();
    
    /**
     * 获取批次选项
     */
    List<RegisterBatch> getBatchOptions();
    
    /**
     * 获取团员注册状态
     */
    Map<String, Object> getRegisterStatus(Integer userId);
    
    /**
     * 获取团员注册历史
     */
    Map<String, Object> getRegisterHistory(Integer userId, Integer page, Integer size);
    
    /**
     * 提交注册申请
     */
    boolean applyRegister(MemberRegister register);
    
    /**
     * 获取注册审批列表
     */
    Map<String, Object> getApprovalList(User currentUser, Integer page, Integer size, Integer batchId, String memberName, String status);
    
    /**
     * 审批通过
     */
    boolean approveRegister(Integer id, User approver, String comments);
    
    /**
     * 审批驳回
     */
    boolean rejectRegister(Integer id, User approver, String comments);
    
    /**
     * 批量审批通过
     */
    boolean batchApproveRegister(List<Integer> ids, User approver, String comments);
    
    /**
     * 批量审批驳回
     */
    boolean batchRejectRegister(List<Integer> ids, User approver, String comments);
    
    /**
     * 获取审批统计数据
     */
    Map<String, Object> getApprovalStatistics(User currentUser);
    
    /**
     * 获取注册统计数据
     */
    Map<String, Object> getStatisticsData(String year, Integer organizationId);
    
    /**
     * 导出统计数据
     */
    String exportStatistics(String year, Integer organizationId);
    
    /**
     * 获取评议活动列表
     */
    Map<String, Object> getEvaluationList(Integer page, Integer size, String year, String status, Integer organizationId);
    
    /**
     * 获取评议活动详情
     */
    MemberEvaluation getEvaluationDetail(Integer id);
    
    /**
     * 获取评议活动原始数据，包括发起人信息
     * 
     * @param id 评议活动ID
     * @return 评议活动数据Map
     */
    Map<String, Object> getEvaluationData(Integer id);
    
    /**
     * 创建评议活动
     */
    boolean createEvaluation(MemberEvaluation evaluation);
    
    /**
     * 更新评议活动
     */
    boolean updateEvaluation(MemberEvaluation evaluation);
    
    /**
     * 删除评议活动
     */
    boolean deleteEvaluation(Integer id);
    
    /**
     * 开始评议活动
     */
    boolean startEvaluation(Integer id);
    
    /**
     * 结束评议活动
     */
    boolean endEvaluation(Integer id);
    
    /**
     * 取消评议活动
     */
    boolean cancelEvaluation(Integer id);
    
    /**
     * 获取评议结果列表
     */
    Map<String, Object> getResultList(Integer evaluationId, Integer page, Integer size, String status, String memberName, String result);
    
    /**
     * 从数据库直接获取评议结果统计数据
     * 
     * @param evaluationId 评议活动ID
     * @return 统计数据
     */
    Map<String, Object> getEvaluationResultsFromDb(Integer evaluationId);
    
    /**
     * 提交评议结果
     */
    boolean submitResults(List<EvaluationResult> results);
    
    /**
     * 获取评议统计数据
     */
    Map<String, Object> getStatisticsData(String year, Integer organizationId, String timeRange);
    
    /**
     * 导出评议统计数据
     */
    String exportStatistics(String year, Integer organizationId, String timeRange);

    /**
     * 重置评议结果
     */
    boolean resetEvaluation(Integer evaluationId, Integer memberId);
    
    /**
     * 发送评议提醒
     */
    boolean sendEvaluationReminder(Integer evaluationId);
    
    /**
     * 导出评议结果
     */
    String exportEvaluationResults(Integer evaluationId);

    /**
     * 获取批次成员列表
     */
    Map<String, Object> getBatchMemberList(Integer batchId, Integer page, Integer size, String keyword, String status);

    /**
     * 判断用户是否为管理员
     * 
     * @param userId 用户ID
     * @return 是否为管理员
     */
    boolean isAdmin(Integer userId);

    /**
     * 激活用户
     * @param userId 用户ID
     * @param adminId 管理员ID
     * @return 是否成功
     */
    boolean activateUser(Integer userId, Integer adminId);

    /**
     * 获取待激活用户列表
     * 
     * @param page 分页参数
     * @param organizationId 组织ID
     * @param name 用户名（可选）
     * @param status 状态（可选）
     * @return 用户分页列表
     */
    IPage<User> getPendingUsers(Page<User> page, Integer organizationId, String name, String status);
} 