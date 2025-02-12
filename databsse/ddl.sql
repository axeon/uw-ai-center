-- uw_ai.ai_session_info definition

CREATE TABLE `ai_session_info` (
                                   `id` bigint NOT NULL COMMENT 'ID',
                                   `saas_id` bigint NOT NULL COMMENT 'saasId',
                                   `mch_id` bigint DEFAULT NULL COMMENT '商户ID',
                                   `user_id` bigint NOT NULL COMMENT '用户id',
                                   `user_type` int DEFAULT NULL COMMENT '用户类型',
                                   `group_id` bigint DEFAULT NULL COMMENT '用户组ID',
                                   `user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用户名',
                                   `nick_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用户昵称',
                                   `real_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '真实名称',
                                   `session_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'session名称',
                                   `create_date` datetime(3) DEFAULT NULL COMMENT '创建时间',
                                   `modify_date` datetime(3) DEFAULT NULL COMMENT '修改时间',
                                   `state` int DEFAULT NULL COMMENT '状态',
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='session信息';


-- uw_ai.ai_session_msg definition

CREATE TABLE `ai_session_msg` (
                                  `id` bigint NOT NULL COMMENT 'ID',
                                  `session_id` bigint NOT NULL COMMENT 'sessionId',
                                  `request_info` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '请求信息',
                                  `response_info` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '返回信息',
                                  `create_date` datetime(3) DEFAULT NULL COMMENT '创建时间',
                                  `state` int DEFAULT NULL COMMENT '状态',
                                  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='session消息';


-- uw_ai.ai_model_config definition

CREATE TABLE `ai_model_config` (
                                   `id` bigint NOT NULL COMMENT 'ID',
                                   `saas_id` bigint NOT NULL DEFAULT '0' COMMENT 'SAAS ID',
                                   `mch_id` bigint NOT NULL DEFAULT '0' COMMENT '商户ID',
                                   `vendor_class` varchar(200) NOT NULL COMMENT '服务商类',
                                   `model_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '服务商代码',
                                   `model_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '服务商名称',
                                   `model_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '服务商描述',
                                   `create_date` datetime(3) DEFAULT NULL COMMENT '创建时间',
                                   `modify_date` datetime(3) DEFAULT NULL COMMENT '修改时间',
                                   `public_data` json DEFAULT NULL COMMENT '公开配置',
                                   `model_data` json DEFAULT NULL COMMENT 'API配置',
                                   `log_data` json DEFAULT NULL COMMENT '日志配置',
                                   `state` int DEFAULT NULL COMMENT '状态',
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AI服务模型';