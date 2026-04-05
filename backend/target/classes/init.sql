-- 数据库初始化脚本

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `phone` varchar(20) DEFAULT NULL COMMENT '电话',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `role` int(11) DEFAULT '2' COMMENT '角色 (1: 管理员, 2: 普通用户)',
  `status` int(11) DEFAULT '1' COMMENT '状态 (0: 禁用, 1: 正常)',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for study_rooms
-- ----------------------------
DROP TABLE IF EXISTS `study_rooms`;
CREATE TABLE `study_rooms` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `location` varchar(255) NOT NULL,
  `floor` int(11) DEFAULT NULL,
  `opening_time` time DEFAULT '08:00:00',
  `closing_time` time DEFAULT '22:00:00',
  `description` text,
  `status` int(11) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for seats
-- ----------------------------
DROP TABLE IF EXISTS `seats`;
CREATE TABLE `seats` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `room_id` bigint(20) NOT NULL,
  `seat_number` varchar(20) NOT NULL,
  `position_x` int(11) DEFAULT NULL,
  `position_y` int(11) DEFAULT NULL,
  `has_power` tinyint(1) DEFAULT '0',
  `is_window` tinyint(1) DEFAULT '0',
  `status` int(11) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for reservations
-- ----------------------------
DROP TABLE IF EXISTS `reservations`;
CREATE TABLE `reservations` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `seat_id` bigint(20) NOT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `check_in_time` datetime DEFAULT NULL,
  `status` int(11) DEFAULT '1' COMMENT '0:取消, 1:待使用, 2:使用中, 3:已完成, 4:违约',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for announcements
-- ----------------------------
DROP TABLE IF EXISTS `announcements`;
CREATE TABLE `announcements` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `content` text,
  `status` int(11) DEFAULT '1',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for carousels
-- ----------------------------
DROP TABLE IF EXISTS `carousels`;
CREATE TABLE `carousels` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `image_url` varchar(255) NOT NULL,
  `sort_order` int(11) DEFAULT '0',
  `status` int(11) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for favorites
-- ----------------------------
DROP TABLE IF EXISTS `favorites`;
CREATE TABLE `favorites` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `seat_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for study_time_stats
-- ----------------------------
DROP TABLE IF EXISTS `study_time_stats`;
CREATE TABLE `study_time_stats` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `stat_date` date NOT NULL,
  `total_minutes` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for violations
-- ----------------------------
DROP TABLE IF EXISTS `violations`;
CREATE TABLE `violations` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `reservation_id` bigint(20) NOT NULL,
  `reason` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records
-- ----------------------------
-- 默认密码：123456 (BCrypt加密)
INSERT INTO `users` (`username`, `password`, `nickname`, `role`, `status`) VALUES ('admin', '$2a$10$7.8d9F7rU/K6m8l5p2r2eOu8vX.yX7s4E1v.f1P/7G7f7G7f7G7f7', '管理员', 1, 1);
INSERT INTO `users` (`username`, `password`, `nickname`, `role`, `status`) VALUES ('user', '$2a$10$7.8d9F7rU/K6m8l5p2r2eOu8vX.yX7s4E1v.f1P/7G7f7G7f7G7f7', '普通用户', 2, 1);

-- 示例自习室
INSERT INTO `study_rooms` (`name`, `location`, `floor`, `opening_time`, `closing_time`, `description`, `status`) VALUES ('A栋一楼自习室', 'A栋101', 1, '08:00:00', '22:00:00', '安静明亮，带窗户', 1);
INSERT INTO `study_rooms` (`name`, `location`, `floor`, `opening_time`, `closing_time`, `description`, `status`) VALUES ('图书馆三楼考研专区', '图书馆三楼', 3, '07:30:00', '23:00:00', '考研冲刺，禁止讨论', 1);

-- 示例座位
INSERT INTO `seats` (`room_id`, `seat_number`, `position_x`, `position_y`, `has_power`, `is_window`, `status`) VALUES (1, 'A101-01', 1, 1, 1, 1, 1);
INSERT INTO `seats` (`room_id`, `seat_number`, `position_x`, `position_y`, `has_power`, `is_window`, `status`) VALUES (1, 'A101-02', 1, 2, 1, 0, 1);
INSERT INTO `seats` (`room_id`, `seat_number`, `position_x`, `position_y`, `has_power`, `is_window`, `status`) VALUES (2, 'L301-01', 1, 1, 1, 1, 1);

SET FOREIGN_KEY_CHECKS = 1;
