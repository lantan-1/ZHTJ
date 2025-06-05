package com.zhtj.controller;

import com.zhtj.common.api.Result;
import com.zhtj.service.FaceService;
import com.zhtj.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 人脸识别控制器
 */
@RestController
@RequestMapping("/face")
@Tag(name = "人脸识别", description = "人脸识别相关接口")
@Slf4j
public class FaceController extends BaseController {

    @Autowired
    private FaceService faceService;
    
    @Autowired
    private PasswordResetService passwordResetService;

    @GetMapping("/check")
    @Operation(summary = "检查当前登录用户是否有人脸信息")
    public Result<Boolean> checkCurrentUserFace() {
        try {
            Integer userId = getCurrentUserIdFromRequest();
            boolean hasFace = faceService.hasFaceImage(userId);
            return Result.success(hasFace);
        } catch (Exception e) {
            log.error("检查用户人脸信息失败", e);
            return Result.failed(e.getMessage());
        }
    }

    @GetMapping("/check/{card}")
    @Operation(summary = "通过身份证号检查用户是否有人脸信息")
    public Result<Boolean> checkUserFaceByCard(@PathVariable String card) {
        try {
            boolean hasFace = faceService.hasFaceImageByCard(card);
            return Result.success(hasFace);
        } catch (Exception e) {
            log.error("检查用户人脸信息失败", e);
            return Result.failed(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    @Operation(summary = "通过人脸验证重置密码")
    public Result<Boolean> resetPasswordByFace(
            @RequestParam("card") String card,
            @RequestParam("image") MultipartFile image,
            @RequestParam("newPassword") String newPassword,
            HttpServletRequest request) {
        try {
            // 检查用户是否有人脸信息
            if (!faceService.hasFaceImageByCard(card)) {
                return Result.failed("用户未录入人脸信息，请使用其他方式找回密码");
            }
            // 先进行活体检测
            if (!faceService.detectLiveness(image)) {
                return Result.failed("活体检测未通过，请正对摄像头并确保光线充足");
            }
            // 验证人脸
            boolean verified = faceService.verifyUserFaceByCard(card, image);
            if (!verified) {
                return Result.failed("人脸验证失败");
            }
            // 重置密码
            String clientIp = request.getRemoteAddr();
            passwordResetService.verifyCodeAndResetPassword(card, "face", null, newPassword);
            return Result.success(true);
        } catch (com.zhtj.common.exception.BusinessException be) {
            log.warn("[人脸重置密码-业务异常] card={}, msg={}", card, be.getMessage());
            return Result.failed(be.getMessage());
        } catch (Exception e) {
            log.error("[人脸重置密码-系统异常] card={}, error=", card, e);
            return Result.failed("系统异常，请稍后重试或联系管理员");
        }
    }

    @PostMapping("/detect-liveness")
    @Operation(summary = "人脸活体检测")
    public Result<Boolean> detectLiveness(@RequestParam("image") MultipartFile image) {
        try {
            boolean result = faceService.detectLiveness(image);
            return Result.success(result);
        } catch (Exception e) {
            log.error("人脸活体检测失败", e);
            return Result.failed(e.getMessage());
        }
    }

    @PostMapping("/compare")
    @Operation(summary = "人脸比对")
    public Result<Boolean> compareFace(
            @RequestParam("image1") MultipartFile image1,
            @RequestParam("image2") MultipartFile image2) {
        try {
            boolean result = faceService.compareFace(image1, image2);
            return Result.success(result);
        } catch (Exception e) {
            log.error("人脸比对失败", e);
            return Result.failed(e.getMessage());
        }
    }

    @PostMapping("/save")
    @Operation(summary = "保存用户人脸图片")
    public Result<Boolean> saveUserFaceImage(@RequestParam("image") MultipartFile image) {
        try {
            Integer userId = getCurrentUserIdFromRequest();
            boolean result = faceService.saveUserFaceImage(userId, image);
            return Result.success(result);
        } catch (Exception e) {
            log.error("保存用户人脸图片失败", e);
            return Result.failed(e.getMessage());
        }
    }

    @PostMapping("/verify")
    @Operation(summary = "验证用户人脸")
    public Result<Boolean> verifyUserFace(
            @RequestParam("userId") Integer userId,
            @RequestParam("image") MultipartFile image) {
        try {
            boolean result = faceService.verifyUserFace(userId, image);
            return Result.success(result);
        } catch (Exception e) {
            log.error("验证用户人脸失败", e);
            return Result.failed(e.getMessage());
        }
    }
} 