import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { getUserInfo } from '../api/user';
import { logout as apiLogout } from '../api/login';
import { ElMessage } from 'element-plus';

// 用户角色常量
export const USER_ROLES = {
  COMMITTEE_SECRETARY: 'committee_secretary', // 团委书记
  DEPUTY_COMMITTEE_SECRETARY: 'deputy_committee_secretary', // 团委副书记
  BRANCH_SECRETARY: 'branch_secretary',      // 团支书
  DEPUTY_BRANCH_SECRETARY: 'deputy_branch_secretary', // 团支部副书记
  MEMBER: 'member'                          // 普通团员
};

interface UserInfo {
  id: string;
  name: string;
  card: string;
  avatar?: string;
  roles?: string[];
  organization?: number | null;
  gender?: string;
  ethnic?: string;
  birthday?: string;
  age?: string;
  grade?: string;
  department?: string;
  organization_name?: string;
  organization_full_name?: string;
  organization_type?: string;
  organization_member_count?: number;
  league_position?: string;
  joinLeagueDate?: string;
  phone?: string;
  email?: string;
  politicalStatus?: string;
  occupation?: string;
  educationLevel?: string;
  educationStatus?: string;
  isActivated?: boolean; // 是否已激活
}

export const useUserStore = defineStore('user', () => {
  const id = ref('');
  const name = ref('');
  const card = ref('');
  const avatar = ref('');
  const role = ref<string[]>([]);
  const token = ref(localStorage.getItem('token') || '');
  const isLoggedIn = ref(!!localStorage.getItem('token'));
  const organization = ref(''); // 组织名称
  const organizationId = ref<number | null>(null); // 组织ID
  const leaguePosition = ref(''); // 团内职务
  const gender = ref(''); // 性别
  const ethnic = ref(''); // 民族
  const birthday = ref(''); // 出生日期
  const age = ref(''); // 年龄
  const grade = ref(''); // 班级
  const department = ref(''); // 部门
  const organizationFullName = ref(''); // 组织全称
  const organizationType = ref(''); // 组织类型
  const organizationMemberCount = ref(0); // 组织成员数量
  const joinLeagueDate = ref(''); // 入团时间
  
  // 用户信息字段
  const phone = ref(''); // 联系电话
  const email = ref(''); // 电子邮箱
  const politicalStatus = ref(''); // 政治面貌
  const occupation = ref(''); // 职业
  const educationLevel = ref(''); // 教育水平
  const educationStatus = ref(''); // 教育状态
  // 新增额外字段
  const address = ref(''); // 户籍地址
  const qq = ref(''); // QQ号码
  const wechat = ref(''); // 微信
  const weibo = ref(''); // 微博
  const workUnit = ref(''); // 工作单位
  const joinPartyDate = ref(''); // 入党时间
  const isActivated = ref<boolean | null>(null); // 是否已激活
  
  // 待办事项计数
  const pendingTasksCount = ref(0);
  const uncompletedTasksCount = ref(0);

  // 计算属性：用户信息对象
  const userInfo = computed(() => ({
    id: id.value,
    name: name.value,
    card: card.value,
    avatar: avatar.value,
    roles: role.value,
    organization: organizationId.value,
    gender: gender.value,
    ethnic: ethnic.value,
    birthday: birthday.value,
    age: age.value,
    grade: grade.value,
    department: department.value,
    organization_name: organization.value,
    organization_full_name: organizationFullName.value,
    organization_type: organizationType.value,
    organization_member_count: organizationMemberCount.value,
    league_position: leaguePosition.value,
    join_league_date: joinLeagueDate.value,
    join_party_date: joinPartyDate.value,
    phone: phone.value,
    email: email.value,
    political_status: politicalStatus.value,
    occupation: occupation.value,
    education_level: educationLevel.value,
    education_status: educationStatus.value,
    address: address.value,
    qq: qq.value,
    wechat: wechat.value,
    weibo: weibo.value,
    work_unit: workUnit.value
  }));

  // 计算属性：判断用户角色
  const isCommitteeSecretary = computed(() => {
    // 团委书记或副书记判断逻辑
    return leaguePosition.value === '团委书记' || leaguePosition.value === '团委副书记';
  });

  const isBranchSecretary = computed(() => {
    // 团支书或副书记判断逻辑
    return leaguePosition.value === '团支书' || leaguePosition.value === '团支部副书记';
  });

  const isMember = computed(() => {
    // 普通团员判断逻辑
    return leaguePosition.value === '团员' || (!isCommitteeSecretary.value && !isBranchSecretary.value);
  });

  // 新增：用户头像计算属性，确保始终有值
  const userAvatar = computed(() => {
    if (avatar.value) {
      console.log('使用现有头像URL:', avatar.value);
      return avatar.value;
    } else {
      // 根据性别返回默认头像
      const defaultAvatar = gender.value === '女' 
        ? new URL('../assets/img/girl.png', import.meta.url).href
        : new URL('../assets/img/boy.png', import.meta.url).href;
      console.log('使用默认头像URL:', defaultAvatar);
      return defaultAvatar;
    }
  });

  // 设置token
  function setToken(newToken: string) {
    if (!newToken) {
      console.warn('尝试设置空token');
      return;
    }
    
    console.log('设置token:', newToken.substring(0, 10) + '...');
    
    // 更新store中的状态
    token.value = newToken;
    isLoggedIn.value = true;
    
    // 同时更新localStorage
    localStorage.setItem('token', newToken);
    
    // 确认设置和状态
    console.log('设置token成功:', !!localStorage.getItem('token'));
    console.log('登录状态已更新:', isLoggedIn.value);
  }
  
  // 设置token过期时间
  function setTokenExpiry(expiryTime: number) {
    localStorage.setItem('token_expiry', expiryTime.toString());
  }
  
  // 清除用户数据（退出登录时使用）
  function clearUserInfo() {
    id.value = '';
    name.value = '';
    card.value = '';
    avatar.value = '';
    role.value = [];
    token.value = '';
    isLoggedIn.value = false;
    organization.value = '';
    organizationId.value = null;
    leaguePosition.value = '';
    gender.value = '';
    ethnic.value = '';
    birthday.value = '';
    age.value = '';
    grade.value = '';
    department.value = '';
    organizationFullName.value = '';
    organizationType.value = '';
    organizationMemberCount.value = 0;
    pendingTasksCount.value = 0;
    uncompletedTasksCount.value = 0;
    joinLeagueDate.value = '';
    
    // 清除用户信息字段
    phone.value = '';
    email.value = '';
    politicalStatus.value = '';
    occupation.value = '';
    educationLevel.value = '';
    educationStatus.value = '';
    
    // 清除新增额外字段
    address.value = '';
    qq.value = '';
    wechat.value = '';
    weibo.value = '';
    workUnit.value = '';
    joinPartyDate.value = '';
    
    // 清除存储的token和用户信息
    localStorage.removeItem('token');
    localStorage.removeItem('token_expiry');
    localStorage.removeItem('userInfo');
    sessionStorage.removeItem('user_info');
  }
  
  // 初始化用户信息
  function initFromStorage() {
    try {
      console.log('开始从localStorage初始化用户状态');
      
      // 1. 首先检查并设置token状态 - 最优先操作
      const storedToken = localStorage.getItem('token');
      console.log('从localStorage获取token:', !!storedToken);
      
      if (storedToken) {
        token.value = storedToken;
        isLoggedIn.value = true;
        console.log('成功设置token状态，isLoggedIn =', isLoggedIn.value);
      } else {
        // 如果没有token，确保状态也是未登录
        token.value = '';
        isLoggedIn.value = false;
        console.log('未找到token，设置为未登录状态');
        return; // 如果没有token，不需要继续获取用户信息
      }
      
      // 2. 其次从localStorage获取用户信息
      const storedUserInfo = localStorage.getItem('userInfo');
      
      if (storedUserInfo) {
        try {
        const userInfo = JSON.parse(storedUserInfo);
          console.log('从localStorage加载用户信息成功');
        
        // 设置所有可用字段
        if (userInfo.id !== undefined) id.value = userInfo.id;
        if (userInfo.name) name.value = userInfo.name;
          if (userInfo.avatar) avatar.value = userInfo.avatar;
        if (userInfo.gender) gender.value = userInfo.gender;
        if (userInfo.organizationId !== undefined) organizationId.value = userInfo.organizationId;
        
          // 设置组织信息
        if (userInfo.organization) organization.value = userInfo.organization;
        if (userInfo.organizationFullName) organizationFullName.value = userInfo.organizationFullName;
        if (userInfo.organizationType) organizationType.value = userInfo.organizationType;
          if (userInfo.organizationMemberCount !== undefined) organizationMemberCount.value = userInfo.organizationMemberCount;
        
          // 设置其他用户字段
        if (userInfo.leaguePosition) leaguePosition.value = userInfo.leaguePosition;
        if (userInfo.ethnic) ethnic.value = userInfo.ethnic;
        if (userInfo.birthday) birthday.value = userInfo.birthday;
          if (userInfo.age) age.value = userInfo.age;
          if (userInfo.grade) grade.value = userInfo.grade;
          if (userInfo.department) department.value = userInfo.department;
          if (userInfo.joinLeagueDate || userInfo.join_league_date) joinLeagueDate.value = userInfo.joinLeagueDate || userInfo.join_league_date;
          if (userInfo.joinPartyDate || userInfo.join_party_date) joinPartyDate.value = userInfo.joinPartyDate || userInfo.join_party_date;
        if (userInfo.phone) phone.value = userInfo.phone;
        if (userInfo.email) email.value = userInfo.email;
        if (userInfo.politicalStatus || userInfo.political_status) politicalStatus.value = userInfo.politicalStatus || userInfo.political_status;
        if (userInfo.occupation) occupation.value = userInfo.occupation;
        if (userInfo.educationLevel || userInfo.education_level) educationLevel.value = userInfo.educationLevel || userInfo.education_level;
        if (userInfo.educationStatus || userInfo.education_status) educationStatus.value = userInfo.educationStatus || userInfo.education_status;
        if (userInfo.address) address.value = userInfo.address;
        if (userInfo.qq) qq.value = userInfo.qq;
        if (userInfo.wechat) wechat.value = userInfo.wechat;
        if (userInfo.weibo) weibo.value = userInfo.weibo;
        if (userInfo.workUnit || userInfo.work_unit) workUnit.value = userInfo.workUnit || userInfo.work_unit;
          if (userInfo.card) card.value = userInfo.card;
          if (userInfo.isActivated !== undefined) isActivated.value = userInfo.isActivated;
          } catch (error) {
          console.error('解析localStorage中的用户信息出错:', error);
        }
      } else {
        console.log('localStorage中没有存储用户信息');
      }
      
      console.log('用户状态初始化完成，登录状态:', isLoggedIn.value);
    } catch (error) {
      console.error('从本地存储初始化用户信息出错:', error);
      // 出错时重置登录状态
      token.value = '';
      isLoggedIn.value = false;
    }
  }
  
  // 保存用户信息到本地
  function saveUserInfoToStorage() {
    // 先清除之前存储的用户信息，防止信息混合
    localStorage.removeItem('userInfo');
    
    const data = {
      id: id.value,
      name: name.value,
      card: card.value,
      avatar: avatar.value,
      organization: organization.value,
      organizationId: organizationId.value,
      organizationFullName: organizationFullName.value,
      organizationType: organizationType.value,
      organizationMemberCount: organizationMemberCount.value,
      leaguePosition: leaguePosition.value,
      gender: gender.value,
      ethnic: ethnic.value,
      birthday: birthday.value,
      age: age.value,
      grade: grade.value,
      department: department.value,
      join_league_date: joinLeagueDate.value,
      join_party_date: joinPartyDate.value,
      phone: phone.value,
      email: email.value,
      political_status: politicalStatus.value,
      occupation: occupation.value,
      education_level: educationLevel.value,
      education_status: educationStatus.value,
      address: address.value,
      qq: qq.value,
      wechat: wechat.value,
      weibo: weibo.value,
      work_unit: workUnit.value,
      isActivated: isActivated.value
    };
    
    // 保存到localStorage
    localStorage.setItem('userInfo', JSON.stringify(data));
    console.log('用户信息已保存到localStorage:', data);
  }
  
  // 获取用户信息
  async function fetchUserInfo(): Promise<UserInfo | null> {
    // 防止重复加载
    const isLoading = sessionStorage.getItem('isLoadingUserInfo');
    if (isLoading === 'true') {
      console.log('已有用户信息加载请求正在进行中，跳过');
      return null;
    }
    
    try {
      sessionStorage.setItem('isLoadingUserInfo', 'true');
      
      // 检查是否已有token
      const storedToken = localStorage.getItem('token');
      if (!token.value && !storedToken) {
        console.warn('尝试获取用户信息但没有令牌');
        sessionStorage.removeItem('isLoadingUserInfo');
        return null;
      }
      
      // 如果store中的token与localStorage不一致，更新store
      if (storedToken && token.value !== storedToken) {
        console.log('发现localStorage的token与store不一致，正在更新...');
        token.value = storedToken;
        isLoggedIn.value = true;
      }
      
      console.log('开始获取用户信息');
      
      // 从API获取最新用户信息
      const response = await getUserInfo();
      
      if (response && ((response as any).code === 200 || response.success) && response.data) {
        let dataToProcess = response.data;
        
        // 如果存在嵌套的data对象，使用它
        if (dataToProcess.data) {
          dataToProcess = dataToProcess.data;
        }
        
        console.log('处理用户数据:', dataToProcess);
        
        // 设置组织信息 - 支持不同命名风格
        organization.value = dataToProcess.org_name || dataToProcess.orgName || '';
        organizationFullName.value = dataToProcess.full_name || dataToProcess.fullName || '';
        organizationType.value = dataToProcess.org_type || dataToProcess.orgType || '';
        
        // 如果顶层字段为空，但有organizationInfo对象，则从其中提取
        if (dataToProcess.organizationInfo) {
          if (!organization.value && dataToProcess.organizationInfo.name) {
            organization.value = dataToProcess.organizationInfo.name;
          }
          if (!organizationFullName.value && dataToProcess.organizationInfo.fullName) {
            organizationFullName.value = dataToProcess.organizationInfo.fullName;
          }
          if (!organizationType.value && dataToProcess.organizationInfo.type) {
            organizationType.value = dataToProcess.organizationInfo.type;
          }
        }
        
        // 提取用户数据
        let userData: any = null;
        
        if (dataToProcess.user) {
          userData = dataToProcess.user;
          console.log('从user字段提取用户数据:', userData);
        } else if (dataToProcess.id && dataToProcess.name) {
          userData = dataToProcess;
          console.log('从顶层数据提取用户数据:', userData);
        } else {
          userData = {
            id: 0,
            name: '未知用户',
            card: ''
          };
          console.log('未找到有效用户数据，使用默认值');
        }
        
        // 设置用户基本信息 - 支持不同命名风格
        id.value = userData.id?.toString() || '';
        name.value = userData.name || '';
        card.value = userData.card || '';
        
        // 设置头像
        if (userData.avatar) {
          avatar.value = userData.avatar;
        } else {
          avatar.value = userData.gender === '女' ? 
            new URL('../assets/img/girl.png', import.meta.url).href : 
            new URL('../assets/img/boy.png', import.meta.url).href;
        }
        
        // 更新各字段映射，确保正确处理不同的命名风格
        gender.value = userData.gender || userData.sex || '';
        ethnic.value = userData.ethnic || userData.ethnicity || '';
        organizationId.value = userData.organization_id || userData.organizationId || userData.organization || null;
        organizationMemberCount.value = userData.organization_member_count || userData.organizationMemberCount || 0;
        birthday.value = userData.birthday || userData.birth_date || '';
        age.value = userData.age || '';
        
        // 联系信息 - 扩展字段匹配
        phone.value = userData.phone || userData.tel || '';
        email.value = userData.email || userData.mail || '';
        
        // 其他个人信息 - 扩展字段匹配
        politicalStatus.value = userData.politicalStatus || userData.political_status || '';
        occupation.value = userData.occupation || userData.job || '';
        educationLevel.value = userData.educationLevel || userData.education_level || '';
        educationStatus.value = userData.educationStatus || userData.education_status || '';
        leaguePosition.value = userData.leaguePosition || userData.league_position || '';
        joinLeagueDate.value = userData.joinLeagueDate || userData.join_league_date || '';
        joinPartyDate.value = userData.joinPartyDate || userData.join_party_date || '';
        
        // 可选的组织结构信息
        grade.value = userData.grade || userData.class_grade || '';
        department.value = userData.department || userData.dept || '';
        
        // 社交和联系地址信息
        address.value = userData.address || '';
        qq.value = userData.qq || '';
        wechat.value = userData.wechat || '';
        weibo.value = userData.weibo || '';
        workUnit.value = userData.workUnit || userData.work_unit || '';
        
        // 输出调试信息，检查数据映射
        console.log('用户数据映射后的状态:', {
          id: id.value,
          name: name.value,
          phone: phone.value,
          email: email.value,
          politicalStatus: politicalStatus.value,
          occupation: occupation.value,
          educationLevel: educationLevel.value,
          educationStatus: educationStatus.value,
          joinLeagueDate: joinLeagueDate.value,
          joinPartyDate: joinPartyDate.value,
          address: address.value,
          qq: qq.value,
          wechat: wechat.value,
          weibo: weibo.value,
          workUnit: workUnit.value
        });
        
        console.log('用户信息处理完成，保存到本地存储');
        
        // 保存到localStorage
        saveUserInfoToStorage();
        
        // 返回用户信息
        return userInfo.value;
      } else {
        console.error('获取用户信息失败:', response);
        return null;
      }
    } catch (error) {
      console.error('获取用户信息异常:', error);
      return null;
    } finally {
      sessionStorage.removeItem('isLoadingUserInfo');
    }
  }

  // 检查是否有指定角色
  function hasRole(roleName: string): boolean {
    return role.value.includes(roleName);
  }
  
  // 用户登出
  async function doLogout(): Promise<boolean> {
    try {
      console.log('开始用户登出流程...');
      
      // 保存当前token用于API调用
      const currentToken = token.value;
      
      // 先调用API登出，确保服务端会话清除
      try {
        // 调用API登出
        const success = await apiLogout();
        console.log('API登出结果:', success ? '成功' : '失败');
      } catch (apiError) {
        // 即使API请求失败，也继续清除本地状态
        console.error('API登出请求失败:', apiError);
        ElMessage.warning('服务器通信失败，但仍将清除本地登录状态');
      }
      
      // 然后清除本地状态
      clearUserInfo();
      console.log('本地用户状态已清除');
      
        return true;
    } catch (error) {
      console.error('登出过程中发生错误', error);
      
      // 确保无论如何都清除本地状态
      try {
        clearUserInfo();
        console.log('错误处理后强制清除本地状态');
      } catch (e) {
        console.error('清除本地状态出错', e);
      }
      
      return true; // 即使有错误，也视为登出成功
    }
  }
  
  // 重置用户信息，但保留登录状态
  function resetUserInfo(): void {
    console.log('清除当前用户数据，准备重新加载');
    // 保留当前token和登录状态
    const currentToken = token.value;
    const loginStatus = isLoggedIn.value;
    
    // 清除所有用户数据
    id.value = '';
    name.value = '';
    card.value = '';
    avatar.value = '';
    role.value = [];
    organization.value = '';
    organizationId.value = null;
    leaguePosition.value = '';
    gender.value = '';
    ethnic.value = '';
    birthday.value = '';
    age.value = '';
    grade.value = '';
    department.value = '';
    organizationFullName.value = '';
    organizationType.value = '';
    organizationMemberCount.value = 0;
    pendingTasksCount.value = 0;
    uncompletedTasksCount.value = 0;
    joinLeagueDate.value = '';
    phone.value = '';
    email.value = '';
    politicalStatus.value = '';
    occupation.value = '';
    educationLevel.value = '';
    educationStatus.value = '';
    
    // 恢复token和登录状态
    token.value = currentToken;
    isLoggedIn.value = loginStatus;
  }

  // 添加updateLocalUserInfo方法
  function updateLocalUserInfo(userData: any) {
    console.log('开始更新本地用户信息:', userData);
    
    // 从localStorage获取当前完整的用户信息
    const storedUserInfo = localStorage.getItem('userInfo');
    let currentUserData = {};
    
    if (storedUserInfo) {
      try {
        currentUserData = JSON.parse(storedUserInfo);
        console.log('从localStorage获取现有用户数据:', currentUserData);
      } catch (error) {
        console.error('解析localStorage中的用户数据失败:', error);
      }
    }
    
    // 更新状态 - 保留现有值作为默认值
    // 关键字段如姓名和性别不从表单更新，保持原值
    // 而是表单中提交的其他字段
    phone.value = userData.phone ?? phone.value;
    email.value = userData.email ?? email.value;
    
    // 确保各种可能的字段名都能被正确处理
    educationLevel.value = userData.education_level ?? userData.educationLevel ?? educationLevel.value;
    educationStatus.value = userData.education_status ?? userData.educationStatus ?? educationStatus.value;
    politicalStatus.value = userData.political_status ?? userData.politicalStatus ?? politicalStatus.value;
    occupation.value = userData.occupation ?? userData.job ?? occupation.value;
    joinLeagueDate.value = userData.join_league_date ?? userData.joinLeagueDate ?? joinLeagueDate.value;
    joinPartyDate.value = userData.join_party_date ?? userData.joinPartyDate ?? joinPartyDate.value;
    workUnit.value = userData.work_unit ?? userData.workUnit ?? workUnit.value;
    address.value = userData.address ?? address.value;
    qq.value = userData.qq ?? qq.value;
    wechat.value = userData.wechat ?? wechat.value;
    weibo.value = userData.weibo ?? weibo.value;
    
    console.log('用户信息更新后的状态:', {
      name: name.value,
      gender: gender.value,
      phone: phone.value,
      email: email.value,
      educationLevel: educationLevel.value,
      educationStatus: educationStatus.value,
      politicalStatus: politicalStatus.value,
      occupation: occupation.value,
      joinLeagueDate: joinLeagueDate.value,
      joinPartyDate: joinPartyDate.value,
      workUnit: workUnit.value,
      address: address.value,
      qq: qq.value,
      wechat: wechat.value,
      weibo: weibo.value
    });
    
    // 更新本地存储
    saveUserInfoToStorage();
  }

  // 添加syncToLocalStorage方法，如果不存在的话
  function syncToLocalStorage() {
    // 将当前状态同步到localStorage
    const userData = {
      id: id.value,
      name: name.value,
      avatar: avatar.value,
      email: email.value,
      phone: phone.value,
      token: token.value,
      roles: role.value,
      organization: organization.value,
      organizationFullName: organizationFullName.value,
      educationLevel: educationLevel.value,
      politicalStatus: politicalStatus.value,
      occupation: occupation.value,
      gender: gender.value
    };
    
    localStorage.setItem('userInfo', JSON.stringify(userData));
  }

  return {
    id,
    name,
    card,
    avatar,
    role,
    token,
    isLoggedIn,
    organization,
    organizationId,
    leaguePosition,
    gender,
    ethnic,
    birthday,
    age,
    grade,
    department,
    organizationFullName,
    organizationType,
    organizationMemberCount,
    joinLeagueDate,
    pendingTasksCount,
    uncompletedTasksCount,
    // 用户信息字段
    phone,
    email,
    politicalStatus,
    occupation,
    educationLevel,
    educationStatus,
    // 新增额外字段
    address,
    qq,
    wechat,
    weibo,
    workUnit,
    joinPartyDate,
    // 计算属性和方法
    userInfo,
    isCommitteeSecretary,
    isBranchSecretary,
    isMember,
    setToken,
    setTokenExpiry,
    clearUserInfo,
    fetchUserInfo,
    hasRole,
    doLogout,
    resetUserInfo,
    initFromStorage,
    saveUserInfoToStorage,
    // 新增：用户头像计算属性，确保始终有值
    userAvatar,
    // 新增：updateLocalUserInfo方法
    updateLocalUserInfo,
    // 新增：syncToLocalStorage方法
    syncToLocalStorage,
    isActivated
  };
}); 