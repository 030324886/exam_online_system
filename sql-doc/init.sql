CREATE DATABASE IF NOT EXISTS exam_online DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE exam_online;

-- 用户表
CREATE TABLE `user` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `username` varchar(32) NOT NULL,
                        `password` varchar(64) NOT NULL,
                        `role` tinyint NOT NULL COMMENT '1-学生 2-教师 3-管理员',
                        `name` varchar(32) NOT NULL,
                        `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;