package uw.ai.center;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import uw.common.app.AppBootStrap;

/**
 * uw-ai-center 服务启动入口。
 * <p>AI 能力中心微服务，基于 LangChain4j 统一对接 OpenAI / Ollama / DashScope 等供应商，
 * 提供对话、流式聊天、图片生成、实时语音识别、RAG 检索与翻译能力。
 * 通过 {@link EnableDiscoveryClient} 注册到 Nacos，由 {@link AppBootStrap} 完成统一启动引导。
 */
@SpringBootApplication
@EnableDiscoveryClient
class UwAiCenterApplication {

    /**
     * 服务主入口。
     *
     * @param args 启动参数
     */
    public static void main(String[] args) {
        AppBootStrap.run(UwAiCenterApplication.class, args);
    }
}
