/**
 * 数据映射工具函数
 * 用于处理API响应数据和前端展示数据之间的映射关系
 */

/**
 * 安全格式化日期
 * @param dateStr 日期字符串
 * @param format 格式化选项，默认为本地化字符串
 * @returns 格式化后的字符串，无效日期返回'--'
 */
export function formatDate(dateStr: string | undefined | null, format: 'locale' | 'yyyy-MM-dd' = 'locale'): string {
  if (!dateStr) return '--';
  
  try {
    const date = new Date(dateStr);
    if (isNaN(date.getTime())) return '--';
    
    if (format === 'locale') {
      return date.toLocaleString();
    } else {
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
    }
  } catch (error) {
    console.error('日期格式化错误:', error);
    return '--';
  }
}

/**
 * 获取状态对应的类型
 * @param status 状态字符串
 * @returns 状态类型 (success/warning/danger/info)
 */
export function getStatusType(status: string | undefined | null): string {
  const safeStatus = status || '未知';
  
  if (safeStatus.includes('通过') || safeStatus.includes('完成')) return 'success';
  if (safeStatus.includes('审核')) return 'warning';
  if (safeStatus.includes('驳回')) return 'danger';
  if (safeStatus.includes('未开始') || safeStatus.includes('未申请')) return 'info';
  
  return 'info';
}

/**
 * 获取注册进度步骤
 * @param status 注册状态
 * @returns 步骤索引 (0-3)
 */
export function getRegisterStep(status: string | undefined | null): number {
  if (!status) return 0;
  
  if (status.includes('未申请')) return 0;
  if (status.includes('待审核')) return 1;
  if (status.includes('支部') && status.includes('审核')) return 2;
  if (status.includes('通过') || status.includes('完成')) return 3;
  if (status.includes('驳回')) return 1;
  
  return 0;
}

/**
 * 安全获取对象属性值
 * @param obj 对象
 * @param primaryKey 首选键名
 * @param fallbackKeys 备选键名数组
 * @param defaultValue 默认值
 * @returns 属性值或默认值
 */
export function getFieldValue<T>(
  obj: any, 
  primaryKey: string, 
  fallbackKeys: string[] = [], 
  defaultValue: T
): T {
  if (!obj) return defaultValue;
  
  if (obj[primaryKey] !== undefined) {
    return obj[primaryKey];
  }
  
  for (const key of fallbackKeys) {
    if (obj[key] !== undefined) {
      return obj[key];
    }
  }
  
  return defaultValue;
}

/**
 * 记录API响应数据
 * @param apiName API名称
 * @param response 响应数据
 */
export function logApiResponse(apiName: string, response: any): void {
  if (import.meta.env.DEV) {
    console.log(`[API ${apiName}] 响应:`, response);
  }
} 