-- 添加人脸图片路径字段
ALTER TABLE `user` ADD COLUMN `face_image_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '人脸图片路径' AFTER `is_activated`; 