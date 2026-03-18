-- 自习室座位预约系统 - 数据库初始化脚本
-- 基于 docs/database.md 设计

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for users
-- ----------------------------
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户主键',
  `username` varchar(50) NOT NULL COMMENT '用户名/登录账号',
  `password` varchar(255) NOT NULL COMMENT '加密后的密码',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像 URL',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `role` tinyint DEFAULT '2' COMMENT '角色 (1: 管理员, 2: 普通用户)',
  `status` tinyint DEFAULT '1' COMMENT '状态 (0: 禁用, 1: 正常)',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone` (`phone`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- Table structure for study_rooms
-- ----------------------------
CREATE TABLE IF NOT EXISTS `study_rooms` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自习室主键',
  `name` varchar(100) NOT NULL COMMENT '自习室名称',
  `location` varchar(255) DEFAULT NULL COMMENT '地理位置/详细地址',
  `floor` int DEFAULT NULL COMMENT '楼层',
  `opening_time` time DEFAULT NULL COMMENT '每日开放时间',
  `closing_time` time DEFAULT NULL COMMENT '每日关闭时间',
  `description` text COMMENT '自习室简介',
  `status` tinyint DEFAULT '1' COMMENT '运营状态 (0: 维护中/关闭, 1: 开放)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自习室表';

-- ----------------------------
-- Table structure for seats
-- ----------------------------
CREATE TABLE IF NOT EXISTS `seats` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '座位主键',
  `room_id` bigint NOT NULL COMMENT '所属自习室 ID',
  `seat_number` varchar(20) NOT NULL COMMENT '座位编号',
  `position_x` int DEFAULT NULL COMMENT '可视化坐标 X',
  `position_y` int DEFAULT NULL COMMENT '可视化坐标 Y',
  `has_power` tinyint(1) DEFAULT '0' COMMENT '是否有电源插座',
  `is_window` tinyint(1) DEFAULT '0' COMMENT '是否靠窗',
  `status` tinyint DEFAULT '1' COMMENT '当前状态 (0: 维修, 1: 可用, 2: 占用)',
  PRIMARY KEY (`id`),
  KEY `idx_room_id` (`room_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='座位表';

-- ----------------------------
-- Table structure for reservations
-- ----------------------------
CREATE TABLE IF NOT EXISTS `reservations` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预约主键',
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `seat_id` bigint NOT NULL COMMENT '座位 ID',
  `start_time` datetime NOT NULL COMMENT '预约开始时间',
  `end_time` datetime NOT NULL COMMENT '预期结束时间',
  `check_in_time` datetime DEFAULT NULL COMMENT '实际签到时间',
  `status` tinyint DEFAULT '1' COMMENT '状态 (0:取消, 1:待使用, 2:使用中, 3:已完成, 4:违约)',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_status` (`user_id`,`status`),
  KEY `idx_seat_id` (`seat_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约流水表';

-- ----------------------------
-- Table structure for violations
-- ----------------------------
CREATE TABLE IF NOT EXISTS `violations` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录 ID',
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `reservation_id` bigint DEFAULT NULL COMMENT '关联的预约 ID',
  `reason` varchar(255) DEFAULT NULL COMMENT '违约原因',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='违约记录表';

-- ----------------------------
-- Table structure for announcements
-- ----------------------------
CREATE TABLE IF NOT EXISTS `announcements` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公告 ID',
  `title` varchar(255) NOT NULL COMMENT '标题',
  `content` text COMMENT '内容',
  `status` tinyint DEFAULT '1' COMMENT '状态 (0: 隐藏, 1: 发布)',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- ----------------------------
-- Table structure for carousels
-- ----------------------------
CREATE TABLE IF NOT EXISTS `carousels` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '轮播图 ID',
  `image_url` varchar(255) NOT NULL COMMENT '图片 URL',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `status` tinyint DEFAULT '1' COMMENT '启用状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播图表';

-- ----------------------------
-- Table structure for favorites
-- ----------------------------
CREATE TABLE IF NOT EXISTS `favorites` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `seat_id` bigint NOT NULL COMMENT '座位 ID',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

-- ----------------------------
-- Table structure for study_time_stats
-- ----------------------------
CREATE TABLE IF NOT EXISTS `study_time_stats` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `total_minutes` int DEFAULT '0' COMMENT '当日学习总时长',
  PRIMARY KEY (`id`),
  KEY `idx_user_date` (`user_id`,`stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习时长统计表';

SET FOREIGN_KEY_CHECKS = 1;
