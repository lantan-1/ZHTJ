import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { useUserStore } from './user';
import type { Activity } from '../api/activity';
import { 
  getActivities, 
  getActivitiesByDate, 
  getActivityDetail,
  addActivity, 
  updateActivity, 
  deleteActivity,
  getActivityStatistics
} from '../api/activity';

export const useActivityStore = defineStore('activity', () => {
  const activities = ref<Activity[]>([]);
  const loading = ref<boolean>(false);
  const totalCount = ref<number>(0);
  const currentActivity = ref<Activity | null>(null);
  const categoryStatistics = ref<Record<string, number>>({});
  
  const userStore = useUserStore();
  
  // 获取当前用户的组织ID
  const currentOrganizationId = computed(() => userStore.organizationId);

  // 获取所有活动
  const fetchActivities = async (params?: {
    page?: number;
    size?: number;
    category?: string;
    startDate?: string;
    endDate?: string;
  }) => {
    loading.value = true;
    try {
      const requestParams = {
        ...params,
        organization: currentOrganizationId.value
      };
      
      const res = await getActivities(requestParams);
      if (res.data) {
        activities.value = res.data.list || [];
        totalCount.value = res.data.total || 0;
      }
      return activities.value;
    } catch (error) {
      console.error('获取活动列表失败', error);
      return [];
    } finally {
      loading.value = false;
    }
  };

  // 根据日期获取活动
  const fetchActivitiesByDate = async (date: string) => {
    loading.value = true;
    try {
      const res = await getActivitiesByDate({
        organization: currentOrganizationId.value || 0,
        date
      });
      if (res.data) {
        activities.value = res.data || [];
        totalCount.value = res.data.length || 0;
      }
      return activities.value;
    } catch (error) {
      console.error('根据日期获取活动失败', error);
      return [];
    } finally {
      loading.value = false;
    }
  };
  
  // 获取活动详情
  const fetchActivityDetail = async (id: number) => {
    loading.value = true;
    try {
      const res = await getActivityDetail(id);
      if (res.data) {
        currentActivity.value = res.data;
        return res.data;
      }
      return null;
    } catch (error) {
      console.error('获取活动详情失败', error);
      return null;
    } finally {
      loading.value = false;
    }
  };

  // 添加活动
  const createActivity = async (activity: Activity) => {
    try {
      // 确保设置了组织ID
      if (!activity.organization) {
        activity.organization = currentOrganizationId.value;
      }
      
      const res = await addActivity(activity);
      if (res.success) {
        await fetchActivities();
        return true;
      }
      return false;
    } catch (error) {
      console.error('添加活动失败', error);
      return false;
    }
  };

  // 更新活动
  const modifyActivity = async (activity: Activity) => {
    try {
      // 确保设置了组织ID
      if (!activity.organization) {
        activity.organization = currentOrganizationId.value;
      }
      
      const res = await updateActivity(activity);
      if (res.success) {
        await fetchActivities();
        return true;
      }
      return false;
    } catch (error) {
      console.error('更新活动失败', error);
      return false;
    }
  };

  // 删除活动
  const removeActivity = async (id: number) => {
    try {
      const res = await deleteActivity(id);
      if (res.success) {
        await fetchActivities();
        return true;
      }
      return false;
    } catch (error) {
      console.error('删除活动失败', error);
      return false;
    }
  };
  
  // 获取活动类别统计
  const fetchCategoryStatistics = async () => {
    try {
      if (!currentOrganizationId.value) return {};
      
      const res = await getActivityStatistics(currentOrganizationId.value);
      if (res.success && res.data) {
        categoryStatistics.value = res.data;
        return res.data;
      }
      return {};
    } catch (error) {
      console.error('获取活动类别统计失败', error);
      return {};
    }
  };

  return {
    activities,
    loading,
    totalCount,
    currentActivity,
    categoryStatistics,
    fetchActivities,
    fetchActivitiesByDate,
    fetchActivityDetail,
    createActivity,
    modifyActivity,
    removeActivity,
    fetchCategoryStatistics
  };
}); 