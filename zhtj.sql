/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80031 (8.0.31)
 Source Host           : localhost:3306
 Source Schema         : zhtj

 Target Server Type    : MySQL
 Target Server Version : 80031 (8.0.31)
 File Encoding         : 65001

 Date: 24/05/2025 00:50:44
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for activity
-- ----------------------------
DROP TABLE IF EXISTS `activity`;
CREATE TABLE `activity`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '序号',
  `organization` int NULL DEFAULT NULL COMMENT '团组织序号',
  `category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '类别(支部大会/团课/主题团日/入团仪式/组织生活会/其他)',
  `required_topic` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '必学专题',
  `optional_topics` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '自学专题(多选,逗号分隔)',
  `participants` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '参加的团员',
  `date` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '开展时间',
  `place` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地点',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '内容',
  `host` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '活动主持人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_act_org`(`organization` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 63 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of activity
-- ----------------------------
INSERT INTO `activity` VALUES (24, 3, '团课', '学习贯彻党的二十大精神', '传承五四精神', '1,2,4', '2023-05-04', '计算机学院报告厅', '学习贯彻党的二十大精神，传承发扬五四精神，做新时代优秀共青团员', '张明', '2025-03-25 10:53:51', '2025-03-25 10:53:51');
INSERT INTO `activity` VALUES (25, 3, '主题团日', '青年大学习', '团员素质拓展', '1,2,4', '2023-06-15', '计算机学院活动室', '开展青年大学习活动，增强团员综合素质', '张明', '2025-03-25 10:53:51', '2025-03-25 10:53:51');
INSERT INTO `activity` VALUES (26, 4, '入团仪式', '学习贯彻党的二十大精神', NULL, '3', '2023-05-10', '电子工程学院报告厅', '举行新团员入团宣誓仪式，学习团章', '王强', '2025-03-25 10:53:51', '2025-03-25 10:53:51');
INSERT INTO `activity` VALUES (27, 3, '支部大会', '学习贯彻党的二十大精神', '传承五四精神', '21,22,24', '2023-06-10', '清华大学学生活动中心', '学习贯彻党的二十大精神，组织团员学习交流', '张明', '2025-05-02 14:11:03', '2025-05-02 14:11:03');
INSERT INTO `activity` VALUES (28, 3, '团课', '青年大学习', '团员素质拓展', '21,22,23,24', '2023-07-15', '清华大学主楼报告厅', '开展青年大学习主题教育活动，提升团员素质', '李民', '2025-05-02 14:11:03', '2025-05-02 14:11:03');
INSERT INTO `activity` VALUES (29, 3, '主题团日', '学习贯彻党的二十大精神', '传承五四精神,团员素质拓展', '22,24', '2023-08-05', '清华大学图书馆报告厅', '开展\"青春心向党\"主题团日活动', '张明', '2025-05-02 14:11:03', '2025-05-02 14:11:03');
INSERT INTO `activity` VALUES (30, 3, '入团仪式', '青年大学习', NULL, '24', '2023-05-04', '清华大学礼堂', '组织新团员入团宣誓仪式，学习团章', '李民', '2025-05-02 14:11:03', '2025-05-02 14:11:03');
INSERT INTO `activity` VALUES (31, 3, '组织生活会', '学习贯彻党的二十大精神', '团员素质拓展', '21,22,23,24,25', '2023-09-20', '清华大学学生活动中心', '总结学期工作，开展批评与自我批评', '李民', '2025-05-02 14:11:03', '2025-05-02 14:11:03');
INSERT INTO `activity` VALUES (32, 14, '支部大会', '学习贯彻党的二十大精神', '传承五四精神', '21,22', '2023-06-12', '计算机学院会议室', '学习二十大精神，讨论团支部建设', '张明', '2025-05-02 14:11:03', '2025-05-02 14:11:03');
INSERT INTO `activity` VALUES (33, 14, '团课', '青年大学习', '团员素质拓展', '21,22,24', '2023-07-18', '计算机学院报告厅', '邀请专家讲授\"信息技术与国家发展\"专题讲座', '李红', '2025-05-02 14:11:03', '2025-05-02 14:11:03');
INSERT INTO `activity` VALUES (34, 14, '主题团日', '青年大学习', '传承五四精神', '21,22,24', '2023-08-08', '计算机学院活动室', '开展编程竞赛活动，提升专业能力', '张明', '2025-05-02 14:11:03', '2025-05-02 14:11:03');
INSERT INTO `activity` VALUES (35, 14, '组织生活会', '学习贯彻党的二十大精神', NULL, '21,22,24', '2023-09-25', '计算机学院会议室', '学期末团组织生活会，总结工作', '李红', '2025-05-02 14:11:03', '2025-05-02 14:11:03');
INSERT INTO `activity` VALUES (36, 14, '其他', '团员素质拓展', '传承五四精神', '21,22,24', '2023-10-15', '计算机学院操场', '组织团员参加体育锻炼活动', '张明', '2025-05-02 14:11:03', '2025-05-02 14:11:03');
INSERT INTO `activity` VALUES (37, 15, '支部大会', '学习贯彻党的二十大精神', NULL, '21,22', '2023-06-15', '计算机系会议室', '讨论学习二十大精神心得体会', '李红', '2025-05-02 14:11:03', '2025-05-02 14:11:03');
INSERT INTO `activity` VALUES (38, 15, '团课', '青年大学习', '团员素质拓展', '21,22', '2023-07-20', '计算机系教室', '开展\"网络安全与个人信息保护\"专题讲座', '张明', '2025-05-02 14:11:03', '2025-05-02 14:11:03');
INSERT INTO `activity` VALUES (39, 15, '主题团日', '学习贯彻党的二十大精神', '传承五四精神', '21,22', '2023-08-10', '计算机系实验室', '参观校史馆，学习校史传统', '李红', '2025-05-02 14:11:03', '2025-05-02 14:11:03');
INSERT INTO `activity` VALUES (40, 15, '入团仪式', '青年大学习', NULL, '24', '2023-05-08', '计算机系会议室', '组织新团员入团宣誓仪式', '张明', '2025-05-02 14:11:03', '2025-05-02 14:11:03');
INSERT INTO `activity` VALUES (41, 15, '组织生活会', '学习贯彻党的二十大精神', '团员素质拓展', '21,22,24', '2023-09-28', '计算机系会议室', '开展批评与自我批评，总结工作', '李红', '2025-05-02 14:11:03', '2025-05-02 14:11:03');
INSERT INTO `activity` VALUES (42, 16, '支部大会', '其他', '传承五四精神', '23,25', '2023-06-18', '电子工程学院会议室', '学习贯彻二十大精神，规划学期工作', '王强', '2025-05-02 14:11:03', '2025-05-02 14:11:03');
INSERT INTO `activity` VALUES (43, 16, '团课', '青年大学习', '团员素质拓展', '22,23,25', '2023-07-25', '电子工程学院报告厅', '开展\"电子信息技术发展前沿\"讲座', '李民', '2025-05-02 14:11:03', '2025-05-02 14:11:03');
INSERT INTO `activity` VALUES (44, 16, '团课', '学习贯彻党的二十大精神', '传承五四精神', '23,22', '2023-08-15', '电子工程学院实验室', '开展电子设计创新大赛活动', '王强', '2025-05-02 14:11:03', '2025-05-02 14:11:03');
INSERT INTO `activity` VALUES (45, 16, '入团仪式', '特色活动', '', '22', '2023-05-10', '电子工程学院报告厅', '组织新团员入团宣誓仪式', '李民', '2025-05-02 14:11:03', '2025-05-02 14:11:03');
INSERT INTO `activity` VALUES (47, 3, '支部大会', '学习贯彻党的二十大精神', '传承五四精神', '21,22,24', '2025-05-02', '清华大学会议中心', '当月团员思想交流会', '张明', '2025-05-02 14:11:03', '2025-05-02 14:11:03');
INSERT INTO `activity` VALUES (48, 14, '团课', '青年大学习', '团员素质拓展', '21,22,24', '2025-05-04', '计算机学院报告厅', '当月青年大学习活动', '李红', '2025-05-02 14:11:03', '2025-05-02 14:11:03');
INSERT INTO `activity` VALUES (49, 15, '主题团日', '学习贯彻党的二十大精神', '传承五四精神', '21,22', '2025-05-07', '计算机系活动室', '当月主题团日活动', '李红', '2025-05-02 14:11:03', '2025-05-02 14:11:03');
INSERT INTO `activity` VALUES (50, 16, '组织生活会', '青年大学习', '团员素质拓展', '22,23,25', '2025-05-12', '电子工程学院会议室', '当月组织生活会', '王强', '2025-05-02 14:11:03', '2025-05-02 14:11:03');
INSERT INTO `activity` VALUES (51, 3, '主题团日', '学习贯彻党的二十大精神', '传承五四精神', '21,22,24', '2025-06-02', '清华大学学生活动中心', '下月团日活动', '张明', '2025-05-02 14:11:03', '2025-05-02 14:11:03');
INSERT INTO `activity` VALUES (52, 14, '团课', '青年大学习', '团员素质拓展', '21,22,24', '2025-06-02', '计算机学院报告厅', '下月团课活动', '李红', '2025-05-02 14:11:03', '2025-05-02 14:11:03');
INSERT INTO `activity` VALUES (53, 15, '支部大会', '学习贯彻党的二十大精神', NULL, '21,22', '2025-06-02', '计算机系会议室', '下月支部大会', '张明', '2025-05-02 14:11:03', '2025-05-02 14:11:03');
INSERT INTO `activity` VALUES (54, 16, '主题团日', '实践活动', '团员素质拓展', '22,23,25', '2025-06-02', '电子工程学院操场', '下月团建活动', '李民', '2025-05-02 14:11:03', '2025-05-02 14:11:03');
INSERT INTO `activity` VALUES (55, 15, '支部大会', '学习贯彻党的二十大精神', '传承五四精神', '33,34,35,36', '2023-06-05', '计算机学院报告厅', '学习贯彻党的二十大精神，结合专业特点讨论发展方向', '刘明', '2025-05-04 13:15:44', '2025-05-04 13:15:44');
INSERT INTO `activity` VALUES (56, 15, '团课', '青年大学习', '团员素质拓展', '33,34,35,36', '2023-07-10', '计算机学院教室', '邀请学院教授讲授计算机发展前沿技术', '王雪', '2025-05-04 13:15:44', '2025-05-04 13:15:44');
INSERT INTO `activity` VALUES (57, 30, '主题团日', '学习贯彻党的二十大精神', '传承五四精神', '37,38', '2023-08-15', '计算机学院活动室', '开展红色经典学习活动，传承五四精神', '陈华', '2025-05-04 13:15:44', '2025-05-04 13:15:44');
INSERT INTO `activity` VALUES (58, 31, '组织生活会', '青年大学习', '团员素质拓展', '39,40', '2023-09-20', '计算机学院会议室', '期末组织生活会，总结本学期工作成果', '赵毅', '2025-05-04 13:15:44', '2025-05-04 13:15:44');
INSERT INTO `activity` VALUES (59, 33, '团课', '学习贯彻党的二十大精神', '团员素质拓展', '41,42', '2023-10-15', '电子工程学院报告厅', '学习电子信息产业发展规划，研讨未来发展方向', '钱强', '2025-05-04 13:15:44', '2025-05-04 13:15:44');
INSERT INTO `activity` VALUES (60, 36, '主题团日', '青年大学习', '传承五四精神', '43,44', '2023-11-05', '数学科学学院会议室', '开展\"数学之美\"主题团日活动', '郑军', '2025-05-04 13:15:44', '2025-05-04 13:15:44');
INSERT INTO `activity` VALUES (61, 39, '支部大会', '学习贯彻党的二十大精神', '团员素质拓展', '45,46', '2023-11-20', '电子信息学院活动室', '传达上级团组织工作精神，制定工作计划', '吴刚', '2025-05-04 13:15:44', '2025-05-04 13:15:44');
INSERT INTO `activity` VALUES (62, 42, '团课', '青年大学习', '传承五四精神', '47,48', '2023-12-10', '机械工程学院报告厅', '开展专业技能培训，提升团员综合能力', '陈刚', '2025-05-04 13:15:44', '2025-05-04 13:15:44');

-- ----------------------------
-- Table structure for evaluation_criteria
-- ----------------------------
DROP TABLE IF EXISTS `evaluation_criteria`;
CREATE TABLE `evaluation_criteria`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '序号',
  `criteria_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '指标名称',
  `criteria_category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '指标类别',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '指标描述',
  `max_score` decimal(5, 2) NULL DEFAULT 10.00 COMMENT '最高分值',
  `min_score` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '最低分值',
  `weight` decimal(5, 2) NULL DEFAULT 1.00 COMMENT '权重',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序号',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态(0-禁用,1-启用)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category`(`criteria_category` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评议指标体系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of evaluation_criteria
-- ----------------------------
INSERT INTO `evaluation_criteria` VALUES (1, '思想政治表现', '政治素质', '评价团员的思想政治表现、理论学习情况', 10.00, 0.00, 0.30, 1, 1, '2025-03-25 10:53:51', '2025-03-25 10:53:51');
INSERT INTO `evaluation_criteria` VALUES (2, '学习成绩表现', '学业表现', '评价团员的学习态度、学习成绩等方面', 10.00, 0.00, 0.30, 2, 1, '2025-03-25 10:53:51', '2025-03-25 10:53:51');
INSERT INTO `evaluation_criteria` VALUES (3, '团组织活动参与度', '组织表现', '评价团员参与团组织活动的积极性和表现', 10.00, 0.00, 0.20, 3, 1, '2025-03-25 10:53:51', '2025-03-25 10:53:51');
INSERT INTO `evaluation_criteria` VALUES (4, '志愿服务表现', '志愿服务', '评价团员参与志愿服务活动的情况', 10.00, 0.00, 0.20, 4, 1, '2025-03-25 10:53:51', '2025-03-25 10:53:51');

-- ----------------------------
-- Table structure for evaluation_scoring
-- ----------------------------
DROP TABLE IF EXISTS `evaluation_scoring`;
CREATE TABLE `evaluation_scoring`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '序号',
  `evaluation_detail_id` int NOT NULL COMMENT '评议详情ID',
  `criteria_id` int NOT NULL COMMENT '评议指标ID',
  `score` decimal(5, 2) NULL DEFAULT NULL COMMENT '得分',
  `comment` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评语',
  `scorer_id` int NULL DEFAULT NULL COMMENT '打分人ID',
  `scorer_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '打分人姓名',
  `scoring_time` datetime NULL DEFAULT NULL COMMENT '打分时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_evaluation_detail_id`(`evaluation_detail_id` ASC) USING BTREE,
  INDEX `idx_criteria_id`(`criteria_id` ASC) USING BTREE,
  INDEX `idx_scorer_id`(`scorer_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评议打分表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of evaluation_scoring
-- ----------------------------

-- ----------------------------
-- Table structure for evaluation_template
-- ----------------------------
DROP TABLE IF EXISTS `evaluation_template`;
CREATE TABLE `evaluation_template`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '序号',
  `template_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模板名称',
  `template_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '模板类型',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '模板描述',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '模板内容',
  `creator_id` int NULL DEFAULT NULL COMMENT '创建人ID',
  `creator_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人姓名',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态(0-禁用,1-启用)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_template_type`(`template_type` ASC) USING BTREE,
  INDEX `idx_creator_id`(`creator_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评议模板表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of evaluation_template
-- ----------------------------
INSERT INTO `evaluation_template` VALUES (1, '团员年度评议模板', '团员评议', '用于团员年度评议的标准模板', '{\"title\":\"团员年度评议表\",\"sections\":[{\"name\":\"基本信息\",\"fields\":[{\"label\":\"姓名\",\"type\":\"text\"},{\"label\":\"性别\",\"type\":\"select\"},{\"label\":\"学号\",\"type\":\"text\"}]},{\"name\":\"评议内容\",\"fields\":[{\"label\":\"思想政治表现\",\"type\":\"radio\"},{\"label\":\"学习成绩\",\"type\":\"radio\"},{\"label\":\"团组织活动参与度\",\"type\":\"radio\"},{\"label\":\"志愿服务表现\",\"type\":\"radio\"}]},{\"name\":\"综合评价\",\"fields\":[{\"label\":\"评议结果\",\"type\":\"select\"},{\"label\":\"评语\",\"type\":\"textarea\"}]}]}', 1, '张明', 1, '2025-03-25 10:53:51', '2025-03-25 10:53:51');

-- ----------------------------
-- Table structure for file_storage
-- ----------------------------
DROP TABLE IF EXISTS `file_storage`;
CREATE TABLE `file_storage`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '序号',
  `file_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件名称',
  `file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件路径',
  `file_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件类型',
  `file_size` bigint NULL DEFAULT NULL COMMENT '文件大小(字节)',
  `upload_user_id` int NULL DEFAULT NULL COMMENT '上传用户ID',
  `upload_user_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '上传用户姓名',
  `business_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '业务类型(荣誉申请/评议/转接/活动)',
  `business_id` int NULL DEFAULT NULL COMMENT '业务ID',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件描述',
  `is_public` tinyint(1) NULL DEFAULT 0 COMMENT '是否公开(0-私有,1-公开)',
  `download_count` int NULL DEFAULT 0 COMMENT '下载次数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_business`(`business_type` ASC, `business_id` ASC) USING BTREE,
  INDEX `idx_upload_user`(`upload_user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文件存储表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of file_storage
-- ----------------------------

-- ----------------------------
-- Table structure for honor_application
-- ----------------------------
DROP TABLE IF EXISTS `honor_application`;
CREATE TABLE `honor_application`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '序号',
  `honor_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '荣誉名称',
  `user_id` int NULL DEFAULT NULL COMMENT '申请用户ID',
  `user_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '申请用户姓名',
  `organization_id` int NULL DEFAULT NULL COMMENT '组织ID',
  `organization_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组织名称',
  `application_year` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '申请年度',
  `application_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '申请理由',
  `related_evaluation_id` int NULL DEFAULT NULL COMMENT '关联评议ID',
  `related_evaluation_result` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关联评议结果',
  `related_evaluation_score` decimal(5, 2) NULL DEFAULT NULL COMMENT '关联评议分数',
  `applicant_id` int NULL DEFAULT NULL COMMENT '提名人ID',
  `applicant_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '提名人姓名',
  `application_time` datetime NULL DEFAULT NULL COMMENT '申请时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '待审批' COMMENT '状态(待审批/已通过/已拒绝)',
  `attachment_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '附件URL',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `approver_id` int NULL DEFAULT NULL COMMENT '审批人ID',
  `approver_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审批人姓名',
  `approval_time` datetime NULL DEFAULT NULL COMMENT '审批时间',
  `approval_comments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '审批意见',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_organization_id`(`organization_id` ASC) USING BTREE,
  INDEX `idx_application_year`(`application_year` ASC) USING BTREE,
  INDEX `idx_related_evaluation_id`(`related_evaluation_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_approver_id`(`approver_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '荣誉申请表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of honor_application
-- ----------------------------
INSERT INTO `honor_application` VALUES (1, '优秀共青团员', 22, '李红', 16, '电子工程学院团委', '2023', '2023年表现优秀，积极参与团组织活动，思想政治素质好，学习成绩优异', 1, '优秀', 92.50, 1, '张明', '2023-12-20 09:00:00', '已通过', NULL, '2025-03-25 10:53:51', '2025-05-10 12:28:16', 25, NULL, '2023-12-30 09:00:00', NULL);
INSERT INTO `honor_application` VALUES (2, '优秀共青团员', 33, '刘明', 15, '计算机科学与技术系2101班团支部', '2025', '2025年表现优秀，积极参与组织活动，思想政治素质好，学习成绩优异', 11, '优秀', 92.50, 33, '刘明', '2025-03-25 10:00:00', '申请中', NULL, '2025-05-04 13:17:23', '2025-05-09 22:20:22', NULL, NULL, NULL, NULL);
INSERT INTO `honor_application` VALUES (3, '优秀共青团员', 43, '郑军', 36, '数学系2101班团支部', '2025', '2025年表现突出，组织能力强，思想政治素质好，学习成绩优异', 15, '优秀', 94.00, 43, '郑军', '2025-05-20 11:00:00', '申请中', NULL, '2025-05-04 13:17:23', '2025-05-09 22:20:25', NULL, NULL, NULL, NULL);
INSERT INTO `honor_application` VALUES (4, '优秀共青团干部', 41, '钱强', 33, '电子工程系2101班团支部', '2025', '作为团支书工作突出，组织了多次有意义的团日活动，团员评价高', 14, '合格', 85.00, 41, '钱强', '2025-05-15 09:30:00', '申请中', NULL, '2025-05-04 13:17:23', '2025-05-09 22:20:28', NULL, NULL, NULL, NULL);
INSERT INTO `honor_application` VALUES (8, '优秀共青团员', 22, '李红', 16, '电子工程学院团委', '2025', '2025年表现突出，组织能力强，思想政治素质好，学习成绩优异', NULL, NULL, NULL, NULL, NULL, '2025-05-09 22:15:24', '申请中', NULL, '2025-05-09 21:56:48', '2025-05-09 22:20:37', NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for honor_award
-- ----------------------------
DROP TABLE IF EXISTS `honor_award`;
CREATE TABLE `honor_award`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '序号',
  `honor_application_id` int NULL DEFAULT NULL COMMENT '荣誉申请ID',
  `honor_type_id` int NOT NULL COMMENT '荣誉类型ID',
  `honor_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '荣誉名称',
  `honor_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '荣誉证书编号',
  `award_year` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '授予年度',
  `user_id` int NULL DEFAULT NULL COMMENT '获奖用户ID',
  `user_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '获奖用户姓名',
  `user_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '获奖用户证件号',
  `organization_id` int NULL DEFAULT NULL COMMENT '组织ID',
  `organization_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组织名称',
  `award_date` date NULL DEFAULT NULL COMMENT '授予日期',
  `expiry_date` date NULL DEFAULT NULL COMMENT '有效期至',
  `award_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '授予理由',
  `award_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '授予级别',
  `issuer_id` int NULL DEFAULT NULL COMMENT '颁发人ID',
  `issuer_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '颁发人姓名',
  `issuer_organization` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '颁发单位',
  `issue_time` datetime NULL DEFAULT NULL COMMENT '颁发时间',
  `certificate_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '证书URL',
  `photo_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '照片URL',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '有效' COMMENT '状态(有效/已过期/已撤销)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `related_evaluation_id` int NULL DEFAULT NULL COMMENT '关联评议ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_honor_application_id`(`honor_application_id` ASC) USING BTREE,
  INDEX `idx_honor_type_id`(`honor_type_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_organization_id`(`organization_id` ASC) USING BTREE,
  INDEX `idx_award_year`(`award_year` ASC) USING BTREE,
  INDEX `idx_award_date`(`award_date` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '荣誉授予记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of honor_award
-- ----------------------------

-- ----------------------------
-- Table structure for honor_type
-- ----------------------------
DROP TABLE IF EXISTS `honor_type`;
CREATE TABLE `honor_type`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '序号',
  `honor_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '荣誉名称',
  `honor_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '荣誉编码',
  `honor_category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '荣誉类别(团干部荣誉/团员荣誉/集体荣誉)',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '荣誉描述',
  `conditions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '授予条件',
  `level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '荣誉级别(国家级/省级/市级/校级/院级/支部级)',
  `valid_period` int NULL DEFAULT 1 COMMENT '有效期(年)',
  `max_quota` int NULL DEFAULT NULL COMMENT '最大名额限制',
  `quota_cycle` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '年度' COMMENT '名额周期(年度/学期/月度)',
  `requires_evaluation` tinyint(1) NULL DEFAULT 1 COMMENT '是否需要评议(0-否,1-是)',
  `min_evaluation_score` decimal(5, 2) NULL DEFAULT NULL COMMENT '最低评议分数要求',
  `evaluation_result_required` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '优秀' COMMENT '要求的评议结果(优秀/合格/基本合格)',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态(0-禁用,1-启用)',
  `creator_id` int NULL DEFAULT NULL COMMENT '创建人ID',
  `creator_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人姓名',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_honor_code`(`honor_code` ASC) USING BTREE,
  INDEX `idx_honor_category`(`honor_category` ASC) USING BTREE,
  INDEX `idx_level`(`level` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '荣誉类型表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of honor_type
-- ----------------------------
INSERT INTO `honor_type` VALUES (1, '优秀共青团员', 'YX_MEMBER', '团员荣誉', '表彰在学习、工作和生活中表现突出的共青团员', '1. 思想政治素质好\n2. 学习成绩优异\n3. 积极参与团组织活动', '校级', 1, 100, '年度', 1, 85.00, '优秀', 1, NULL, NULL, '2025-03-25 10:53:51', '2025-03-25 10:53:51');
INSERT INTO `honor_type` VALUES (2, '优秀共青团干部', 'YX_CADRE', '团干部荣誉', '表彰在团务工作中表现突出的共青团干部', '1. 思想政治素质好\n2. 团务工作出色\n3. 有较强的组织能力', '校级', 1, 50, '年度', 1, 90.00, '优秀', 1, NULL, NULL, '2025-03-25 10:53:51', '2025-03-25 10:53:51');
INSERT INTO `honor_type` VALUES (3, '先进团支部', 'ADV_BRANCH', '集体荣誉', '表彰工作成绩突出的团支部', '1. 团支部建设规范\n2. 团日活动丰富多彩\n3. 团员发展和教育管理工作扎实', '校级', 1, 10, '年度', 1, 95.00, '优秀', 1, NULL, NULL, '2025-03-25 10:53:51', '2025-03-25 10:53:51');

-- ----------------------------
-- Table structure for league_transfer_approval_log
-- ----------------------------
DROP TABLE IF EXISTS `league_transfer_approval_log`;
CREATE TABLE `league_transfer_approval_log`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '序号',
  `application_id` int NOT NULL COMMENT '关联的申请ID',
  `approval_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '审批类型(转出/转入)',
  `approver` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审批人',
  `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '审批状态',
  `approval_time` datetime NULL DEFAULT NULL COMMENT '审批时间',
  `approval_remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '审批备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_application_id`(`application_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '团组织关系转接审批记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of league_transfer_approval_log
-- ----------------------------

-- ----------------------------
-- Table structure for member_evaluation
-- ----------------------------
DROP TABLE IF EXISTS `member_evaluation`;
CREATE TABLE `member_evaluation`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '序号',
  `evaluation_year` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评议年度',
  `organization_id` int NOT NULL COMMENT '评议组织ID',
  `organization_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评议组织名称',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评议标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '评议说明',
  `start_time` datetime NULL DEFAULT NULL COMMENT '评议开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '评议结束时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '草稿' COMMENT '状态(草稿/进行中/已完成/已取消)',
  `initiator_id` int NULL DEFAULT NULL COMMENT '发起人ID',
  `initiator_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发起人姓名',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_organization_id`(`organization_id` ASC) USING BTREE,
  INDEX `idx_evaluation_year`(`evaluation_year` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_initiator_id`(`initiator_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '团员评议表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of member_evaluation
-- ----------------------------
INSERT INTO `member_evaluation` VALUES (1, '2023', 3, '计算机科学与技术系团支部', '2023年度团员评议', '开展2023年度团员评议工作，全面评价团员在思想、学习、工作等方面的表现', '2023-12-01 00:00:00', '2023-12-31 23:59:59', '进行中', 21, '张明', '2025-03-25 10:53:51', '2025-05-04 19:36:02');
INSERT INTO `member_evaluation` VALUES (2, '2025', 16, '电子工程学院团委', '2025年春季团员教育评议', '开展2025年春季团员教育评议，全面评估团员思想政治素质、作风纪律等方面情况', '2025-03-01 00:00:00', '2025-04-30 23:59:59', '已完成', 25, '李民', '2025-05-04 12:23:00', '2025-05-05 11:31:51');
INSERT INTO `member_evaluation` VALUES (3, '2025', 16, '电子工程学院团委', '2025年夏季团员教育评议', '开展2025年夏季团员教育评议，全面评估团员思想政治素质、作风纪律等方面情况', '2025-05-01 00:00:00', '2025-06-30 23:59:59', '进行中', 25, '李民', '2025-05-04 12:23:00', '2025-05-05 11:32:12');
INSERT INTO `member_evaluation` VALUES (4, '2025', 16, '电子工程学院团委', '2025年秋季团员教育评议', '开展2025年秋季团员教育评议，全面评估团员思想政治素质、作风纪律等方面情况', '2025-09-01 12:23:00', '2025-10-31 12:23:00', '进行中', 25, '李民', '2025-05-04 12:23:00', '2025-05-05 11:32:37');
INSERT INTO `member_evaluation` VALUES (5, '2025', 16, '电子工程学院团委', '2025年冬季团员教育评议', '开展2025年冬季团员教育评议，全面评估团员思想政治素质、作风纪律等方面情况', '2025-11-01 12:23:00', '2025-12-31 12:23:00', '草稿', 25, '李民', '2025-05-04 12:23:00', '2025-05-05 11:33:00');
INSERT INTO `member_evaluation` VALUES (6, '2024', 16, '电子工程学院团委', '2024年秋季团员教育评议', '开展2024年秋季团员教育评议，全面评估团员思想政治素质、作风纪律等方面情况', '2024-09-01 00:00:00', '2024-09-30 23:59:59', '已完成', 25, '李民', '2025-05-04 12:23:00', '2025-05-04 19:34:57');
INSERT INTO `member_evaluation` VALUES (7, '2024', 16, '电子工程学院团委', '2024年冬季团员教育评议', '开展2024年冬季团员教育评议，全面评估团员思想政治素质、作风纪律等方面情况', '2024-12-01 00:00:00', '2024-12-31 23:59:59', '已完成', 25, '李民', '2025-05-04 12:23:00', '2025-05-04 19:34:57');
INSERT INTO `member_evaluation` VALUES (8, '2025', 17, '数学科学学院团委', '数学科学学院团委2025年春季团员教育评议', '开展数学科学学院2025年春季团员教育评议', '2025-03-01 00:00:00', '2025-03-31 23:59:59', '已完成', 36, '郑军', '2025-05-04 12:23:00', '2025-05-04 19:36:02');
INSERT INTO `member_evaluation` VALUES (9, '2025', 18, '电子信息学院团委', '电子信息学院团委2025年春季团员教育评议', '开展电子信息学院2025年春季团员教育评议', '2025-03-01 00:00:00', '2025-03-31 23:59:59', '已完成', 38, '吴刚', '2025-05-04 12:23:00', '2025-05-04 19:36:02');
INSERT INTO `member_evaluation` VALUES (10, '2025', 19, '机械工程学院团委', '机械工程学院团委2025年春季团员教育评议', '开展机械工程学院2025年春季团员教育评议', '2025-05-04 12:23:00', '2025-05-29 12:23:00', '进行中', 40, '陈刚', '2025-05-04 12:23:00', '2025-05-04 19:36:02');
INSERT INTO `member_evaluation` VALUES (11, '2025', 15, '计算机科学与技术系2101班团支部', '计算机科学与技术系2101班团支部2025年春季团员教育评议', '开展计算机科学与技术系2101班团支部2025年春季团员教育评议', '2025-03-01 00:00:00', '2025-03-31 23:59:59', '已完成', 33, '孙敏', '2025-05-04 13:15:44', '2025-05-04 19:34:57');
INSERT INTO `member_evaluation` VALUES (12, '2025', 30, '计算机科学与技术系2102班团支部', '计算机科学与技术系2102班团支部2025年春季团员教育评议', '开展计算机科学与技术系2102班团支部2025年春季团员教育评议', '2025-03-01 00:00:00', '2025-03-31 23:59:59', '已完成', 37, '周萍', '2025-05-04 13:15:44', '2025-05-04 19:34:57');
INSERT INTO `member_evaluation` VALUES (13, '2025', 31, '软件工程系2101班团支部', '软件工程系2101班团支部2025年春季团员教育评议', '开展软件工程系2101班团支部2025年春季团员教育评议', '2025-03-01 00:00:00', '2025-03-31 23:59:59', '已完成', 39, '林丽', '2025-05-04 13:15:44', '2025-05-04 19:34:57');
INSERT INTO `member_evaluation` VALUES (14, '2025', 33, '电子工程系2101班团支部', '电子工程系2101班团支部2025年春季团员教育评议', '开展电子工程系2101班团支部2025年春季团员教育评议', '2025-03-01 00:00:00', '2025-03-31 23:59:59', '已完成', 41, '赵丽', '2025-05-04 13:15:44', '2025-05-04 19:34:57');
INSERT INTO `member_evaluation` VALUES (15, '2025', 36, '数学系2101班团支部', '数学系2101班团支部2025年春季团员教育评议', '开展数学系2101班团支部2025年春季团员教育评议', '2025-05-01 00:00:00', '2025-05-31 23:59:59', '进行中', 36, '郑军', '2025-05-04 13:15:44', '2025-05-04 19:36:02');
INSERT INTO `member_evaluation` VALUES (16, '2025', 39, '信息工程系2101班团支部', '信息工程系2101班团支部2025年春季团员教育评议', '开展信息工程系2101班团支部2025年春季团员教育评议', '2025-05-01 00:00:00', '2025-05-31 23:59:59', '进行中', 38, '吴刚', '2025-05-04 13:15:44', '2025-05-04 19:36:02');
INSERT INTO `member_evaluation` VALUES (17, '2025', 42, '机械设计系2101班团支部', '机械设计系2101班团支部2025年春季团员教育评议', '开展机械设计系2101班团支部2025年春季团员教育评议', '2025-05-10 00:00:00', '2025-06-10 23:59:59', '进行中', 40, '陈刚', '2025-05-04 13:15:44', '2025-05-04 19:36:02');

-- ----------------------------
-- Table structure for member_evaluation_detail
-- ----------------------------
DROP TABLE IF EXISTS `member_evaluation_detail`;
CREATE TABLE `member_evaluation_detail`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '序号',
  `evaluation_id` int NOT NULL COMMENT '评议ID',
  `user_id` int NOT NULL COMMENT '被评议用户ID',
  `user_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '被评议用户姓名',
  `result` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评议结果(优秀/合格/基本合格/不合格)',
  `score` decimal(5, 2) NULL DEFAULT NULL COMMENT '评分',
  `comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '评语',
  `evaluator_id` int NULL DEFAULT NULL COMMENT '评议人ID',
  `evaluator_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评议人姓名',
  `evaluation_time` datetime NULL DEFAULT NULL COMMENT '评议时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '待评议' COMMENT '状态(待评议/已评议/已审核)',
  `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '待审批' COMMENT '审批状态(待审批/已通过/已拒绝)',
  `approver_id` int NULL DEFAULT NULL COMMENT '审批人ID',
  `approver_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审批人姓名',
  `approval_time` datetime NULL DEFAULT NULL COMMENT '审批时间',
  `approval_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '审批意见',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_evaluation_id`(`evaluation_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_result`(`result` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_approval_status`(`approval_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '团员评议详情表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of member_evaluation_detail
-- ----------------------------
INSERT INTO `member_evaluation_detail` VALUES (1, 1, 22, '李红', '优秀', 92.50, '该团员在思想政治、学习成绩和团组织活动参与度等方面表现优秀', 21, '张明', '2023-12-15 10:30:00', '已评议', '待审批', NULL, NULL, NULL, NULL, '2025-03-25 10:53:51', '2025-05-04 19:38:25');
INSERT INTO `member_evaluation_detail` VALUES (2, 1, 24, '赵芳', '合格', 83.00, '该团员能够积极参与团组织活动，但在志愿服务方面还需加强', 21, '张明', '2023-12-15 11:00:00', '已评议', '待审批', NULL, NULL, NULL, NULL, '2025-03-25 10:53:51', '2025-05-04 19:38:25');
INSERT INTO `member_evaluation_detail` VALUES (3, 2, 22, '李红', '优秀', 94.50, '该团员在思想政治、学习成绩和团组织活动参与度等方面表现突出，积极参与组织活动', 25, '李民', '2025-05-04 12:32:24', '已评议', '已通过', NULL, NULL, NULL, NULL, '2025-05-04 12:32:24', '2025-05-04 12:32:24');
INSERT INTO `member_evaluation_detail` VALUES (4, 2, 24, '赵芳', '合格', 82.00, '该团员能够积极参与团组织活动，在思想学习方面有进步', 25, '李民', '2025-05-04 12:32:24', '已评议', '已通过', NULL, NULL, NULL, NULL, '2025-05-04 12:32:24', '2025-05-04 12:32:24');
INSERT INTO `member_evaluation_detail` VALUES (5, 3, 22, '李红', '优秀', 93.00, '该团员在思想政治、学习成绩和团组织活动参与度等方面表现突出，积极参与组织活动', 22, '李红', '2025-05-05 20:27:04', '已评议', '待审批', NULL, NULL, NULL, NULL, '2025-05-04 12:32:24', '2025-05-05 20:27:04');
INSERT INTO `member_evaluation_detail` VALUES (6, 3, 24, '赵芳', '合格', 81.50, '该团员能够积极参与团组织活动，但在志愿服务方面还需加强', 22, '李红', '2025-05-05 20:26:58', '已评议', '待审批', NULL, NULL, NULL, NULL, '2025-05-04 12:32:24', '2025-05-05 20:26:58');
INSERT INTO `member_evaluation_detail` VALUES (7, 4, 22, '李红', '优秀', 96.00, '该团员各方面表现突出，特别是在志愿服务方面做出了突出贡献', 25, '李民', '2025-05-04 12:32:24', '已评议', '待审批', NULL, NULL, NULL, NULL, '2025-05-04 12:32:24', '2025-05-04 12:32:24');
INSERT INTO `member_evaluation_detail` VALUES (8, 4, 24, '赵芳', '优秀', 90.50, '该团员进步明显，积极参与各项活动', 25, '李民', '2025-05-04 12:32:24', '已评议', '待审批', NULL, NULL, NULL, NULL, '2025-05-04 12:32:24', '2025-05-04 12:32:24');
INSERT INTO `member_evaluation_detail` VALUES (9, 6, 22, '李红', '优秀', 95.00, '该团员在理论学习和实践活动中均表现优异', 25, '李民', '2024-09-20 15:30:00', '已评议', '已通过', NULL, NULL, NULL, NULL, '2025-05-04 12:32:24', '2025-05-04 12:32:24');
INSERT INTO `member_evaluation_detail` VALUES (10, 6, 24, '赵芳', '合格', 85.00, '该团员能够完成基本任务，但主动性有待提高', 25, '李民', '2024-09-20 16:00:00', '已评议', '已通过', NULL, NULL, NULL, NULL, '2025-05-04 12:32:24', '2025-05-04 12:32:24');
INSERT INTO `member_evaluation_detail` VALUES (11, 7, 22, '李红', '优秀', 94.00, '该团员全面发展，在各方面都有良好表现', 25, '李民', '2024-12-15 14:00:00', '已评议', '已通过', NULL, NULL, NULL, NULL, '2025-05-04 12:32:24', '2025-05-04 12:32:24');
INSERT INTO `member_evaluation_detail` VALUES (12, 7, 24, '赵芳', '优秀', 91.00, '该团员进步显著，积极参与各项活动并有出色表现', 25, '李民', '2024-12-15 14:30:00', '已评议', '已通过', NULL, NULL, NULL, NULL, '2025-05-04 12:32:24', '2025-05-04 12:32:24');
INSERT INTO `member_evaluation_detail` VALUES (13, 11, 35, '吴佳', '优秀', 92.50, '该团员在思想政治、学习成绩和团组织活动参与度等方面表现突出', 33, '孙敏', '2025-03-15 10:30:00', '已评议', '已通过', NULL, NULL, NULL, NULL, '2025-05-04 13:15:44', '2025-05-04 19:34:57');
INSERT INTO `member_evaluation_detail` VALUES (14, 11, 36, '郑军', '合格', 83.00, '该团员能够积极参与团组织活动，但在创新活动方面有待提高', 33, '孙敏', '2025-03-15 11:00:00', '已评议', '已通过', NULL, NULL, NULL, NULL, '2025-05-04 13:15:44', '2025-05-04 19:34:57');
INSERT INTO `member_evaluation_detail` VALUES (15, 12, 38, '吴刚', '优秀', 91.00, '该团员各方面表现优秀，特别是在团组织活动中发挥积极作用', 37, '周萍', '2025-03-16 10:30:00', '已评议', '已通过', NULL, NULL, NULL, NULL, '2025-05-04 13:15:44', '2025-05-04 19:34:57');
INSERT INTO `member_evaluation_detail` VALUES (16, 13, 40, '陈刚', '优秀', 93.50, '该团员在学习和团内活动方面表现突出，有较强的专业能力', 39, '林丽', '2025-03-18 14:30:00', '已评议', '已通过', NULL, NULL, NULL, NULL, '2025-05-04 13:15:44', '2025-05-04 19:34:57');
INSERT INTO `member_evaluation_detail` VALUES (17, 14, 35, '吴佳', '合格', 85.00, '该团员能够完成基本任务，在志愿服务方面有突出表现', 41, '赵丽', '2025-03-20 15:00:00', '已评议', '已通过', NULL, NULL, NULL, NULL, '2025-05-04 13:15:44', '2025-05-04 19:38:25');
INSERT INTO `member_evaluation_detail` VALUES (18, 15, 37, '周萍', '优秀', 94.00, '该团员政治表现优秀，学习成绩突出，是班级的模范', 36, '郑军', '2025-05-12 10:00:00', '已评议', '待审批', NULL, NULL, NULL, NULL, '2025-05-04 13:15:44', '2025-05-04 19:38:25');
INSERT INTO `member_evaluation_detail` VALUES (19, 16, 39, '林丽', '合格', 86.50, '该团员在团内活动参与度较高，但学习成绩有待提高', 38, '吴刚', '2025-05-15 11:30:00', '已评议', '待审批', NULL, NULL, NULL, NULL, '2025-05-04 13:15:44', '2025-05-04 19:38:25');
INSERT INTO `member_evaluation_detail` VALUES (20, 17, 41, '赵丽', '优秀', 95.00, '该团员在各方面表现优异，特别是在专业技能和志愿服务方面', 40, '陈刚', '2025-05-18 14:00:00', '已评议', '待审批', NULL, NULL, NULL, NULL, '2025-05-04 13:15:44', '2025-05-04 19:38:25');

-- ----------------------------
-- Table structure for member_incentive
-- ----------------------------
DROP TABLE IF EXISTS `member_incentive`;
CREATE TABLE `member_incentive`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '序号',
  `user_id` int NOT NULL COMMENT '团员ID',
  `user_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '团员姓名',
  `organization_id` int NULL DEFAULT NULL COMMENT '组织ID',
  `organization_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组织名称',
  `incentive_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '激励类型(荣誉称号/表彰奖励/优秀评价/其他)',
  `incentive_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '激励名称',
  `incentive_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '激励级别(校级/院级/班级/其他)',
  `incentive_date` date NULL DEFAULT NULL COMMENT '激励日期',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '详细描述',
  `related_evaluation_id` int NULL DEFAULT NULL COMMENT '关联的评议ID',
  `approver_id` int NULL DEFAULT NULL COMMENT '审批人ID',
  `approver_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审批人姓名',
  `approval_time` datetime NULL DEFAULT NULL COMMENT '审批时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '已生效' COMMENT '状态(待审批/已生效/已撤销)',
  `attachment_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '附件URL',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_organization_id`(`organization_id` ASC) USING BTREE,
  INDEX `idx_incentive_type`(`incentive_type` ASC) USING BTREE,
  INDEX `idx_incentive_date`(`incentive_date` ASC) USING BTREE,
  INDEX `idx_related_evaluation_id`(`related_evaluation_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '团员激励表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of member_incentive
-- ----------------------------

-- ----------------------------
-- Table structure for member_register
-- ----------------------------
DROP TABLE IF EXISTS `member_register`;
CREATE TABLE `member_register`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '注册ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `user_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '被注册用户姓名',
  `organization_id` int NOT NULL COMMENT '组织ID',
  `organization_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组织名称',
  `batch_id` int NULL DEFAULT NULL COMMENT '批次ID',
  `register_year` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '注册年度',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '待审核' COMMENT '状态(待审核/已通过/已拒绝)',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '注册备注',
  `register_time` datetime NULL DEFAULT NULL COMMENT '注册时间',
  `approval_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `approver_id` int NULL DEFAULT NULL COMMENT '审核人ID',
  `approver_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核人姓名',
  `approval_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '审核意见',
  `register_deadline` datetime NULL DEFAULT NULL COMMENT '注册截止时间',
  `is_reminder_sent` tinyint(1) NULL DEFAULT 0 COMMENT '是否已发送提醒',
  `reminder_time` datetime NULL DEFAULT NULL COMMENT '提醒时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_organization_id`(`organization_id` ASC) USING BTREE,
  INDEX `idx_register_year`(`register_year` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_register_deadline`(`register_deadline` ASC) USING BTREE,
  INDEX `idx_batch_id`(`batch_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '团员年度注册表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of member_register
-- ----------------------------
INSERT INTO `member_register` VALUES (1, 22, '李红', 16, '电子工程学院团委', 1, '2025', '已通过', '年度团籍注册', '2025-01-15 10:00:00', '2025-01-20 14:30:00', 25, '李民', NULL, NULL, 1, '2025-04-29 12:32:24', '2025-05-04 12:32:24', '2025-05-07 13:11:25');
INSERT INTO `member_register` VALUES (2, 24, '赵芳', 3, '清华大学团委', 1, '2025', '已通过', '年度团籍注册', '2025-01-16 11:00:00', '2025-01-21 15:30:00', 21, '张明', NULL, NULL, 1, '2025-04-29 12:32:24', '2025-05-04 12:32:24', '2025-05-07 13:11:25');
INSERT INTO `member_register` VALUES (3, 21, '张明', 3, '清华大学团委', 1, '2025', '已通过', '年度团籍注册', '2025-01-10 09:00:00', '2025-01-12 14:00:00', 21, '张明', NULL, NULL, 1, '2025-04-29 12:32:24', '2025-05-04 12:32:24', '2025-05-07 13:11:25');
INSERT INTO `member_register` VALUES (4, 23, '王强', 16, '电子工程学院团委', 1, '2025', '待审核', '年度团籍注册', '2025-03-10 10:30:00', NULL, NULL, NULL, NULL, NULL, 0, NULL, '2025-05-04 12:32:24', '2025-05-07 13:11:25');
INSERT INTO `member_register` VALUES (5, 33, '孙敏', 15, '计算机科学与技术系2101班团支部', 1, '2025', '已通过', '年度团籍注册', '2025-01-05 10:00:00', '2025-01-10 14:00:00', 33, '孙敏', NULL, NULL, 0, NULL, '2025-05-04 13:15:44', '2025-05-07 13:11:25');
INSERT INTO `member_register` VALUES (6, 34, '钱强', 15, '计算机科学与技术系2101班团支部', 1, '2025', '已通过', '年度团籍注册', '2025-01-06 11:00:00', '2025-01-10 14:30:00', 33, '孙敏', NULL, NULL, 0, NULL, '2025-05-04 13:15:44', '2025-05-07 13:11:25');
INSERT INTO `member_register` VALUES (7, 35, '吴佳', 15, '计算机科学与技术系2101班团支部', 1, '2025', '已通过', '年度团籍注册', '2025-01-07 09:00:00', '2025-01-10 15:00:00', 33, '孙敏', NULL, NULL, 0, NULL, '2025-05-04 13:15:44', '2025-05-07 13:11:25');
INSERT INTO `member_register` VALUES (8, 36, '郑军', 15, '计算机科学与技术系2101班团支部', 1, '2025', '已通过', '年度团籍注册', '2025-01-08 10:30:00', '2025-01-10 15:30:00', 33, '孙敏', NULL, NULL, 0, NULL, '2025-05-04 13:15:44', '2025-05-07 13:11:25');
INSERT INTO `member_register` VALUES (9, 37, '周萍', 30, '计算机科学与技术系2102班团支部', 1, '2025', '已通过', '年度团籍注册', '2025-01-12 10:00:00', '2025-01-15 14:00:00', 37, '周萍', NULL, NULL, 0, NULL, '2025-05-04 13:15:44', '2025-05-07 13:11:25');
INSERT INTO `member_register` VALUES (10, 38, '吴刚', 30, '计算机科学与技术系2102班团支部', 1, '2025', '已通过', '年度团籍注册', '2025-01-13 11:00:00', '2025-01-15 14:30:00', 37, '周萍', NULL, NULL, 0, NULL, '2025-05-04 13:15:44', '2025-05-07 13:11:25');
INSERT INTO `member_register` VALUES (11, 39, '林丽', 31, '软件工程系2101班团支部', 1, '2025', '已通过', '年度团籍注册', '2025-01-15 09:00:00', '2025-01-18 14:00:00', 39, '林丽', NULL, NULL, 0, NULL, '2025-05-04 13:15:44', '2025-05-07 13:11:25');
INSERT INTO `member_register` VALUES (12, 40, '陈刚', 31, '软件工程系2101班团支部', 1, '2025', '已通过', '年度团籍注册', '2025-01-16 10:30:00', '2025-01-18 14:30:00', 39, '林丽', NULL, NULL, 0, NULL, '2025-05-04 13:15:44', '2025-05-07 13:11:25');
INSERT INTO `member_register` VALUES (13, 41, '赵丽', 33, '电子工程系2101班团支部', 1, '2025', '已通过', '年度团籍注册', '2025-01-20 10:00:00', '2025-01-25 14:00:00', 41, '赵丽', NULL, NULL, 0, NULL, '2025-05-04 13:15:44', '2025-05-07 13:11:25');
INSERT INTO `member_register` VALUES (14, 42, '吴佳', 33, '电子工程系2101班团支部', 1, '2025', '已通过', '年度团籍注册', '2025-01-21 11:00:00', '2025-01-25 14:30:00', 41, '赵丽', NULL, NULL, 0, NULL, '2025-05-04 13:15:44', '2025-05-07 13:11:25');
INSERT INTO `member_register` VALUES (15, 43, '郑军', 36, '数学系2101班团支部', 1, '2025', '已通过', '年度团籍注册', '2025-03-10 10:00:00', '2025-03-15 14:00:00', NULL, NULL, NULL, NULL, 0, NULL, '2025-05-04 13:15:44', '2025-05-07 13:11:25');
INSERT INTO `member_register` VALUES (16, 44, '周萍', 36, '数学系2101班团支部', 1, '2025', '已通过', '年度团籍注册', '2025-03-11 11:00:00', '2025-03-15 14:30:00', NULL, NULL, NULL, NULL, 0, NULL, '2025-05-04 13:15:44', '2025-05-07 13:11:25');
INSERT INTO `member_register` VALUES (17, 45, '吴刚', 39, '信息工程系2101班团支部', 1, '2025', '已通过', '年度团籍注册', '2025-03-12 09:00:00', '2025-03-16 14:00:00', NULL, NULL, NULL, NULL, 0, NULL, '2025-05-04 13:15:44', '2025-05-07 13:11:25');
INSERT INTO `member_register` VALUES (18, 46, '林丽', 39, '信息工程系2101班团支部', 1, '2025', '待审核', '年度团籍注册', '2025-03-13 10:30:00', NULL, NULL, NULL, NULL, NULL, 0, NULL, '2025-05-04 13:15:44', '2025-05-07 13:11:25');
INSERT INTO `member_register` VALUES (19, 47, '陈刚', 42, '机械设计系2101班团支部', 1, '2025', '已通过', '年度团籍注册', '2025-03-20 10:00:00', '2025-03-25 14:00:00', NULL, NULL, NULL, NULL, 0, NULL, '2025-05-04 13:15:44', '2025-05-07 13:11:25');
INSERT INTO `member_register` VALUES (20, 48, '赵丽', 42, '机械设计系2101班团支部', 1, '2025', '待审核', '年度团籍注册', '2025-03-21 11:00:00', NULL, NULL, NULL, NULL, NULL, 0, NULL, '2025-05-04 13:15:44', '2025-05-07 13:11:25');
INSERT INTO `member_register` VALUES (21, 22, '李红', 16, '电子工程学院团委', 2, '2025', '待审核', '2025年第二批次团员年度注册', '2025-05-06 12:30:56', '2025-05-06 12:30:56', 25, '李民', NULL, NULL, 0, NULL, '2025-05-06 12:30:56', '2025-05-08 14:16:16');
INSERT INTO `member_register` VALUES (22, 23, '王强', 16, '电子工程学院团委', 2, '2025', '待审核', '', '2025-05-06 11:15:54', '2025-05-07 13:19:22', 22, '李红', NULL, NULL, 0, NULL, '2025-05-07 11:15:50', '2025-05-08 14:16:13');

-- ----------------------------
-- Table structure for member_register_backup
-- ----------------------------
DROP TABLE IF EXISTS `member_register_backup`;
CREATE TABLE `member_register_backup`  (
  `id` int NOT NULL DEFAULT 0 COMMENT '注册ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `user_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '被注册用户姓名',
  `organization_id` int NOT NULL COMMENT '组织ID',
  `organization_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组织名称',
  `batch_id` int NULL DEFAULT NULL COMMENT '批次ID',
  `register_year` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '注册年度',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '待审核' COMMENT '状态(待审核/已通过/已拒绝)',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '注册备注',
  `register_time` datetime NULL DEFAULT NULL COMMENT '注册时间',
  `approval_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `approver_id` int NULL DEFAULT NULL COMMENT '审核人ID',
  `approver_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核人姓名',
  `approval_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '审核意见',
  `register_deadline` datetime NULL DEFAULT NULL COMMENT '注册截止时间',
  `is_reminder_sent` tinyint(1) NULL DEFAULT 0 COMMENT '是否已发送提醒',
  `reminder_time` datetime NULL DEFAULT NULL COMMENT '提醒时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of member_register_backup
-- ----------------------------
INSERT INTO `member_register_backup` VALUES (1, 22, '李红', 16, '电子工程学院团委', NULL, '2025', '已通过', '年度团籍注册', '2025-01-15 10:00:00', '2025-01-20 14:30:00', 25, '李民', NULL, NULL, 1, '2025-04-29 12:32:24', '2025-05-04 12:32:24', '2025-05-07 13:11:21');
INSERT INTO `member_register_backup` VALUES (2, 24, '赵芳', 3, '清华大学团委', NULL, '2025', '已通过', '年度团籍注册', '2025-01-16 11:00:00', '2025-01-21 15:30:00', 21, '张明', NULL, NULL, 1, '2025-04-29 12:32:24', '2025-05-04 12:32:24', '2025-05-07 13:11:21');
INSERT INTO `member_register_backup` VALUES (3, 21, '张明', 3, '清华大学团委', NULL, '2025', '已通过', '年度团籍注册', '2025-01-10 09:00:00', '2025-01-12 14:00:00', 21, '张明', NULL, NULL, 1, '2025-04-29 12:32:24', '2025-05-04 12:32:24', '2025-05-07 13:11:21');
INSERT INTO `member_register_backup` VALUES (4, 23, '王强', 16, '电子工程学院团委', NULL, '2025', '待审核', '年度团籍注册', '2025-03-10 10:30:00', NULL, NULL, NULL, NULL, NULL, 0, NULL, '2025-05-04 12:32:24', '2025-05-07 13:11:21');
INSERT INTO `member_register_backup` VALUES (5, 33, '孙敏', 15, '计算机科学与技术系2101班团支部', NULL, '2025', '已通过', '年度团籍注册', '2025-01-05 10:00:00', '2025-01-10 14:00:00', 33, '孙敏', NULL, NULL, 0, NULL, '2025-05-04 13:15:44', '2025-05-07 13:11:21');
INSERT INTO `member_register_backup` VALUES (6, 34, '钱强', 15, '计算机科学与技术系2101班团支部', NULL, '2025', '已通过', '年度团籍注册', '2025-01-06 11:00:00', '2025-01-10 14:30:00', 33, '孙敏', NULL, NULL, 0, NULL, '2025-05-04 13:15:44', '2025-05-07 13:11:21');
INSERT INTO `member_register_backup` VALUES (7, 35, '吴佳', 15, '计算机科学与技术系2101班团支部', NULL, '2025', '已通过', '年度团籍注册', '2025-01-07 09:00:00', '2025-01-10 15:00:00', 33, '孙敏', NULL, NULL, 0, NULL, '2025-05-04 13:15:44', '2025-05-07 13:11:21');
INSERT INTO `member_register_backup` VALUES (8, 36, '郑军', 15, '计算机科学与技术系2101班团支部', NULL, '2025', '已通过', '年度团籍注册', '2025-01-08 10:30:00', '2025-01-10 15:30:00', 33, '孙敏', NULL, NULL, 0, NULL, '2025-05-04 13:15:44', '2025-05-07 13:11:21');
INSERT INTO `member_register_backup` VALUES (9, 37, '周萍', 30, '计算机科学与技术系2102班团支部', NULL, '2025', '已通过', '年度团籍注册', '2025-01-12 10:00:00', '2025-01-15 14:00:00', 37, '周萍', NULL, NULL, 0, NULL, '2025-05-04 13:15:44', '2025-05-07 13:11:21');
INSERT INTO `member_register_backup` VALUES (10, 38, '吴刚', 30, '计算机科学与技术系2102班团支部', NULL, '2025', '已通过', '年度团籍注册', '2025-01-13 11:00:00', '2025-01-15 14:30:00', 37, '周萍', NULL, NULL, 0, NULL, '2025-05-04 13:15:44', '2025-05-07 13:11:21');
INSERT INTO `member_register_backup` VALUES (11, 39, '林丽', 31, '软件工程系2101班团支部', NULL, '2025', '已通过', '年度团籍注册', '2025-01-15 09:00:00', '2025-01-18 14:00:00', 39, '林丽', NULL, NULL, 0, NULL, '2025-05-04 13:15:44', '2025-05-07 13:11:21');
INSERT INTO `member_register_backup` VALUES (12, 40, '陈刚', 31, '软件工程系2101班团支部', NULL, '2025', '已通过', '年度团籍注册', '2025-01-16 10:30:00', '2025-01-18 14:30:00', 39, '林丽', NULL, NULL, 0, NULL, '2025-05-04 13:15:44', '2025-05-07 13:11:21');
INSERT INTO `member_register_backup` VALUES (13, 41, '赵丽', 33, '电子工程系2101班团支部', NULL, '2025', '已通过', '年度团籍注册', '2025-01-20 10:00:00', '2025-01-25 14:00:00', 41, '赵丽', NULL, NULL, 0, NULL, '2025-05-04 13:15:44', '2025-05-07 13:11:21');
INSERT INTO `member_register_backup` VALUES (14, 42, '吴佳', 33, '电子工程系2101班团支部', NULL, '2025', '已通过', '年度团籍注册', '2025-01-21 11:00:00', '2025-01-25 14:30:00', 41, '赵丽', NULL, NULL, 0, NULL, '2025-05-04 13:15:44', '2025-05-07 13:11:21');
INSERT INTO `member_register_backup` VALUES (15, 43, '郑军', 36, '数学系2101班团支部', NULL, '2025', '已通过', '年度团籍注册', '2025-03-10 10:00:00', '2025-03-15 14:00:00', NULL, NULL, NULL, NULL, 0, NULL, '2025-05-04 13:15:44', '2025-05-07 13:11:21');
INSERT INTO `member_register_backup` VALUES (16, 44, '周萍', 36, '数学系2101班团支部', NULL, '2025', '已通过', '年度团籍注册', '2025-03-11 11:00:00', '2025-03-15 14:30:00', NULL, NULL, NULL, NULL, 0, NULL, '2025-05-04 13:15:44', '2025-05-07 13:11:21');
INSERT INTO `member_register_backup` VALUES (17, 45, '吴刚', 39, '信息工程系2101班团支部', NULL, '2025', '已通过', '年度团籍注册', '2025-03-12 09:00:00', '2025-03-16 14:00:00', NULL, NULL, NULL, NULL, 0, NULL, '2025-05-04 13:15:44', '2025-05-07 13:11:21');
INSERT INTO `member_register_backup` VALUES (18, 46, '林丽', 39, '信息工程系2101班团支部', NULL, '2025', '待审核', '年度团籍注册', '2025-03-13 10:30:00', NULL, NULL, NULL, NULL, NULL, 0, NULL, '2025-05-04 13:15:44', '2025-05-07 13:11:21');
INSERT INTO `member_register_backup` VALUES (19, 47, '陈刚', 42, '机械设计系2101班团支部', NULL, '2025', '已通过', '年度团籍注册', '2025-03-20 10:00:00', '2025-03-25 14:00:00', NULL, NULL, NULL, NULL, 0, NULL, '2025-05-04 13:15:44', '2025-05-07 13:11:21');
INSERT INTO `member_register_backup` VALUES (20, 48, '赵丽', 42, '机械设计系2101班团支部', NULL, '2025', '待审核', '年度团籍注册', '2025-03-21 11:00:00', NULL, NULL, NULL, NULL, NULL, 0, NULL, '2025-05-04 13:15:44', '2025-05-07 13:11:21');
INSERT INTO `member_register_backup` VALUES (21, 22, '李红', 16, '电子工程学院团委', NULL, '2025', '已通过', '2025年第二批次团员年度注册', '2025-05-06 12:30:56', '2025-05-06 12:30:56', 25, '李民', NULL, NULL, 0, NULL, '2025-05-06 12:30:56', '2025-05-07 13:11:21');
INSERT INTO `member_register_backup` VALUES (22, 23, '王强', 16, '电子工程学院团委', NULL, '2025', '已通过', '2025年第二批次团员年度注册', '2025-05-06 11:15:54', '2025-05-07 12:27:48', 22, '李红', NULL, NULL, 0, NULL, '2025-05-07 11:15:50', '2025-05-07 13:11:21');

-- ----------------------------
-- Table structure for member_register_batch
-- ----------------------------
DROP TABLE IF EXISTS `member_register_batch`;
CREATE TABLE `member_register_batch`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '批次ID',
  `batch_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '批次名称',
  `organization_id` int NOT NULL COMMENT '组织ID',
  `organization_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组织名称',
  `register_year` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '注册年度',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '未开始' COMMENT '状态(未开始/进行中/已结束)',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '批次说明',
  `creator_id` int NOT NULL COMMENT '创建人ID',
  `creator_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人姓名',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_organization_id`(`organization_id` ASC) USING BTREE,
  INDEX `idx_register_year`(`register_year` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '团员年度注册批次表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of member_register_batch
-- ----------------------------
INSERT INTO `member_register_batch` VALUES (1, '2025年团员年度注册第一批次', 16, '电子工程学院团委', '2025', '2025-01-01 00:00:00', '2025-02-28 23:59:59', '已结束', '2025年第一批次团员年度注册工作', 25, '李民', '2025-05-04 12:32:24', '2025-05-04 12:32:24');
INSERT INTO `member_register_batch` VALUES (2, '2025年团员年度注册第二批次', 16, '电子工程学院团委', '2025', '2025-03-01 00:00:00', '2025-06-01 23:59:59', '进行中', '2025年第二批次团员年度注册工作', 25, '李民', '2025-05-04 12:32:24', '2025-05-06 17:45:45');
INSERT INTO `member_register_batch` VALUES (3, '2025年团员年度注册第一批次', 3, '清华大学团委', '2025', '2025-01-01 00:00:00', '2025-02-28 23:59:59', '已结束', '2025年第一批次团员年度注册工作', 21, '张明', '2025-05-04 12:32:24', '2025-05-04 12:32:24');
INSERT INTO `member_register_batch` VALUES (4, '2025年团员年度注册第二批次', 3, '清华大学团委', '2025', '2025-03-01 00:00:00', '2025-06-01 23:59:59', '进行中', '2025年第二批次团员年度注册工作', 21, '张明', '2025-05-04 12:32:24', '2025-05-06 17:45:52');
INSERT INTO `member_register_batch` VALUES (5, '2025年团员年度注册第一批次', 14, '计算机学院团委', '2025', '2025-01-01 00:00:00', '2025-02-28 23:59:59', '已结束', '2025年第一批次团员年度注册工作', 26, '刘明', '2025-05-04 13:15:44', '2025-05-06 13:52:59');
INSERT INTO `member_register_batch` VALUES (6, '2025年团员年度注册第二批次', 14, '计算机学院团委', '2025', '2025-03-01 00:00:00', '2025-06-01 23:59:59', '进行中', '2025年第二批次团员年度注册工作', 26, '刘明', '2025-05-04 13:15:44', '2025-05-06 17:46:01');
INSERT INTO `member_register_batch` VALUES (7, '2024年团员年度注册第一批次', 16, '电子工程学院团委', '2024', '2024-01-01 00:00:00', '2024-02-29 23:59:59', '已结束', '2024年第一批次团员年度注册工作', 25, '李民', '2023-12-30 13:15:44', '2025-05-06 22:18:30');
INSERT INTO `member_register_batch` VALUES (8, '2024年团员年度注册第二批次', 16, '电子工程学院团委', '2024', '2024-03-01 00:00:00', '2024-06-01 23:59:59', '已结束', '2024年第二批次团员年度注册工作', 25, '李民', '2024-02-29 13:15:44', '2025-05-06 22:18:39');

-- ----------------------------
-- Table structure for member_register_batch_organization
-- ----------------------------
DROP TABLE IF EXISTS `member_register_batch_organization`;
CREATE TABLE `member_register_batch_organization`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `batch_id` int NOT NULL COMMENT '批次ID',
  `organization_id` int NOT NULL COMMENT '组织ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_batch_organization`(`batch_id` ASC, `organization_id` ASC) USING BTREE,
  INDEX `idx_batch_id`(`batch_id` ASC) USING BTREE,
  INDEX `idx_organization_id`(`organization_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 105 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '注册批次组织关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of member_register_batch_organization
-- ----------------------------
INSERT INTO `member_register_batch_organization` VALUES (1, 3, 14, '2025-05-04 13:20:00');
INSERT INTO `member_register_batch_organization` VALUES (2, 3, 16, '2025-05-04 13:20:00');
INSERT INTO `member_register_batch_organization` VALUES (3, 3, 17, '2025-05-04 13:20:00');
INSERT INTO `member_register_batch_organization` VALUES (4, 3, 18, '2025-05-04 13:20:00');
INSERT INTO `member_register_batch_organization` VALUES (5, 3, 19, '2025-05-04 13:20:00');
INSERT INTO `member_register_batch_organization` VALUES (6, 4, 14, '2025-05-04 13:20:00');
INSERT INTO `member_register_batch_organization` VALUES (7, 4, 16, '2025-05-04 13:20:00');
INSERT INTO `member_register_batch_organization` VALUES (8, 4, 17, '2025-05-04 13:20:00');
INSERT INTO `member_register_batch_organization` VALUES (9, 4, 18, '2025-05-04 13:20:00');
INSERT INTO `member_register_batch_organization` VALUES (10, 4, 19, '2025-05-04 13:20:00');
INSERT INTO `member_register_batch_organization` VALUES (11, 5, 15, '2025-05-04 13:21:00');
INSERT INTO `member_register_batch_organization` VALUES (12, 5, 30, '2025-05-04 13:21:00');
INSERT INTO `member_register_batch_organization` VALUES (13, 5, 31, '2025-05-04 13:21:00');
INSERT INTO `member_register_batch_organization` VALUES (14, 5, 32, '2025-05-04 13:21:00');
INSERT INTO `member_register_batch_organization` VALUES (15, 6, 15, '2025-05-04 13:21:00');
INSERT INTO `member_register_batch_organization` VALUES (16, 6, 30, '2025-05-04 13:21:00');
INSERT INTO `member_register_batch_organization` VALUES (17, 6, 31, '2025-05-04 13:21:00');
INSERT INTO `member_register_batch_organization` VALUES (18, 6, 32, '2025-05-04 13:21:00');
INSERT INTO `member_register_batch_organization` VALUES (19, 1, 33, '2025-05-04 13:22:00');
INSERT INTO `member_register_batch_organization` VALUES (20, 1, 34, '2025-05-04 13:22:00');
INSERT INTO `member_register_batch_organization` VALUES (21, 1, 35, '2025-05-04 13:22:00');
INSERT INTO `member_register_batch_organization` VALUES (22, 7, 33, '2025-05-04 13:22:00');
INSERT INTO `member_register_batch_organization` VALUES (23, 7, 34, '2025-05-04 13:22:00');
INSERT INTO `member_register_batch_organization` VALUES (24, 7, 35, '2025-05-04 13:22:00');
INSERT INTO `member_register_batch_organization` VALUES (25, 2, 33, '2025-05-04 13:22:00');
INSERT INTO `member_register_batch_organization` VALUES (26, 2, 34, '2025-05-04 13:22:00');
INSERT INTO `member_register_batch_organization` VALUES (27, 2, 35, '2025-05-04 13:22:00');
INSERT INTO `member_register_batch_organization` VALUES (28, 8, 33, '2025-05-04 13:22:00');
INSERT INTO `member_register_batch_organization` VALUES (29, 8, 34, '2025-05-04 13:22:00');
INSERT INTO `member_register_batch_organization` VALUES (30, 8, 35, '2025-05-04 13:22:00');
INSERT INTO `member_register_batch_organization` VALUES (31, 4, 36, '2025-05-04 13:23:00');
INSERT INTO `member_register_batch_organization` VALUES (32, 4, 37, '2025-05-04 13:23:00');
INSERT INTO `member_register_batch_organization` VALUES (33, 4, 38, '2025-05-04 13:23:00');
INSERT INTO `member_register_batch_organization` VALUES (34, 4, 39, '2025-05-04 13:23:00');
INSERT INTO `member_register_batch_organization` VALUES (35, 4, 40, '2025-05-04 13:23:00');
INSERT INTO `member_register_batch_organization` VALUES (36, 4, 41, '2025-05-04 13:23:00');
INSERT INTO `member_register_batch_organization` VALUES (37, 4, 42, '2025-05-04 13:23:00');
INSERT INTO `member_register_batch_organization` VALUES (38, 4, 43, '2025-05-04 13:23:00');
INSERT INTO `member_register_batch_organization` VALUES (39, 4, 44, '2025-05-04 13:23:00');
INSERT INTO `member_register_batch_organization` VALUES (40, 8, 16, '2025-05-06 12:30:56');
INSERT INTO `member_register_batch_organization` VALUES (41, 7, 16, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (42, 6, 16, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (43, 5, 16, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (44, 2, 16, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (45, 1, 16, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (46, 8, 3, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (47, 7, 3, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (48, 6, 3, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (49, 5, 3, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (50, 4, 3, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (51, 3, 3, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (52, 2, 3, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (53, 1, 3, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (54, 8, 4, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (55, 7, 4, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (56, 6, 4, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (57, 5, 4, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (58, 4, 4, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (59, 3, 4, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (60, 2, 4, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (61, 1, 4, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (62, 8, 15, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (63, 7, 15, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (64, 4, 15, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (65, 3, 15, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (66, 2, 15, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (67, 1, 15, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (68, 8, 30, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (69, 7, 30, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (70, 4, 30, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (71, 3, 30, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (72, 2, 30, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (73, 1, 30, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (74, 8, 31, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (75, 7, 31, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (76, 4, 31, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (77, 3, 31, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (78, 2, 31, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (79, 1, 31, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (80, 6, 33, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (81, 5, 33, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (82, 4, 33, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (83, 3, 33, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (84, 8, 36, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (85, 7, 36, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (86, 6, 36, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (87, 5, 36, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (88, 3, 36, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (89, 2, 36, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (90, 1, 36, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (91, 8, 39, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (92, 7, 39, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (93, 6, 39, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (94, 5, 39, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (95, 3, 39, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (96, 2, 39, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (97, 1, 39, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (98, 8, 42, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (99, 7, 42, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (100, 6, 42, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (101, 5, 42, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (102, 3, 42, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (103, 2, 42, '2025-05-06 15:17:59');
INSERT INTO `member_register_batch_organization` VALUES (104, 1, 42, '2025-05-06 15:17:59');

-- ----------------------------
-- Table structure for member_register_statistics
-- ----------------------------
DROP TABLE IF EXISTS `member_register_statistics`;
CREATE TABLE `member_register_statistics`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '统计ID',
  `organization_id` int NOT NULL COMMENT '组织ID',
  `organization_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组织名称',
  `register_year` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '注册年度',
  `total_members` int NULL DEFAULT 0 COMMENT '团员总数',
  `registered_members` int NULL DEFAULT 0 COMMENT '已注册团员数',
  `approval_pending` int NULL DEFAULT 0 COMMENT '待审核数',
  `approved_members` int NULL DEFAULT 0 COMMENT '已通过数',
  `rejected_members` int NULL DEFAULT 0 COMMENT '已拒绝数',
  `registration_rate` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '注册率',
  `approval_rate` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '通过率',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_org_year`(`organization_id` ASC, `register_year` ASC) USING BTREE,
  INDEX `idx_register_year`(`register_year` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '团员注册统计表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of member_register_statistics
-- ----------------------------
INSERT INTO `member_register_statistics` VALUES (1, 3, '清华大学团委', '2025', 50, 2, 0, 2, 0, 4.00, 100.00, '2025-05-06 15:17:59');
INSERT INTO `member_register_statistics` VALUES (17, 14, '计算机学院团委', '2025', 95, 85, 5, 80, 0, 89.47, 94.12, '2025-05-04 13:17:23');
INSERT INTO `member_register_statistics` VALUES (18, 16, '电子工程学院团委', '2025', 85, 2, 0, 2, 0, 2.35, 100.00, '2025-05-06 15:17:59');
INSERT INTO `member_register_statistics` VALUES (19, 17, '数学科学学院团委', '2025', 75, 65, 4, 61, 0, 86.67, 93.85, '2025-05-04 13:17:23');
INSERT INTO `member_register_statistics` VALUES (20, 18, '电子信息学院团委', '2025', 80, 70, 6, 64, 0, 87.50, 91.43, '2025-05-04 13:17:23');
INSERT INTO `member_register_statistics` VALUES (21, 19, '机械工程学院团委', '2025', 90, 78, 5, 73, 0, 86.67, 93.59, '2025-05-04 13:17:23');
INSERT INTO `member_register_statistics` VALUES (22, 15, '计算机科学与技术系2101班团支部', '2025', 32, 4, 0, 4, 0, 12.50, 100.00, '2025-05-06 15:17:59');
INSERT INTO `member_register_statistics` VALUES (23, 30, '计算机科学与技术系2102班团支部', '2025', 30, 2, 0, 2, 0, 6.67, 100.00, '2025-05-06 15:17:59');
INSERT INTO `member_register_statistics` VALUES (24, 31, '软件工程系2101班团支部', '2025', 33, 2, 0, 2, 0, 6.06, 100.00, '2025-05-06 15:17:59');
INSERT INTO `member_register_statistics` VALUES (25, 33, '电子工程系2101班团支部', '2025', 38, 2, 0, 2, 0, 5.26, 100.00, '2025-05-06 15:17:59');
INSERT INTO `member_register_statistics` VALUES (26, 36, '数学系2101班团支部', '2025', 34, 2, 0, 2, 0, 5.88, 100.00, '2025-05-06 15:17:59');
INSERT INTO `member_register_statistics` VALUES (27, 39, '信息工程系2101班团支部', '2025', 37, 2, 1, 1, 0, 5.41, 50.00, '2025-05-06 15:17:59');
INSERT INTO `member_register_statistics` VALUES (28, 42, '机械设计系2101班团支部', '2025', 38, 2, 1, 1, 0, 5.26, 50.00, '2025-05-06 15:17:59');

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '序号',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '通知内容',
  `sender_id` int NULL DEFAULT NULL COMMENT '发送者ID',
  `sender_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发送者姓名',
  `sender_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发送者类型(用户/系统)',
  `recipient_id` int NULL DEFAULT NULL COMMENT '接收者ID',
  `recipient_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '接收者姓名',
  `organization_id` int NULL DEFAULT NULL COMMENT '相关组织ID',
  `notification_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '通知类型(转接申请/审批通知/系统通知等)',
  `reference_id` int NULL DEFAULT NULL COMMENT '关联ID(如转接申请ID)',
  `reference_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关联类型(如转接申请)',
  `is_read` tinyint(1) NULL DEFAULT 0 COMMENT '是否已读(0-未读,1-已读)',
  `read_time` datetime NULL DEFAULT NULL COMMENT '阅读时间',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态(0-失效,1-有效)',
  `priority` int NULL DEFAULT 0 COMMENT '优先级(0-普通,1-重要,2-紧急)',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '过期时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_recipient_id`(`recipient_id` ASC) USING BTREE,
  INDEX `idx_sender_id`(`sender_id` ASC) USING BTREE,
  INDEX `idx_organization_id`(`organization_id` ASC) USING BTREE,
  INDEX `idx_reference_id`(`reference_id` ASC) USING BTREE,
  INDEX `idx_is_read`(`is_read` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '消息通知表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of notification
-- ----------------------------
INSERT INTO `notification` VALUES (1, '评议通知', '请参加2023年度团员评议工作', 1, '张明', '用户', 2, '李红', 3, '评议通知', 1, '团员评议', 0, NULL, 1, 1, NULL, '2025-03-25 10:53:51', '2025-03-25 10:53:51');
INSERT INTO `notification` VALUES (2, '评议通知', '请参加2023年度团员评议工作', 1, '张明', '用户', 4, '赵芳', 3, '评议通知', 1, '团员评议', 0, NULL, 1, 1, NULL, '2025-03-25 10:53:51', '2025-03-25 10:53:51');
INSERT INTO `notification` VALUES (3, '荣誉申请通知', '您已被提名申请\"优秀共青团员\"荣誉，请关注后续进展', 1, '张明', '用户', 2, '李红', 3, '荣誉申请', 1, '荣誉申请', 0, NULL, 1, 1, NULL, '2025-03-25 10:53:51', '2025-03-25 10:53:51');
INSERT INTO `notification` VALUES (4, '新的荣誉推荐等待审批', '组织ID为16的团支部推荐了一个荣誉申请，请及时审批。', 0, '系统', NULL, 1, NULL, NULL, '荣誉通知', 8, 'honor_application', 0, NULL, 1, 0, NULL, '2025-05-10 00:41:35', '2025-05-10 00:41:35');
INSERT INTO `notification` VALUES (5, '新的荣誉推荐等待审批', '组织ID为16的团支部推荐了一个荣誉申请，请及时审批。', 0, '系统', NULL, 1, NULL, NULL, '荣誉通知', 8, 'honor_application', 0, NULL, 1, 0, NULL, '2025-05-10 12:09:03', '2025-05-10 12:09:03');

-- ----------------------------
-- Table structure for notification_read_log
-- ----------------------------
DROP TABLE IF EXISTS `notification_read_log`;
CREATE TABLE `notification_read_log`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '序号',
  `notification_id` int NOT NULL COMMENT '通知ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `read_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '阅读时间',
  `read_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '阅读IP地址',
  `read_device` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '阅读设备信息',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_notification_id`(`notification_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '消息阅读记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of notification_read_log
-- ----------------------------

-- ----------------------------
-- Table structure for organization
-- ----------------------------
DROP TABLE IF EXISTS `organization`;
CREATE TABLE `organization`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '序号',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '组织名称',
  `full_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组织全称',
  `org_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组织类型',
  `unit_category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '单位类别',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '电子邮箱',
  `member_count` int NULL DEFAULT 0 COMMENT '团员人数',
  `city_member_count` int NULL DEFAULT 0 COMMENT '城市团员数',
  `secretary_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '书记姓名',
  `secretary_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '书记手机号码',
  `cadre_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '团干部姓名',
  `cadre_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '团干部手机号码',
  `parent_id` int NULL DEFAULT NULL COMMENT '上级组织ID',
  `level` int NULL DEFAULT 0 COMMENT '组织层级(0-校级,1-院级,2-班级等)',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组织层级路径(例如:1,2,3表示从顶层到当前)',
  `sort_order` int NULL DEFAULT 0 COMMENT '同级排序序号',
  `org_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组织编码',
  `is_leaf` tinyint(1) NULL DEFAULT 1 COMMENT '是否末端节点(0-否,1-是)',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态(0-禁用,1-启用)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_org_code`(`org_code` ASC) USING BTREE,
  INDEX `idx_name`(`name` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 45 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of organization
-- ----------------------------
INSERT INTO `organization` VALUES (3, '清华大学团委', '清华大学共青团委员会', '高校', '学校', '010-12345678', 'tuanwei@tsinghua.edu.cn', 5000, 3000, '张三', '13800000001', '李四', '13800000002', NULL, 0, '3', 1, 'THU001', 0, 1, '2025-03-25 10:53:51', '2025-05-02 12:07:47');
INSERT INTO `organization` VALUES (14, '计算机学院团委', '清华大学计算机学院团委', '高校', '学院', '010-12345679', 'cs_tuanwei@tsinghua.edu.cn', 800, 500, '王五', '13800000003', '赵六', '13800000004', 3, 1, '3,14', 1, 'THU002', 0, 1, '2025-03-25 10:53:51', '2025-05-02 12:07:47');
INSERT INTO `organization` VALUES (15, '计算机科学与技术系2101班团支部', '清华大学计算机学院计算机科学与技术系2101班团支部', '高校', '班级', '010-12345680', 'cs_class@tsinghua.edu.cn', 120, 80, '钱七', '13800000005', '孙八', '13800000006', 14, 2, '3,14,15', 1, 'THU003', 1, 1, '2025-03-25 10:53:51', '2025-05-04 13:11:10');
INSERT INTO `organization` VALUES (16, '电子工程学院团委', '清华大学电子工程学院团委', '高校', '学院', '010-12345681', 'ee_tuanwei@tsinghua.edu.cn', 5, 450, '周九', '13800000007', '吴十', '13800000008', 3, 1, '3,16', 2, 'THU004', 0, 1, '2025-03-25 10:53:51', '2025-05-24 00:06:33');
INSERT INTO `organization` VALUES (17, '数学科学学院团委', '清华大学数学科学学院团委', '高校', '学院', '010-12345682', 'math_tuanwei@tsinghua.edu.cn', 600, 400, '张学', '13800000009', NULL, NULL, 3, 1, '3,17', 3, 'THU005', 0, 1, '2025-05-04 12:51:41', '2025-05-04 13:15:44');
INSERT INTO `organization` VALUES (18, '电子信息学院团委', '清华大学电子信息学院团委', '高校', '学院', '010-12345683', 'ei_tuanwei@tsinghua.edu.cn', 550, 350, '李信', '13800000010', NULL, NULL, 3, 1, '3,18', 4, 'THU006', 0, 1, '2025-05-04 12:51:41', '2025-05-04 13:15:44');
INSERT INTO `organization` VALUES (19, '机械工程学院团委', '清华大学机械工程学院团委', '高校', '学院', '010-12345684', 'me_tuanwei@tsinghua.edu.cn', 650, 400, '王机', '13800000011', NULL, NULL, 3, 1, '3,19', 5, 'THU007', 0, 1, '2025-05-04 12:51:41', '2025-05-04 13:15:44');
INSERT INTO `organization` VALUES (30, '计算机科学与技术系2102班团支部', '清华大学计算机学院计算机科学与技术系2102班团支部', '高校', '班级', '010-12345701', 'cs_2102@tsinghua.edu.cn', 35, 30, '刘计', '13900002001', NULL, NULL, 14, 2, '3,14,30', 2, 'THU018', 1, 1, '2025-05-04 13:14:35', '2025-05-04 13:14:35');
INSERT INTO `organization` VALUES (31, '软件工程系2101班团支部', '清华大学计算机学院软件工程系2101班团支部', '高校', '班级', '010-12345702', 'se_2101@tsinghua.edu.cn', 34, 30, '孙软', '13900002002', NULL, NULL, 14, 2, '3,14,31', 3, 'THU019', 1, 1, '2025-05-04 13:14:35', '2025-05-04 13:14:35');
INSERT INTO `organization` VALUES (32, '软件工程系2102班团支部', '清华大学计算机学院软件工程系2102班团支部', '高校', '班级', '010-12345703', 'se_2102@tsinghua.edu.cn', 36, 32, '钱软', '13900002003', NULL, NULL, 14, 2, '3,14,32', 4, 'THU020', 1, 1, '2025-05-04 13:14:35', '2025-05-04 13:14:35');
INSERT INTO `organization` VALUES (33, '电子工程系2101班团支部', '清华大学电子工程学院电子工程系2101班团支部', '高校', '班级', '010-12345704', 'ee_2101@tsinghua.edu.cn', 38, 34, '周电', '13900002004', NULL, NULL, 16, 2, '3,16,33', 1, 'THU021', 1, 1, '2025-05-04 13:14:35', '2025-05-04 13:14:35');
INSERT INTO `organization` VALUES (34, '电子工程系2102班团支部', '清华大学电子工程学院电子工程系2102班团支部', '高校', '班级', '010-12345705', 'ee_2102@tsinghua.edu.cn', 36, 32, '吴电', '13900002005', NULL, NULL, 16, 2, '3,16,34', 2, 'THU022', 1, 1, '2025-05-04 13:14:35', '2025-05-04 13:14:35');
INSERT INTO `organization` VALUES (35, '微电子系2101班团支部', '清华大学电子工程学院微电子系2101班团支部', '高校', '班级', '010-12345706', 'me_2101@tsinghua.edu.cn', 35, 30, '郑微', '13900002006', NULL, NULL, 16, 2, '3,16,35', 3, 'THU023', 1, 1, '2025-05-04 13:14:35', '2025-05-04 13:14:35');
INSERT INTO `organization` VALUES (36, '数学系2101班团支部', '清华大学数学科学学院数学系2101班团支部', '高校', '班级', '010-12345707', 'math_2101@tsinghua.edu.cn', 34, 30, '王数', '13900002007', NULL, NULL, 17, 2, '3,17,36', 1, 'THU024', 1, 1, '2025-05-04 13:14:35', '2025-05-04 13:14:35');
INSERT INTO `organization` VALUES (37, '数学系2102班团支部', '清华大学数学科学学院数学系2102班团支部', '高校', '班级', '010-12345708', 'math_2102@tsinghua.edu.cn', 32, 28, '李数', '13900002008', NULL, NULL, 17, 2, '3,17,37', 2, 'THU025', 1, 1, '2025-05-04 13:14:35', '2025-05-04 13:14:35');
INSERT INTO `organization` VALUES (38, '应用数学系2101班团支部', '清华大学数学科学学院应用数学系2101班团支部', '高校', '班级', '010-12345709', 'am_2101@tsinghua.edu.cn', 33, 29, '张应', '13900002009', NULL, NULL, 17, 2, '3,17,38', 3, 'THU026', 1, 1, '2025-05-04 13:14:35', '2025-05-04 13:14:35');
INSERT INTO `organization` VALUES (39, '信息工程系2101班团支部', '清华大学电子信息学院信息工程系2101班团支部', '高校', '班级', '010-12345710', 'ie_2101@tsinghua.edu.cn', 37, 33, '陈信', '13900002010', NULL, NULL, 18, 2, '3,18,39', 1, 'THU027', 1, 1, '2025-05-04 13:14:35', '2025-05-04 13:14:35');
INSERT INTO `organization` VALUES (40, '信息工程系2102班团支部', '清华大学电子信息学院信息工程系2102班团支部', '高校', '班级', '010-12345711', 'ie_2102@tsinghua.edu.cn', 36, 32, '林信', '13900002011', NULL, NULL, 18, 2, '3,18,40', 2, 'THU028', 1, 1, '2025-05-04 13:14:35', '2025-05-04 13:14:35');
INSERT INTO `organization` VALUES (41, '通信工程系2101班团支部', '清华大学电子信息学院通信工程系2101班团支部', '高校', '班级', '010-12345712', 'ce_2101@tsinghua.edu.cn', 35, 31, '赵通', '13900002012', NULL, NULL, 18, 2, '3,18,41', 3, 'THU029', 1, 1, '2025-05-04 13:14:35', '2025-05-04 13:14:35');
INSERT INTO `organization` VALUES (42, '机械设计系2101班团支部', '清华大学机械工程学院机械设计系2101班团支部', '高校', '班级', '010-12345713', 'md_2101@tsinghua.edu.cn', 38, 34, '钱机', '13900002013', NULL, NULL, 19, 2, '3,19,42', 1, 'THU030', 1, 1, '2025-05-04 13:14:35', '2025-05-04 13:14:35');
INSERT INTO `organization` VALUES (43, '机械设计系2102班团支部', '清华大学机械工程学院机械设计系2102班团支部', '高校', '班级', '010-12345714', 'md_2102@tsinghua.edu.cn', 37, 33, '孙机', '13900002014', NULL, NULL, 19, 2, '3,19,43', 2, 'THU031', 1, 1, '2025-05-04 13:14:35', '2025-05-04 13:14:35');
INSERT INTO `organization` VALUES (44, '自动化系2101班团支部', '清华大学机械工程学院自动化系2101班团支部', '高校', '班级', '010-12345715', 'auto_2101@tsinghua.edu.cn', 36, 32, '周自', '13900002015', NULL, NULL, 19, 2, '3,19,44', 3, 'THU032', 1, 1, '2025-05-04 13:14:35', '2025-05-04 13:14:35');

-- ----------------------------
-- Table structure for organization_hierarchy
-- ----------------------------
DROP TABLE IF EXISTS `organization_hierarchy`;
CREATE TABLE `organization_hierarchy`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '序号',
  `organization_id` int NOT NULL COMMENT '组织ID',
  `parent_organization_id` int NULL DEFAULT NULL COMMENT '上级组织ID',
  `level` int NULL DEFAULT 0 COMMENT '组织层级',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组织层级路径',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_organization_id`(`organization_id` ASC) USING BTREE,
  INDEX `idx_parent_organization_id`(`parent_organization_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '组织层级关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of organization_hierarchy
-- ----------------------------
INSERT INTO `organization_hierarchy` VALUES (1, 3, NULL, 0, '3', '2025-05-04 13:19:43', '2025-05-04 13:19:43');
INSERT INTO `organization_hierarchy` VALUES (2, 14, 3, 1, '3,14', '2025-05-04 13:19:43', '2025-05-04 13:19:43');
INSERT INTO `organization_hierarchy` VALUES (3, 15, 14, 2, '3,14,15', '2025-05-04 13:19:43', '2025-05-04 13:19:43');
INSERT INTO `organization_hierarchy` VALUES (4, 16, 3, 1, '3,16', '2025-05-04 13:19:43', '2025-05-04 13:19:43');
INSERT INTO `organization_hierarchy` VALUES (5, 17, 3, 1, '3,17', '2025-05-04 13:19:43', '2025-05-04 13:19:43');
INSERT INTO `organization_hierarchy` VALUES (6, 18, 3, 1, '3,18', '2025-05-04 13:19:43', '2025-05-04 13:19:43');
INSERT INTO `organization_hierarchy` VALUES (7, 19, 3, 1, '3,19', '2025-05-04 13:19:43', '2025-05-04 13:19:43');
INSERT INTO `organization_hierarchy` VALUES (8, 30, 14, 2, '3,14,30', '2025-05-04 13:19:43', '2025-05-04 13:19:43');
INSERT INTO `organization_hierarchy` VALUES (9, 31, 14, 2, '3,14,31', '2025-05-04 13:19:43', '2025-05-04 13:19:43');
INSERT INTO `organization_hierarchy` VALUES (10, 32, 14, 2, '3,14,32', '2025-05-04 13:19:43', '2025-05-04 13:19:43');
INSERT INTO `organization_hierarchy` VALUES (11, 33, 16, 2, '3,16,33', '2025-05-04 13:19:43', '2025-05-04 13:19:43');
INSERT INTO `organization_hierarchy` VALUES (12, 34, 16, 2, '3,16,34', '2025-05-04 13:19:43', '2025-05-04 13:19:43');
INSERT INTO `organization_hierarchy` VALUES (13, 35, 16, 2, '3,16,35', '2025-05-04 13:19:43', '2025-05-04 13:19:43');
INSERT INTO `organization_hierarchy` VALUES (14, 36, 17, 2, '3,17,36', '2025-05-04 13:19:43', '2025-05-04 13:19:43');
INSERT INTO `organization_hierarchy` VALUES (15, 37, 17, 2, '3,17,37', '2025-05-04 13:19:43', '2025-05-04 13:19:43');
INSERT INTO `organization_hierarchy` VALUES (16, 38, 17, 2, '3,17,38', '2025-05-04 13:19:43', '2025-05-04 13:19:43');
INSERT INTO `organization_hierarchy` VALUES (17, 39, 18, 2, '3,18,39', '2025-05-04 13:19:43', '2025-05-04 13:19:43');
INSERT INTO `organization_hierarchy` VALUES (18, 40, 18, 2, '3,18,40', '2025-05-04 13:19:43', '2025-05-04 13:19:43');
INSERT INTO `organization_hierarchy` VALUES (19, 41, 18, 2, '3,18,41', '2025-05-04 13:19:43', '2025-05-04 13:19:43');
INSERT INTO `organization_hierarchy` VALUES (20, 42, 19, 2, '3,19,42', '2025-05-04 13:19:43', '2025-05-04 13:19:43');
INSERT INTO `organization_hierarchy` VALUES (21, 43, 19, 2, '3,19,43', '2025-05-04 13:19:43', '2025-05-04 13:19:43');
INSERT INTO `organization_hierarchy` VALUES (22, 44, 19, 2, '3,19,44', '2025-05-04 13:19:43', '2025-05-04 13:19:43');

-- ----------------------------
-- Table structure for role_permission
-- ----------------------------
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '序号',
  `role_id` int NOT NULL COMMENT '角色ID',
  `permission_id` int NOT NULL COMMENT '权限ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE,
  INDEX `idx_permission_id`(`permission_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色权限关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role_permission
-- ----------------------------
INSERT INTO `role_permission` VALUES (1, 5, 1, '2025-04-29 22:00:00', '2025-04-29 22:00:00');
INSERT INTO `role_permission` VALUES (2, 5, 2, '2025-04-29 22:00:00', '2025-04-29 22:00:00');
INSERT INTO `role_permission` VALUES (3, 5, 3, '2025-04-29 22:00:00', '2025-04-29 22:00:00');
INSERT INTO `role_permission` VALUES (4, 1, 1, '2025-04-29 22:00:00', '2025-04-29 22:00:00');
INSERT INTO `role_permission` VALUES (5, 1, 2, '2025-04-29 22:00:00', '2025-04-29 22:00:00');
INSERT INTO `role_permission` VALUES (6, 1, 3, '2025-04-29 22:00:00', '2025-04-29 22:00:00');
INSERT INTO `role_permission` VALUES (7, 5, 1, '2025-04-29 22:41:51', '2025-04-29 22:41:51');
INSERT INTO `role_permission` VALUES (8, 2, 4, '2025-05-01 15:40:07', '2025-05-01 15:40:07');
INSERT INTO `role_permission` VALUES (9, 4, 4, '2025-05-01 15:40:07', '2025-05-01 15:40:07');
INSERT INTO `role_permission` VALUES (10, 3, 5, '2025-05-01 15:40:07', '2025-05-01 15:40:07');
INSERT INTO `role_permission` VALUES (11, 3, 5, '2025-05-01 16:10:00', '2025-05-01 16:10:00');
INSERT INTO `role_permission` VALUES (12, 3, 6, '2025-05-01 16:10:00', '2025-05-01 16:10:00');
INSERT INTO `role_permission` VALUES (13, 4, 6, '2025-05-01 16:10:00', '2025-05-01 16:10:00');
INSERT INTO `role_permission` VALUES (14, 3, 6, '2025-05-01 16:17:46', '2025-05-01 16:17:46');
INSERT INTO `role_permission` VALUES (15, 4, 6, '2025-05-01 16:17:46', '2025-05-01 16:17:46');
INSERT INTO `role_permission` VALUES (17, 4, 7, '2025-05-01 19:12:03', '2025-05-01 19:12:03');
INSERT INTO `role_permission` VALUES (18, 3, 7, '2025-05-01 19:12:03', '2025-05-01 19:12:03');
INSERT INTO `role_permission` VALUES (19, 1, 7, '2025-05-01 19:12:03', '2025-05-01 19:12:03');
INSERT INTO `role_permission` VALUES (20, 4, 7, '2025-05-01 19:24:36', '2025-05-01 19:24:36');
INSERT INTO `role_permission` VALUES (21, 4, 7, '2025-05-01 19:55:22', '2025-05-01 19:55:22');
INSERT INTO `role_permission` VALUES (22, 4, 7, '2025-05-01 20:14:01', '2025-05-01 20:14:01');
INSERT INTO `role_permission` VALUES (23, 3, 4, '2025-05-01 21:52:17', '2025-05-01 21:52:17');
INSERT INTO `role_permission` VALUES (24, 3, 6, '2025-05-01 22:03:23', '2025-05-01 22:03:23');
INSERT INTO `role_permission` VALUES (25, 1, 8, '2025-05-06 12:24:00', '2025-05-06 12:24:00');
INSERT INTO `role_permission` VALUES (26, 1, 9, '2025-05-06 12:24:00', '2025-05-06 12:24:00');
INSERT INTO `role_permission` VALUES (27, 1, 10, '2025-05-06 12:24:00', '2025-05-06 12:24:00');
INSERT INTO `role_permission` VALUES (28, 3, 8, '2025-05-06 12:24:00', '2025-05-06 12:24:00');
INSERT INTO `role_permission` VALUES (29, 3, 10, '2025-05-06 12:24:00', '2025-05-06 12:24:00');
INSERT INTO `role_permission` VALUES (30, 6, 8, '2025-05-06 12:24:00', '2025-05-06 12:24:00');
INSERT INTO `role_permission` VALUES (31, 6, 10, '2025-05-06 12:24:00', '2025-05-06 12:24:00');
INSERT INTO `role_permission` VALUES (32, 4, 9, '2025-05-06 12:24:00', '2025-05-06 12:24:00');
INSERT INTO `role_permission` VALUES (33, 7, 9, '2025-05-06 12:24:00', '2025-05-06 12:24:00');

-- ----------------------------
-- Table structure for study_resource
-- ----------------------------
DROP TABLE IF EXISTS `study_resource`;
CREATE TABLE `study_resource`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '资源名称',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '资源描述',
  `file_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件URL',
  `file_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件名',
  `file_size` bigint NOT NULL COMMENT '文件大小(字节)',
  `format` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件格式',
  `category_id` int NOT NULL COMMENT '资源分类ID：1-思想理论，2-时政热点，3-团史学习，4-团章团规，5-入团教育，6-其他',
  `organization_id` int NOT NULL COMMENT '所属组织ID',
  `creator_id` int NOT NULL COMMENT '创建人ID',
  `creator_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人姓名',
  `downloads` int NULL DEFAULT 0 COMMENT '下载次数',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_organization`(`organization_id` ASC) USING BTREE,
  INDEX `idx_category`(`category_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '团课资源表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of study_resource
-- ----------------------------
INSERT INTO `study_resource` VALUES (1, '一堂跨越百年的团课', '通过回顾共青团百年历史，展示共青团的光辉历程和优良传统', '/一堂跨越百年的团课.mp4', '一堂跨越百年的团课', 26214400, 'MP4', 3, 16, 25, '李民', 25, '2025-05-07 17:50:14', '2025-05-07 17:50:14');
INSERT INTO `study_resource` VALUES (2, '做可堪大任的时代青年', '引导当代青年坚定理想信念，勇于担当时代责任，成为堪当民族复兴大任的时代新人', '/做可堪大任的时代青年.mp4', '做可堪大任的时代青年', 56623104, 'MP4', 1, 16, 25, '李民', 1, '2025-05-07 17:50:14', '2025-05-07 17:50:14');
INSERT INTO `study_resource` VALUES (3, '中国共青团简介', '介绍中国共青团的组织架构、历史沿革、性质宗旨等基本情况', '/中国共青团简介.pdf', '中国共青团简介', 248832, 'PDF', 3, 16, 25, '李民', 10, '2025-05-07 17:50:14', '2025-05-07 17:50:14');
INSERT INTO `study_resource` VALUES (4, '中国共产主义青年团章程', '最新版中国共产主义青年团章程全文，是团组织和团员必须遵循的基本规范', '/中国共产主义青年团章程.pdf', '中国共产主义青年团章程', 292864, 'PDF', 4, 16, 25, '李民', 2, '2025-05-07 17:50:14', '2025-05-07 17:50:14');
INSERT INTO `study_resource` VALUES (5, '中国共产主义青年团第十九次全国代表大会报告', '中国共产主义青年团第十九次全国代表大会重要文件', '/中国共产主义青年团第十九次全国代表大会报告.docx', '中国共产主义青年团第十九次全国代表大会报告', 53248, 'DOCX', 1, 15, 24, '赵芳', 1, '2025-05-08 09:26:00', '2025-05-08 09:26:00');
INSERT INTO `study_resource` VALUES (16, '上传功能测试文件', '上传功能测试文件，仅供测试使用！', '20250508141201_bc10fb46.pdf', '上传功能测试文件.pdf', 1203, 'PDF', 6, 16, 22, '李红', 0, '2025-05-08 14:12:01', '2025-05-08 14:12:01');
INSERT INTO `study_resource` VALUES (18, '第26次团课名单', '第26次团课名单，请大家仔细查看！', '20250508144241_966c6ac7.xlsx', '第26次团课名单.xlsx', 11378, 'XLSX', 6, 16, 22, '李红', 0, '2025-05-08 14:42:41', '2025-05-08 14:42:41');

-- ----------------------------
-- Table structure for system_log
-- ----------------------------
DROP TABLE IF EXISTS `system_log`;
CREATE TABLE `system_log`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '序号',
  `operation_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作类型',
  `business_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '业务类型',
  `business_id` int NULL DEFAULT NULL COMMENT '业务ID',
  `user_id` int NULL DEFAULT NULL COMMENT '操作用户ID',
  `user_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作用户姓名',
  `operation_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作IP',
  `operation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `operation_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '操作内容',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '操作状态(0-失败,1-成功)',
  `error_message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '错误信息',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_operation_type`(`operation_type` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_operation_time`(`operation_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统操作日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of system_log
-- ----------------------------
INSERT INTO `system_log` VALUES (1, '登录', '用户认证', NULL, 33, '刘明', '192.168.1.100', '2025-05-01 08:30:00', '用户登录系统', 1, NULL);
INSERT INTO `system_log` VALUES (2, '创建', '团员评议', 11, 33, '刘明', '192.168.1.100', '2025-05-01 09:00:00', '创建团员评议记录', 1, NULL);
INSERT INTO `system_log` VALUES (3, '更新', '团员评议', 11, 33, '刘明', '192.168.1.100', '2025-05-01 10:30:00', '更新团员评议状态为已完成', 1, NULL);
INSERT INTO `system_log` VALUES (4, '申请', '荣誉申请', 2, 33, '刘明', '192.168.1.100', '2025-05-01 11:00:00', '申请优秀共青团员荣誉', 1, NULL);
INSERT INTO `system_log` VALUES (5, '登录', '用户认证', NULL, 41, '钱强', '192.168.1.101', '2025-05-02 08:45:00', '用户登录系统', 1, NULL);
INSERT INTO `system_log` VALUES (6, '审批', '组织关系转接', 2, 41, '钱强', '192.168.1.101', '2025-05-02 09:30:00', '审批组织关系转入申请', 1, NULL);
INSERT INTO `system_log` VALUES (7, '登录', '用户认证', NULL, 43, '郑军', '192.168.1.102', '2025-05-03 08:50:00', '用户登录系统', 1, NULL);
INSERT INTO `system_log` VALUES (8, '创建', '团日活动', 58, 43, '郑军', '192.168.1.102', '2025-05-03 09:45:00', '创建团日活动记录', 1, NULL);

-- ----------------------------
-- Table structure for system_permission
-- ----------------------------
DROP TABLE IF EXISTS `system_permission`;
CREATE TABLE `system_permission`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `permission_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限名称',
  `permission_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限编码',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '权限描述',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态(0-禁用,1-启用)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_permission_code`(`permission_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_permission
-- ----------------------------
INSERT INTO `system_permission` VALUES (1, '查看转接申请列表', 'transfer:list', '查看团组织关系转接申请列表', 1, '2025-04-29 22:00:00', '2025-04-29 22:00:00');
INSERT INTO `system_permission` VALUES (2, '查看转接申请详情', 'transfer:detail', '查看团组织关系转接申请详情', 1, '2025-04-29 22:00:00', '2025-04-29 22:00:00');
INSERT INTO `system_permission` VALUES (3, '创建转接申请', 'transfer:create', '创建团组织关系转接申请', 1, '2025-04-29 22:00:00', '2025-04-29 22:00:00');
INSERT INTO `system_permission` VALUES (4, '转出申请审批', 'transfer:approve:out', '审批团员组织关系转出申请', 1, '2025-05-01 15:40:07', '2025-05-01 15:40:07');
INSERT INTO `system_permission` VALUES (5, '转入申请审批', 'transfer:approve:in', '审批团员组织关系转入申请', 1, '2025-05-01 15:40:07', '2025-05-01 15:40:07');
INSERT INTO `system_permission` VALUES (6, '查看审批列表', 'transfer:approval:list', '查看待审批的转接申请列表', 1, '2025-05-01 16:10:00', '2025-05-01 16:10:00');
INSERT INTO `system_permission` VALUES (7, '查看审批日志', 'transfer:view', '查看团组织关系转接申请的审批日志', 1, '2025-05-01 19:12:03', '2025-05-01 19:12:03');
INSERT INTO `system_permission` VALUES (8, '团籍注册批次管理', 'twosystem:register:batch', '管理团籍注册批次的创建、修改等操作', 1, '2025-05-06 12:24:00', '2025-05-06 12:24:00');
INSERT INTO `system_permission` VALUES (9, '团籍注册审批', 'twosystem:register:approve', '审批团员的团籍注册申请', 1, '2025-05-06 12:24:00', '2025-05-06 12:24:00');
INSERT INTO `system_permission` VALUES (10, '团籍注册统计', 'twosystem:register:statistics', '查看团籍注册统计数据', 1, '2025-05-06 12:24:00', '2025-05-06 12:24:00');

-- ----------------------------
-- Table structure for system_role
-- ----------------------------
DROP TABLE IF EXISTS `system_role`;
CREATE TABLE `system_role`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '序号',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色编码',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '角色描述',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态(0-禁用,1-启用)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_code`(`role_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of system_role
-- ----------------------------
INSERT INTO `system_role` VALUES (1, '系统管理员', 'ADMIN', '系统最高权限管理员', 1, '2025-03-25 10:53:51', '2025-03-25 10:53:51');
INSERT INTO `system_role` VALUES (2, '校团委管理员', 'SCHOOL_ADMIN', '校级团委管理员', 1, '2025-03-25 10:53:51', '2025-03-25 10:53:51');
INSERT INTO `system_role` VALUES (3, '院团委管理员', 'COLLEGE_ADMIN', '院级团委管理员', 1, '2025-03-25 10:53:51', '2025-03-25 10:53:51');
INSERT INTO `system_role` VALUES (4, '团支部管理员', 'BRANCH_ADMIN', '团支部管理员', 1, '2025-03-25 10:53:51', '2025-03-25 10:53:51');
INSERT INTO `system_role` VALUES (5, '普通团员', 'MEMBER', '普通团员用户', 1, '2025-03-25 10:53:51', '2025-03-25 10:53:51');
INSERT INTO `system_role` VALUES (6, '团委副书记', 'DEPUTY_COLLEGE_SECRETARY', '团委副书记，拥有与团委书记相同的系统权限', 1, '2025-05-05 13:22:38', '2025-05-05 13:22:38');
INSERT INTO `system_role` VALUES (7, '团支部副书记', 'DEPUTY_BRANCH_SECRETARY', '团支部副书记，拥有与团支书相同的系统权限', 1, '2025-05-05 13:22:38', '2025-05-05 13:22:38');

-- ----------------------------
-- Table structure for topic_reference
-- ----------------------------
DROP TABLE IF EXISTS `topic_reference`;
CREATE TABLE `topic_reference`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '序号',
  `topic_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '专题名称',
  `topic_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '专题类型(必学/自学)',
  `year` int NULL DEFAULT NULL COMMENT '年份',
  `is_required` tinyint(1) NULL DEFAULT 0 COMMENT '是否必选',
  `is_new` tinyint(1) NULL DEFAULT 0 COMMENT '是否新增',
  `for_school` tinyint(1) NULL DEFAULT 0 COMMENT '是否高校专题',
  `topic_category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '专题类别',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态(0-禁用,1-启用)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_topic_type`(`topic_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '专题参考表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of topic_reference
-- ----------------------------
INSERT INTO `topic_reference` VALUES (1, '学习贯彻党的二十大精神', '必学', 2023, 1, 1, 1, '思想政治', 1, '2025-03-25 10:53:51', '2025-03-25 10:53:51');
INSERT INTO `topic_reference` VALUES (2, '青年大学习', '必学', 2023, 1, 0, 1, '思想政治', 1, '2025-03-25 10:53:51', '2025-03-25 10:53:51');
INSERT INTO `topic_reference` VALUES (3, '传承五四精神', '自学', 2023, 0, 0, 1, '历史文化', 1, '2025-03-25 10:53:51', '2025-03-25 10:53:51');
INSERT INTO `topic_reference` VALUES (4, '团员素质拓展', '自学', 2023, 0, 1, 1, '技能培养', 1, '2025-03-25 10:53:51', '2025-03-25 10:53:51');

-- ----------------------------
-- Table structure for transfer
-- ----------------------------
DROP TABLE IF EXISTS `transfer`;
CREATE TABLE `transfer`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '转接ID',
  `transfer_user_id` int NOT NULL COMMENT '转接用户ID',
  `transfer_out_org_id` int NOT NULL COMMENT '转出组织ID',
  `transfer_in_org_id` int NOT NULL COMMENT '转入组织ID',
  `transfer_reason` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '转接理由',
  `transfer_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '转接备注',
  `application_time` datetime NOT NULL COMMENT '申请时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '状态(申请中/转出审批中/转入审批中/已通过/已拒绝)',
  `status_code` tinyint NOT NULL DEFAULT 0 COMMENT '状态码(0:申请中,1:转出审批中,2:转入审批中,3:已通过,4:已拒绝)',
  `expire_time` datetime NOT NULL COMMENT '过期时间(申请时间+3个月)',
  `out_approval_time` datetime NULL DEFAULT NULL COMMENT '转出审批时间',
  `out_approver_id` int NULL DEFAULT NULL COMMENT '转出审批人ID',
  `out_approved` tinyint(1) NULL DEFAULT NULL COMMENT '转出是否通过',
  `out_approval_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '转出审批备注',
  `in_approval_time` datetime NULL DEFAULT NULL COMMENT '转入审批时间',
  `in_approver_id` int NULL DEFAULT NULL COMMENT '转入审批人ID',
  `in_approved` tinyint(1) NULL DEFAULT NULL COMMENT '转入是否通过',
  `in_approval_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '转入审批备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_transfer_user_id`(`transfer_user_id` ASC) USING BTREE,
  INDEX `idx_transfer_out_org_id`(`transfer_out_org_id` ASC) USING BTREE,
  INDEX `idx_transfer_in_org_id`(`transfer_in_org_id` ASC) USING BTREE,
  INDEX `idx_application_time`(`application_time` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_status_code`(`status_code` ASC) USING BTREE,
  INDEX `idx_expire_time`(`expire_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '团员关系转接表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of transfer
-- ----------------------------
INSERT INTO `transfer` VALUES (1, 22, 3, 16, '升学', '', '2025-04-30 22:17:34', '已通过', 3, '2025-07-30 22:17:34', '2025-05-01 18:36:25', 21, 1, '', '2025-05-01 22:43:11', 25, 1, '', '2025-04-30 22:17:34', '2025-05-01 22:43:11');
INSERT INTO `transfer` VALUES (3, 35, 15, 33, '专业调整', '因专业兴趣调整至电子工程专业', '2025-04-01 10:00:00', '已通过', 3, '2025-07-01 10:00:00', '2025-04-05 14:00:00', 33, 1, '同意转出', '2025-04-10 15:00:00', 41, 1, '同意接收', '2025-04-01 10:00:00', '2025-04-10 15:00:00');
INSERT INTO `transfer` VALUES (4, 38, 30, 31, '专业调整', '因专业发展需要调整至软件工程专业', '2025-04-05 11:00:00', '已通过', 3, '2025-07-05 11:00:00', '2025-04-08 14:30:00', 37, 1, '同意转出', '2025-04-12 16:00:00', 39, 1, '同意接收', '2025-04-05 11:00:00', '2025-04-12 16:00:00');
INSERT INTO `transfer` VALUES (5, 44, 36, 39, '专业调整', '因个人发展需要调整至信息工程专业', '2025-04-15 09:30:00', '转入审批中', 2, '2025-07-15 09:30:00', '2025-04-18 15:00:00', 43, 1, '同意转出', NULL, NULL, NULL, NULL, '2025-04-15 09:30:00', '2025-04-18 15:00:00');
INSERT INTO `transfer` VALUES (6, 46, 39, 42, '专业调整', '因个人兴趣调整至机械设计专业', '2025-04-20 10:30:00', '申请中', 0, '2025-07-20 10:30:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2025-04-20 10:30:00', '2025-04-20 10:30:00');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '序号',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '姓名',
  `gender` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '性别',
  `card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '身份证号码',
  `ethnic` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '民族',
  `occupation` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '职业',
  `education_status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '在读状况',
  `education_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学历',
  `political_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '政治面貌',
  `join_league_date` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '入团时间',
  `join_party_date` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '入党时间',
  `league_number` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发展团员编号',
  `work_unit` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学习工作单位',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号码',
  `address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '户籍地址',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '电子邮箱',
  `qq` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'QQ号码',
  `wechat` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信',
  `weibo` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微博',
  `league_position` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '团内现任职务',
  `position_date` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '任现职年月',
  `position_method` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '任现职方式',
  `cadre_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '团干部性质',
  `is_party_committee` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '是否同级党(工)委(党支部)委员',
  `has_applied_party` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '是否递交入党申请书',
  `party_application_date` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '递交入党申请书时间',
  `is_volunteer` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '是否成为注册志愿者',
  `volunteer_date` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '成为注册志愿者的时间',
  `branch_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '团籍所在团支部',
  `organization_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '所在组织名称',
  `organization` int NULL DEFAULT NULL COMMENT '团组织序号',
  `has_league_application` tinyint(1) NULL DEFAULT 0 COMMENT '是否有入团志愿书',
  `pwd` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '密码',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `previous_organization` int NULL DEFAULT NULL COMMENT '上一个组织ID',
  `transfer_date` datetime NULL DEFAULT NULL COMMENT '最近一次转接时间',
  `transfer_count` int NULL DEFAULT 0 COMMENT '转接次数',
  `first_organization` int NULL DEFAULT NULL COMMENT '首次加入的组织ID',
  `join_date` datetime NULL DEFAULT NULL COMMENT '首次加入组织时间',
  `is_activated` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已激活(0-未激活,1-已激活)',
  `face_image_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '人脸图片存储路径',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_card`(`card` ASC) USING BTREE,
  INDEX `idx_name`(`name` ASC) USING BTREE,
  INDEX `idx_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_organization`(`organization` ASC) USING BTREE,
  INDEX `idx_previous_organization`(`previous_organization` ASC) USING BTREE,
  INDEX `idx_first_organization`(`first_organization` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 58 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (21, '张明', '男', '110101200001011234', '汉族', '学生', '在读', '本科', '共青团员', '2018-08-01', NULL, 'TH20180801001', '清华大学计算机学院', '13900000001', '北京市海淀区清华大学', 'zhangming@example.com', '123456789', 'zm905035', 'zm_weibo', '团支书', '2022-09-01', '选举', '学生干部', '否', '是', '2022-10-01', '是', '2021-05-01', NULL, '清华大学共青团委员会', 3, 1, '$2a$10$1BVZopNH7qCXTzI5VyPY4umW.P4H6ie0.r1NXV4GlvBPyPOLKTXJ2', '2025-03-25 10:53:51', '2025-05-23 12:12:52', NULL, '2024-05-04 00:00:00', 1, NULL, '2021-05-04 00:00:00', 1, NULL);
INSERT INTO `user` VALUES (22, '李红', '女', '110101200002022345', '汉族', '学生', '在读', '本科', '共青团员', '2018-09-01', NULL, 'TH20180901001', '清华大学电子工程学院团委', '13383356583', '北京市西城区文兴西街星光弄5号', '1362842536@qq.com', '234567890', 'lh891510', 'lh_weibo', '团委副书记', '2022-09-01', '委任', '学生干部', '否', '是', '2023-01-01', '是', '2021-06-01', NULL, '清华大学电子工程学院团委', 16, 1, '$2a$10$JqL8PB6paz17LbonmqSh7O.4mEkritmujLtyCE8JH/Zy1uPjd2/OG', '2025-03-25 10:53:51', '2025-05-23 21:09:39', NULL, '2024-05-04 00:00:00', 0, 16, '2020-05-04 00:00:00', 1, 'E:\\Hunter\\Project\\zhtj\\user-face\\faces\\217d07de-4dfc-40c2-b3bb-9c6daa1832c3.png');
INSERT INTO `user` VALUES (23, '王强', '男', '110101200003033456', '汉族', '学生', '在读', '研究生', '中共党员', '2017-09-01', '2022-07-01', 'TH20170901001', '清华大学电子工程学院', '13900000003', '北京市海淀区清华大学', 'wangqiang@example.com', '345678901', 'wq742445', 'wq_weibo', '团干部', '2021-09-01', '选举', '学生干部', '是', '是', '2021-01-01', '是', '2020-05-01', NULL, '清华大学电子工程学院团委', 16, 1, '$2a$10$1BVZopNH7qCXTzI5VyPY4umW.P4H6ie0.r1NXV4GlvBPyPOLKTXJ2', '2025-03-25 10:53:51', '2025-05-09 14:10:47', NULL, NULL, 0, 16, '2019-05-04 00:00:00', 1, NULL);
INSERT INTO `user` VALUES (24, '赵芳', '女', '110101200004044567', '满族', '学生', '在读', '本科', '中共党员', '2019-09-01', '2023-05-04', 'TH20190901001', '清华大学计算机学院', '13900000004', '北京市海淀区清华大学', 'zhaofang@example.com', '456789012', 'zf937693', 'zf_weibo', '团委书记', '2023-11-04', '选举', '教师干部', '是', '否', NULL, '否', NULL, NULL, '清华大学共青团委员会', 3, 1, '$2a$10$1BVZopNH7qCXTzI5VyPY4umW.P4H6ie0.r1NXV4GlvBPyPOLKTXJ2', '2025-03-25 10:53:51', '2025-05-09 14:10:47', NULL, NULL, 0, 3, '2022-05-04 00:00:00', 1, NULL);
INSERT INTO `user` VALUES (25, '李民', '男', '110101199001015678', '汉族', '教师', '工作', '研究生', '中共党员', '2006-09-01', '2022-05-04', 'TH20060901001', '清华大学电子工程学院', '13900001234', '北京市海淀区清华大学', 'limin@example.com', '288398230', 'lm561133', 'lm_weibo', '团委书记', '2023-11-04', '选举', '学生干部', '是', '否', NULL, '是', '2024-02-28', NULL, '清华大学电子工程学院团委', 16, 1, '$2a$10$1BVZopNH7qCXTzI5VyPY4umW.P4H6ie0.r1NXV4GlvBPyPOLKTXJ2', '2025-05-01 20:57:32', '2025-05-09 14:10:47', NULL, NULL, 0, 16, '2021-05-04 00:00:00', 1, NULL);
INSERT INTO `user` VALUES (26, '刘明', '男', '110101200108010011', '汉族', '学生', '在读', '本科', '中共党员', '2019-09-01', '2021-05-04', 'TH20190901020', '清华大学计算机学院', '13900003001', '北京市海淀区清华大学', 'lm@example.com', '629393014', 'lm792587', 'lm_weibo', '团委委员', '2023-11-04', '选举', '学生干部', '是', '否', NULL, '否', NULL, NULL, '清华大学计算机学院计算机科学与技术系2101班团支部', 15, 1, '$2a$10$1BVZopNH7qCXTzI5VyPY4umW.P4H6ie0.r1NXV4GlvBPyPOLKTXJ2', '2025-05-04 13:15:44', '2025-05-09 14:10:47', NULL, NULL, 0, 15, '2020-05-04 00:00:00', 1, NULL);
INSERT INTO `user` VALUES (27, '王雪', '女', '110101200109020022', '汉族', '学生', '在读', '本科', '共青团员', '2019-09-01', NULL, 'TH20190901021', '清华大学计算机学院', '13900003002', '北京市海淀区清华大学', 'wx@example.com', '878989520', 'wx379537', 'wx_weibo', '宣传委员', '2024-11-26', '选举', '学生干部', NULL, '是', '2024-04-19', '是', '2024-12-09', NULL, '清华大学计算机学院计算机科学与技术系2101班团支部', 15, 1, '$2a$10$1BVZopNH7qCXTzI5VyPY4umW.P4H6ie0.r1NXV4GlvBPyPOLKTXJ2', '2025-05-04 13:15:44', '2025-05-09 14:10:47', NULL, NULL, 0, 15, '2019-05-04 00:00:00', 1, NULL);
INSERT INTO `user` VALUES (28, '张伟', '男', '110101200107030033', '汉族', '学生', '在读', '本科', '共青团员', '2019-09-01', NULL, 'TH20190901022', '清华大学计算机学院', '13900003003', '北京市海淀区清华大学', 'zw@example.com', '086213007', 'zw319924', 'zw_weibo', '团员', NULL, NULL, '普通团员', NULL, '是', '2024-07-23', '是', '2022-10-06', NULL, '清华大学计算机学院计算机科学与技术系2101班团支部', 15, 1, '$2a$10$1BVZopNH7qCXTzI5VyPY4umW.P4H6ie0.r1NXV4GlvBPyPOLKTXJ2', '2025-05-04 13:15:44', '2025-05-09 14:10:47', NULL, '2024-05-04 00:00:00', 1, NULL, '2022-05-04 00:00:00', 1, NULL);
INSERT INTO `user` VALUES (29, '李小', '女', '110101200108040044', '汉族', '学生', '在读', '本科', '共青团员', '2019-09-01', NULL, 'TH20190901023', '清华大学计算机学院', '13900003004', '北京市海淀区清华大学', 'lx@example.com', '019771245', 'lx361008', 'lx_weibo', '团员', NULL, NULL, '普通团员', NULL, '是', '2025-02-05', '否', NULL, NULL, '清华大学计算机学院计算机科学与技术系2101班团支部', 15, 1, '$2a$10$1BVZopNH7qCXTzI5VyPY4umW.P4H6ie0.r1NXV4GlvBPyPOLKTXJ2', '2025-05-04 13:15:44', '2025-05-09 14:10:47', NULL, NULL, 0, 15, '2021-05-04 00:00:00', 1, NULL);
INSERT INTO `user` VALUES (30, '陈华', '男', '110101200105050055', '汉族', '学生', '在读', '本科', '中共党员', '2019-09-01', '2024-05-04', 'TH20190901024', '清华大学计算机学院', '13900003005', '北京市海淀区清华大学', 'ch@example.com', '430149114', 'ch745268', 'ch_weibo', '团支部委员', '2024-05-04', '选举', '学生干部', '是', '否', NULL, '是', '2023-07-09', NULL, '清华大学计算机学院计算机科学与技术系2102班团支部', 30, 1, '$2a$10$1BVZopNH7qCXTzI5VyPY4umW.P4H6ie0.r1NXV4GlvBPyPOLKTXJ2', '2025-05-04 13:15:44', '2025-05-09 14:10:47', NULL, '2024-05-04 00:00:00', 2, NULL, '2020-05-04 00:00:00', 1, NULL);
INSERT INTO `user` VALUES (31, '刘芳', '女', '110101200106060066', '汉族', '学生', '在读', '本科', '共青团员', '2019-09-01', NULL, 'TH20190901025', '清华大学计算机学院', '13900003006', '北京市海淀区清华大学', 'lf@example.com', '493830430', 'lf743319', 'lf_weibo', '组织委员', '2025-01-31', '任命', '学生干部', NULL, '否', NULL, '是', '2022-08-27', NULL, '清华大学计算机学院计算机科学与技术系2102班团支部', 30, 1, '$2a$10$1BVZopNH7qCXTzI5VyPY4umW.P4H6ie0.r1NXV4GlvBPyPOLKTXJ2', '2025-05-04 13:15:44', '2025-05-09 14:10:47', NULL, NULL, 0, 30, '2019-05-04 00:00:00', 1, NULL);
INSERT INTO `user` VALUES (32, '赵毅', '男', '110101200107070077', '汉族', '学生', '在读', '本科', '共青团员', '2019-09-01', NULL, 'TH20190901026', '清华大学计算机学院', '13900003007', '北京市海淀区清华大学', 'zy@example.com', '822145590', 'zy480789', 'zy_weibo', '团支书', '2024-04-05', '任命', '学生干部', NULL, '是', '2024-04-01', '否', NULL, NULL, '清华大学计算机学院软件工程系2101班团支部', 31, 1, '$2a$10$1BVZopNH7qCXTzI5VyPY4umW.P4H6ie0.r1NXV4GlvBPyPOLKTXJ2', '2025-05-04 13:15:44', '2025-05-09 14:10:47', NULL, NULL, 0, 31, '2022-05-04 00:00:00', 1, NULL);
INSERT INTO `user` VALUES (33, '孙敏', '女', '110101200108080088', '汉族', '学生', '在读', '本科', '共青团员', '2019-09-01', NULL, 'TH20190901027', '清华大学计算机学院', '13900003008', '北京市海淀区清华大学', 'sm@example.com', '678965499', 'sm973990', 'sm_weibo', '团员', NULL, NULL, '普通团员', NULL, '否', NULL, '否', NULL, NULL, '清华大学计算机学院软件工程系2101班团支部', 31, 1, '$2a$10$1BVZopNH7qCXTzI5VyPY4umW.P4H6ie0.r1NXV4GlvBPyPOLKTXJ2', '2025-05-04 13:15:44', '2025-05-09 14:10:47', NULL, NULL, 0, 31, '2021-05-04 00:00:00', 1, NULL);
INSERT INTO `user` VALUES (34, '钱强', '男', '110101200109090099', '汉族', '学生', '在读', '本科', '共青团员', '2019-09-01', NULL, 'TH20190901028', '清华大学电子工程学院', '13900003009', '北京市海淀区清华大学', 'qq@example.com', '301920269', 'qq627584', 'qq_weibo', '团支书', '2025-04-27', '选举', '学生干部', NULL, '是', '2024-07-31', '否', NULL, NULL, '清华大学电子工程学院电子工程系2101班团支部', 33, 1, '$2a$10$1BVZopNH7qCXTzI5VyPY4umW.P4H6ie0.r1NXV4GlvBPyPOLKTXJ2', '2025-05-04 13:15:44', '2025-05-09 14:10:47', NULL, NULL, 0, 33, '2020-05-04 00:00:00', 1, NULL);
INSERT INTO `user` VALUES (35, '吴佳', '女', '110101200110100110', '汉族', '学生', '在读', '本科', '共青团员', '2019-09-01', NULL, 'TH20190901029', '清华大学电子工程学院', '13900003010', '北京市海淀区清华大学', 'wj@example.com', '425801171', 'wj115951', 'wj_weibo', '团支部委员', '2024-05-04', '选举', '学生干部', '否', '是', '2024-11-04', '是', '2022-12-09', NULL, '清华大学电子工程学院电子工程系2101班团支部', 33, 1, '$2a$10$1BVZopNH7qCXTzI5VyPY4umW.P4H6ie0.r1NXV4GlvBPyPOLKTXJ2', '2025-05-04 13:15:44', '2025-05-09 14:10:47', NULL, '2024-05-04 00:00:00', 1, NULL, '2019-05-04 00:00:00', 1, NULL);
INSERT INTO `user` VALUES (36, '郑军', '男', '110101200111110121', '汉族', '学生', '在读', '本科', '共青团员', '2019-09-01', NULL, 'TH20190901030', '清华大学数学科学学院', '13900003011', '北京市海淀区清华大学', 'zj@example.com', '727868919', 'zj397003', 'zj_weibo', '团支书', '2024-03-17', '任命', '学生干部', NULL, '否', NULL, '是', '2024-09-19', NULL, '清华大学数学科学学院数学系2101班团支部', 36, 1, '$2a$10$1BVZopNH7qCXTzI5VyPY4umW.P4H6ie0.r1NXV4GlvBPyPOLKTXJ2', '2025-05-04 13:15:44', '2025-05-09 14:10:47', NULL, NULL, 0, 36, '2022-05-04 00:00:00', 1, NULL);
INSERT INTO `user` VALUES (37, '周萍', '女', '110101200112120132', '汉族', '学生', '在读', '本科', '共青团员', '2019-09-01', NULL, 'TH20190901031', '清华大学数学科学学院', '13900003012', '北京市海淀区清华大学', 'zp@example.com', '555241567', 'zp637162', 'zp_weibo', '团员', NULL, NULL, '普通团员', NULL, '是', '2024-06-29', '否', NULL, NULL, '清华大学数学科学学院数学系2101班团支部', 36, 1, '$2a$10$1BVZopNH7qCXTzI5VyPY4umW.P4H6ie0.r1NXV4GlvBPyPOLKTXJ2', '2025-05-04 13:15:44', '2025-05-09 14:10:47', NULL, NULL, 0, 36, '2021-05-04 00:00:00', 1, NULL);
INSERT INTO `user` VALUES (38, '吴刚', '男', '110101200201010143', '汉族', '学生', '在读', '本科', '共青团员', '2020-09-01', NULL, 'TH20200901032', '清华大学电子信息学院', '13900003013', '北京市海淀区清华大学', 'wg@example.com', '014234797', 'wg994802', 'wg_weibo', '团支书', '2025-04-23', '任命', '学生干部', NULL, '是', '2025-03-04', '是', '2025-02-21', NULL, '清华大学电子信息学院信息工程系2101班团支部', 39, 1, '$2a$10$1BVZopNH7qCXTzI5VyPY4umW.P4H6ie0.r1NXV4GlvBPyPOLKTXJ2', '2025-05-04 13:15:44', '2025-05-09 14:10:47', NULL, NULL, 0, 39, '2020-05-04 00:00:00', 1, NULL);
INSERT INTO `user` VALUES (39, '林丽', '女', '110101200202020154', '汉族', '学生', '在读', '本科', '共青团员', '2020-09-01', NULL, 'TH20200901033', '清华大学电子信息学院', '13900003014', '北京市海淀区清华大学', 'll@example.com', '206879925', 'll262524', 'll_weibo', '团员', NULL, NULL, '普通团员', NULL, '否', NULL, '否', NULL, NULL, '清华大学电子信息学院信息工程系2101班团支部', 39, 1, '$2a$10$1BVZopNH7qCXTzI5VyPY4umW.P4H6ie0.r1NXV4GlvBPyPOLKTXJ2', '2025-05-04 13:15:44', '2025-05-09 14:10:47', NULL, NULL, 0, 39, '2019-05-04 00:00:00', 1, NULL);
INSERT INTO `user` VALUES (40, '陈刚', '男', '110101200203030165', '汉族', '学生', '在读', '本科', '共青团员、中共预备党员', '2020-09-01', '2025-02-04', 'TH20200901034', '清华大学机械工程学院', '13900003015', '北京市海淀区清华大学', 'cg@example.com', '787370088', 'cg928217', 'cg_weibo', '团支部委员', '2024-05-04', '选举', '学生干部', '否', '否', NULL, '是', '2023-06-28', NULL, '清华大学机械工程学院机械设计系2101班团支部', 42, 1, '$2a$10$1BVZopNH7qCXTzI5VyPY4umW.P4H6ie0.r1NXV4GlvBPyPOLKTXJ2', '2025-05-04 13:15:44', '2025-05-09 14:10:47', NULL, NULL, 0, 42, '2022-05-04 00:00:00', 1, NULL);
INSERT INTO `user` VALUES (41, '赵丽', '女', '110101200204040176', '汉族', '学生', '在读', '本科', '共青团员', '2020-09-01', NULL, 'TH20200901035', '清华大学机械工程学院', '13900003016', '北京市海淀区清华大学', 'zl@example.com', '784340927', 'zl153510', 'zl_weibo', '团员', NULL, NULL, '普通团员', NULL, '是', '2024-02-27', '否', NULL, NULL, '清华大学机械工程学院机械设计系2101班团支部', 42, 1, '$2a$10$1BVZopNH7qCXTzI5VyPY4umW.P4H6ie0.r1NXV4GlvBPyPOLKTXJ2', '2025-05-04 13:15:44', '2025-05-09 14:10:47', NULL, NULL, 0, 42, '2021-05-04 00:00:00', 1, NULL);
INSERT INTO `user` VALUES (42, '吴佳佳', '女', '320123199003122222', '汉族', NULL, NULL, NULL, '共青团员', '2005-06-15', NULL, NULL, NULL, '13811112222', NULL, 'user42@example.com', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 33, 0, '$2a$10$1BVZopNH7qCXTzI5VyPY4umW.P4H6ie0.r1NXV4GlvBPyPOLKTXJ2', '2025-05-06 14:21:53', '2025-05-09 17:54:33', NULL, NULL, 0, NULL, NULL, 1, NULL);
INSERT INTO `user` VALUES (43, '郑军军', '男', '320123199104133333', '汉族', NULL, NULL, NULL, '共青团员', '2006-05-20', NULL, NULL, NULL, '13822223333', NULL, 'user43@example.com', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 36, 0, '$2a$10$1BVZopNH7qCXTzI5VyPY4umW.P4H6ie0.r1NXV4GlvBPyPOLKTXJ2', '2025-05-06 14:21:53', '2025-05-09 17:54:58', NULL, NULL, 0, NULL, NULL, 1, NULL);
INSERT INTO `user` VALUES (44, '周萍萍', '女', '320123199205144444', '汉族', NULL, NULL, NULL, '共青团员', '2007-06-10', NULL, NULL, NULL, '13833334444', NULL, 'user44@example.com', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 36, 0, '$2a$10$1BVZopNH7qCXTzI5VyPY4umW.P4H6ie0.r1NXV4GlvBPyPOLKTXJ2', '2025-05-06 14:21:53', '2025-05-09 17:54:43', NULL, NULL, 0, NULL, NULL, 1, NULL);
INSERT INTO `user` VALUES (45, '吴刚刚', '男', '320123199306155555', '汉族', NULL, NULL, NULL, '共青团员', '2008-07-15', NULL, NULL, NULL, '13844445555', NULL, 'user45@example.com', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 39, 0, '$2a$10$1BVZopNH7qCXTzI5VyPY4umW.P4H6ie0.r1NXV4GlvBPyPOLKTXJ2', '2025-05-06 14:21:53', '2025-05-09 17:54:38', NULL, NULL, 0, NULL, NULL, 1, NULL);
INSERT INTO `user` VALUES (46, '林丽丽', '男', '320123199407166666', '汉族', NULL, NULL, NULL, '共青团员', '2009-08-20', NULL, NULL, NULL, '13855556666', NULL, 'user46@example.com', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 39, 0, '$2a$10$1BVZopNH7qCXTzI5VyPY4umW.P4H6ie0.r1NXV4GlvBPyPOLKTXJ2', '2025-05-06 14:21:53', '2025-05-09 17:54:48', NULL, NULL, 0, NULL, NULL, 1, NULL);
INSERT INTO `user` VALUES (47, '陈刚刚', '男', '320123199508177777', '汉族', NULL, NULL, NULL, '共青团员', '2010-09-25', NULL, NULL, NULL, '13866667777', NULL, 'user47@example.com', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 42, 0, '$2a$10$1BVZopNH7qCXTzI5VyPY4umW.P4H6ie0.r1NXV4GlvBPyPOLKTXJ2', '2025-05-06 14:21:53', '2025-05-09 17:55:02', NULL, NULL, 0, NULL, NULL, 1, NULL);
INSERT INTO `user` VALUES (48, '赵丽丽', '女', '320123199609188888', '汉族', NULL, NULL, NULL, '共青团员', '2011-10-30', NULL, NULL, NULL, '13877778888', NULL, 'user48@example.com', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 42, 0, '$2a$10$1BVZopNH7qCXTzI5VyPY4umW.P4H6ie0.r1NXV4GlvBPyPOLKTXJ2', '2025-05-06 14:21:53', '2025-05-09 17:54:55', NULL, NULL, 0, NULL, NULL, 1, NULL);
INSERT INTO `user` VALUES (49, '单佳龙', '男', '130323200309023621', '汉族', '学生', '在读', '本科', '共青团员', '2017-09-01', NULL, 'TH20170901054', '清华大学电子工程学院团委', '15733596689', '北京市西城区文兴西街星光弄6号', 'sjl@example.com', '654612546', 'sjl65465', 'sjl_weibo', '团员', NULL, NULL, '普通团员', NULL, '是', '2021-01-01', '是', '2021-06-01', NULL, '清华大学电子工程学院团委', 16, 1, '$2a$10$oyFyqSCRFI4dSBzFA9V1nuyBJtetLkjm2bZAX6qukyQADg35kla/q', '2025-05-09 16:32:51', '2025-05-23 21:54:16', NULL, NULL, 0, NULL, NULL, 0, NULL);
INSERT INTO `user` VALUES (57, '蓝探', '男', '130323200309023611', NULL, NULL, NULL, NULL, NULL, '2024-05-01', NULL, NULL, NULL, '15733596689', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 16, 0, '$2a$10$ZniwlOLaKTUvKwrn3iLQWu5JZiQji/3TyduqtOO39jx7vuipBk5Ju', '2025-05-24 00:06:33', '2025-05-24 00:06:33', NULL, NULL, 0, NULL, NULL, 0, NULL);

-- ----------------------------
-- Table structure for user_organization_history
-- ----------------------------
DROP TABLE IF EXISTS `user_organization_history`;
CREATE TABLE `user_organization_history`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '序号',
  `user_id` int NOT NULL COMMENT '用户ID',
  `user_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户姓名',
  `previous_organization_id` int NULL DEFAULT NULL COMMENT '原组织ID',
  `previous_organization_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '原组织名称',
  `new_organization_id` int NULL DEFAULT NULL COMMENT '新组织ID',
  `new_organization_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '新组织名称',
  `change_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '变更类型(转入/转出/初始加入)',
  `change_reason` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '变更原因',
  `change_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '变更时间',
  `transfer_application_id` int NULL DEFAULT NULL COMMENT '关联的转接申请ID',
  `operator_id` int NULL DEFAULT NULL COMMENT '操作者ID',
  `operator_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作者姓名',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_previous_organization_id`(`previous_organization_id` ASC) USING BTREE,
  INDEX `idx_new_organization_id`(`new_organization_id` ASC) USING BTREE,
  INDEX `idx_change_time`(`change_time` ASC) USING BTREE,
  INDEX `idx_transfer_application_id`(`transfer_application_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户组织变更历史表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_organization_history
-- ----------------------------
INSERT INTO `user_organization_history` VALUES (1, 35, '张伟', 15, '计算机科学与技术系2101班团支部', 33, '电子工程系2101班团支部', '转出', '专业调整', '2025-04-10 15:00:00', 2, 41, '钱强', '2025-05-04 13:17:23');
INSERT INTO `user_organization_history` VALUES (2, 38, '刘芳', 30, '计算机科学与技术系2102班团支部', 31, '软件工程系2101班团支部', '转出', '专业调整', '2025-04-12 16:00:00', 3, 39, '赵毅', '2025-05-04 13:17:23');

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '序号',
  `user_id` int NOT NULL COMMENT '用户ID',
  `role_id` int NOT NULL COMMENT '角色ID',
  `organization_id` int NULL DEFAULT NULL COMMENT '组织ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE,
  INDEX `idx_organization_id`(`organization_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES (1, 1, 4, 3, '2025-03-25 10:53:51', '2025-03-25 10:53:51');
INSERT INTO `user_role` VALUES (2, 2, 5, 3, '2025-03-25 10:53:51', '2025-03-25 10:53:51');
INSERT INTO `user_role` VALUES (3, 3, 3, 4, '2025-03-25 10:53:51', '2025-03-25 10:53:51');
INSERT INTO `user_role` VALUES (4, 4, 5, 3, '2025-03-25 10:53:51', '2025-03-25 10:53:51');
INSERT INTO `user_role` VALUES (6, 22, 5, 3, '2025-04-29 22:39:50', '2025-04-29 22:39:50');
INSERT INTO `user_role` VALUES (7, 21, 4, 3, '2025-05-01 16:10:00', '2025-05-01 16:10:00');
INSERT INTO `user_role` VALUES (8, 25, 3, 16, '2025-05-01 20:57:32', '2025-05-01 20:57:32');
INSERT INTO `user_role` VALUES (9, 25, 3, 16, '2025-05-01 21:13:31', '2025-05-01 21:13:31');
INSERT INTO `user_role` VALUES (10, 33, 4, 15, '2025-05-04 13:15:44', '2025-05-04 13:15:44');
INSERT INTO `user_role` VALUES (11, 34, 4, 15, '2025-05-04 13:15:44', '2025-05-04 13:15:44');
INSERT INTO `user_role` VALUES (12, 35, 5, 15, '2025-05-04 13:15:44', '2025-05-04 13:15:44');
INSERT INTO `user_role` VALUES (13, 36, 5, 15, '2025-05-04 13:15:44', '2025-05-04 13:15:44');
INSERT INTO `user_role` VALUES (14, 37, 4, 30, '2025-05-04 13:15:44', '2025-05-04 13:15:44');
INSERT INTO `user_role` VALUES (15, 38, 4, 30, '2025-05-04 13:15:44', '2025-05-04 13:15:44');
INSERT INTO `user_role` VALUES (16, 39, 4, 31, '2025-05-04 13:15:44', '2025-05-04 13:15:44');
INSERT INTO `user_role` VALUES (17, 40, 5, 31, '2025-05-04 13:15:44', '2025-05-04 13:15:44');
INSERT INTO `user_role` VALUES (18, 41, 4, 33, '2025-05-04 13:15:44', '2025-05-04 13:15:44');
INSERT INTO `user_role` VALUES (19, 42, 5, 33, '2025-05-04 13:15:44', '2025-05-04 13:15:44');
INSERT INTO `user_role` VALUES (20, 43, 4, 36, '2025-05-04 13:15:44', '2025-05-04 13:15:44');
INSERT INTO `user_role` VALUES (21, 44, 5, 36, '2025-05-04 13:15:44', '2025-05-04 13:15:44');
INSERT INTO `user_role` VALUES (22, 45, 4, 39, '2025-05-04 13:15:44', '2025-05-04 13:15:44');
INSERT INTO `user_role` VALUES (23, 46, 5, 39, '2025-05-04 13:15:44', '2025-05-04 13:15:44');
INSERT INTO `user_role` VALUES (24, 47, 4, 42, '2025-05-04 13:15:44', '2025-05-04 13:15:44');
INSERT INTO `user_role` VALUES (25, 48, 5, 42, '2025-05-04 13:15:44', '2025-05-04 13:15:44');
INSERT INTO `user_role` VALUES (26, 22, 6, 16, '2025-05-05 13:22:38', '2025-05-05 13:22:38');

-- ----------------------------
-- Table structure for volunteer_service
-- ----------------------------
DROP TABLE IF EXISTS `volunteer_service`;
CREATE TABLE `volunteer_service`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '序号',
  `user_id` int NOT NULL COMMENT '团员ID',
  `user_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '团员姓名',
  `organization_id` int NULL DEFAULT NULL COMMENT '组织ID',
  `organization_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组织名称',
  `service_year` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '服务年度',
  `service_duration` int NULL DEFAULT NULL COMMENT '服务时长(小时)',
  `service_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '服务描述',
  `service_achievements` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '服务成就',
  `service_evaluation` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '服务评价',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_organization_id`(`organization_id` ASC) USING BTREE,
  INDEX `idx_service_year`(`service_year` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '志愿服务记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of volunteer_service
-- ----------------------------
INSERT INTO `volunteer_service` VALUES (1, 1, '张明', 3, '计算机科学与技术系团支部', '2023', 48, '参与校园环保志愿服务活动', '组织开展3次校园环保活动，获得好评', NULL, '2025-03-25 10:53:51', '2025-03-25 10:53:51');
INSERT INTO `volunteer_service` VALUES (2, 2, '李红', 3, '计算机科学与技术系团支部', '2023', 36, '参与社区关爱老人志愿服务', '定期走访社区孤寡老人，提供生活帮助', NULL, '2025-03-25 10:53:51', '2025-03-25 10:53:51');
INSERT INTO `volunteer_service` VALUES (3, 3, '王强', 4, '电子工程学院团委', '2023', 60, '担任科技支教志愿者', '为贫困地区学校提供科技课程教学支持', NULL, '2025-03-25 10:53:51', '2025-03-25 10:53:51');
INSERT INTO `volunteer_service` VALUES (4, 4, '赵芳', 3, '计算机科学与技术系团支部', '2023', 24, '参与校园公益活动', '协助组织校园公益跳蚤市场活动', NULL, '2025-03-25 10:53:51', '2025-03-25 10:53:51');
INSERT INTO `volunteer_service` VALUES (5, 33, '刘明', 15, '计算机科学与技术系2101班团支部', '2025', 40, '参与校园信息技术支持志愿服务', '为学校信息系统提供技术支持，协助解决师生技术问题', NULL, '2025-05-04 13:17:23', '2025-05-04 13:17:23');
INSERT INTO `volunteer_service` VALUES (6, 34, '王雪', 15, '计算机科学与技术系2101班团支部', '2025', 32, '参与编程教育志愿服务', '为社区青少年开展编程教育活动，普及计算机知识', NULL, '2025-05-04 13:17:23', '2025-05-04 13:17:23');
INSERT INTO `volunteer_service` VALUES (7, 35, '张伟', 15, '计算机科学与技术系2101班团支部', '2025', 30, '参与校园科技创新活动', '协助组织校园科技创新大赛，推广创新思维', NULL, '2025-05-04 13:17:23', '2025-05-04 13:17:23');
INSERT INTO `volunteer_service` VALUES (8, 41, '钱强', 33, '电子工程系2101班团支部', '2025', 36, '参与电子产品维修志愿服务', '为社区居民提供电子产品维修服务，推广环保理念', NULL, '2025-05-04 13:17:23', '2025-05-04 13:17:23');
INSERT INTO `volunteer_service` VALUES (9, 42, '吴佳', 33, '电子工程系2101班团支部', '2025', 28, '参与科技普及志愿活动', '在中小学开展科技普及讲座，激发青少年科技兴趣', NULL, '2025-05-04 13:17:23', '2025-05-04 13:17:23');
INSERT INTO `volunteer_service` VALUES (10, 43, '郑军', 36, '数学系2101班团支部', '2025', 32, '参与数学辅导志愿服务', '为贫困地区学生提供在线数学辅导，提高学习成绩', NULL, '2025-05-04 13:17:23', '2025-05-04 13:17:23');
INSERT INTO `volunteer_service` VALUES (11, 44, '周萍', 36, '数学系2101班团支部', '2025', 26, '参与社区文化活动志愿服务', '组织社区数学文化节活动，普及数学知识', NULL, '2025-05-04 13:17:23', '2025-05-04 13:17:23');
INSERT INTO `volunteer_service` VALUES (12, 47, '陈刚', 42, '机械设计系2101班团支部', '2025', 38, '参与环保机械设计志愿服务', '设计开发环保机械设备，参与校园环保项目', NULL, '2025-05-04 13:17:23', '2025-05-04 13:17:23');
INSERT INTO `volunteer_service` VALUES (13, 48, '赵丽', 42, '机械设计系2101班团支部', '2025', 30, '参与社区科技服务志愿活动', '为社区开展机械知识普及活动，推广科学知识', NULL, '2025-05-04 13:17:23', '2025-05-04 13:17:23');

-- ----------------------------
-- Procedure structure for RepairOrganizationTree
-- ----------------------------
DROP PROCEDURE IF EXISTS `RepairOrganizationTree`;
delimiter ;;
CREATE PROCEDURE `RepairOrganizationTree`()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE org_id INT;
    DECLARE org_parent_id INT;
    DECLARE org_level INT;
    DECLARE org_path VARCHAR(255);
    DECLARE cur CURSOR FOR SELECT id FROM organization ORDER BY id;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    -- 清空层级表
    TRUNCATE TABLE organization_hierarchy;
    
    -- 先处理顶级组织
    UPDATE organization SET level = 0, path = CAST(id AS CHAR) WHERE parent_id IS NULL;
    
    -- 递归更新子组织
    OPEN cur;
    read_loop: LOOP
        FETCH cur INTO org_id;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- 获取父组织ID
        SELECT parent_id INTO org_parent_id FROM organization WHERE id = org_id;
        
        IF org_parent_id IS NOT NULL THEN
            -- 获取父组织的level和path
            SELECT level, path INTO org_level, org_path FROM organization WHERE id = org_parent_id;
            
            -- 更新当前组织的level和path
            SET org_level = org_level + 1;
            SET org_path = CONCAT(org_path, ',', org_id);
            
            UPDATE organization 
            SET level = org_level, 
                path = org_path
            WHERE id = org_id;
            
            -- 同时更新父组织为非叶子节点
            UPDATE organization SET is_leaf = 0 WHERE id = org_parent_id;
        END IF;
        
        -- 同步数据到层级表
        INSERT INTO organization_hierarchy
            (organization_id, parent_organization_id, level, path, create_time, update_time)
        VALUES
            (org_id, org_parent_id, 
             (SELECT level FROM organization WHERE id = org_id),
             (SELECT path FROM organization WHERE id = org_id),
             NOW(), NOW());
    END LOOP;
    CLOSE cur;
    
    -- 更新叶子节点状态
    UPDATE organization o
    SET o.is_leaf = (SELECT COUNT(*) = 0 FROM organization WHERE parent_id = o.id);
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
