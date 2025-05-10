/**
 * 证书生成工具
 */
import html2canvas from 'html2canvas';
import { jsPDF } from 'jspdf';
import config from '../config';

// 证书模板样式
const CERTIFICATE_STYLES = {
  width: 760, // 证书宽度
  height: 550, // 证书高度
  borderColor: '#D4AF37', // 证书边框颜色
  backgroundColor: '#FFFBF0', // 证书背景颜色
  primaryColor: '#8B0000', // 主要文字颜色
  secondaryColor: '#333333', // 次要文字颜色
  fontPrimary: 'Arial, "Microsoft YaHei", sans-serif', // 主要字体
  fontSecondary: '"Times New Roman", serif', // 次要字体
  logoWidth: 80, // logo宽度
}; 

/**
 * 创建证书DOM
 * @param awardData 授予数据
 * @returns {HTMLElement} 证书DOM元素
 */
export function createCertificateElement(awardData: any): HTMLElement {
  // 创建证书容器
  const container = document.createElement('div');
  container.style.width = `${CERTIFICATE_STYLES.width}px`;
  container.style.height = `${CERTIFICATE_STYLES.height}px`;
  container.style.position = 'relative';
  container.style.backgroundColor = CERTIFICATE_STYLES.backgroundColor;
  container.style.border = `10px solid ${CERTIFICATE_STYLES.borderColor}`;
  container.style.boxSizing = 'border-box';
  container.style.padding = '50px';
  container.style.fontFamily = CERTIFICATE_STYLES.fontPrimary;
  container.style.color = CERTIFICATE_STYLES.secondaryColor;
  container.style.textAlign = 'center';
  container.style.overflow = 'hidden';
  
  // 添加证书标题
  const title = document.createElement('h1');
  title.textContent = '荣誉证书';
  title.style.fontSize = '42px';
  title.style.fontWeight = 'bold';
  title.style.marginTop = '20px';
  title.style.color = CERTIFICATE_STYLES.primaryColor;
  title.style.position = 'relative';
  title.style.fontFamily = CERTIFICATE_STYLES.fontSecondary;
  container.appendChild(title);
  
  // 添加副标题
  const subtitle = document.createElement('h2');
  subtitle.textContent = awardData.honorTypeName;
  subtitle.style.fontSize = '30px';
  subtitle.style.marginTop = '20px';
  subtitle.style.fontFamily = CERTIFICATE_STYLES.fontPrimary;
  container.appendChild(subtitle);
  
  // 添加分割线
  const divider = document.createElement('div');
  divider.style.width = '80%';
  divider.style.height = '2px';
  divider.style.backgroundColor = CERTIFICATE_STYLES.borderColor;
  divider.style.margin = '30px auto';
  container.appendChild(divider);
  
  // 添加证书内容
  const content = document.createElement('div');
  content.style.fontSize = '18px';
  content.style.lineHeight = '1.8';
  content.style.textAlign = 'center';
  content.style.margin = '30px 0';
  content.innerHTML = `
    <p>兹授予</p>
    <p style="font-size: 28px; font-weight: bold; margin: 15px 0;">${awardData.userName}</p>
    <p>"${awardData.honorTypeName}"荣誉称号</p>
    <p>以表彰其在学习和工作中的突出表现</p>
  `;
  container.appendChild(content);
  
  // 添加颁发信息
  const issueInfo = document.createElement('div');
  issueInfo.style.marginTop = '30px';
  issueInfo.style.display = 'flex';
  issueInfo.style.justifyContent = 'space-between';
  issueInfo.style.fontSize = '16px';
  
  // 颁发日期
  const date = document.createElement('div');
  date.style.textAlign = 'left';
  date.textContent = `颁发日期：${awardData.awardTime.split(' ')[0]}`;
  issueInfo.appendChild(date);
  
  // 颁发单位
  const issuer = document.createElement('div');
  issuer.style.textAlign = 'right';
  issuer.style.marginRight = '20px';
  issuer.textContent = `颁发单位：${awardData.awardingDepartment || '校团委'}`;
  issueInfo.appendChild(issuer);
  
  container.appendChild(issueInfo);
  
  // 添加印章
  const stamp = document.createElement('div');
  stamp.style.position = 'absolute';
  stamp.style.bottom = '100px';
  stamp.style.right = '80px';
  stamp.style.width = '100px';
  stamp.style.height = '100px';
  stamp.style.border = `2px solid ${CERTIFICATE_STYLES.primaryColor}`;
  stamp.style.borderRadius = '50%';
  stamp.style.display = 'flex';
  stamp.style.alignItems = 'center';
  stamp.style.justifyContent = 'center';
  stamp.style.color = CERTIFICATE_STYLES.primaryColor;
  stamp.style.fontSize = '16px';
  stamp.style.transform = 'rotate(-15deg)';
  stamp.style.opacity = '0.8';
  stamp.textContent = awardData.awardingDepartment || '校团委';
  container.appendChild(stamp);
  
  // 添加水印
  const watermark = document.createElement('div');
  watermark.style.position = 'absolute';
  watermark.style.top = '0';
  watermark.style.left = '0';
  watermark.style.width = '100%';
  watermark.style.height = '100%';
  watermark.style.backgroundImage = 'linear-gradient(45deg, rgba(212, 175, 55, 0.1) 25%, transparent 25%, transparent 50%, rgba(212, 175, 55, 0.1) 50%, rgba(212, 175, 55, 0.1) 75%, transparent 75%, transparent)';
  watermark.style.backgroundSize = '40px 40px';
  watermark.style.opacity = '0.3';
  watermark.style.pointerEvents = 'none';
  watermark.style.zIndex = '1';
  container.insertBefore(watermark, container.firstChild);
  
  // 添加证书编号
  const serialNumber = document.createElement('div');
  serialNumber.style.position = 'absolute';
  serialNumber.style.bottom = '20px';
  serialNumber.style.left = '50%';
  serialNumber.style.transform = 'translateX(-50%)';
  serialNumber.style.fontSize = '12px';
  serialNumber.style.color = CERTIFICATE_STYLES.secondaryColor;
  serialNumber.textContent = `证书编号：${awardData.id.toString().padStart(6, '0')}`;
  container.appendChild(serialNumber);
  
  return container;
}

