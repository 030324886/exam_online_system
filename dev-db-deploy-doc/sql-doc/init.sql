-- =============================================
-- 在线考试系统 - 数据库初始化脚本（完整版）
-- =============================================
CREATE DATABASE IF NOT EXISTS exam_online DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE exam_online;

-- =============================================
-- 1. 用户表
-- =============================================
DROP TABLE IF EXISTS `message`;
DROP TABLE IF EXISTS `wrong_question`;
DROP TABLE IF EXISTS `answer_record`;
DROP TABLE IF EXISTS `paper_question`;
DROP TABLE IF EXISTS `paper`;
DROP TABLE IF EXISTS `question`;
DROP TABLE IF EXISTS `user_class`;
DROP TABLE IF EXISTS `class`;
DROP TABLE IF EXISTS `user`;

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
-- 3. 用户-班级关联表
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
-- =============================================
-- 测试数据
-- =============================================
-- =============================================

-- =============================================
-- 1. 用户数据
-- =============================================
INSERT INTO `user` (username, password, role, name, class_id, status) VALUES
('stu1', '123456', 1, '张三', 1, 1),
('stu2', '123456', 1, '李四', 1, 1),
('stu3', '123456', 1, '王五', 1, 1),
('stu4', '123456', 1, '赵六', 2, 1),
('stu5', '123456', 1, '陈七', 2, 1),
('stu6', '123456', 1, '刘八', 2, 1),
('teacher1', '123456', 2, '李老师', NULL, 1),
('teacher2', '123456', 2, '张老师', NULL, 1),
('admin1', '123456', 3, '王教务', NULL, 1);

-- =============================================
-- 2. 班级数据
-- =============================================
INSERT INTO `class` (class_name, teacher_id) VALUES
('计算机1班', 7),
('计算机2班', 7),
('软件工程1班', 8);

-- =============================================
-- 3. 用户-班级关联
-- =============================================
INSERT INTO `user_class` (user_id, class_id) VALUES
(1, 1), (2, 1), (3, 1),
(4, 2), (5, 2), (6, 2);

-- =============================================
-- 4. 题目数据（20道题，覆盖各类型、各知识点）
-- =============================================

-- ---- Java基础（单选） ----
INSERT INTO `question` (type, content, options, answer, analysis, knowledge_point, difficulty, creator_id) VALUES
(1, 'Java中main方法的参数是？', '["String[] args","int[] args","String args","void"]', 'A', 'Java程序的入口是public static void main(String[] args)，参数必须是String数组。', 'Java基础', 1, 7),
(1, '以下哪个不是Java的基本数据类型？', '["int","boolean","String","char"]', 'C', 'String是引用类型，不是基本数据类型。基本数据类型包括byte、short、int、long、float、double、boolean、char。', 'Java基础', 1, 7),
(1, 'Java中继承使用的关键字是？', '["implements","extends","super","this"]', 'B', 'Java中使用extends关键字实现继承，implements用于实现接口。', 'Java基础', 2, 7),
(1, '下列哪个是Java的访问修饰符？', '["private","static","final","abstract"]', 'A', '访问修饰符包括private、default、protected、public，用于控制访问权限。', 'Java基础', 2, 7),
(1, 'ArrayList和LinkedList的区别描述正确的是？', '["ArrayList是链表结构","LinkedList是数组结构","ArrayList查询快增删慢","LinkedList查询快增删慢"]', 'C', 'ArrayList基于数组实现，查询快（O(1)）但增删慢（O(n)）；LinkedList基于链表实现，增删快（O(1)）但查询慢（O(n)）。', 'Java基础', 3, 7);

