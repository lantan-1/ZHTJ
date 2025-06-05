package com.zhtj.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 人脸识别服务接口
 */
public interface FaceService {
    
    /**
     * 检查用户是否有人脸信息（通过用户ID）
     * @param userId 用户ID
     * @return 是否有人脸信息
     */
    boolean hasFaceImage(Integer userId);
    
    /**
     * 检查用户是否有人脸信息（通过身份证号）
     * @param card 身份证号
     * @return 是否有人脸信息
     */
    boolean hasFaceImageByCard(String card);
    
    /**
     * 人脸活体检测
     * @param imageFile 图片文件
     * @return 检测结果
     */
    boolean detectLiveness(MultipartFile imageFile);
    
    /**
     * 人脸比对
     * @param imageFile1 第一张图片文件
     * @param imageFile2 第二张图片文件
     * @return 比对结果
     */
    boolean compareFace(MultipartFile imageFile1, MultipartFile imageFile2);
    
    /**
     * 保存用户人脸图片
     * @param userId 用户ID
     * @param imageFile 图片文件
     * @return 保存结果
     */
    boolean saveUserFaceImage(Integer userId, MultipartFile imageFile);
    
    /**
     * 验证用户人脸
     * @param userId 用户ID
     * @param imageFile 待验证的图片文件
     * @return 验证结果
     */
    boolean verifyUserFace(Integer userId, MultipartFile imageFile);
    
    /**
     * 通过身份证号验证用户人脸
     * @param card 身份证号
     * @param imageFile 待验证的图片文件
     * @return 验证结果
     */
    boolean verifyUserFaceByCard(String card, MultipartFile imageFile);
} 