/**
 * 日期格式化函数
 * @param date 日期对象或日期字符串
 * @param fmt 格式化模板，默认为 'YYYY-MM-DD HH:mm:ss'
 * @returns 格式化后的日期字符串
 */
export function formatDate(date: Date | string, fmt: string = 'YYYY-MM-DD HH:mm:ss'): string {
  if (!date) return '';
  
  // 如果是字符串，尝试转换为日期对象
  const d = date instanceof Date ? date : new Date(date);
  
  // 如果日期无效，返回原始字符串
  if (isNaN(d.getTime())) {
    return typeof date === 'string' ? date : '';
  }
  
  const o: Record<string, any> = {
    'M+': d.getMonth() + 1, // 月份
    'D+': d.getDate(), // 日
    'H+': d.getHours(), // 小时
    'm+': d.getMinutes(), // 分
    's+': d.getSeconds(), // 秒
    'Q+': Math.floor((d.getMonth() + 3) / 3), // 季度
    'S': d.getMilliseconds() // 毫秒
  };
  
  // 年份单独处理
  if (/(Y+)/.test(fmt)) {
    fmt = fmt.replace(RegExp.$1, (d.getFullYear() + '').substr(4 - RegExp.$1.length));
  }
  
  for (const k in o) {
    if (new RegExp('(' + k + ')').test(fmt)) {
      fmt = fmt.replace(
        RegExp.$1,
        RegExp.$1.length === 1 ? o[k] : ('00' + o[k]).substr(('' + o[k]).length)
      );
    }
  }
  
  return fmt;
}

/**
 * 文件大小格式化函数
 * @param bytes 文件大小（字节）
 * @param decimals 小数位数，默认为2
 * @returns 格式化后的文件大小字符串，如 1.5 MB、800 KB
 */
export function formatFileSize(bytes: number, decimals: number = 2): string {
  if (bytes === 0) return '0 Bytes';
  
  const k = 1024;
  const dm = decimals < 0 ? 0 : decimals;
  const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
  
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  
  return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
}

/**
 * 金额格式化函数
 * @param amount 金额数值
 * @param currency 货币符号，默认为 ¥
 * @param decimals 小数位数，默认为2
 * @returns 格式化后的金额字符串，如 ¥1,234.56
 */
export function formatAmount(amount: number, currency: string = '¥', decimals: number = 2): string {
  if (amount === null || amount === undefined) return '';
  
  // 格式化数字部分
  const num = amount.toFixed(decimals);
  
  // 分割整数部分和小数部分
  const parts = num.split('.');
  const integerPart = parts[0];
  const decimalPart = parts.length > 1 ? '.' + parts[1] : '';
  
  // 添加千位分隔符
  const regex = /(\d+)(\d{3})/;
  let formattedInteger = integerPart;
  
  while (regex.test(formattedInteger)) {
    formattedInteger = formattedInteger.replace(regex, '$1,$2');
  }
  
  return currency + formattedInteger + decimalPart;
} 