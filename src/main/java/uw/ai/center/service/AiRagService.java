package uw.ai.center.service;

import uw.common.vo.ConfigParam;

import java.util.List;

/**
 * RAG库服务.
 */
public class AiRagService {

    /**
     * RAG库配置参数.
     */
    public static final List<ConfigParam> RAG_LIB_CONFIG_PARAMS = List.of(
            new ConfigParam("apiUrl", "https://api.openai.com/v1/chat/completions", "string", "apiUrl", "apiUrl")
    );
}
