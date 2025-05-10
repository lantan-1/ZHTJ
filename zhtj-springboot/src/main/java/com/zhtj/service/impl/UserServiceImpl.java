package com.zhtj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhtj.common.exception.BusinessException;
import com.zhtj.common.exception.ResourceNotFoundException;
import com.zhtj.common.exception.UnauthorizedException;
import com.zhtj.domain.Organization;
import com.zhtj.domain.User;
import com.zhtj.mapper.MemberEvaluationDetailMapper;
import com.zhtj.mapper.MemberEvaluationMapper;
import com.zhtj.mapper.UserMapper;
import com.zhtj.mapper.OrganizationMapper;
import com.zhtj.mapper.RegisterBatchMapper;
import com.zhtj.mapper.RegisterBatchOrganizationMapper;
import com.zhtj.mapper.MemberRegisterMapper;
import com.zhtj.model.twosystem.EvaluationResult;
import com.zhtj.model.twosystem.MemberEvaluation;
import com.zhtj.model.twosystem.MemberEvaluationDetail;
import com.zhtj.model.twosystem.MemberRegister;
import com.zhtj.model.twosystem.RegisterBatch;
import com.zhtj.model.twosystem.RegisterBatchOrganization;
import com.zhtj.service.OrganizationService;
import com.zhtj.service.RoleService;
import com.zhtj.service.UserService;
import com.zhtj.config.JwtConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.time.LocalDate;
import java.lang.reflect.Field;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户服务实现类
 */
