package com.zhtj.service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 消息队列服务接口
 */
public interface MessageQueueService {
    
    /**
     * 同步发送消息
     *
     * @param topic 主题
     * @param message 消息内容
     * @return 是否发送成功
     */
    boolean sendMessage(String topic, String message);
    
    /**
     * 同步发送消息，带标签
     *
     * @param topic 主题
     * @param tag 标签
     * @param message 消息内容
     * @return 是否发送成功
     */
    boolean sendMessage(String topic, String tag, String message);
    
    /**
     * 同步发送消息，带标签和键
     *
     * @param topic 主题
     * @param tag 标签
     * @param key 消息键
     * @param message 消息内容
     * @return 是否发送成功
     */
    boolean sendMessage(String topic, String tag, String key, String message);
    
    /**
     * 同步发送消息，带标签、键和属性
     *
     * @param topic 主题
     * @param tag 标签
     * @param key 消息键
     * @param message 消息内容
     * @param properties 消息属性
     * @return 是否发送成功
     */
    boolean sendMessage(String topic, String tag, String key, String message, Map<String, String> properties);
    
    /**
     * 异步发送消息
     *
     * @param topic 主题
     * @param message 消息内容
     * @return 异步结果
     */
    CompletableFuture<Boolean> sendMessageAsync(String topic, String message);
    
    /**
     * 异步发送消息，带标签
     *
     * @param topic 主题
     * @param tag 标签
     * @param message 消息内容
     * @return 异步结果
     */
    CompletableFuture<Boolean> sendMessageAsync(String topic, String tag, String message);
    
    /**
     * 延迟发送消息
     *
     * @param topic 主题
     * @param tag 标签
     * @param message 消息内容
     * @param delayLevel 延迟级别 (1-18, 分别对应 1s/5s/10s/30s/1m/2m/3m/4m/5m/6m/7m/8m/9m/10m/20m/30m/1h/2h)
     * @return 是否发送成功
     */
    boolean sendDelayMessage(String topic, String tag, String message, int delayLevel);
    
    /**
     * 发送顺序消息
     *
     * @param topic 主题
     * @param tag 标签
     * @param message 消息内容
     * @param orderId 顺序ID
     * @return 是否发送成功
     */
    boolean sendOrderedMessage(String topic, String tag, String message, String orderId);
    
    /**
     * 发送事务消息
     * 
     * @param topic 主题
     * @param tag 标签
     * @param message 消息内容
     * @param arg 事务参数
     * @return 事务ID
     */
    String sendTransactionMessage(String topic, String tag, String message, Object arg);
    
    /**
     * 创建主题
     *
     * @param topic 主题名称
     * @param queueNum 队列数量
     * @return 是否创建成功
     */
    boolean createTopic(String topic, int queueNum);
    
    /**
     * 删除主题
     *
     * @param topic 主题名称
     * @return 是否删除成功
     */
    boolean deleteTopic(String topic);
} 