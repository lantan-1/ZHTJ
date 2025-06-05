package com.zhtj.service.impl;

import com.aliyun.facebody20191230.Client;
import com.aliyun.facebody20191230.models.CompareFaceAdvanceRequest;
import com.aliyun.facebody20191230.models.CompareFaceResponse;
import com.aliyun.facebody20191230.models.DetectLivingFaceAdvanceRequest;
import com.aliyun.facebody20191230.models.DetectLivingFaceResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.tea.TeaModel;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.zhtj.common.exception.BusinessException;
import com.zhtj.domain.User;
import com.zhtj.service.FaceService;
import com.zhtj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 人脸识别服务实现类
 */
@Service
@Slf4j
public class FaceServiceImpl implements FaceService {

    @Value("${aliyun.face.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.face.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.face.endpoint}")
    private String endpoint;

    @Value("${aliyun.face.image-path}")
    private String imagePath;

    @Value("${file.upload.user-face.max-size}")
    private int maxFileSize;

    @Value("${file.upload.user-face.allowed-types}")
    private String allowedTypes;

    @Value("${aliyun.face.liveness-rate-threshold:80}")
    private double livenessRateThreshold;

    @Value("${aliyun.face.similarity-threshold:75}")
    private double similarityThreshold;

    @Autowired
    private UserService userService;

