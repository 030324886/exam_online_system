-- 创建数据库
CREATE DATABASE IF NOT EXISTS exam_online DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE exam_online;

-- =============================================
-- 1. 用户表（你已创建，建议补充字段）
-- =============================================
CREATE TABLE `user` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `username` varchar(32) NOT NULL,
                        `password` varchar(64) NOT NULL,
                        `role` tinyint NOT NULL COMMENT '1-学生 2-教师 3-管理员',
                        `name` varchar(32) NOT NULL,
                        `class_id` bigint DEFAULT NULL COMMENT '所属班级ID',
                        `status` tinyint DEFAULT 1 COMMENT '1-正常 0-禁用',
                        `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- =============================================
-- 2. 班级表
-- =============================================
CREATE TABLE `class` (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `class_name` varchar(100) NOT NULL COMMENT '班级名称',
                         `teacher_id` bigint DEFAULT NULL COMMENT '班主任ID',
                         `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级表';

-- =============================================
-- 3. 用户-班级关联表（支持学生多班级）
-- =============================================
CREATE TABLE `user_class` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `user_id` bigint NOT NULL COMMENT '用户ID',
                              `class_id` bigint NOT NULL COMMENT '班级ID',
                              PRIMARY KEY (`id`),
                              KEY `idx_user_id` (`user_id`),
                              KEY `idx_class_id` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户班级关联表';

-- =============================================
-- 4. 题目表
-- =============================================
CREATE TABLE `question` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `type` tinyint NOT NULL COMMENT '1-单选 2-多选 3-填空 4-简答',
                            `content` text NOT NULL COMMENT '题目内容',
                            `options` json DEFAULT NULL COMMENT '选项（JSON数组）',
                            `answer` text NOT NULL COMMENT '正确答案',
                            `analysis` text COMMENT '答案解析',
                            `knowledge_point` varchar(100) DEFAULT NULL COMMENT '知识点',
                            `difficulty` tinyint DEFAULT 3 COMMENT '难度 1-5',
                            `creator_id` bigint DEFAULT NULL COMMENT '出题人ID',
                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目表';

-- =============================================
-- 5. 试卷表
-- =============================================
CREATE TABLE `paper` (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `title` varchar(200) NOT NULL COMMENT '试卷标题',
                         `total_score` int NOT NULL DEFAULT 0 COMMENT '总分',
                         `duration_minutes` int NOT NULL DEFAULT 60 COMMENT '考试时长（分钟）',
                         `creator_id` bigint DEFAULT NULL COMMENT '创建人ID',
                         `status` tinyint DEFAULT 1 COMMENT '1-草稿 2-已发布 3-已关闭',
                         `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷表';

-- =============================================
-- 6. 试卷-题目关联表
-- =============================================
CREATE TABLE `paper_question` (
                                  `id` bigint NOT NULL AUTO_INCREMENT,
                                  `paper_id` bigint NOT NULL COMMENT '试卷ID',
                                  `question_id` bigint NOT NULL COMMENT '题目ID',
                                  `score` int NOT NULL DEFAULT 0 COMMENT '该题分值',
                                  `sort_order` int DEFAULT 0 COMMENT '排序',
                                  PRIMARY KEY (`id`),
                                  KEY `idx_paper_id` (`paper_id`),
                                  KEY `idx_question_id` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷题目关联表';

-- =============================================
-- 7. 答题记录表
-- =============================================
CREATE TABLE `answer_record` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `user_id` bigint NOT NULL COMMENT '学生ID',
                                 `paper_id` bigint NOT NULL COMMENT '试卷ID',
                                 `question_id` bigint NOT NULL COMMENT '题目ID',
                                 `answer` text COMMENT '学生答案',
                                 `is_correct` tinyint DEFAULT 0 COMMENT '0-错误 1-正确',
                                 `score` int DEFAULT 0 COMMENT '得分',
                                 `submit_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`id`),
                                 KEY `idx_user_id` (`user_id`),
                                 KEY `idx_paper_id` (`paper_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='答题记录表';

-- =============================================
-- 8. 错题本表
-- =============================================
CREATE TABLE `wrong_question` (
                                  `id` bigint NOT NULL AUTO_INCREMENT,
                                  `user_id` bigint NOT NULL COMMENT '学生ID',
                                  `question_id` bigint NOT NULL COMMENT '题目ID',
                                  `knowledge_point` varchar(100) DEFAULT NULL COMMENT '知识点',
                                  `wrong_count` int DEFAULT 1 COMMENT '错题次数',
                                  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                  PRIMARY KEY (`id`),
                                  UNIQUE KEY `uk_user_question` (`user_id`, `question_id`),
                                  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='错题本表';

-- =============================================
-- 9. 消息通知表
-- =============================================
CREATE TABLE `message` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `to_user_id` bigint NOT NULL COMMENT '接收人ID',
                           `type` varchar(50) DEFAULT NULL COMMENT '消息类型',
                           `content` text COMMENT '消息内容',
                           `is_read` tinyint DEFAULT 0 COMMENT '0-未读 1-已读',
                           `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                           PRIMARY KEY (`id`),
                           KEY `idx_to_user_id` (`to_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息通知表';

-- =============================================
-- 测试数据（可选）
-- =============================================

-- 插入测试用户
INSERT INTO `user` (username, password, role, name) VALUES
                                                        ('stu1', '123456', 1, '张三'),
                                                        ('stu2', '123456', 1, '李四'),
                                                        ('teacher1', '123456', 2, '李老师'),
                                                        ('admin1', '123456', 3, '王教务');

-- 插入测试班级
INSERT INTO `class` (class_name, teacher_id) VALUES
    ('计算机1班', 3);

-- 插入测试题目
INSERT INTO `question` (type, content, options, answer, knowledge_point) VALUES
                                                                             (1, 'Java中main方法的参数是？', '["String[] args","int[] args","String args","void"]', 'A', 'Java基础'),
                                                                             (3, 'Spring Boot的默认端口是？', NULL, '8080', 'Spring框架');

-- 插入测试试卷
INSERT INTO `paper` (title, total_score, duration_minutes, creator_id, status) VALUES
    ('期中模拟卷', 100, 60, 3, 2);