package com.zhtj.task;

import com.zhtj.domain.Transfer;
import com.zhtj.service.TransferService;
import com.zhtj.utils.TransferNotificationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 团员关系转接定时任务
 * 用于处理过期的团员关系转接申请
 */
@Component
public class TransferTask {
    
    private static final Logger logger = LoggerFactory.getLogger(TransferTask.class);
    
    @Autowired
    private TransferService transferService;
    
    @Autowired
    private TransferNotificationHelper notificationHelper;
    
    /**
     * 每天凌晨1点执行一次，检查过期的转接申请
     * 过期申请将自动标记为拒绝，并发送通知
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void handleExpiredTransfers() {
        logger.info("开始执行过期转接申请处理任务");
        
        try {
            LocalDateTime now = LocalDateTime.now();
            List<Transfer> expiredTransfers = transferService.getExpiredTransfers(now);
            
            logger.info("找到 {} 条过期的转接申请", expiredTransfers.size());
            
            for (Transfer transfer : expiredTransfers) {
                // 获取当前状态，设置适当的拒绝消息
                String rejectMessage = "申请已过期自动拒绝";
                Integer transferId = transfer.getId();
                Integer transferUserId = transfer.getTransferUserId();
                
                // 根据当前状态码判断应该执行哪种拒绝操作
                if (transfer.getStatusCode() == 0) { // 申请中
                    transferService.outApprove(transferId, false, 0, rejectMessage); // 0表示系统操作
                    notificationHelper.sendExpiredNotification(transfer, "转出组织");
                } else if (transfer.getStatusCode() == 1) { // 转出审批中
                    transferService.inApprove(transferId, false, 0, rejectMessage);
                    notificationHelper.sendExpiredNotification(transfer, "转入组织");
                }
                
                logger.info("已处理过期转接申请 ID: {}, 申请人ID: {}", transferId, transferUserId);
            }
            
            logger.info("过期转接申请处理任务执行完成");
        } catch (Exception e) {
            logger.error("处理过期转接申请时发生错误: {}", e.getMessage(), e);
        }
    }
} 