-- ---- Vue3（单选） ----
INSERT INTO `question` (type, content, options, answer, analysis, knowledge_point, difficulty, creator_id) VALUES
(1, 'Vue3中创建响应式数据使用哪个API？', '["ref()","reactive()","两者都可以","computed()"]', 'C', 'ref()用于基本类型和对象，reactive()只能用于对象，两者都可以创建响应式数据。', 'Vue3', 2, 7),
(1, 'Vue3的setup函数在什么时候执行？', '["beforeCreate之前","created之后","mounted之后","updated之后"]', 'A', 'setup函数在组件创建之前执行，即在beforeCreate生命周期钩子之前执行。', 'Vue3', 2, 7),
(1, 'Vue3中<script setup>语法糖的特点正确的是？', '["需要return暴露变量","没有this指向","只能用选项式API","不支持TypeScript"]', 'B', '<script setup>中无需return即可暴露变量和方法，且没有this指向。', 'Vue3', 2, 7),
(1, 'Vue3的Teleport组件的作用是？', '["传送数据","将内容渲染到指定DOM节点","实现动画","路由跳转"]', 'B', 'Teleport组件可以将子组件渲染到DOM树的任意位置，常用于模态框、弹出层等场景。', 'Vue3', 3, 7);

-- ---- 数据库（单选） ----
INSERT INTO `question` (type, content, options, answer, analysis, knowledge_point, difficulty, creator_id) VALUES
(1, 'SQL中用于查询所有记录的关键字是？', '["SELECT * FROM","SELECT ALL","FIND ALL","QUERY *"]', 'A', 'SELECT * FROM table_name用于查询表中的所有记录。', '数据库', 1, 7),
(1, 'MySQL中哪个关键字用于去重？', '["UNIQUE","DISTINCT","DIFFERENT","REMOVE"]', 'B', 'DISTINCT关键字用于去除查询结果中的重复行。', '数据库', 1, 7);

-- ---- 多选题 ----
INSERT INTO `question` (type, content, options, answer, analysis, knowledge_point, difficulty, creator_id) VALUES
(2, '以下哪些是Vue3的生命周期钩子？', '["onMounted","onCreated","onUpdated","onDestroyed"]', 'A,C', 'Vue3中onMounted和onUpdated是生命周期钩子，onCreated改为setup，onDestroyed改为onUnmounted。', 'Vue3', 3, 7),
(2, '以下哪些属于Element Plus的组件？', '["el-button","el-table","vant-button","el-dialog"]', 'A,B,D', 'el-button、el-table、el-dialog是Element Plus组件，vant-button是Vant UI组件。', 'Vue3', 2, 7),
(2, 'Java中哪些是合法的标识符？', '["_name","123abc","$value","class"]', 'A,C', 'Java标识符由字母、数字、下划线、美元符号组成，不能以数字开头，不能是关键字。', 'Java基础', 2, 7);

-- ---- 判断题 ----
INSERT INTO `question` (type, content, options, answer, analysis, knowledge_point, difficulty, creator_id) VALUES
(3, 'Spring Boot的默认端口是8080。', '["正确","错误"]', 'A', 'Spring Boot的默认端口确实是8080，可以通过server.port属性修改。', 'Spring框架', 1, 7),
(3, 'MySQL中索引越多查询越快。', '["正确","错误"]', 'B', '索引并非越多越好，过多索引会降低写入性能并占用存储空间，需要根据实际查询场景合理创建。', '数据库', 3, 7),
(3, 'Vue3完全兼容Vue2的选项式API。', '["正确","错误"]', 'A', 'Vue3兼容Vue2的选项式API，同时新增了组合式API（Composition API）。', 'Vue3', 2, 7);

-- ---- 填空题 ----
INSERT INTO `question` (type, content, options, answer, analysis, knowledge_point, difficulty, creator_id) VALUES
(4, 'Spring Boot的默认端口是？', NULL, '8080', 'Spring Boot默认端口为8080，可在application.yml中通过server.port修改。', 'Spring框架', 1, 7),
(4, 'HTTP状态码200表示____。', NULL, '成功', '200 OK表示请求成功，是最常见的成功状态码。', '计算机网络', 1, 7);

