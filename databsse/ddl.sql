-- uw_ai.ai_model_config definition

CREATE TABLE `ai_model_config` (
                                   `id` bigint NOT NULL COMMENT 'ID',
                                   `saas_id` bigint NOT NULL DEFAULT '0' COMMENT 'SAAS ID',
                                   `mch_id` bigint NOT NULL DEFAULT '0' COMMENT '商户ID',
                                   `vendor_class` varchar(200) NOT NULL COMMENT '服务商类',
                                   `config_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '服务商代码',
                                   `config_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '服务商名称',
                                   `config_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '服务商描述',
                                   `api_url` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'api地址',
                                   `api_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'api key',
                                   `model_main` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '主模型',
                                   `model_embed` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '嵌入模型',
                                   `vendor_data` json DEFAULT NULL COMMENT '服务商配置',
                                   `model_data` json DEFAULT NULL COMMENT '模型配置',
                                   `embed_data` json DEFAULT NULL COMMENT '嵌入配置',
                                   `create_date` datetime(3) DEFAULT NULL COMMENT '创建时间',
                                   `modify_date` datetime(3) DEFAULT NULL COMMENT '修改时间',
                                   `state` int DEFAULT NULL COMMENT '状态',
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AI服务模型';


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


-- uw_ai.sys_crit_log definition

CREATE TABLE `sys_crit_log` (
                                `id` bigint NOT NULL COMMENT 'ID',
                                `saas_id` bigint NOT NULL COMMENT 'saasId',
                                `mch_id` bigint DEFAULT NULL COMMENT '商户ID',
                                `user_id` bigint NOT NULL COMMENT '用户id',
                                `user_type` int DEFAULT NULL COMMENT '用户类型',
                                `group_id` bigint DEFAULT NULL COMMENT '用户组ID',
                                `user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用户名',
                                `nick_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用户昵称',
                                `real_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '真实名称',
                                `ref_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '操作对象类型',
                                `ref_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '操作对象id',
                                `api_uri` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '请求uri',
                                `api_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'API名称',
                                `op_state` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '操作状态',
                                `op_log` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '日志内容',
                                `request_body` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '请求参数',
                                `response_body` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '响应日志',
                                `response_millis` bigint DEFAULT NULL COMMENT '请求毫秒数',
                                `exception` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '异常信息',
                                `status_code` int DEFAULT NULL COMMENT '响应状态码',
                                `app_info` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '应用信息',
                                `app_host` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '应用主机',
                                `user_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用户ip',
                                `request_date` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '请求时间',
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统关键日志';


-- uw_ai.sys_data_history definition

CREATE TABLE `sys_data_history` (
                                    `id` bigint NOT NULL COMMENT 'ID',
                                    `saas_id` bigint NOT NULL COMMENT 'saasId',
                                    `mch_id` bigint DEFAULT NULL COMMENT '商户ID',
                                    `user_id` bigint NOT NULL COMMENT '用户ID',
                                    `user_type` int DEFAULT NULL COMMENT '用户类型',
                                    `group_id` bigint DEFAULT NULL COMMENT '用户的组ID',
                                    `user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用户名称',
                                    `nick_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用户昵称',
                                    `real_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '真实名称',
                                    `entity_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '实体类',
                                    `entity_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '实体ID',
                                    `entity_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '实体名',
                                    `entity_data` json DEFAULT NULL COMMENT '实体数据',
                                    `entity_update_info` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '实体修改信息',
                                    `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '备注信息',
                                    `user_ip` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用户IP',
                                    `create_date` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建日期',
                                    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统数据历史';


-- uw_ai.sys_seq definition

CREATE TABLE `sys_seq` (
                           `seq_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '序列名',
                           `seq_id` bigint DEFAULT NULL COMMENT '当前序列id',
                           `seq_desc` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '序列描述',
                           `increment_num` int DEFAULT NULL COMMENT '每次递增大小',
                           `create_date` datetime(3) DEFAULT NULL COMMENT '建立日期',
                           `last_update` datetime(3) DEFAULT NULL COMMENT '最后更新日期',
                           PRIMARY KEY (`seq_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='SYS序列';