    /**
     * 创建阿里云人脸识别客户端
     */
    private Client createClient() throws Exception {
        Config config = new Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        config.endpoint = endpoint;
        return new Client(config);
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) {
        // 检查文件大小
        if (file.getSize() > maxFileSize * 1024 * 1024) {
            throw new BusinessException("文件大小超过限制，最大允许" + maxFileSize + "MB");
        }
    
        // 检查文件类型
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename).toLowerCase();
        if (extension.isEmpty()) {
            throw new BusinessException("文件缺少扩展名，仅支持：" + allowedTypes);
        }
        // allowedTypes建议用逗号分隔，如"jpg,jpeg,png"
        if (!allowedTypes.contains(extension)) {
            throw new BusinessException("不支持的文件类型，仅支持：" + allowedTypes);
        }
    }

    /**
     * 人脸活体检测
     * @param imageFile 图片文件
     * @return 检测结果，true为通过，false为未通过
     * @throws BusinessException 检测失败时抛出
     */
    @Override
    public boolean detectLiveness(MultipartFile imageFile) {
        try {
            // 验证文件
            validateFile(imageFile);
            
            // 保存临时文件
            String tempFilePath = saveTempFile(imageFile);
            
            // 创建客户端
            Client client = createClient();
            
            // 构建任务和请求，使用 try-with-resources 保证流关闭
            DetectLivingFaceAdvanceRequest.DetectLivingFaceAdvanceRequestTasks task;
            DetectLivingFaceAdvanceRequest request;
            DetectLivingFaceResponse response;
            try (FileInputStream fis = new FileInputStream(new File(tempFilePath))) {
                task = new DetectLivingFaceAdvanceRequest.DetectLivingFaceAdvanceRequestTasks()
                    .setImageURLObject(fis);
                request = new DetectLivingFaceAdvanceRequest()
                    .setTasks(java.util.Arrays.asList(task));
                response = client.detectLivingFaceAdvance(request, new RuntimeOptions());
                System.out.println("-----------------");
                System.out.println(response);
            }
            // 删除临时文件（流已关闭）
            Files.deleteIfExists(Paths.get(tempFilePath));
            
            // 检查响应数据
            if (response == null || response.getBody() == null || 
                response.getBody().getData() == null || 
                response.getBody().getData().getElements() == null || 
                response.getBody().getData().getElements().isEmpty() ||
                response.getBody().getData().getElements().get(0).getResults() == null ||
                response.getBody().getData().getElements().get(0).getResults().isEmpty()) {
                log.warn("活体检测响应数据为空或格式异常，response={}", response);
                throw new BusinessException("活体检测失败：未检测到人脸或图片不合规，请正对摄像头并确保光线充足");
            }
            
            // 获取活体检测结果的Label和Rate
            String label = response.getBody().getData().getElements().get(0).getResults().get(0).getLabel();
            Float rate = response.getBody().getData().getElements().get(0).getResults().get(0).getRate();

            // 判断是否为活体（Label为normal且Rate达到阈值）
            return "normal".equals(label) && rate != null && rate >= livenessRateThreshold;

        } catch (BusinessException be) {
            // 已知业务异常直接抛出
            throw be;
        } catch (Exception e) {
            log.error("人脸活体检测失败，异常信息：{}", e.getMessage(), e);
            if (e.getMessage() != null && e.getMessage().contains("Range") && e.getMessage().contains("out of bounds")) {
                throw new BusinessException("活体检测失败：未检测到人脸或图片不合规，请正对摄像头并确保光线充足");
            }
            throw new BusinessException("人脸活体检测失败：" + e.getMessage());
        }
    }

    /**
     * 人脸比对
     * @param imageFile1 第一张图片文件
     * @param imageFile2 第二张图片文件
     * @return 相似度是否超过阈值
     * @throws BusinessException 比对失败时抛出
     */
    @Override
    public boolean compareFace(MultipartFile imageFile1, MultipartFile imageFile2) {
        try {
            validateFile(imageFile1);
            validateFile(imageFile2);
            String tempFilePath1 = saveTempFile(imageFile1);
            String tempFilePath2 = saveTempFile(imageFile2);
            Client client = createClient();
            CompareFaceAdvanceRequest request;
            CompareFaceResponse response;
            try (FileInputStream fis1 = new FileInputStream(new File(tempFilePath1));
                 FileInputStream fis2 = new FileInputStream(new File(tempFilePath2))) {
                request = new CompareFaceAdvanceRequest()
                        .setImageURLAObject(fis1)
                        .setImageURLBObject(fis2);
                response = client.compareFaceAdvance(request, new RuntimeOptions());
            }
            Files.deleteIfExists(Paths.get(tempFilePath1));
            Files.deleteIfExists(Paths.get(tempFilePath2));
            if (response == null || response.getBody() == null || 
                response.getBody().getData() == null) {
                log.warn("人脸比对响应数据为空或格式异常，response={}", response);
                throw new BusinessException("人脸比对失败：未检测到人脸或图片不合规，请正对摄像头并确保光线充足");
            }
            return response.getBody().getData().getConfidence() > similarityThreshold;
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            log.error("人脸比对失败，异常信息：{}", e.getMessage(), e);
            if (e.getMessage() != null && e.getMessage().contains("Range") && e.getMessage().contains("out of bounds")) {
                throw new BusinessException("人脸比对失败：未检测到人脸或图片不合规，请正对摄像头并确保光线充足");
            }
            throw new BusinessException("人脸比对失败：" + e.getMessage());
        }
    }

    /**
     * 保存用户人脸图片（含活体检测）
     * @param userId 用户ID
     * @param imageFile 图片文件
     * @return 保存结果
     * @throws BusinessException 失败时抛出
     */
    @Override
    public boolean saveUserFaceImage(Integer userId, MultipartFile imageFile) {
        try {
            validateFile(imageFile);
            if (!detectLiveness(imageFile)) {
                throw new BusinessException("活体检测未通过，请正对摄像头并确保光线充足");
            }
            User user = userService.getById(userId);
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
            Path savePath = Paths.get(imagePath, "faces");
            if (!Files.exists(savePath)) {
                Files.createDirectories(savePath);
            }
            String fileName = UUID.randomUUID().toString() + "." + getFileExtension(imageFile.getOriginalFilename());
            Path filePath = savePath.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath);
            user.setFaceImagePath(filePath.toString());
            userService.updateById(user);
            return true;
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            log.error("保存用户人脸图片失败，异常信息：{}", e.getMessage(), e);
            throw new BusinessException("保存用户人脸图片失败：" + e.getMessage());
        }
    }

    /**
     * 验证用户人脸（通过用户ID）
     * @param userId 用户ID
     * @param imageFile 待验证的图片文件
     * @return 验证结果
     * @throws BusinessException 验证失败时抛出
     */
    @Override
    public boolean verifyUserFace(Integer userId, MultipartFile imageFile) {
        try {
            validateFile(imageFile);
            User user = userService.getById(userId);
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
            if (user.getFaceImagePath() == null || user.getFaceImagePath().isEmpty()) {
                throw new BusinessException("用户未录入人脸信息");
            }
            String tempFilePath = saveTempFile(imageFile);
            Client client = createClient();
            CompareFaceAdvanceRequest request;
            CompareFaceResponse response;
            try (FileInputStream fisA = new FileInputStream(new File(user.getFaceImagePath()));
                 FileInputStream fisB = new FileInputStream(new File(tempFilePath))) {
                request = new CompareFaceAdvanceRequest()
                        .setImageURLAObject(fisA)
                        .setImageURLBObject(fisB);
                response = client.compareFaceAdvance(request, new RuntimeOptions());
            }
            Files.deleteIfExists(Paths.get(tempFilePath));
            if (response == null || response.getBody() == null || 
                response.getBody().getData() == null) {
                log.warn("人脸验证响应数据为空或格式异常，response={}", response);
                throw new BusinessException("人脸验证失败：未检测到人脸或图片不合规，请正对摄像头并确保光线充足");
            }
            return response.getBody().getData().getConfidence() > similarityThreshold;
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            log.error("人脸验证失败，异常信息：{}", e.getMessage(), e);
            if (e.getMessage() != null && e.getMessage().contains("Range") && e.getMessage().contains("out of bounds")) {
                throw new BusinessException("人脸验证失败：未检测到人脸或图片不合规，请正对摄像头并确保光线充足");
            }
            throw new BusinessException("人脸验证失败：" + e.getMessage());
        }
    }

    @Override
    public boolean hasFaceImage(Integer userId) {
        try {
            User user = userService.getById(userId);
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
            return user.getFaceImagePath() != null && !user.getFaceImagePath().isEmpty();
        } catch (Exception e) {
            log.error("检查用户人脸信息失败", e);
            throw new BusinessException("检查用户人脸信息失败：" + e.getMessage());
        }
    }

    @Override
    public boolean hasFaceImageByCard(String card) {
        try {
            // 通过身份证号查询用户
            User user = userService.getByCard(card);
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
            return user.getFaceImagePath() != null && !user.getFaceImagePath().isEmpty();
        } catch (Exception e) {
            log.error("检查用户人脸信息失败", e);
            throw new BusinessException("检查用户人脸信息失败：" + e.getMessage());
        }
    }

    /**
     * 验证用户人脸（通过身份证号）
     * @param card 身份证号
     * @param imageFile 待验证的图片文件
     * @return 验证结果
     * @throws BusinessException 验证失败时抛出
     */
    @Override
    public boolean verifyUserFaceByCard(String card, MultipartFile imageFile) {
        try {
            User user = userService.getByCard(card);
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
            if (user.getFaceImagePath() == null || user.getFaceImagePath().isEmpty()) {
                throw new BusinessException("用户未录入人脸信息");
            }
            String tempFilePath = saveTempFile(imageFile);
            Client client = createClient();
            CompareFaceAdvanceRequest request;
            CompareFaceResponse response;
            try (FileInputStream fisA = new FileInputStream(new File(user.getFaceImagePath()));
                 FileInputStream fisB = new FileInputStream(new File(tempFilePath))) {
                request = new CompareFaceAdvanceRequest()
                        .setImageURLAObject(fisA)
                        .setImageURLBObject(fisB);
                response = client.compareFaceAdvance(request, new RuntimeOptions());
            }
            Files.deleteIfExists(Paths.get(tempFilePath));
            if (response == null || response.getBody() == null || 
                response.getBody().getData() == null) {
                log.warn("人脸验证响应数据为空或格式异常，response={}", response);
                throw new BusinessException("人脸验证失败：未检测到人脸或图片不合规，请正对摄像头并确保光线充足");
            }
            return response.getBody().getData().getConfidence() > similarityThreshold;
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            log.error("人脸验证失败，异常信息：{}", e.getMessage(), e);
            if (e.getMessage() != null && e.getMessage().contains("Range") && e.getMessage().contains("out of bounds")) {
                throw new BusinessException("人脸验证失败：未检测到人脸或图片不合规，请正对摄像头并确保光线充足");
            }
            throw new BusinessException("人脸验证失败：" + e.getMessage());
        }
    }

    /**
     * 保存临时文件
     */
    private String saveTempFile(MultipartFile file) throws Exception {
        String tempDir = System.getProperty("java.io.tmpdir");
        String fileName = UUID.randomUUID().toString() + "." + getFileExtension(file.getOriginalFilename());
        Path tempPath = Paths.get(tempDir, fileName);
        Files.copy(file.getInputStream(), tempPath);
        return tempPath.toString();
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null) return "";
        int lastDotIndex = fileName.lastIndexOf(".");
        return (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) ? "" : fileName.substring(lastDotIndex + 1);
    }
} 