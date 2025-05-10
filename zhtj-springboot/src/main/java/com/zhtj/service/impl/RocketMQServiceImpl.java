package com.zhtj.service.impl;

import com.zhtj.common.exception.BusinessException;
import com.zhtj.service.MessageQueueService;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * RocketMQ服务实现类
 */
@Service
public class RocketMQServiceImpl implements MessageQueueService {

    private final Logger logger = LoggerFactory.getLogger(RocketMQServiceImpl.class);

    // 改为非必需的依赖注入
    @Autowired(required = false)
    private RocketMQTemplate rocketMQTemplate;

    @Value("${rocketmq.producer.group:zhtj-producer-group}")
    private String producerGroup;

    @Value("${rocketmq.name-server:127.0.0.1:9876}")
    private String nameServer;
    
    // 检查RocketMQ是否可用
    private boolean isRocketMQAvailable() {
        if (rocketMQTemplate == null) {
            logger.warn("RocketMQ服务不可用，请检查RocketMQ相关配置");
            return false;
        }
        return true;
    }

    @Override
    public boolean sendMessage(String topic, String message) {
        return sendMessage(topic, null, null, message, null);
    }

    @Override
    public boolean sendMessage(String topic, String tag, String message) {
        return sendMessage(topic, tag, null, message, null);
    }

    @Override
    public boolean sendMessage(String topic, String tag, String key, String message) {
        return sendMessage(topic, tag, key, message, null);
    }

    @Override
    public boolean sendMessage(String topic, String tag, String key, String message, Map<String, String> properties) {
        // 检查RocketMQ是否可用
        if (!isRocketMQAvailable()) {
            logger.error("消息发送失败，RocketMQ服务不可用");
            return false;
        }
        
        try {
            String destination = topic;
            if (tag != null && !tag.isEmpty()) {
                destination = topic + ":" + tag;
            }

            org.springframework.messaging.Message<String> springMessage;
            if (key != null && !key.isEmpty()) {
                springMessage = MessageBuilder.withPayload(message)
                        .setHeader("KEYS", key)
                        .build();
            } else {
                springMessage = MessageBuilder.withPayload(message).build();
            }

            SendResult sendResult = rocketMQTemplate.syncSend(destination, springMessage);
            if (SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                logger.info("消息发送成功，主题: {}, 标签: {}, 消息ID: {}", topic, tag, sendResult.getMsgId());
                return true;
            } else {
                logger.error("消息发送失败，主题: {}, 标签: {}, 状态: {}", topic, tag, sendResult.getSendStatus());
                return false;
            }
        } catch (Exception e) {
            logger.error("消息发送异常，主题: {}, 标签: {}, 错误信息: {}", topic, tag, e.getMessage());
            throw new BusinessException("消息发送失败: " + e.getMessage());
        }
    }

    @Override
    public CompletableFuture<Boolean> sendMessageAsync(String topic, String message) {
        return sendMessageAsync(topic, null, message);
    }

    @Override
    public CompletableFuture<Boolean> sendMessageAsync(String topic, String tag, String message) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        
        // 检查RocketMQ是否可用
        if (!isRocketMQAvailable()) {
            logger.error("异步消息发送失败，RocketMQ服务不可用");
            future.complete(false);
            return future;
        }
        