@Service
@Slf4j
@SuppressWarnings("unchecked")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    // 从配置文件中读取开发环境万能密码
    @Value("${dev.auth.master-password.password}")
    private String devMasterPassword;
    
    // 从配置文件中读取是否启用万能密码
    @Value("${dev.auth.master-password.enabled}")
    private boolean enableMasterPassword;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private OrganizationService organizationService;
    
    @Autowired
    private OrganizationMapper organizationMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private JwtConfig jwtConfig;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RoleService roleService;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MemberEvaluationMapper memberEvaluationMapper;

    @Autowired
    private MemberEvaluationDetailMapper memberEvaluationDetailMapper;

    @Autowired
    private RegisterBatchMapper registerBatchMapper;
    
    @Autowired
    private RegisterBatchOrganizationMapper registerBatchOrganizationMapper;
    
    @Autowired
    private MemberRegisterMapper memberRegisterMapper;

    @Override
    public boolean save(User user) {
        // 密码加密
        if (StringUtils.hasText(user.getPwd())) {
            user.setPwd(passwordEncoder.encode(user.getPwd()));
        }
        
        // 设置创建时间
        user.setCreateTime(LocalDateTime.now());
        
        return super.save(user);
    }

    @Override
    public User getByCard(String card) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getCard, card);
        return this.getOne(queryWrapper);
    }

    @Override
    public Integer getId(User user) {
        return userMapper.getIdByCardAndPwd(user.getCard(), user.getPwd());
    }

    @Override
    @Transactional
    public boolean update(User user) {
        if (user.getId() == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        User existingUser = this.getById(user.getId());
        if (existingUser == null) {
            throw new ResourceNotFoundException("用户", "id", user.getId());
        }
        
        // 如果修改了密码，需要重新加密
        if (StringUtils.hasText(user.getPwd()) && !user.getPwd().equals(existingUser.getPwd())) {
            user.setPwd(passwordEncoder.encode(user.getPwd()));
        }
        
        // 设置更新时间
        user.setUpdateTime(LocalDateTime.now());
        
        return this.updateById(user);
    }

    @Override
    @Transactional
    public boolean delete(Integer id) {
        if (id == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        return this.removeById(id);
    }

    @Override
    @Transactional
    public boolean delete(String card) {
        if (!StringUtils.hasText(card)) {
            throw new BusinessException("证件号不能为空");
        }
        
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getCard, card);
        
        return this.remove(queryWrapper);
    }

    @Override
    public List<User> getOrg(Integer organization) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getOrganization, organization);
        return this.list(queryWrapper);
    }

    @Override
    public User login(String card, String pwd) {
        Map<String, Object> result = loginInternal(card, pwd, false);
        return (User) result.get("user");
    }

    @Override
    public Map<String, Object> login(String card, String password, String captcha) {
        // 注意：验证码校验已在Controller层完成，此处无需重复验证
        return loginInternal(card, password, true);
    }

    @Override
    public IPage<User> getUserPage(Page<User> page, String name, Integer organizationId) {
        return userMapper.selectUserPage(page, name, organizationId);
    }

    @Override
    public User getUserDetail(Integer id) {
        if (id == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        User user = userMapper.getUserDetail(id);
        if (user == null) {
            throw new ResourceNotFoundException("用户", "id", id);
        }
        
        return user;
    }

    @Override
    public User getById(Integer id) {
        if (id == null) {
            return null;
        }
        // 使用自定义查询方法代替MyBatis-Plus默认的查询
        return userMapper.getUserDetail(id);
    }

    @Override
    public User getUserById(Integer id) {
        if (id == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        User user = this.getById(id);
        if (user == null) {
            throw new ResourceNotFoundException("用户", "id", id);
        }
        
        return user;
    }

    @Override
    @Transactional
    public boolean updatePassword(Integer id, String oldPwd, String newPwd) {
        if (id == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        if (!StringUtils.hasText(oldPwd)) {
            throw new BusinessException("原密码不能为空");
        }
        
        if (!StringUtils.hasText(newPwd)) {
            throw new BusinessException("新密码不能为空");
        }
        
        // 验证原密码
        User user = this.getById(id);
        if (user == null) {
            throw new ResourceNotFoundException("用户", "id", id);
        }
        
        // 使用BCrypt验证原密码
        if (!passwordEncoder.matches(oldPwd, user.getPwd())) {
            throw new BusinessException("原密码错误");
        }
        
        // 更新密码
        String encodedNewPwd = passwordEncoder.encode(newPwd);
        return userMapper.updatePassword(id, encodedNewPwd) > 0;
    }

    @Override
    public List<User> getUsersByOrganization(Integer organizationId) {
        if (organizationId == null) {
            throw new BusinessException("组织ID不能为空");
        }
        
        return userMapper.getUsersByOrganization(organizationId);
    }

    @Override
    @Transactional
    public boolean updateUserOrganization(Integer userId, Integer newOrganizationId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        if (newOrganizationId == null) {
            throw new BusinessException("新组织ID不能为空");
        }
        
        User user = this.getById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("用户", "id", userId);
        }
        
        // 保存原组织信息
        Integer previousOrganization = user.getOrganization();
        
        // 更新用户组织信息
        user.setOrganization(newOrganizationId);
        user.setPreviousOrganization(previousOrganization);
        user.setTransferDate(LocalDateTime.now());
        user.setTransferCount(user.getTransferCount() == null ? 1 : user.getTransferCount() + 1);
        user.setUpdateTime(LocalDateTime.now());
        
        return this.updateById(user);
    }

    @Override
    public List<Map<String, Object>> countByPoliticalStatus(Integer organizationId) {
        // UserMapper中的countByPoliticalStatus方法不接受organizationId参数
        // 我们可以在这里对整体结果进行过滤，只返回特定组织的数据
        // 但如果需要精确统计，应该修改Mapper方法
        
        List<Map<String, Object>> allStats = userMapper.countByPoliticalStatus();
        
        // 如果organizationId为null，返回所有结果
        if (organizationId == null) {
            return allStats;
        }
        
        // 这里是一个简化处理，实际可能需要修改Mapper方法
        // 返回所有结果，真实场景可能需要调整
        return allStats;
    }

    @Override
    @Transactional
    public boolean updatePwd(User user) {
        if (user == null || !StringUtils.hasText(user.getCard()) || !StringUtils.hasText(user.getPwd())) {
            throw new BusinessException("证件号和密码不能为空");
        }
        
        User existingUser = this.getByCard(user.getCard());
        if (existingUser == null) {
            throw new ResourceNotFoundException("用户", "证件号", user.getCard());
        }
        
        // 密码加密
        String encodedPwd = passwordEncoder.encode(user.getPwd());
        
        // 更新密码
        existingUser.setPwd(encodedPwd);
        existingUser.setUpdateTime(LocalDateTime.now());
        
        return this.updateById(existingUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createUser(User user) {
        // 参数校验
        if (user == null) {
            throw new BusinessException("用户信息不能为空");
        }
        
        if (!StringUtils.hasText(user.getName())) {
            throw new BusinessException("用户姓名不能为空");
        }
        
        if (!StringUtils.hasText(user.getCard())) {
            throw new BusinessException("身份证号不能为空");
        }
        
        if (!StringUtils.hasText(user.getPwd())) {
            throw new BusinessException("密码不能为空");
        }
        
        // 检查身份证号是否已存在
        User existingUser = userMapper.selectUserByCard(user.getCard());
        if (existingUser != null) {
            throw new BusinessException("该身份证号已存在");
        }
        
        // 密码加密
        user.setPwd(passwordEncoder.encode(user.getPwd()));
        
        // 设置初始状态
        user.setStatus(1); // 1-正常，0-禁用
        // 设置激活状态为未激活
        user.setIsActivated(false);
        
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        user.setCreateTime(now);
        user.setUpdateTime(now);
        
        boolean result = this.save(user);
        
        // 更新组织的成员数量
        if (result && user.getOrganization() != null) {
            organizationService.updateMemberCount(user.getOrganization());
        }
        
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(User user) {
        // 参数校验
        if (user == null || user.getId() == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        // 检查用户是否存在
        User existingUser = this.getById(user.getId());
        if (existingUser == null) {
            throw new ResourceNotFoundException("用户", "id", user.getId());
        }
        
        // 如果修改了身份证号，需要检查是否已存在
        if (StringUtils.hasText(user.getCard()) && !user.getCard().equals(existingUser.getCard())) {
            User userWithSameCard = userMapper.selectUserByCard(user.getCard());
            if (userWithSameCard != null && !userWithSameCard.getId().equals(user.getId())) {
                throw new BusinessException("该身份证号已存在");
            }
        }
        
        // 如果修改了密码，需要加密
        if (StringUtils.hasText(user.getPwd()) && !user.getPwd().equals(existingUser.getPwd())) {
            user.setPwd(passwordEncoder.encode(user.getPwd()));
        }
        
        // 设置更新时间
        user.setUpdateTime(LocalDateTime.now());
        
        // 记录原组织ID
        Integer oldOrganizationId = existingUser.getOrganization();
        
        boolean result = this.updateById(user);
        
        // 如果组织变更，更新相关组织的成员数量
        if (result && user.getOrganization() != null 
                && !user.getOrganization().equals(oldOrganizationId)) {
            if (oldOrganizationId != null) {
                organizationService.updateMemberCount(oldOrganizationId);
            }
            organizationService.updateMemberCount(user.getOrganization());
        }
        
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(Integer id) {
        // 参数校验
        if (id == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        // 检查用户是否存在
        User existingUser = this.getById(id);
        if (existingUser == null) {
            throw new ResourceNotFoundException("用户", "id", id);
        }
        
        boolean result = this.removeById(id);
        
        // 更新组织的成员数量
        if (result && existingUser.getOrganization() != null) {
            organizationService.updateMemberCount(existingUser.getOrganization());
        }
        
        return result;
    }
    
    /**
     * 内部统一的用户登录方法，合并简单登录和带令牌生成的登录
     *
     * @param card 身份证号
     * @param password 密码
     * @param generateToken 是否生成令牌
     * @return 登录结果(用户对象或带令牌的Map)
     */
    private Map<String, Object> loginInternal(String card, String password, boolean generateToken) {
        log.info("用户尝试登录: {}", card);
        
        // 参数校验
        if (!StringUtils.hasText(card)) {
            throw new BusinessException("身份证号不能为空");
        }
        if (!StringUtils.hasText(password)) {
            throw new BusinessException("密码不能为空");
        }
        
        // 查询用户
        User user = userMapper.selectUserByCard(card);
        if (user == null) {
            log.warn("登录失败: 用户不存在 - {}", card);
            throw new BusinessException("用户不存在");
        }
        
        // 验证密码 - 添加万能密码支持
        boolean passwordValid = passwordEncoder.matches(password, user.getPwd());
        
        // 如果常规密码验证失败，检查是否使用了万能密码
        if (!passwordValid && enableMasterPassword && devMasterPassword.equals(password)) {
            log.warn("用户 {} 使用开发环境万能密码登录，仅供开发测试使用!", card);
            passwordValid = true;
        }
        
        if (!passwordValid) {
            log.warn("登录失败: 密码错误 - {}", card);
            throw new BusinessException("密码错误");
        }
        
        // 检查用户状态
        if (user.getStatus() != null && user.getStatus() == 0) {
            log.warn("登录失败: 用户已被禁用 - {}", card);
            throw new BusinessException("用户已被禁用");
        }
        
        // 检查激活状态
        if (user.getIsActivated() == null || !user.getIsActivated()) {
            log.warn("登录提示: 用户注册审批中 - {}", card);
            Map<String, Object> result = new HashMap<>();
            result.put("user", user);
            result.put("isActivated", false);
            result.put("needActivation", true);
            result.put("message", "用户注册审批中，请联系组织管理员审批您的注册");
            
            if (generateToken) {
                // 生成有效的token，但在前端标记需要激活
                Map<String, Object> claims = new HashMap<>();
                claims.put("user_id", user.getId());
                claims.put("username", user.getName());
                claims.put("org_id", user.getOrganization());
                claims.put("iat", new Date());
                
                String token = doGenerateToken(claims, user.getId().toString());
                result.put("token", token);
                result.put("tokenHead", "Bearer");
            }
            
            return result;
        }
        
        if (!generateToken) {
            // 如果不需要生成令牌，直接返回用户信息
            Map<String, Object> simpleResult = new HashMap<>();
            simpleResult.put("user", user);
            return simpleResult;
        }
        
        // 以下是生成令牌的逻辑
        log.info("用户 {} 密码验证成功", card);
        
        // 获取用户角色
        String[] roles = determineUserRoles(user);
        
        // 生成JWT令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", user.getId());
        claims.put("username", user.getName());
        claims.put("org_id", user.getOrganization());
        claims.put("iat", new Date());

        // 生成令牌，明确传入userId作为subject
        String token = doGenerateToken(claims, user.getId().toString());
        long expirationInSeconds = jwtConfig.getExpiration();
        
        // 记录JWT令牌的关键信息用于调试
        log.info("已生成用户 {} (ID={}) 的JWT令牌", card, user.getId());
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("expiry_time", System.currentTimeMillis() + expirationInSeconds * 1000);
        result.put("user", user);
        
        // 记录最后登录时间（仅在内存中，不更新数据库）
        user.setLastLoginTime(LocalDateTime.now());
        // 注释掉数据库更新操作
        // this.updateById(user);
        
        // 将令牌信息存入Redis
        String redisKey = String.format("user:token:%d", user.getId());
        
        // 创建令牌信息对象
        Map<String, Object> tokenInfo = new HashMap<>();
        tokenInfo.put("token", token);
        tokenInfo.put("userId", user.getId());
        tokenInfo.put("username", user.getName());
        tokenInfo.put("roles", roles);
        tokenInfo.put("loginTime", new Date());
        tokenInfo.put("expiryTime", new Date(System.currentTimeMillis() + expirationInSeconds * 1000));
        
        // 保存到Redis，过期时间与JWT相同
        redisTemplate.opsForValue().set(redisKey, tokenInfo, expirationInSeconds, TimeUnit.SECONDS);
        
        log.info("用户 {} 的令牌信息已保存到Redis", card);
        
        return result;
    }
    
    /**
     * 对敏感信息进行脱敏处理
     * 例如：将123456789012345678 转换为 1234********5678
     */
    private String maskSensitiveInfo(String info) {
        if (info.length() <= 8) {
            return "****" + info.substring(Math.max(0, info.length() - 4));
        }
        return info.substring(0, 4) + "********" + info.substring(Math.max(4, info.length() - 4));
    }
    
    /**
     * 根据用户信息确定用户角色
     */
    private String[] determineUserRoles(User user) {
        List<String> roleList = new ArrayList<>();
        
        // 基本角色：所有登录用户都是普通用户
        roleList.add("user");
        
        // 根据leaguePosition确定团内角色
        String leaguePosition = user.getLeaguePosition();
        if (StringUtils.hasText(leaguePosition)) {
            switch (leaguePosition) {
                case "团委书记":
                    roleList.add("committee_secretary");
                    break;
                case "团委副书记":
                    roleList.add("deputy_committee_secretary");
                    break;
                case "团支书":
                    roleList.add("branch_secretary");
                    break;
                case "团支部副书记":
                    roleList.add("deputy_branch_secretary");
                    break;
                case "团员":
                default:
                    roleList.add("member");
                    break;
            }
        } else {
            // 默认为普通团员
            roleList.add("member");
        }
        
        return roleList.toArray(new String[0]);
    }
    
    /**
     * 真正生成令牌的辅助方法
     */
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        // 调用JwtConfig生成令牌，确保传入正确的subject
        log.debug("生成Token，subject={}", subject);
        return jwtConfig.generateToken(claims);
    }
    
    @Override
    public Map<String, Object> refreshToken(String oldToken) {
        log.info("用户请求刷新令牌");
        
        // 1. 验证旧令牌是否有效
        if (!jwtConfig.validateToken(oldToken)) {
            log.warn("刷新令牌失败: 无效的令牌");
            throw new BusinessException("无效的令牌");
        }
        
        // 2. 从令牌中获取用户ID
        Integer userId = jwtConfig.getUserIdFromToken(oldToken);
        if (userId == null) {
            log.warn("刷新令牌失败: 无法从令牌中提取用户ID");
            throw new BusinessException("无效的令牌");
        }
        
        log.info("用户 {} 请求刷新令牌", userId);
        
        // 3. 检查Redis中是否存在该令牌信息
        String redisKey = String.format("user:token:%d", userId);
        Object tokenInfoObj = redisTemplate.opsForValue().get(redisKey);
        
        if (tokenInfoObj == null) {
            log.warn("刷新令牌失败: Redis中不存在令牌信息 - 用户 {}", userId);
            throw new BusinessException("令牌已失效");
        }
        
        // 验证Redis中存储的令牌与请求中的令牌是否匹配
        Map<String, Object> tokenInfo;
        try {
            tokenInfo = (Map<String, Object>) tokenInfoObj;
            String storedToken = (String) tokenInfo.get("token");
            
            if (storedToken == null || !oldToken.equals(storedToken)) {
                log.warn("刷新令牌失败: 令牌不匹配 - 用户 {}", userId);
                throw new BusinessException("令牌已失效");
            }
        } catch (Exception e) {
            log.error("刷新令牌过程中发生错误: 令牌信息格式错误", e);
            throw new BusinessException("令牌信息格式错误");
        }
        
        // 4. 获取用户信息生成新令牌
        User user = this.getById(userId);
        if (user == null) {
            log.warn("刷新令牌失败: 用户不存在 - {}", userId);
            throw new ResourceNotFoundException("用户", "id", userId);
        }
        
        // 保留原始角色信息
        Object rolesObj = tokenInfo.get("roles");
        String[] roles = (rolesObj instanceof String[]) ? 
                (String[]) rolesObj : 
                determineUserRoles(user);
        
        // 5. 构建新令牌的声明
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", user.getId());
        claims.put("name", user.getName());
        claims.put("card", user.getCard());
        claims.put("org_id", user.getOrganization());
        claims.put("roles", roles);
        
        // 6. 生成新令牌
        String newToken = jwtConfig.generateToken(claims);
        long expirationInSeconds = jwtConfig.getExpiration();
        Date expirationDate = new Date(System.currentTimeMillis() + expirationInSeconds * 1000);
        
        log.info("已为用户 {} 生成新的JWT令牌", userId);
        
        // 7. 更新Redis中的令牌信息
        // 创建令牌信息对象
        Map<String, Object> newTokenInfo = new HashMap<>();
        newTokenInfo.put("token", newToken);
        newTokenInfo.put("userId", user.getId());
        newTokenInfo.put("username", user.getName());
        newTokenInfo.put("roles", roles);
        newTokenInfo.put("loginTime", tokenInfo.get("loginTime")); // 保留原始登录时间
        newTokenInfo.put("refreshTime", new Date()); // 添加刷新时间
        newTokenInfo.put("expiryTime", expirationDate);
        
        // 更新Redis中的令牌信息
        redisTemplate.opsForValue().set(redisKey, newTokenInfo, expirationInSeconds, TimeUnit.SECONDS);
        
        log.info("用户 {} 的新令牌信息已保存到Redis", userId);
        
        // 8. 将旧令牌加入黑名单
        Date oldExpirationDate = jwtConfig.getExpirationDateFromToken(oldToken);
        long remainingTime = 0;
        if (oldExpirationDate != null) {
            remainingTime = Math.max(0, oldExpirationDate.getTime() - System.currentTimeMillis()) / 1000;
        }
        remainingTime = Math.max(30, remainingTime); // 最小30秒有效期
        
        String blacklistKey = String.format("jwt:blacklist:%s", generateShortUuid());
        redisTemplate.opsForValue().set(blacklistKey, oldToken, remainingTime, TimeUnit.SECONDS);
        
        log.info("用户 {} 的旧令牌已加入黑名单", userId);
        
        // 9. 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("token", newToken);
        result.put("expires_in", expirationInSeconds);
        result.put("expiry_time", expirationDate.getTime());
        
        log.info("用户 {} 刷新令牌成功", userId);
        return result;
    }
    
    @Override
    public boolean logout(String token) {
        log.info("用户请求退出登录");
        
        // 1. 验证令牌
        if (!jwtConfig.validateToken(token)) {
            log.warn("登出失败: 无效的令牌");
            return false;
        }
        
        try {
            // 2. 获取用户ID
            Integer userId = jwtConfig.getUserIdFromToken(token);
            if (userId == null) {
                log.warn("登出失败: 无法从令牌中提取用户ID");
                return false;
            }
            
            log.info("用户 {} 请求退出登录", userId);
            
            // 3. 从Redis中删除令牌
            String redisKey = String.format("user:token:%d", userId);
            Boolean deleted = redisTemplate.delete(redisKey);
            
            if (deleted != null && deleted) {
                log.info("用户 {} 的令牌信息已从Redis中删除", userId);
            } else {
                log.warn("用户 {} 的令牌信息在Redis中不存在", userId);
            }
            
            // 4. 将令牌加入黑名单，直到过期
            // 从令牌中获取过期时间
            Date expirationDate = jwtConfig.getExpirationDateFromToken(token);
            long remainingTime = 0;
            if (expirationDate != null) {
                remainingTime = Math.max(0, expirationDate.getTime() - System.currentTimeMillis()) / 1000;
            }
            
            // 如果令牌即将过期，设置最小黑名单有效期为30秒
            remainingTime = Math.max(30, remainingTime);
            
            // 生成黑名单令牌的键
            String blacklistKey = String.format("jwt:blacklist:%s", generateShortUuid());
            redisTemplate.opsForValue().set(blacklistKey, token, remainingTime, TimeUnit.SECONDS);
            
            log.info("用户 {} 的令牌已加入黑名单，有效期 {} 秒", userId, remainingTime);
            return true;
        } catch (Exception e) {
            log.error("登出过程中发生错误", e);
            return false;
        }
    }
    
    /**
     * 生成短UUID，用于黑名单键
     */
    private String generateShortUuid() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    @Override
    public Integer getUserIdFromToken(String token) {
        return jwtConfig.getUserIdFromToken(token);
    }

    @Override
    public boolean validateToken(String token) {
        return jwtConfig.validateToken(token);
    }

    @Override
    public Set<String> getUserRoles(Integer userId) {
        if (userId == null) {
            return new HashSet<>();
        }
        return roleService.getUserRoleCodes(userId);
    }

    @Override
    public boolean hasPermission(Integer userId, String permission) {
        if (userId == null || !StringUtils.hasText(permission)) {
            return false;
        }
        return roleService.hasPermission(userId, permission);
    }

    // 团籍注册批次相关方法实现
    @Override
    public Map<String, Object> getBatchList(Integer page, Integer size, String batchName, String registerYear, String status, HttpServletRequest request) {
        try {
            log.debug("获取注册批次列表: page={}, size={}, batchName={}, registerYear={}, status={}", 
                    page, size, batchName, registerYear, status);
            
            // 获取当前用户的组织ID
            Integer organizationId = null;
            try {
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    Integer userId = jwtConfig.getUserIdFromToken(token);
                    if (userId != null) {
                        User currentUser = this.getById(userId);
                        if (currentUser != null) {
                            organizationId = currentUser.getOrganization();
                            log.debug("当前用户的组织ID: {}", organizationId);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("获取当前用户组织ID失败: {}", e.getMessage(), e);
            }
            
            Page<RegisterBatch> pageParam = new Page<>(page, size);
            IPage<RegisterBatch> pageResult = registerBatchMapper.selectBatchPage(pageParam, batchName, registerYear, status, organizationId);
            
            // 处理每个批次，添加目标组织ID列表
            for (RegisterBatch batch : pageResult.getRecords()) {
                // 获取批次关联的组织ID列表
                List<Integer> organizationIds = registerBatchOrganizationMapper.getOrganizationIdsByBatchId(batch.getId());
                batch.setTargetOrganizationIds(organizationIds);
                
                // 设置前端兼容字段
                batch.setBatchCode(batch.getBatchName());
                
                // TODO: 添加注册统计数据（这里可以进一步实现）
                batch.setTotalCount(100); // 模拟数据，后续可实现实际统计
                batch.setRegisteredCount(80);
                batch.setProgressPercentage(new BigDecimal("80.0"));
            }
            
        Map<String, Object> result = new HashMap<>();
            result.put("list", pageResult.getRecords());
            result.put("total", pageResult.getTotal());
            
        return result;
        } catch (Exception e) {
            log.error("获取注册批次列表失败: {}", e.getMessage(), e);
            return Map.of("list", new ArrayList<>(), "total", 0);
        }
    }
    
    @Override
    public RegisterBatch getBatchDetail(Integer id) {
        try {
            log.debug("获取批次详情: batchId={}", id);
            
            // 查询批次信息
            RegisterBatch batch = registerBatchMapper.selectById(id);
            if (batch == null) {
                log.warn("获取批次详情失败: 批次不存在, batchId={}", id);
                return null;
            }
            
            // 获取批次关联的组织ID列表
            List<Integer> organizationIds = registerBatchOrganizationMapper.getOrganizationIdsByBatchId(id);
            batch.setTargetOrganizationIds(organizationIds);
            
            // 设置前端兼容字段
            batch.setBatchCode(batch.getBatchName());
            batch.setTitle(batch.getBatchName());
            batch.setYear(batch.getRegisterYear());
            batch.setStartDate(batch.getStartTime());
            batch.setEndDate(batch.getEndTime());
            
            // 获取注册统计数据
            Map<String, Object> statistics = memberRegisterMapper.getRegisterStatistics(null, batch.getRegisterYear());
            if (statistics != null) {
                // 使用Number类型来避免Long到Integer的直接强制转换
                Number totalNumber = (Number) statistics.get("total");
                Number approvedNumber = (Number) statistics.get("approved");
                
                Integer total = totalNumber != null ? totalNumber.intValue() : 0;
                Integer approved = approvedNumber != null ? approvedNumber.intValue() : 0;
                
                batch.setTotalCount(total);
                batch.setRegisteredCount(approved);
                
                if (total > 0 && approved != null) {
                    BigDecimal percentage = new BigDecimal(approved)
                            .multiply(new BigDecimal("100"))
                            .divide(new BigDecimal(total), 2, BigDecimal.ROUND_HALF_UP);
                    batch.setProgressPercentage(percentage);
                } else {
                    batch.setProgressPercentage(BigDecimal.ZERO);
                }
            } else {
                batch.setTotalCount(0);
                batch.setRegisteredCount(0);
                batch.setProgressPercentage(BigDecimal.ZERO);
            }
            
            return batch;
        } catch (Exception e) {
            log.error("获取批次详情失败: {}", e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createBatch(RegisterBatch batch) {
        try {
            log.debug("创建注册批次: {}", batch.getBatchName());
            
            // 设置默认值
            batch.setStatus("未开始");
            batch.setCreateTime(LocalDateTime.now());
            batch.setUpdateTime(LocalDateTime.now());
            
            // 保存批次
            int result = registerBatchMapper.insert(batch);
            if (result <= 0) {
                log.error("保存注册批次失败");
                return false;
            }
            
            // 保存批次与组织的关联
            List<Integer> organizationIds = batch.getTargetOrganizationIds();
            if (organizationIds != null && !organizationIds.isEmpty()) {
                for (Integer organizationId : organizationIds) {
                    RegisterBatchOrganization relation = new RegisterBatchOrganization();
                    relation.setBatchId(batch.getId());
                    relation.setOrganizationId(organizationId);
                    relation.setCreateTime(LocalDateTime.now());
                    
                    registerBatchOrganizationMapper.insert(relation);
                }
            }
            
        return true;
        } catch (Exception e) {
            log.error("创建注册批次失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建注册批次失败", e);
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatch(RegisterBatch batch) {
        try {
            log.debug("更新注册批次: id={}, title={}", batch.getId(), batch.getTitle());
            
            // 查询原记录，确保存在
            RegisterBatch existingBatch = registerBatchMapper.selectById(batch.getId());
            if (existingBatch == null) {
                log.warn("更新注册批次失败: 批次不存在, id={}", batch.getId());
                return false;
            }
            
            // 只有未开始的批次才能修改
            if (!"未开始".equals(existingBatch.getStatus())) {
                log.warn("更新注册批次失败: 只有未开始的批次才能修改, id={}, status={}", 
                        batch.getId(), existingBatch.getStatus());
                return false;
            }
            
            // 更新批次信息
            batch.setUpdateTime(LocalDateTime.now());
            int result = registerBatchMapper.updateById(batch);
            if (result <= 0) {
                log.error("更新注册批次失败");
                return false;
            }
            
            // 更新批次与组织的关联
            List<Integer> organizationIds = batch.getTargetOrganizationIds();
            if (organizationIds != null) {
                // 先删除原有关联
                LambdaQueryWrapper<RegisterBatchOrganization> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(RegisterBatchOrganization::getBatchId, batch.getId());
                registerBatchOrganizationMapper.delete(queryWrapper);
                
                // 添加新的关联
                for (Integer organizationId : organizationIds) {
                    RegisterBatchOrganization relation = new RegisterBatchOrganization();
                    relation.setBatchId(batch.getId());
                    relation.setOrganizationId(organizationId);
                    relation.setCreateTime(LocalDateTime.now());
                    
                    registerBatchOrganizationMapper.insert(relation);
                }
            }
            
        return true;
        } catch (Exception e) {
            log.error("更新注册批次失败: {}", e.getMessage(), e);
            throw new RuntimeException("更新注册批次失败", e);
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBatch(Integer id) {
        try {
            log.debug("删除注册批次: id={}", id);
            
            // 查询原记录，确保存在
            RegisterBatch existingBatch = registerBatchMapper.selectById(id);
            if (existingBatch == null) {
                log.warn("删除注册批次失败: 批次不存在, id={}", id);
                return false;
            }
            
            // 只有未开始的批次才能删除
            if (!"未开始".equals(existingBatch.getStatus())) {
                log.warn("删除注册批次失败: 只有未开始的批次才能删除, id={}, status={}", 
                        id, existingBatch.getStatus());
                return false;
            }
            
            // 删除批次与组织的关联
            LambdaQueryWrapper<RegisterBatchOrganization> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(RegisterBatchOrganization::getBatchId, id);
            registerBatchOrganizationMapper.delete(queryWrapper);
            
            // 删除批次
            int result = registerBatchMapper.deleteById(id);
            
            return result > 0;
        } catch (Exception e) {
            log.error("删除注册批次失败: {}", e.getMessage(), e);
            throw new RuntimeException("删除注册批次失败", e);
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean startBatch(Integer id) {
        try {
            log.debug("开始注册批次: id={}", id);
            
            // 查询原记录，确保存在
            RegisterBatch existingBatch = registerBatchMapper.selectById(id);
            if (existingBatch == null) {
                log.warn("开始注册批次失败: 批次不存在, id={}", id);
                return false;
            }
            
            // 只有未开始的批次才能开始
            if (!"未开始".equals(existingBatch.getStatus())) {
                log.warn("开始注册批次失败: 只有未开始的批次才能开始, id={}, status={}", 
                        id, existingBatch.getStatus());
                return false;
            }
            
            // 更新批次状态
            int result = registerBatchMapper.updateStatus(id, "进行中");
            
            return result > 0;
        } catch (Exception e) {
            log.error("开始注册批次失败: {}", e.getMessage(), e);
            throw new RuntimeException("开始注册批次失败", e);
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean endBatch(Integer id) {
        try {
            log.debug("结束注册批次: id={}", id);
            
            // 查询原记录，确保存在
            RegisterBatch existingBatch = registerBatchMapper.selectById(id);
            if (existingBatch == null) {
                log.warn("结束注册批次失败: 批次不存在, id={}", id);
                return false;
            }
            
            // 只有进行中的批次才能结束
            if (!"进行中".equals(existingBatch.getStatus())) {
                log.warn("结束注册批次失败: 只有进行中的批次才能结束, id={}, status={}", 
                        id, existingBatch.getStatus());
                return false;
            }
            
            // 更新批次状态
            int result = registerBatchMapper.updateStatus(id, "已结束");
            
            return result > 0;
        } catch (Exception e) {
            log.error("结束注册批次失败: {}", e.getMessage(), e);
            throw new RuntimeException("结束注册批次失败", e);
        }
    }
    
    @Override
    public RegisterBatch getCurrentBatch() {
        try {
            log.debug("获取当前注册批次");
            
            // 查询当前进行中的批次
            RegisterBatch batch = registerBatchMapper.getCurrentBatch();
            if (batch != null) {
                // 获取批次关联的组织ID列表
                List<Integer> organizationIds = registerBatchOrganizationMapper.getOrganizationIdsByBatchId(batch.getId());
                batch.setTargetOrganizationIds(organizationIds);
                
                // 设置前端兼容字段
                batch.setBatchCode(batch.getBatchName());
                batch.setTitle(batch.getBatchName());
                batch.setYear(batch.getRegisterYear());
                batch.setStartDate(batch.getStartTime());
                batch.setEndDate(batch.getEndTime());
                
                // TODO: 添加注册统计数据（这里可以进一步实现）
                batch.setTotalCount(100); // 模拟数据，后续可实现实际统计
                batch.setRegisteredCount(80);
                batch.setProgressPercentage(new BigDecimal("80.0"));
            }
            
            return batch;
        } catch (Exception e) {
            log.error("获取当前注册批次失败: {}", e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    public List<RegisterBatch> getBatchOptions() {
        try {
            log.debug("获取注册批次选项");
            
            return registerBatchMapper.getBatchOptions();
        } catch (Exception e) {
            log.error("获取注册批次选项失败: {}", e.getMessage(), e);
        return new ArrayList<>();
        }
    }
    
    // 团员注册相关方法实现
    @Override
    public Map<String, Object> getRegisterStatus(Integer userId) {
        try {
            log.debug("获取团员注册状态: userId={}", userId);
            
        Map<String, Object> result = new HashMap<>();
            
            // 获取当前进行中的批次
            RegisterBatch currentBatch = registerBatchMapper.getCurrentBatch();
            if (currentBatch == null) {
                // 没有进行中的批次
        result.put("status", "未注册");
                result.put("message", "当前没有进行中的注册批次");
        return result;
            }
            
            // 设置前端兼容字段
            currentBatch.setBatchCode(currentBatch.getBatchName());
            currentBatch.setTitle(currentBatch.getBatchName());
            currentBatch.setYear(currentBatch.getRegisterYear());
            currentBatch.setStartDate(currentBatch.getStartTime());
            currentBatch.setEndDate(currentBatch.getEndTime());
            
            // 获取用户
            User user = this.getById(userId);
            if (user == null || user.getOrganization() == null) {
                result.put("status", "未注册");
                result.put("message", "用户不存在或未分配组织");
                result.put("batch", currentBatch);
        return result;
            }
            
            // 查询用户在当前批次中的注册状态
            MemberRegister register = memberRegisterMapper.getRegisterStatus(userId, currentBatch.getId());
            
            if (register == null) {
                // 用户未在当前批次中注册
                result.put("status", "未注册");
                result.put("message", "您尚未在当前批次中注册");
                result.put("batch", currentBatch);
                return result;
            }
            
            // 用户已注册，返回注册详情
            result.put("status", register.getStatus());
            result.put("register", register);
            result.put("batch", currentBatch);
            
            return result;
        } catch (Exception e) {
            log.error("获取团员注册状态失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("status", "未知");
            result.put("message", "获取注册状态失败: " + e.getMessage());
            return result;
        }
    }
    
    @Override
    public Map<String, Object> getRegisterHistory(Integer userId, Integer page, Integer size) {
        try {
            log.debug("获取团员注册历史: userId={}, page={}, size={}", userId, page, size);
            
            Page<MemberRegister> pageParam = new Page<>(page, size);
            IPage<MemberRegister> pageResult = memberRegisterMapper.getRegisterHistory(pageParam, userId);
            
            // 处理每条记录，设置兼容字段
            for (MemberRegister register : pageResult.getRecords()) {
                if (register.getBatchCode() == null && register.getBatchId() != null) {
                    // 查询批次信息设置batchCode
                    RegisterBatch batch = registerBatchMapper.selectById(register.getBatchId());
                    if (batch != null) {
                        register.setBatchCode(batch.getBatchName());
                    }
                }
            }
            
        Map<String, Object> result = new HashMap<>();
            result.put("list", pageResult.getRecords());
            result.put("total", pageResult.getTotal());
            
        return result;
        } catch (Exception e) {
            log.error("获取团员注册历史失败: {}", e.getMessage(), e);
            return Map.of("list", new ArrayList<>(), "total", 0);
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean applyRegister(MemberRegister register) {
        try {
            log.debug("提交注册申请: memberId={}, batchId={}", register.getUserId(), register.getBatchId());
            
            // 查询批次，确保存在且状态为进行中
            RegisterBatch batch = registerBatchMapper.selectById(register.getBatchId());
            if (batch == null) {
                log.warn("提交注册申请失败: 批次不存在, batchId={}", register.getBatchId());
                throw new BusinessException("注册批次不存在");
            }
            
            if (!"进行中".equals(batch.getStatus())) {
                log.warn("提交注册申请失败: 批次状态不是进行中, batchId={}, status={}", 
                        register.getBatchId(), batch.getStatus());
                throw new BusinessException("当前批次不接受注册");
            }
            
            // 查询用户信息
            User user = this.getById(register.getUserId());
            if (user == null) {
                log.warn("提交注册申请失败: 用户不存在, memberId={}", register.getUserId());
                throw new BusinessException("用户不存在");
            }
            
            // 检查用户所在组织是否在批次允许范围内
            if (user.getOrganization() == null) {
                log.warn("提交注册申请失败: 用户没有所属组织, memberId={}", register.getUserId());
                throw new BusinessException("您没有所属组织，无法注册");
            }
            
            // 获取用户组织信息
            Organization org = organizationService.getById(user.getOrganization());
            if (org == null) {
                log.warn("提交注册申请失败: 用户组织不存在, memberId={}, organizationId={}", 
                        register.getUserId(), user.getOrganization());
                throw new BusinessException("您的所属组织不存在");
            }
            
            // 检查用户所在组织是否在批次允许范围内
            int count = registerBatchOrganizationMapper.checkOrganizationInBatch(
                    batch.getId(), user.getOrganization());
            if (count <= 0) {
                log.warn("提交注册申请失败: 用户组织不在批次范围内, memberId={}, batchId={}, organizationId={}", 
                        register.getUserId(), batch.getId(), user.getOrganization());
                throw new BusinessException("您所在的组织不在当前批次的注册范围内");
            }
            
            // 查询用户是否已在当前批次中注册
            MemberRegister existingRegister = memberRegisterMapper.getRegisterStatus(
                    register.getUserId(), register.getBatchId());
            
            if (existingRegister != null) {
                // 如果状态是已驳回，则可以重新申请，否则不允许重复注册
                if (!"已驳回".equals(existingRegister.getStatus())) {
                    log.warn("提交注册申请失败: 用户已在当前批次中注册, memberId={}, batchId={}, status={}", 
                            register.getUserId(), register.getBatchId(), existingRegister.getStatus());
                    throw new BusinessException("您已在当前批次中提交注册申请");
                }
                
                // 更新现有记录
                existingRegister.setPoliticalPerformance(register.getPoliticalPerformance());
                existingRegister.setStudyPerformance(register.getStudyPerformance());
                existingRegister.setWorkPerformance(register.getWorkPerformance());
                existingRegister.setActivityParticipation(register.getActivityParticipation());
                existingRegister.setPaidFees(register.getPaidFees());
                existingRegister.setSelfEvaluation(register.getSelfEvaluation());
                existingRegister.setRemark(register.getRemark());
                existingRegister.setStatus("待审核");
                existingRegister.setUpdateTime(LocalDateTime.now());
                
                return memberRegisterMapper.updateById(existingRegister) > 0;
            }
            
            // 创建新的注册记录
            register.setUserId(user.getId());
            register.setUserName(user.getName());
            register.setOrganizationId(user.getOrganization());
            register.setOrganizationName(org.getName());
            register.setRegisterYear(batch.getRegisterYear());
            // 设置批次ID，确保直接关联
            register.setBatchId(batch.getId());
            register.setStatus("待审核");
            register.setRegisterTime(LocalDateTime.now());
            register.setCreateTime(LocalDateTime.now());
            register.setUpdateTime(LocalDateTime.now());
            
            // 在remark中保留批次信息，便于兼容旧代码
            if (register.getRemark() == null || register.getRemark().trim().isEmpty()) {
                register.setRemark("批次ID:" + batch.getId() + "," + batch.getBatchName());
            } else if (!register.getRemark().contains("批次ID:" + batch.getId())) {
                register.setRemark(register.getRemark() + " (批次ID:" + batch.getId() + ")");
            }
            
            return memberRegisterMapper.insert(register) > 0;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("提交注册申请失败: {}", e.getMessage(), e);
            throw new RuntimeException("提交注册申请失败", e);
        }
    }
    
    @Override
    public Map<String, Object> getApprovalList(User currentUser, Integer page, Integer size, Integer batchId, String memberName, String status) {
        try {
            log.debug("获取注册审批列表: userId={}, page={}, size={}, batchId={}, memberName={}, status={}", 
                    currentUser.getId(), page, size, batchId, memberName, status);
            
            // 获取当前用户可以管理的组织ID列表
            List<Integer> managedOrgIds = new ArrayList<>();
            
            // 特殊处理：用于批次成员列表API，跳过组织权限检查
            if (currentUser.getId() == 0) {
                log.debug("批次成员列表接口调用，跳过组织权限检查");
                
                // 查询所有组织批次成员
                Page<MemberRegister> pageParam = new Page<>(page, size);
                IPage<MemberRegister> pageResult = memberRegisterMapper.getApprovalList(
                        pageParam, batchId, null, memberName, status);
                
                Map<String, Object> result = new HashMap<>();
                result.put("list", pageResult.getRecords());
                result.put("total", pageResult.getTotal());
                
                return result;
            }
            
            // 普通审批列表调用 - 进行权限检查
            // 获取用户角色
            Set<String> roles = getUserRoles(currentUser.getId());
            boolean isCommitteeAdmin = roles.contains("COMMITTEE_SECRETARY") || roles.contains("DEPUTY_COMMITTEE_SECRETARY")
                                     || "团委书记".equals(currentUser.getLeaguePosition()) || "团委副书记".equals(currentUser.getLeaguePosition());
            boolean isBranchAdmin = roles.contains("BRANCH_SECRETARY") || roles.contains("DEPUTY_BRANCH_SECRETARY")
                                  || "团支书".equals(currentUser.getLeaguePosition()) || "团支部副书记".equals(currentUser.getLeaguePosition());
            
            if (isCommitteeAdmin || isBranchAdmin) {
                // 如果是团委书记/副书记或团支书/副书记，可以管理自己所在组织及下级组织
                if (currentUser.getOrganization() != null) {
                    // 添加自己所在组织
                    managedOrgIds.add(currentUser.getOrganization());
                    // 添加下级组织
                    List<Integer> subOrgIds = organizationMapper.getSubOrganizationIds(currentUser.getOrganization());
                    if (subOrgIds != null && !subOrgIds.isEmpty()) {
                        managedOrgIds.addAll(subOrgIds);
                    }
                }
            }
            
            if (managedOrgIds.isEmpty()) {
                // 如果用户没有管理权限，返回空列表
                log.warn("获取注册审批列表失败: 用户没有管理权限, userId={}", currentUser.getId());
                return Map.of("list", new ArrayList<>(), "total", 0);
            }
            
            // 查询审批列表
            Page<MemberRegister> pageParam = new Page<>(page, size);
            IPage<MemberRegister> pageResult = memberRegisterMapper.getApprovalList(
                    pageParam, batchId, managedOrgIds, memberName, status);
            
        Map<String, Object> result = new HashMap<>();
            result.put("list", pageResult.getRecords());
            result.put("total", pageResult.getTotal());
            
        return result;
        } catch (Exception e) {
            log.error("获取注册审批列表失败: {}", e.getMessage(), e);
            return Map.of("list", new ArrayList<>(), "total", 0);
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean approveRegister(Integer id, User approver, String comments) {
        try {
            log.debug("审批通过注册申请: id={}, approverId={}", id, approver.getId());
            
            // 查询注册申请
            MemberRegister register = memberRegisterMapper.selectById(id);
            if (register == null) {
                log.warn("审批通过注册申请失败: 注册申请不存在, id={}", id);
                throw new BusinessException("注册申请不存在");
            }
            
            // 只有待审核的申请才能审批
            if (!"待审核".equals(register.getStatus())) {
                log.warn("审批通过注册申请失败: 注册申请状态不是待审核, id={}, status={}", 
                        id, register.getStatus());
                throw new BusinessException("只有待审核的申请才能审批");
            }
            
            // 判断用户是否有权限审批
            Set<String> roles = getUserRoles(approver.getId());
            boolean isCommitteeAdmin = roles.contains("COMMITTEE_SECRETARY") || roles.contains("DEPUTY_COMMITTEE_SECRETARY")
                                     || "团委书记".equals(approver.getLeaguePosition()) || "团委副书记".equals(approver.getLeaguePosition());
            boolean isBranchAdmin = roles.contains("BRANCH_SECRETARY") || roles.contains("DEPUTY_BRANCH_SECRETARY")
                                  || "团支书".equals(approver.getLeaguePosition()) || "团支部副书记".equals(approver.getLeaguePosition());
            
            if (!isCommitteeAdmin && !isBranchAdmin) {
                log.warn("审批通过注册申请失败: 用户没有审批权限, approverId={}", approver.getId());
                throw new BusinessException("您没有审批权限");
            }
            
            // 验证组织权限
            if (register.getOrganizationId() != null) {
                boolean hasOrgPermission = false;
                
                // 检查是否是同组织或上级组织
                if (approver.getOrganization() != null) {
                    if (approver.getOrganization().equals(register.getOrganizationId())) {
                        // 同一组织
                        hasOrgPermission = true;
                    } else {
                        // 检查是否是上级组织
                        Organization org = organizationMapper.selectById(register.getOrganizationId());
                        if (org != null && org.getParentId() != null && org.getParentId().equals(approver.getOrganization())) {
                            hasOrgPermission = true;
                        }
                    }
                }
                
                if (!hasOrgPermission) {
                    log.warn("审批通过注册申请失败: 用户没有该组织的审批权限, approverId={}, orgId={}", 
                            approver.getId(), register.getOrganizationId());
                    throw new BusinessException("您没有该组织的审批权限");
                }
            }
            
            // 审批逻辑
            String newStatus;
            if (isBranchAdmin && !isCommitteeAdmin) {
                // 团支书审批通过，还需要团委审批
                newStatus = "团支部已审核";
            } else {
                // 团委书记审批直接通过
                newStatus = "已通过";
            }
            
            // 更新注册状态
            int result = memberRegisterMapper.updateStatus(
                    id, newStatus, comments, approver.getId(), approver.getName());
            
            return result > 0;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("审批通过注册申请失败: {}", e.getMessage(), e);
            throw new RuntimeException("审批通过注册申请失败", e);
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean rejectRegister(Integer id, User approver, String comments) {
        try {
            log.debug("驳回注册申请: id={}, approverId={}", id, approver.getId());
            
            // 查询注册申请
            MemberRegister register = memberRegisterMapper.selectById(id);
            if (register == null) {
                log.warn("驳回注册申请失败: 注册申请不存在, id={}", id);
                throw new BusinessException("注册申请不存在");
            }
            
            // 只有待审核的申请才能驳回
            if (!"待审核".equals(register.getStatus())) {
                log.warn("驳回注册申请失败: 注册申请状态不是待审核, id={}, status={}", 
                        id, register.getStatus());
                throw new BusinessException("只有待审核的申请才能驳回");
            }
            
            // 判断用户是否有权限审批
            Set<String> roles = getUserRoles(approver.getId());
            boolean isCommitteeAdmin = roles.contains("COMMITTEE_SECRETARY") || roles.contains("DEPUTY_COMMITTEE_SECRETARY");
            boolean isBranchAdmin = roles.contains("BRANCH_SECRETARY") || roles.contains("DEPUTY_BRANCH_SECRETARY");
            
            if (!isCommitteeAdmin && !isBranchAdmin) {
                log.warn("驳回注册申请失败: 用户没有审批权限, approverId={}", approver.getId());
                throw new BusinessException("您没有审批权限");
            }
            
            // 更新注册状态
            int result = memberRegisterMapper.updateStatus(
                    id, "已驳回", comments, approver.getId(), approver.getName());
            
            return result > 0;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("驳回注册申请失败: {}", e.getMessage(), e);
            throw new RuntimeException("驳回注册申请失败", e);
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchApproveRegister(List<Integer> ids, User approver, String comments) {
        try {
            log.debug("批量审批通过注册申请: ids={}, approverId={}", ids, approver.getId());
            
            if (ids == null || ids.isEmpty()) {
        return true;
            }
            
            for (Integer id : ids) {
                approveRegister(id, approver, comments);
            }
            
            return true;
        } catch (Exception e) {
            log.error("批量审批通过注册申请失败: {}", e.getMessage(), e);
            throw new RuntimeException("批量审批通过注册申请失败", e);
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchRejectRegister(List<Integer> ids, User approver, String comments) {
        try {
            log.debug("批量驳回注册申请: ids={}, approverId={}", ids, approver.getId());
            
            if (ids == null || ids.isEmpty()) {
        return true;
            }
            
            for (Integer id : ids) {
                rejectRegister(id, approver, comments);
            }
            
            return true;
        } catch (Exception e) {
            log.error("批量驳回注册申请失败: {}", e.getMessage(), e);
            throw new RuntimeException("批量驳回注册申请失败", e);
        }
    }
    
    @Override
    public Map<String, Object> getApprovalStatistics(User currentUser) {
        try {
            log.debug("获取审批统计数据: userId={}", currentUser.getId());
            
            // 获取当前用户可以管理的组织ID列表
            List<Integer> managedOrgIds = new ArrayList<>();
            
            // 获取用户角色
            Set<String> roles = getUserRoles(currentUser.getId());
            boolean isCommitteeAdmin = roles.contains("COMMITTEE_SECRETARY") || roles.contains("DEPUTY_COMMITTEE_SECRETARY");
            boolean isBranchAdmin = roles.contains("BRANCH_SECRETARY") || roles.contains("DEPUTY_BRANCH_SECRETARY");
            
            if (isCommitteeAdmin || isBranchAdmin) {
                // 如果是团委书记/副书记或团支书/副书记，可以管理自己所在组织及下级组织
                if (currentUser.getOrganization() != null) {
                    managedOrgIds.addAll(organizationMapper.getSubOrganizationIds(currentUser.getOrganization()));
                }
            }
            
            if (managedOrgIds.isEmpty()) {
                // 如果用户没有管理权限，返回空统计
                return Map.of("total", 0, "approved", 0, "rejected", 0, "pending", 0);
            }
            
            // 获取当前年度
            String currentYear = String.valueOf(LocalDate.now().getYear());
            
            // 查询统计数据
            Map<String, Object> statistics = memberRegisterMapper.getRegisterStatistics(managedOrgIds, currentYear);
            
            // 如果统计数据为null，返回空统计
            if (statistics == null) {
                return Map.of("total", 0, "approved", 0, "rejected", 0, "pending", 0);
            }
            
            return statistics;
        } catch (Exception e) {
            log.error("获取审批统计数据失败: {}", e.getMessage(), e);
            return Map.of("total", 0, "approved", 0, "rejected", 0, "pending", 0);
        }
    }
    
    @Override
    public Map<String, Object> getStatisticsData(String year, Integer organizationId) {
        // 调用带timeRange参数的方法，默认使用"year"
        return getStatisticsData(year, organizationId, "year");
    }
    
    @Override
    public Map<String, Object> getStatisticsData(String year, Integer organizationId, String timeRange) {
        try {
            log.debug("获取评议统计数据: year={}, organizationId={}, timeRange={}", year, organizationId, timeRange);
            Map<String, Object> result = new HashMap<>();
            result.put("year", year);
            result.put("organization", organizationId);
            result.put("timeRange", timeRange);
            
            // 使用MemberEvaluationMapper查询指定组织的评议活动
            LambdaQueryWrapper<MemberEvaluation> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(organizationId != null, MemberEvaluation::getOrganizationId, organizationId);
            queryWrapper.eq(StringUtils.hasText(year), MemberEvaluation::getEvaluationYear, year);
            queryWrapper.orderByDesc(MemberEvaluation::getStartTime);
            
            List<MemberEvaluation> evaluations = memberEvaluationMapper.selectList(queryWrapper);
            
            List<Map<String, Object>> data = new ArrayList<>();
            for (MemberEvaluation evaluation : evaluations) {
                Map<String, Object> item = new HashMap<>();
                
                // 基本信息
                item.put("id", evaluation.getId());
                item.put("title", evaluation.getTitle());
                item.put("status", evaluation.getStatus());
                item.put("startTime", evaluation.getStartTime());
                item.put("endTime", evaluation.getEndTime());
                
                // 使用MemberEvaluationDetailMapper获取统计数据
                Map<String, Object> statistics = memberEvaluationDetailMapper.getEvaluationStatistics(evaluation.getId());
                
                // 处理可能为null的统计数据
                Long totalCount = statistics != null ? ((Number) statistics.getOrDefault("totalCount", 0)).longValue() : 0L;
                Long completedCount = statistics != null ? ((Number) statistics.getOrDefault("completedCount", 0)).longValue() : 0L;
                Long excellentCount = statistics != null ? ((Number) statistics.getOrDefault("excellentCount", 0)).longValue() : 0L;
                Long qualifiedCount = statistics != null ? ((Number) statistics.getOrDefault("qualifiedCount", 0)).longValue() : 0L;
                Long basicQualifiedCount = statistics != null ? ((Number) statistics.getOrDefault("basicQualifiedCount", 0)).longValue() : 0L;
                Long unqualifiedCount = statistics != null ? ((Number) statistics.getOrDefault("unqualifiedCount", 0)).longValue() : 0L;
                
                // 统计数据
                item.put("totalMembers", totalCount);
                item.put("completedCount", completedCount);
                item.put("excellentCount", excellentCount);
                item.put("qualifiedCount", qualifiedCount);
                item.put("basicQualifiedCount", basicQualifiedCount);
                item.put("unqualifiedCount", unqualifiedCount);
                
                // 计算完成率
                double completionRate = (totalCount > 0) ? (completedCount * 100.0 / totalCount) : 0;
                item.put("completionRate", Math.round(completionRate));
                
                // 计算各结果比率
                double excellentRate = (completedCount > 0) ? (excellentCount * 100.0 / completedCount) : 0;
                double qualifiedRate = (completedCount > 0) ? (qualifiedCount * 100.0 / completedCount) : 0;
                double basicQualifiedRate = (completedCount > 0) ? (basicQualifiedCount * 100.0 / completedCount) : 0;
                double unqualifiedRate = (completedCount > 0) ? (unqualifiedCount * 100.0 / completedCount) : 0;
                
                item.put("excellentRate", Math.round(excellentRate));
                item.put("qualifiedRate", Math.round(qualifiedRate));
                item.put("basicQualifiedRate", Math.round(basicQualifiedRate));
                item.put("unqualifiedRate", Math.round(unqualifiedRate));
                
                data.add(item);
            }
            
            result.put("data", data);
            return result;
        } catch (Exception e) {
            log.error("获取评议统计数据失败: {}", e.getMessage(), e);
            
            // 返回空结果
            Map<String, Object> emptyResult = new HashMap<>();
            emptyResult.put("year", year);
            emptyResult.put("organization", organizationId);
            emptyResult.put("timeRange", timeRange);
            emptyResult.put("data", new ArrayList<>());
            return emptyResult;
        }
    }
    
    @Override
    public String exportStatistics(String year, Integer organizationId) {
        // 模拟实现
        return "/export/register-statistics-" + year + "-" + organizationId + ".xlsx";
    }
    
    @Override
    public String exportStatistics(String year, Integer organizationId, String timeRange) {
        // 导出评议统计数据实现
        log.info("导出评议统计数据: year={}, organizationId={}, timeRange={}", year, organizationId, timeRange);
        // 实际实现应该生成统计报表并返回文件URL
        return "/api/files/evaluations/statistics_" + System.currentTimeMillis() + ".xlsx";
    }
    
    @Override
    public boolean resetEvaluation(Integer evaluationId, Integer memberId) {
        log.info("重置评议结果: evaluationId={}, memberId={}", evaluationId, memberId);
        try {
            // 使用MyBatis-Plus删除评议结果
            LambdaQueryWrapper<MemberEvaluationDetail> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(MemberEvaluationDetail::getEvaluationId, evaluationId)
                       .eq(MemberEvaluationDetail::getUserId, memberId);
            
            int result = memberEvaluationDetailMapper.delete(queryWrapper);
            return result > 0;
        } catch (Exception e) {
            log.error("重置评议结果失败", e);
            return false;
        }
    }
    
    @Override
    public boolean sendEvaluationReminder(Integer evaluationId) {
        log.info("发送评议提醒: evaluationId={}", evaluationId);
        try {
            // 查询未完成评议的成员
            String sql = "SELECT d.user_id, u.name, u.phone, u.email " +
                    "FROM member_evaluation_detail d " +
                    "JOIN user u ON d.user_id = u.id " +
                    "WHERE d.evaluation_id = ? AND (d.status IS NULL OR d.status != '已评议')";
            
            List<Map<String, Object>> members = jdbcTemplate.queryForList(sql, evaluationId);
            
            // 查询评议活动信息
            MemberEvaluation evaluation = memberEvaluationMapper.selectById(evaluationId);
            if (evaluation == null) {
                log.warn("发送评议提醒失败: 评议活动不存在, id={}", evaluationId);
                return false;
            }
            
            // 在实际项目中，这里应该调用消息发送服务发送提醒
            // 比如发送短信、邮件或系统通知等
            for (Map<String, Object> member : members) {
                String name = (String) member.get("name");
                String phone = (String) member.get("phone");
                String email = (String) member.get("email");
                
                log.info("向成员{}发送评议提醒: 手机={}, 邮箱={}, 评议活动={}", 
                        name, phone, email, evaluation.getTitle());
                
                // 模拟消息发送成功
            }
            
            return true;
        } catch (Exception e) {
            log.error("发送评议提醒失败", e);
            return false;
        }
    }
    
    @Override
    public String exportEvaluationResults(Integer evaluationId) {
        try {
            log.info("导出评议结果: evaluationId={}", evaluationId);
            
            // 查询评议活动信息
            MemberEvaluation evaluation = memberEvaluationMapper.selectById(evaluationId);
            if (evaluation == null) {
                log.warn("导出评议结果失败: 评议活动不存在, id={}", evaluationId);
                return null;
            }
            
            // 在实际项目中，这里应该生成Excel文件并保存到文件系统
            // 生成文件名
            String fileName = "evaluation_results_" + evaluationId + "_" + System.currentTimeMillis() + ".xlsx";
            String filePath = "/api/files/evaluations/" + fileName;
            
            // 记录导出日志
            log.info("成功导出评议结果: evaluationId={}, filePath={}", evaluationId, filePath);
            
            return filePath;
        } catch (Exception e) {
            log.error("导出评议结果失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getEvaluationList(Integer page, Integer size, String year, String status, Integer organizationId) {
        try {
            log.debug("获取评议列表: page={}, size={}, year={}, status={}, organizationId={}", page, size, year, status, organizationId);
            
            // 构建查询SQL
            StringBuilder countSqlBuilder = new StringBuilder();
            countSqlBuilder.append("SELECT COUNT(*) FROM member_evaluation WHERE 1=1 ");
            
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("SELECT * FROM member_evaluation WHERE 1=1 ");
            
            List<Object> params = new ArrayList<>();
            
            if (organizationId != null) {
                sqlBuilder.append("AND organization_id = ? ");
                countSqlBuilder.append("AND organization_id = ? ");
                params.add(organizationId);
            }
            
            if (status != null && !status.isEmpty()) {
                sqlBuilder.append("AND status = ? ");
                countSqlBuilder.append("AND status = ? ");
                params.add(status);
            }
            
            if (year != null && !year.isEmpty()) {
                sqlBuilder.append("AND evaluation_year = ? ");
                countSqlBuilder.append("AND evaluation_year = ? ");
                params.add(year);
            }
            
            // 添加排序
            sqlBuilder.append("ORDER BY start_time DESC ");
            
            // 添加分页
            int offset = (page - 1) * size;
            sqlBuilder.append("LIMIT ?, ? ");
            List<Object> pagingParams = new ArrayList<>(params);
            pagingParams.add(offset);
            pagingParams.add(size);
            
            log.debug("执行SQL: {}", sqlBuilder.toString());
            log.debug("参数: {}", pagingParams);
            
            // 查询总数
            Integer total = jdbcTemplate.queryForObject(countSqlBuilder.toString(), Integer.class, params.toArray());
            
            // 查询数据
            List<Map<String, Object>> items = jdbcTemplate.queryForList(sqlBuilder.toString(), pagingParams.toArray());
            
            Map<String, Object> result = new HashMap<>();
            result.put("total", total != null ? total : 0);
            result.put("items", items);
            
            return result;
        } catch (Exception e) {
            log.error("获取评议列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("total", 0);
            result.put("items", new ArrayList<>());
            return result;
        }
    }

    @Override
    public Map<String, Object> getResultList(Integer evaluationId, Integer page, Integer size, String status, String memberName, String result) {
        try {
            // 创建分页对象
            Page<MemberEvaluationDetail> pageParam = new Page<>(page, size);
            
            // 通过Mapper接口进行分页查询
            IPage<MemberEvaluationDetail> pageResult = memberEvaluationDetailMapper.selectResultList(
                pageParam, evaluationId, status, memberName, result);
            
            // 如果前端需要兼容之前的API格式，转换为Map格式
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("total", pageResult.getTotal());
            resultMap.put("list", pageResult.getRecords());  // 使用list字段名而不是items
            
            return resultMap;
        } catch (Exception e) {
            log.error("获取评议结果列表失败: {}", e.getMessage(), e);
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("total", 0);
            resultMap.put("list", new ArrayList<>());
            return resultMap;
        }
    }

    @Override
    public Map<String, Object> getEvaluationData(Integer id) {
        try {
            // 使用JdbcTemplate直接从数据库查询
            String sql = "SELECT * FROM member_evaluation WHERE id = ?";
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, id);
            
            if (results != null && !results.isEmpty()) {
                return results.get(0);
            }
            
            log.warn("未找到ID为{}的评议活动", id);
            return null;
        } catch (Exception e) {
            log.error("获取评议活动数据失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public MemberEvaluation getEvaluationDetail(Integer id) {
        if (id == null) {
            log.warn("获取评议活动详情失败：ID为空");
            return null;
        }
        
        try {
            // 使用MemberEvaluationMapper查询评议活动详情
            MemberEvaluation evaluation = memberEvaluationMapper.selectById(id);
            
            if (evaluation == null) {
                log.warn("未找到ID为{}的评议活动", id);
                return null;
            }
            
            // 设置组织名称
            if (evaluation.getOrganizationId() != null) {
                try {
                    Organization org = organizationService.getById(evaluation.getOrganizationId());
                    if (org != null) {
                        evaluation.setOrganizationName(org.getName());
                        log.debug("设置组织名称成功: organizationId={}, organizationName={}", 
                                evaluation.getOrganizationId(), evaluation.getOrganizationName());
                    } else {
                        log.warn("未找到对应的组织信息: organizationId={}", evaluation.getOrganizationId());
                        // 设置默认值，避免前端显示为null
                        evaluation.setOrganizationName("组织未知");
                    }
                } catch (Exception e) {
                    log.error("获取组织信息失败: {}", e.getMessage());
                    // 设置默认值，避免前端显示为null
                    evaluation.setOrganizationName("组织未知");
                }
            } else {
                log.warn("评议活动缺少组织ID: evaluationId={}", evaluation.getId());
                // 设置默认值，避免前端显示为null
                evaluation.setOrganizationName("组织未知");
            }
            
            log.debug("查询到评议活动: id={}, title={}", evaluation.getId(), evaluation.getTitle());
            return evaluation;
        } catch (Exception e) {
            log.error("获取评议活动详情失败", e);
            return null;
        }
    }

    @Override
    public boolean createEvaluation(MemberEvaluation evaluation) {
        try {
            log.debug("创建评议活动: {}", evaluation.getTitle());
            
            if (evaluation.getCreateTime() == null) {
                evaluation.setCreateTime(LocalDateTime.now());
            }
            evaluation.setUpdateTime(LocalDateTime.now());
            
            // 默认状态为草稿
            if (evaluation.getStatus() == null) {
                evaluation.setStatus("草稿");
            }
            
            // 保存到数据库
            int result = memberEvaluationMapper.insert(evaluation);
            
            if (result > 0 && evaluation.getOrganizationId() != null) {
                // 创建成功后，将所有组织成员添加到评议详情表
                try {
                    log.debug("开始为组织{}的成员创建评议记录", evaluation.getOrganizationId());
                    
                    // 获取该组织的所有成员
                    List<User> members = this.getUsersByOrganization(evaluation.getOrganizationId());
                    
                    if (members != null && !members.isEmpty()) {
                        int count = 0;
                        for (User member : members) {
                            try {
                                // 创建未评议状态的记录
                                MemberEvaluationDetail detail = new MemberEvaluationDetail();
                                detail.setEvaluationId(evaluation.getId());
                                detail.setUserId(member.getId());
                                detail.setUserName(member.getName()); // 设置用户姓名
                                detail.setStatus("未评议"); // 设置初始状态为未评议
                                detail.setCreateTime(LocalDateTime.now());
                                detail.setUpdateTime(LocalDateTime.now());
                                
                                // 保存评议详情
                                memberEvaluationDetailMapper.insert(detail);
                                count++;
                            } catch (Exception e) {
                                log.warn("为成员{}创建评议记录失败: {}", member.getId(), e.getMessage());
                            }
                        }
                        
                        log.info("已为评议活动{}创建{}条成员评议记录", evaluation.getId(), count);
                    } else {
                        log.warn("组织{}没有找到成员，无法创建评议记录", evaluation.getOrganizationId());
                    }
                } catch (Exception e) {
                    log.error("创建成员评议记录时出错: {}", e.getMessage());
                    // 记录错误但不影响评议活动的创建结果
                }
            }
            
            return result > 0;
        } catch (Exception e) {
            log.error("创建评议活动失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean updateEvaluation(MemberEvaluation evaluation) {
        try {
            log.debug("更新评议活动: id={}, title={}", evaluation.getId(), evaluation.getTitle());
            
            // 查询原记录，确保存在
            MemberEvaluation existingEvaluation = memberEvaluationMapper.selectById(evaluation.getId());
            if (existingEvaluation == null) {
                log.warn("更新评议活动失败: 评议活动不存在, id={}", evaluation.getId());
                return false;
            }
            
            evaluation.setUpdateTime(LocalDateTime.now());
            
            // 更新数据库
            int result = memberEvaluationMapper.updateById(evaluation);
            return result > 0;
        } catch (Exception e) {
            log.error("更新评议活动失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean deleteEvaluation(Integer id) {
        try {
            log.debug("删除评议活动: id={}", id);
            
            // 查询原记录，确保存在
            MemberEvaluation existingEvaluation = memberEvaluationMapper.selectById(id);
            if (existingEvaluation == null) {
                log.warn("删除评议活动失败: 评议活动不存在, id={}", id);
                return false;
            }
            
            // 删除关联的评议详情
            LambdaQueryWrapper<MemberEvaluationDetail> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(MemberEvaluationDetail::getEvaluationId, id);
            memberEvaluationDetailMapper.delete(queryWrapper);
            
            // 删除评议活动
            int result = memberEvaluationMapper.deleteById(id);
            return result > 0;
        } catch (Exception e) {
            log.error("删除评议活动失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean startEvaluation(Integer id) {
        try {
            log.debug("开始评议活动: id={}", id);
            
            // 查询原记录，确保存在
            MemberEvaluation existingEvaluation = memberEvaluationMapper.selectById(id);
            if (existingEvaluation == null) {
                log.warn("开始评议活动失败: 评议活动不存在, id={}", id);
                return false;
            }
            
            // 更新状态
            MemberEvaluation updateEvaluation = new MemberEvaluation();
            updateEvaluation.setId(id);
            updateEvaluation.setStatus("进行中");
            updateEvaluation.setStartTime(LocalDateTime.now());
            updateEvaluation.setUpdateTime(LocalDateTime.now());
            
            int result = memberEvaluationMapper.updateById(updateEvaluation);
            return result > 0;
        } catch (Exception e) {
            log.error("开始评议活动失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean endEvaluation(Integer id) {
        try {
            log.debug("结束评议活动: id={}", id);
            
            // 查询原记录，确保存在
            MemberEvaluation existingEvaluation = memberEvaluationMapper.selectById(id);
            if (existingEvaluation == null) {
                log.warn("结束评议活动失败: 评议活动不存在, id={}", id);
                return false;
            }
            
            // 更新状态
            MemberEvaluation updateEvaluation = new MemberEvaluation();
            updateEvaluation.setId(id);
            updateEvaluation.setStatus("已完成");
            updateEvaluation.setEndTime(LocalDateTime.now());
            updateEvaluation.setUpdateTime(LocalDateTime.now());
            
            int result = memberEvaluationMapper.updateById(updateEvaluation);
            return result > 0;
        } catch (Exception e) {
            log.error("结束评议活动失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean cancelEvaluation(Integer id) {
        try {
            log.debug("取消评议活动: id={}", id);
            
            // 查询原记录，确保存在
            MemberEvaluation existingEvaluation = memberEvaluationMapper.selectById(id);
            if (existingEvaluation == null) {
                log.warn("取消评议活动失败: 评议活动不存在, id={}", id);
                return false;
            }
            
            // 更新状态
            MemberEvaluation updateEvaluation = new MemberEvaluation();
            updateEvaluation.setId(id);
            updateEvaluation.setStatus("已取消");
            updateEvaluation.setUpdateTime(LocalDateTime.now());
            
            int result = memberEvaluationMapper.updateById(updateEvaluation);
            return result > 0;
        } catch (Exception e) {
            log.error("取消评议活动失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Map<String, Object> getEvaluationResultsFromDb(Integer evaluationId) {
        try {
            log.debug("从数据库获取评议结果: evaluationId={}", evaluationId);
            
            // 使用MemberEvaluationDetailMapper获取统计数据
            Map<String, Object> statistics = memberEvaluationDetailMapper.getEvaluationStatistics(evaluationId);
            
            // 计算总体进度
            Long totalCount = ((Number) statistics.getOrDefault("totalCount", 0)).longValue();
            Long completedCount = ((Number) statistics.getOrDefault("completedCount", 0)).longValue();
            
            int overallProgress = 0;
            if (totalCount > 0) {
                overallProgress = (int) ((completedCount * 100) / totalCount);
            }
            statistics.put("overallProgress", overallProgress);
            
            // 计算各类评议结果比率
            Long excellentCount = ((Number) statistics.getOrDefault("excellentCount", 0)).longValue();
            Long qualifiedCount = ((Number) statistics.getOrDefault("qualifiedCount", 0)).longValue();
            Long basicQualifiedCount = ((Number) statistics.getOrDefault("basicQualifiedCount", 0)).longValue();
            Long unqualifiedCount = ((Number) statistics.getOrDefault("unqualifiedCount", 0)).longValue();
            
            int excellentRate = 0;
            int qualifiedRate = 0;
            int basicQualifiedRate = 0;
            int unqualifiedRate = 0;
            
            if (completedCount > 0) {
                excellentRate = (int) ((excellentCount * 100) / completedCount);
                qualifiedRate = (int) ((qualifiedCount * 100) / completedCount);
                basicQualifiedRate = (int) ((basicQualifiedCount * 100) / completedCount);
                unqualifiedRate = (int) ((unqualifiedCount * 100) / completedCount);
            }
            
            statistics.put("excellentRate", excellentRate);
            statistics.put("qualifiedRate", qualifiedRate);
            statistics.put("basicQualifiedRate", basicQualifiedRate);
            statistics.put("unqualifiedRate", unqualifiedRate);
            
            return statistics;
        } catch (Exception e) {
            log.error("从数据库获取评议结果统计失败: {}", e.getMessage(), e);
            Map<String, Object> emptyStats = new HashMap<>();
            emptyStats.put("totalCount", 0);
            emptyStats.put("completedCount", 0);
            emptyStats.put("overallProgress", 0);
            emptyStats.put("excellentCount", 0);
            emptyStats.put("excellentRate", 0);
            emptyStats.put("qualifiedCount", 0);
            emptyStats.put("qualifiedRate", 0);
            emptyStats.put("basicQualifiedCount", 0);
            emptyStats.put("basicQualifiedRate", 0);
            emptyStats.put("unqualifiedCount", 0);
            emptyStats.put("unqualifiedRate", 0);
            return emptyStats;
        }
    }

    @Override
    public boolean submitResults(List<EvaluationResult> results) {
        try {
            log.debug("提交评议结果: 共{}条", results.size());
            
            if (results.isEmpty()) {
                return true;
            }
            
            for (EvaluationResult result : results) {
                // 直接使用ID更新记录
                if (result.getId() != null) {
                    // 查询现有评议结果
                    MemberEvaluationDetail detail = memberEvaluationDetailMapper.selectById(result.getId());
                    
                    if (detail != null) {
                        // 更新现有评议结果
                        detail.setResult(result.getResult());
                        detail.setComment(result.getComment());
                        detail.setEvaluatorId(result.getEvaluatorId());
                        detail.setEvaluatorName(result.getEvaluatorName());
                        detail.setEvaluationTime(result.getEvaluationTime());
                        detail.setStatus("已评议");
                        detail.setUpdateTime(LocalDateTime.now());
                        
                        memberEvaluationDetailMapper.updateById(detail);
                        log.debug("更新评议结果: id={}, result={}", detail.getId(), detail.getResult());
                    } else {
                        log.warn("更新评议结果失败: 未找到ID为{}的评议记录", result.getId());
                    }
                } else {
                // 查询是否已存在评议结果
                LambdaQueryWrapper<MemberEvaluationDetail> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(MemberEvaluationDetail::getEvaluationId, result.getEvaluationId())
                          .eq(MemberEvaluationDetail::getUserId, result.getMemberId());
                
                MemberEvaluationDetail detail = memberEvaluationDetailMapper.selectOne(queryWrapper);
                
                if (detail != null) {
                    // 更新现有评议结果
                    detail.setResult(result.getResult());
                        detail.setComment(result.getComment());
                    detail.setEvaluatorId(result.getEvaluatorId());
                    detail.setEvaluatorName(result.getEvaluatorName());
                    detail.setEvaluationTime(result.getEvaluationTime());
                    detail.setStatus("已评议");
                    detail.setUpdateTime(LocalDateTime.now());
                    
                    memberEvaluationDetailMapper.updateById(detail);
                        log.debug("更新评议结果: id={}, result={}", detail.getId(), detail.getResult());
                } else {
                    // 插入新的评议结果
                    detail = new MemberEvaluationDetail();
                    detail.setEvaluationId(result.getEvaluationId());
                    detail.setUserId(result.getMemberId());
                    detail.setResult(result.getResult());
                        detail.setComment(result.getComment());
                    detail.setEvaluatorId(result.getEvaluatorId());
                    detail.setEvaluatorName(result.getEvaluatorName());
                    detail.setEvaluationTime(result.getEvaluationTime());
                    detail.setStatus("已评议");
                    detail.setCreateTime(LocalDateTime.now());
                    detail.setUpdateTime(LocalDateTime.now());
                    
                    memberEvaluationDetailMapper.insert(detail);
                        log.debug("插入新评议结果: evaluationId={}, userId={}, result={}", 
                                detail.getEvaluationId(), detail.getUserId(), detail.getResult());
                }
                }
            }
            
            return true;
        } catch (Exception e) {
            log.error("提交评议结果失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Map<String, Object> getBatchMemberList(Integer batchId, Integer page, Integer size, String keyword, String status) {
        try {
            log.info("获取批次成员列表: batchId={}, page={}, size={}, keyword={}, status={}", 
                    batchId, page, size, keyword, status);
            
            if (batchId == null) {
                log.warn("获取批次成员列表失败: 批次ID不能为空");
                return Map.of("list", new ArrayList<>(), "total", 0);
            }
            
            // 使用新的专用方法查询批次成员
            Page<MemberRegister> pageParam = new Page<>(page, size);
            IPage<MemberRegister> pageResult = memberRegisterMapper.getBatchMemberList(
                    pageParam, batchId, keyword, status);
            
            log.info("批次成员查询结果: 总记录数={}, 当前页记录数={}", 
                    pageResult.getTotal(), pageResult.getRecords().size());
            
            Map<String, Object> result = new HashMap<>();
            result.put("list", pageResult.getRecords());
            result.put("total", pageResult.getTotal());
            
            return result;
        } catch (Exception e) {
            log.error("获取批次成员列表出错", e);
            return Map.of("list", new ArrayList<>(), "total", 0);
        }
    }

    @Override
    public boolean isAdmin(Integer userId) {
        if (userId == null) {
            return false;
        }
        
        User user = getById(userId);
        if (user == null) {
            return false;
        }
        
        // 根据系统的角色设计判断是否为管理员
        // 通过用户表中的职位判断，假设leaguePosition为"管理员"表示系统管理员
        return "管理员".equals(user.getLeaguePosition());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean activateUser(Integer userId, Integer adminId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        if (adminId == null) {
            throw new BusinessException("管理员ID不能为空");
        }
        
        // 检查用户是否存在
        User user = this.getById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("用户", "id", userId);
        }
        
        // 检查管理员是否有权限激活该用户
        User admin = this.getById(adminId);
        if (admin == null) {
            throw new ResourceNotFoundException("管理员", "id", adminId);
        }
        
        // 检查管理员是否有权限（简单检查：管理员必须与用户在同一组织或是上级组织）
        if (admin.getOrganization() == null || !admin.getOrganization().equals(user.getOrganization())) {
            // 简化权限检查：只检查是否同组织，或管理员是否具有管理员角色
            boolean isAdminRole = "团委书记".equals(admin.getLeaguePosition()) || 
                                  "团委副书记".equals(admin.getLeaguePosition()) ||
                                  "团支书".equals(admin.getLeaguePosition()) ||
                                  isAdmin(adminId);
                                  
            if (!isAdminRole) {
                throw new BusinessException("您没有权限激活该用户");
            }
        }
        
        // 激活用户
        user.setIsActivated(true);
        user.setUpdateTime(LocalDateTime.now());
        
        // 记录日志
        log.info("用户 {} 被管理员 {} 激活", userId, adminId);
        
        return this.updateById(user);
    }

    @Override
    public IPage<User> getPendingUsers(Page<User> page, Integer organizationId, String name, String status) {
        if (organizationId == null) {
            throw new BusinessException("组织ID不能为空");
        }
        
        // 查询当前组织及其子组织列表
        List<Integer> orgIds = new ArrayList<>();
        orgIds.add(organizationId);
        
        // 添加子组织
        List<Integer> subOrgIds = organizationMapper.getSubOrganizationIds(organizationId);
        if (subOrgIds != null && !subOrgIds.isEmpty()) {
            orgIds.addAll(subOrgIds);
        }
        
        log.info("查询组织及子组织未激活用户: 组织ID={}, 子组织数量={}", organizationId, orgIds.size() - 1);

        // 根据status参数确定isActivated值
        Boolean isActivated = null;
        if (StringUtils.hasText(status)) {
            if ("activated".equals(status)) {
                isActivated = true;
            } else if ("pending".equals(status)) {
                isActivated = false;
            }
        } else {
            // 默认查询未激活用户
            isActivated = false;
        }
        
        // 使用专用的Mapper方法查询，该方法不会返回密码字段
        return userMapper.selectPendingUsersPage(page, orgIds, name, isActivated);
    }
} 