/**
 * 渲染证书到目标元素
 * @param targetElement 目标元素
 * @param awardData 授予数据
 */
export function renderCertificateToElement(targetElement: HTMLElement, awardData: any): void {
  // 清空目标元素
  targetElement.innerHTML = '';
  
  // 创建并添加证书
  const certificateElement = createCertificateElement(awardData);
  targetElement.appendChild(certificateElement);
}

/**
 * 生成证书PDF
 * @param awardData 授予数据
 * @returns {Promise<Blob>} PDF Blob对象
 */
export async function generateCertificatePDF(awardData: any): Promise<Blob> {
  // 创建一个临时容器用于生成PDF
  const tempContainer = document.createElement('div');
  tempContainer.style.position = 'absolute';
  tempContainer.style.left = '-9999px';
  tempContainer.style.top = '-9999px';
  document.body.appendChild(tempContainer);
  
  try {
    // 渲染证书
    renderCertificateToElement(tempContainer, awardData);
    
    // 使用html2canvas捕获元素
    const canvas = await html2canvas(tempContainer.firstChild as HTMLElement, {
      scale: 2, // 提高清晰度
      logging: false,
      useCORS: true
    });
    
    // 创建PDF
    const imgData = canvas.toDataURL('image/jpeg', 0.95);
    const pdf = new jsPDF({
      orientation: 'landscape',
      unit: 'mm',
      format: 'a4'
    });
    
    const pdfWidth = pdf.internal.pageSize.getWidth();
    const pdfHeight = pdf.internal.pageSize.getHeight();
    
    pdf.addImage(imgData, 'JPEG', 0, 0, pdfWidth, pdfHeight);
    
    // 返回PDF Blob
    return pdf.output('blob');
  } finally {
    // 清理临时容器
    document.body.removeChild(tempContainer);
  }
}

/**
 * 下载证书为PDF
 * @param awardData 授予数据
 */
export async function downloadCertificate(awardData: any): Promise<void> {
  try {
    // 检查是否启用证书生成功能
    if (!config.features.certificateGeneration) {
      throw new Error('证书生成功能未启用');
    }
    
    // 生成PDF blob
    const pdfBlob = await generateCertificatePDF(awardData);
    
    // 创建下载链接
    const url = URL.createObjectURL(pdfBlob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `荣誉证书_${awardData.userName}_${awardData.honorTypeName}.pdf`;
    document.body.appendChild(link);
    
    // 触发下载
    link.click();
    
    // 清理资源
    setTimeout(() => {
      document.body.removeChild(link);
      URL.revokeObjectURL(url);
    }, 100);
  } catch (error) {
    console.error('下载证书失败', error);
    throw error;
  }
}

/**
 * 打印证书
 * @param awardData 授予数据
 */
export async function printCertificate(awardData: any): Promise<void> {
  try {
    // 检查是否启用证书生成功能
    if (!config.features.certificateGeneration) {
      throw new Error('证书生成功能未启用');
    }
    
    // 创建一个临时iframe用于打印
    const iframe = document.createElement('iframe');
    iframe.style.position = 'absolute';
    iframe.style.left = '-9999px';
    iframe.style.top = '-9999px';
    document.body.appendChild(iframe);
    
    // 等待iframe加载完成
    await new Promise<void>(resolve => {
      iframe.onload = () => resolve();
      
      // 设置iframe内容
      const doc = iframe.contentDocument || iframe.contentWindow?.document;
      if (doc) {
        doc.open();
        doc.write('<html><head><title>荣誉证书打印</title>');
        doc.write('<style>body { margin: 0; padding: 0; }</style>');
        doc.write('</head><body>');
        doc.write('</body></html>');
        doc.close();
        
        // 渲染证书
        const certificateElement = createCertificateElement(awardData);
        doc.body.appendChild(certificateElement);
      }
    });
    
    // 打印
    iframe.contentWindow?.print();
    
    // 打印后移除iframe
    setTimeout(() => {
      document.body.removeChild(iframe);
    }, 1000);
  } catch (error) {
    console.error('打印证书失败', error);
    throw error;
  }
} 