        try {
            String destination = topic;
            if (tag != null && !tag.isEmpty()) {
                destination = topic + ":" + tag;
            }

            rocketMQTemplate.asyncSend(destination, message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    if (SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                        logger.info("异步消息发送成功，主题: {}, 标签: {}, 消息ID: {}", topic, tag, sendResult.getMsgId());
                        future.complete(true);
                    } else {
                        logger.error("异步消息发送失败，主题: {}, 标签: {}, 状态: {}", topic, tag, sendResult.getSendStatus());
                        future.complete(false);
                    }
                }

                @Override
                public void onException(Throwable throwable) {
                    logger.error("异步消息发送异常，主题: {}, 标签: {}, 错误信息: {}", topic, tag, throwable.getMessage());
                    future.completeExceptionally(new BusinessException("异步消息发送失败: " + throwable.getMessage()));
                }
            });
        } catch (Exception e) {
            logger.error("异步消息发送异常，主题: {}, 标签: {}, 错误信息: {}", topic, tag, e.getMessage());
            future.completeExceptionally(new BusinessException("异步消息发送失败: " + e.getMessage()));
        }
        
        return future;
    }

    @Override
    public boolean sendDelayMessage(String topic, String tag, String message, int delayLevel) {
        // 检查RocketMQ是否可用
        if (!isRocketMQAvailable()) {
            logger.error("延迟消息发送失败，RocketMQ服务不可用");
            return false;
        }
        
        try {
            String destination = topic;
            if (tag != null && !tag.isEmpty()) {
                destination = topic + ":" + tag;
            }

            SendResult sendResult = rocketMQTemplate.syncSend(destination, 
                    MessageBuilder.withPayload(message).build(), 
                    rocketMQTemplate.getProducer().getSendMsgTimeout(), 
                    delayLevel);
            
            if (SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                logger.info("延迟消息发送成功，主题: {}, 标签: {}, 延迟级别: {}, 消息ID: {}", 
                        topic, tag, delayLevel, sendResult.getMsgId());
                return true;
            } else {
                logger.error("延迟消息发送失败，主题: {}, 标签: {}, 延迟级别: {}, 状态: {}", 
                        topic, tag, delayLevel, sendResult.getSendStatus());
                return false;
            }
        } catch (Exception e) {
            logger.error("延迟消息发送异常，主题: {}, 标签: {}, 延迟级别: {}, 错误信息: {}", 
                    topic, tag, delayLevel, e.getMessage());
            throw new BusinessException("延迟消息发送失败: " + e.getMessage());
        }
    }

    @Override
    public boolean sendOrderedMessage(String topic, String tag, String message, String orderId) {
        // 检查RocketMQ是否可用
        if (!isRocketMQAvailable()) {
            logger.error("顺序消息发送失败，RocketMQ服务不可用");
            return false;
        }
        
        try {
            String destination = topic;
            if (tag != null && !tag.isEmpty()) {
                destination = topic + ":" + tag;
            }

            // 根据orderId选择队列，确保相同orderId的消息进入同一个队列
            SendResult sendResult = rocketMQTemplate.syncSendOrderly(destination, message, orderId);
            
            if (SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                logger.info("顺序消息发送成功，主题: {}, 标签: {}, 顺序ID: {}, 消息ID: {}", 
                        topic, tag, orderId, sendResult.getMsgId());
                return true;
            } else {
                logger.error("顺序消息发送失败，主题: {}, 标签: {}, 顺序ID: {}, 状态: {}", 
                        topic, tag, orderId, sendResult.getSendStatus());
                return false;
            }
        } catch (Exception e) {
            logger.error("顺序消息发送异常，主题: {}, 标签: {}, 顺序ID: {}, 错误信息: {}", 
                    topic, tag, orderId, e.getMessage());
            throw new BusinessException("顺序消息发送失败: " + e.getMessage());
        }
    }

    @Override
    public String sendTransactionMessage(String topic, String tag, String message, Object arg) {
        // 检查RocketMQ是否可用
        if (!isRocketMQAvailable()) {
            logger.error("事务消息发送失败，RocketMQ服务不可用");
            return null;
        }
        
        try {
            String destination = topic;
            if (tag != null && !tag.isEmpty()) {
                destination = topic + ":" + tag;
            }

            String transactionId = UUID.randomUUID().toString();
            org.springframework.messaging.Message<String> springMessage = MessageBuilder.withPayload(message)
                    .setHeader("TRANSACTION_ID", transactionId)
                    .build();

            TransactionSendResult sendResult = rocketMQTemplate.sendMessageInTransaction(
                    destination, springMessage, arg);
            
            if (SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                logger.info("事务消息发送成功，主题: {}, 标签: {}, 事务ID: {}, 消息ID: {}", 
                        topic, tag, transactionId, sendResult.getMsgId());
                return transactionId;
            } else {
                logger.error("事务消息发送失败，主题: {}, 标签: {}, 事务ID: {}, 状态: {}", 
                        topic, tag, transactionId, sendResult.getSendStatus());
                throw new BusinessException("事务消息发送失败，状态: " + sendResult.getSendStatus());
            }
        } catch (Exception e) {
            logger.error("事务消息发送异常，主题: {}, 标签: {}, 错误信息: {}", topic, tag, e.getMessage());
            throw new BusinessException("事务消息发送失败: " + e.getMessage());
        }
    }

    @Override
    public boolean createTopic(String topic, int queueNum) {
        // 检查RocketMQ是否可用
        if (!isRocketMQAvailable()) {
            logger.error("创建主题失败，RocketMQ服务不可用");
            return false;
        }
        
        try {
            DefaultMQProducer producer = rocketMQTemplate.getProducer();
            producer.createTopic(producer.getCreateTopicKey(), topic, queueNum, new HashMap<>());
            logger.info("创建主题成功，主题: {}, 队列数: {}", topic, queueNum);
            return true;
        } catch (Exception e) {
            logger.error("创建主题异常，主题: {}, 队列数: {}, 错误信息: {}", 
                    topic, queueNum, e.getMessage());
            throw new BusinessException("创建主题失败: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteTopic(String topic) {
        // 检查RocketMQ是否可用
        if (!isRocketMQAvailable()) {
            logger.error("删除主题失败，RocketMQ服务不可用");
            return false;
        }
        
        try {
            // 注意：RocketMQ不提供直接删除主题的API，需要通过管理工具或REST API进行
            logger.warn("RocketMQ不提供直接删除主题的Java API，请通过管理工具或REST API删除主题: {}", topic);
            // 这里只是记录日志，实际上没有执行删除操作
            return false;
        } catch (Exception e) {
            logger.error("删除主题异常，主题: {}, 错误信息: {}", topic, e.getMessage());
            throw new BusinessException("删除主题失败: " + e.getMessage());
        }
    }
} 