-- ---- 简答题 ----
INSERT INTO `question` (type, content, options, answer, analysis, knowledge_point, difficulty, creator_id) VALUES
(4, '请简述面向对象编程的三大特性。', NULL, '封装、继承、多态', '封装隐藏实现细节，继承实现代码复用，多态实现动态绑定，三者共同构成了面向对象编程的基础。', 'Java基础', 3, 7),
(4, '请简述MVVM模式的工作流程。', NULL, 'View通过数据绑定与ViewModel交互，ViewModel通过响应式数据更新View', 'MVVM模式中View和Model通过ViewModel进行双向绑定，View的变化自动反映到Model，Model的变化自动更新View。', 'Vue3', 4, 7);

-- =============================================
-- 5. 试卷数据
-- =============================================
INSERT INTO `paper` (title, total_score, duration_minutes, creator_id, status) VALUES
('Java基础摸底考试', 100, 60, 7, 2),
('Vue3框架阶段测试', 100, 45, 7, 2),
('数据库综合测试', 100, 60, 8, 1);

-- =============================================
-- 6. 试卷-题目关联
-- =============================================
-- 试卷1：Java基础摸底考试（题目1-5 + 15-17 + 20）
INSERT INTO `paper_question` (paper_id, question_id, score, sort_order) VALUES
(1, 1, 10, 1), (1, 2, 10, 2), (1, 3, 10, 3), (1, 4, 10, 4), (1, 5, 10, 5),
(1, 15, 10, 6), (1, 16, 10, 7), (1, 20, 10, 8), (1, 22, 10, 9), (1, 23, 10, 10);

-- 试卷2：Vue3框架阶段测试（题目6-9 + 12-13 + 17）
INSERT INTO `paper_question` (paper_id, question_id, score, sort_order) VALUES
(2, 6, 10, 1), (2, 7, 10, 2), (2, 8, 10, 3), (2, 9, 10, 4),
(2, 12, 10, 5), (2, 13, 10, 6), (2, 17, 10, 7);

-- 试卷3：数据库综合测试（题目10-11 + 16 + 18 + 21）
INSERT INTO `paper_question` (paper_id, question_id, score, sort_order) VALUES
(3, 10, 20, 1), (3, 11, 20, 2), (3, 16, 20, 3), (3, 18, 20, 4), (3, 21, 20, 5);

-- =============================================
-- 7. 答题记录数据
-- =============================================
-- 学生1（张三）做了试卷1的5道题
INSERT INTO `answer_record` (user_id, paper_id, question_id, answer, is_correct, score) VALUES
(1, 1, 1, 'A', 1, 10),
(1, 1, 2, 'C', 1, 10),
(1, 1, 3, 'B', 1, 10),
(1, 1, 4, 'D', 0, 0),
(1, 1, 5, 'C', 1, 10);

-- 学生2（李四）做了试卷1的5道题（有对有错）
INSERT INTO `answer_record` (user_id, paper_id, question_id, answer, is_correct, score) VALUES
(2, 1, 1, 'B', 0, 0),
(2, 1, 2, 'C', 1, 10),
(2, 1, 3, 'B', 1, 10),
(2, 1, 4, 'A', 1, 10),
(2, 1, 5, 'C', 1, 10);

-- 学生1（张三）做了试卷2的3道题
INSERT INTO `answer_record` (user_id, paper_id, question_id, answer, is_correct, score) VALUES
(1, 2, 6, 'C', 1, 10),
(1, 2, 7, 'A', 1, 10),
(1, 2, 8, 'B', 1, 10);

-- =============================================
-- 8. 错题本数据
-- =============================================
INSERT INTO `wrong_question` (user_id, question_id, knowledge_point, wrong_count) VALUES
(1, 4, 'Java基础', 1),
(2, 1, 'Java基础', 2),
(3, 5, 'Java基础', 1);

-- =============================================
-- 9. 消息通知数据
-- =============================================
INSERT INTO `message` (to_user_id, type, content, is_read) VALUES
(1, 'SYSTEM', '欢迎使用在线刷题系统！祝你学习进步！', 0),
(2, 'SYSTEM', '欢迎使用在线刷题系统！祝你学习进步！', 0),
(1, 'GRADE', '你的Java基础摸底考试已批改完成，请查看成绩。', 1),
(2, 'GRADE', '你的Java基础摸底考试已批改完成，请查看成绩。', 0);
