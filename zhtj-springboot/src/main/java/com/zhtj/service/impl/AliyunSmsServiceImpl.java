package com.zhtj.service.impl;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.*;
import com.aliyun.tea.TeaException;
import com.aliyun.teautil.Common;
import com.zhtj.common.exception.BusinessException;
import com.zhtj.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Primary;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云短信服务实现类
 */
@Service
@Primary
@ConditionalOnProperty(name = "aliyun.sms.enabled", havingValue = "true", matchIfMissing = true)
public class AliyunSmsServiceImpl implements SmsService {

    private final Logger logger = LoggerFactory.getLogger(AliyunSmsServiceImpl.class);

    @Value("${aliyun.sms.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.sms.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.sms.sign-name}")
    private String signName;

    @Value("${aliyun.sms.template-code.verification}")
    private String verificationCodeTemplate;

    /**
     * 创建阿里云短信客户端
     */
    private Client createClient() {
        try {
            com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                    .setAccessKeyId(accessKeyId)
                    .setAccessKeySecret(accessKeySecret)
                    .setEndpoint("dysmsapi.aliyuncs.com");
            return new Client(config);
        } catch (Exception e) {
            logger.error("创建阿里云短信客户端失败: {}", e.getMessage());
            throw new BusinessException("初始化短信服务失败: " + e.getMessage());
        }
    }

    @Override
    public boolean sendVerificationCode(String phoneNumber, String code) {
        Map<String, String> templateParams = new HashMap<>();
        templateParams.put("code", code);
        return sendNotification(phoneNumber, verificationCodeTemplate, templateParams);
    }

    @Override
    public boolean sendNotification(String phoneNumber, String templateCode, Map<String, String> templateParams) {
        try {
            Client client = createClient();
            
            // 构建短信参数JSON
            String paramString = Common.toJSONString(templateParams);
            
            // 构建发送短信请求
            SendSmsRequest sendSmsRequest = new SendSmsRequest()
                    .setPhoneNumbers(phoneNumber)
                    .setSignName(signName)
                    .setTemplateCode(templateCode)
                    .setTemplateParam(paramString);
            
            // 发送短信
            SendSmsResponse response = client.sendSms(sendSmsRequest);
            
            // 检查发送结果
            if (response != null && "OK".equals(response.getBody().getCode())) {
                logger.info("短信发送成功，手机号: {}, 业务ID: {}", phoneNumber, response.getBody().getBizId());
                return true;
            } else {
                String message = response != null ? 
                        response.getBody().getMessage() : "未知错误";
                logger.error("短信发送失败，手机号: {}, 错误信息: {}", phoneNumber, message);
                return false;
            }
            
        } catch (TeaException error) {
            logger.error("短信发送异常，手机号: {}, 错误代码: {}, 错误信息: {}", 
                    phoneNumber, error.getCode(), error.getMessage());
            throw new BusinessException("短信发送失败: " + error.getMessage());
            
        } catch (Exception e) {
            logger.error("短信发送异常，手机号: {}, 错误信息: {}", phoneNumber, e.getMessage());
            throw new BusinessException("短信发送失败: " + e.getMessage());
        }
    }

    @Override
    public boolean sendBatchSms(String[] phoneNumbers, String templateCode, Map<String, String> templateParams) {
        try {
            Client client = createClient();

            // 构建短信参数JSON
            String paramString = Common.toJSONString(templateParams);
            
            // 将电话号码数组转为以逗号分隔的字符串
            String phoneNumberString = String.join(",", phoneNumbers);
            
            // 构建批量发送短信请求
            SendBatchSmsRequest sendBatchSmsRequest = new SendBatchSmsRequest()
                    .setPhoneNumberJson(Common.toJSONString(phoneNumbers))
                    .setSignNameJson(Common.toJSONString(new String[]{signName}))
                    .setTemplateCode(templateCode)
                    .setTemplateParamJson(Common.toJSONString(new String[]{paramString}));
            
            // 发送批量短信
            SendBatchSmsResponse response = client.sendBatchSms(sendBatchSmsRequest);
            
            // 检查发送结果
            if (response != null && "OK".equals(response.getBody().getCode())) {
                logger.info("批量短信发送成功，手机号: {}, 业务ID: {}", phoneNumberString, response.getBody().getBizId());
                return true;
            } else {
                String message = response != null ? 
                        response.getBody().getMessage() : "未知错误";
                logger.error("批量短信发送失败，手机号: {}, 错误信息: {}", phoneNumberString, message);
                return false;
            }
            
        } catch (TeaException error) {
            logger.error("批量短信发送异常，错误代码: {}, 错误信息: {}", 
                    error.getCode(), error.getMessage());
            throw new BusinessException("批量短信发送失败: " + error.getMessage());
            
        } catch (Exception e) {
            logger.error("批量短信发送异常，错误信息: {}", e.getMessage());
            throw new BusinessException("批量短信发送失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> querySendStatus(String bizId, String phoneNumber) {
        try {
            Client client = createClient();
            
            // 构建查询发送状态请求
            QuerySendDetailsRequest querySendDetailsRequest = new QuerySendDetailsRequest()
                    .setBizId(bizId)
                    .setPhoneNumber(phoneNumber)
                    .setPageSize(10L)
                    .setCurrentPage(1L);
            
            // 查询发送状态
            QuerySendDetailsResponse response = client.querySendDetails(querySendDetailsRequest);
            
            // 解析响应结果
            Map<String, Object> result = new HashMap<>();
            if (response != null && "OK".equals(response.getBody().getCode())) {
                result.put("success", true);
                result.put("totalCount", response.getBody().getTotalCount());
                result.put("details", response.getBody().getSmsSendDetailDTOs());
                logger.info("查询短信状态成功，业务ID: {}, 手机号: {}", bizId, phoneNumber);
            } else {
                result.put("success", false);
                result.put("message", response != null ? response.getBody().getMessage() : "未知错误");
                logger.error("查询短信状态失败，业务ID: {}, 手机号: {}, 错误信息: {}", 
                        bizId, phoneNumber, result.get("message"));
            }
            
            return result;
            
        } catch (TeaException error) {
            logger.error("查询短信状态异常，业务ID: {}, 手机号: {}, 错误代码: {}, 错误信息: {}", 
                    bizId, phoneNumber, error.getCode(), error.getMessage());
            throw new BusinessException("查询短信状态失败: " + error.getMessage());
            
        } catch (Exception e) {
            logger.error("查询短信状态异常，业务ID: {}, 手机号: {}, 错误信息: {}", 
                    bizId, phoneNumber, e.getMessage());
            throw new BusinessException("查询短信状态失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> queryBalance() {
        // 阿里云SMS API 没有直接提供查询余额的接口
        // 这里可以通过阿里云的其他API来实现，或者返回一个提示
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", "阿里云SMS API不直接提供查询余额功能，请登录阿里云控制台查看");
        return result;
    }
} 