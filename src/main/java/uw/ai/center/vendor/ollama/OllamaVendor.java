package uw.ai.center.vendor.ollama;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.ollama.management.ModelManagementOptions;
import org.springframework.ai.ollama.management.PullModelStrategy;
import org.springframework.stereotype.Service;
import uw.ai.center.advisor.AiChatLoggerAdvisor;
import uw.ai.center.advisor.AiMysqlChatMemory;

import uw.ai.center.vendor.AiVendor;
import uw.ai.center.vendor.AiVendorClientWrapper;
import uw.ai.center.vo.AiModelConfigData;
import uw.app.common.constant.JsonParamType;
import uw.app.common.vo.JsonParam;
import uw.app.common.vo.JsonParamBox;

import java.time.Duration;
import java.util.List;

/**
 * OllamaVendor。
 */
@Service
public class OllamaVendor implements AiVendor {

    public OllamaVendor() {
    }

    /**
     * 链接器名称
     */
    @Override
    public String vendorName() {
        return "Ollama";
    }

    /**
     * 供应商描述
     */
    @Override
    public String vendorDesc() {
        return "Ollama";
    }

    /**
     * 链接器版本
     */
    @Override
    public String vendorVersion() {
        return "1.0.0";
    }

    /**
     * 链接器图标
     */
    @Override
    public String vendorIcon() {
        return "";
    }

    /**
     * Vendor参数信息集合。
     */
    @Override
    public List<JsonParam> vendorParam() {
        return List.of(
                new JsonParam( JsonParamType.BOOLEAN, "numa", "false", "numa", "是否使用 NUMA。" ),
                new JsonParam( JsonParamType.INT, "num-ctx", "2048", "num-ctx", "设置用于生成下一个标记的上下文窗口的大小。" ),
                new JsonParam( JsonParamType.INT, "num-batch", "512", "num-batch", "提示处理最大批次大小。" ),
                new JsonParam( JsonParamType.INT, "num-gpu", "-1", "num-gpu", "发送到 GPU 的数量。在 macOS 上，默认值为 1 以启用 metal 支持，0 以禁用。这里的 1 表示 NumGPU 应动态设置" ),
                new JsonParam( JsonParamType.INT, "main-gpu", "0", "main-gpu", "当使用多个 GPU 时，此选项控制使用哪个 GPU 来处理小张量，因为将计算拆分到所有 GPU 上的开销不值得。所指的 GPU 将使用略多的 VRAM " +
                        "来存储临时结果的临时缓冲区。" ),
                new JsonParam( JsonParamType.BOOLEAN, "low-vram", "false", "low-vram", "-" ),
                new JsonParam( JsonParamType.BOOLEAN, "f16-kv", "true", "f16-kv", "-" ),
                new JsonParam( JsonParamType.BOOLEAN, "logits-all", "true", "logits-all", "返回所有标记的 logits，而不仅仅是最后一个。要启用完成返回 logprobs，此选项必须为真。" ),
                new JsonParam( JsonParamType.BOOLEAN, "vocab-only", "true", "vocab-only", "只加载词汇表，不加载权重。" ),
                new JsonParam( JsonParamType.BOOLEAN, "use-mmap", "true", "use-mmap", "默认情况下，模型会被映射到内存中，这使得系统可以根据需要只加载模型的必要部分。然而，如果模型的大小超过了你的总 RAM " +
                        "量，或者你的系统可用内存不足，使用 mmap 可能会增加页面换出的风险，从而负面影响性能。禁用 mmap 会导致加载时间变慢，但如果不用 mlock，可能会减少页面换出。请注意，如果模型的大小超过了总 RAM 量，关闭 mmap 将阻止模型加载。" ),
                new JsonParam( JsonParamType.BOOLEAN, "use-mlock", "false", "use-mlock", "将模型锁定在内存中，防止在内存映射时将其交换出去。这可以提高性能，但会牺牲一些内存映射的优势，因为它需要更多的 RAM " +
                        "来运行，并且可能在模型加载到 RAM 时减慢加载时间。" ),
                new JsonParam( JsonParamType.INT, "num-thread", "0", "num-thread", "设置在计算过程中使用的线程数量。默认情况下，Ollama 会检测此值以获得最佳性能。建议将此值设置为系统中的物理 CPU 核心数（而不是逻辑核心数）。0 = " +
                        "由运行时决定" ),
                new JsonParam( JsonParamType.INT, "num-keep", "4", "num-keep", "-" ),
                new JsonParam( JsonParamType.INT, "seed", "-1", "seed", "设置生成时使用的随机数种子。将此值设置为特定数字，可以在相同的提示下生成相同的文本。" ),
                new JsonParam( JsonParamType.INT, "num-predict", "-1", "num-predict", "生成文本时预测的最大 token 数。（-1 表示无限生成，-2 表示填充上下文）" ),
                new JsonParam( JsonParamType.INT, "top-k", "40", "top-k", "减少生成无意义内容的概率。更高的值（例如 100）将给出更多样化的答案，而较低的值（例如 10）将更加保守。" ),
                new JsonParam( JsonParamType.FLOAT, "top-p", "0.9", "top-p", "与 top-k 一起工作。更高的值（例如，0.95）会导致更多样化的文本，而较低的值（例如，0.5）将生成更集中和保守的文本。" ),
                new JsonParam( JsonParamType.INT, "tfs-z", "1", "tfs-z", "尾部无信息采样用于减少不太可能的标记对输出的影响。更高的值（例如，2.0）将减少这种影响，而值为 1.0 将禁用此设置。" ),
                new JsonParam( JsonParamType.INT, "typical-p", "1", "typical-p", "-" ),
                new JsonParam( JsonParamType.INT, "repeat-last-n", "64", "repeat-last-n", "设置模型回溯的范围以防止重复。默认值：64，0 表示禁用，-1 表示上下文长度。" ),
                new JsonParam( JsonParamType.FLOAT, "temperature", "0.8", "temperature", "模型的温度。增加温度会使模型的回答更具创造性。" ),
                new JsonParam( JsonParamType.FLOAT, "repeat-penalty", "1.1", "repeat-penalty", "设置重复的惩罚强度。较高的值（例如，1.5）会更强烈地惩罚重复，而较低的值（例如，0.9）会更宽容。" ),
                new JsonParam( JsonParamType.INT, "presence-penalty", "0", "presence-penalty", "-" ),
                new JsonParam( JsonParamType.INT, "frequency-penalty", "0", "frequency-penalty", "-" ),
                new JsonParam( JsonParamType.INT, "mirostat", "0", "mirostat", "启用mirostat采样以控制困惑度。(默认: 0, 0 = 禁用, 1 = mirostat, 2 = mirostat 2.0)" ),
                new JsonParam( JsonParamType.INT, "mirostat-tau", "5", "mirostat-tau", "控制输出的连贯性和多样性之间的平衡。较低的值会导致更专注和连贯的文本。" ),
                new JsonParam( JsonParamType.FLOAT, "mirostat-eta", "0.1", "mirostat-eta", "影响算法对生成文本反馈的响应速度。较低的学习率会导致较慢的调整，而较高的学习率会使算法更具响应性。" ),
                new JsonParam( JsonParamType.BOOLEAN, "penalize-newline", "true", "penalize-newline", "-" ),
                new JsonParam( JsonParamType.STRING, "stop", "-", "stop", "设置要使用的停止序列。当遇到此模式时，LLM 将停止生成文本并返回。可以通过在模型文件中指定多个单独的停止参数来设置多个停止模式。" ),
                new JsonParam( JsonParamType.STRING, "functions", "-", "functions", "在单个提示请求中启用的功能列表，通过它们的名称进行标识。这些名称中的功能必须存在于 functionCallbacks 注册表中。" ),
                new JsonParam( JsonParamType.BOOLEAN, "proxy-tool-calls", "false", "proxy-tool-calls",
                        "如果为真，则将不会处理函数调用，而是将它们代理给客户端。然后是客户端的责任来处理函数调用，将它们分发到适当的函数，并返回结果。如果为假（默认值），则 Spring AI 将内部处理函数调用。仅适用于具有函数调用支持的聊天模型" )
        );
    }

    /**
     * Model参数信息集合。
     */
    @Override
    public List<JsonParam> modelParam() {
        return List.of();
    }

    /**
     * embed参数信息集合，仅管理员可见。
     */
    @Override
    public List<JsonParam> embedParam() {
        return List.of();
    }

    /**
     * 构造模型实例。
     *
     * @param aiModelConfigData
     * @return
     */
    @Override
    public AiVendorClientWrapper buildClientWrapper(AiModelConfigData aiModelConfigData) {
        JsonParamBox vendorParamBox = aiModelConfigData.getVendorParamBox();
        JsonParamBox embedParamBox = aiModelConfigData.getEmbedParamBox();
        OllamaApi ollamaApi = new OllamaApi( aiModelConfigData.getApiUrl() );
        ChatModel chatModel = OllamaChatModel.builder().ollamaApi( ollamaApi ).modelManagementOptions( new ModelManagementOptions( PullModelStrategy.NEVER,
                List.of( aiModelConfigData.getModelMain() ), Duration.ofSeconds( 0 ), 3 ) ).defaultOptions(
                OllamaOptions.builder()
                        .useNUMA( vendorParamBox.getBooleanParam( "numa" ) )
                        .numCtx( vendorParamBox.getIntParam( "num-ctx" ) )
                        .numBatch( vendorParamBox.getIntParam( "num-batch" ) )
                        .numGPU( vendorParamBox.getIntParam( "num-gpu" ) )
                        .mainGPU( vendorParamBox.getIntParam( "main-gpu" ) )
                        .lowVRAM( vendorParamBox.getBooleanParam( "low-vram" ) )
                        .f16KV( vendorParamBox.getBooleanParam( "f16-kv" ) )
                        .logitsAll( vendorParamBox.getBooleanParam( "logits-all" ) )
                        .vocabOnly( vendorParamBox.getBooleanParam( "vocab-only" ) )
                        .useMMap( vendorParamBox.getBooleanParam( "use-mmap" ) )
                        .useMLock( vendorParamBox.getBooleanParam( "use-mlock" ) )
                        .numThread( vendorParamBox.getIntParam( "num-thread" ) )
                        .numKeep( vendorParamBox.getIntParam( "num-keep" ) )
                        .seed( vendorParamBox.getIntParam( "seed" ) )
                        .numPredict( vendorParamBox.getIntParam( "num-predict" ) )
                        .topK( vendorParamBox.getIntParam( "top-k" ) )
                        .topP( vendorParamBox.getDoubleParam( "top-p" ) )
                        .tfsZ( vendorParamBox.getFloatParam( "tfs-z" ) )
                        .typicalP( vendorParamBox.getFloatParam( "typical-p" ) )
                        .repeatLastN( vendorParamBox.getIntParam( "repeat-last-n" ) )
                        .repeatPenalty( vendorParamBox.getDoubleParam( "repeat-penalty" ) )
                        .presencePenalty( vendorParamBox.getDoubleParam( "presence-penalty" ) )
                        .frequencyPenalty( vendorParamBox.getDoubleParam( "frequency-penalty" ) )
                        .temperature( vendorParamBox.getDoubleParam( "temperature" ) )
                        .mirostat( vendorParamBox.getIntParam( "mirostat" ) )
                        .mirostatEta( vendorParamBox.getFloatParam( "mirostat-eta" ) )
                        .mirostatTau( vendorParamBox.getFloatParam( "mirostat-tau" ) )
                        .penalizeNewline( vendorParamBox.getBooleanParam( "penalize-newline" ) )
                        .model( aiModelConfigData.getModelMain() ).build()
        ).build();
        EmbeddingModel embeddingModel = OllamaEmbeddingModel.builder().ollamaApi( ollamaApi ).modelManagementOptions( new ModelManagementOptions( PullModelStrategy.NEVER,
                List.of( aiModelConfigData.getModelEmbed() ), Duration.ofSeconds( 0 ), 3 ) ).defaultOptions(
                OllamaOptions.builder()
                        .useNUMA( vendorParamBox.getBooleanParam( "numa" ) )
                        .numCtx( vendorParamBox.getIntParam( "num-ctx" ) )
                        .numBatch( vendorParamBox.getIntParam( "num-batch" ) )
                        .numGPU( vendorParamBox.getIntParam( "num-gpu" ) )
                        .mainGPU( vendorParamBox.getIntParam( "main-gpu" ) )
                        .lowVRAM( vendorParamBox.getBooleanParam( "low-vram" ) )
                        .f16KV( vendorParamBox.getBooleanParam( "f16-kv" ) )
                        .logitsAll( vendorParamBox.getBooleanParam( "logits-all" ) )
                        .vocabOnly( vendorParamBox.getBooleanParam( "vocab-only" ) )
                        .useMMap( vendorParamBox.getBooleanParam( "use-mmap" ) )
                        .useMLock( vendorParamBox.getBooleanParam( "use-mlock" ) )
                        .numThread( vendorParamBox.getIntParam( "num-thread" ) )
                        .numKeep( vendorParamBox.getIntParam( "num-keep" ) )
                        .seed( vendorParamBox.getIntParam( "seed" ) )
                        .numPredict( vendorParamBox.getIntParam( "num-predict" ) )
                        .topK( vendorParamBox.getIntParam( "top-k" ) )
                        .topP( vendorParamBox.getDoubleParam( "top-p" ) )
                        .tfsZ( vendorParamBox.getFloatParam( "tfs-z" ) )
                        .typicalP( vendorParamBox.getFloatParam( "typical-p" ) )
                        .repeatLastN( vendorParamBox.getIntParam( "repeat-last-n" ) )
                        .repeatPenalty( vendorParamBox.getDoubleParam( "repeat-penalty" ) )
                        .presencePenalty( vendorParamBox.getDoubleParam( "presence-penalty" ) )
                        .frequencyPenalty( vendorParamBox.getDoubleParam( "frequency-penalty" ) )
                        .temperature( vendorParamBox.getDoubleParam( "temperature" ) )
                        .mirostat( vendorParamBox.getIntParam( "mirostat" ) )
                        .mirostatEta( vendorParamBox.getFloatParam( "mirostat-eta" ) )
                        .mirostatTau( vendorParamBox.getFloatParam( "mirostat-tau" ) )
                        .penalizeNewline( vendorParamBox.getBooleanParam( "penalize-newline" ) )
                        .model( aiModelConfigData.getEmbedData() ).build()
        ).build();
        ChatClient chatClient = ChatClient.builder( chatModel )
                // 实现 Chat Memory 的 Advisor
                // 在使用 Chat Memory 时，需要指定对话 ID，以便 Spring AI 处理上下文。
                .defaultAdvisors( new MessageChatMemoryAdvisor( new AiMysqlChatMemory(), "0:0", 10 ) )
                // 实现 Logger 的 Advisor
                .defaultAdvisors( new AiChatLoggerAdvisor() )
                .build();
        return new AiVendorClientWrapper( aiModelConfigData, chatClient, embeddingModel );
    }

    /**
     * 获取模型列表。
     *
     * @return
     */
    @Override
    public List<String> listModel(String apiUrl, String apiKey) {
        OllamaApi ollamaApi = new OllamaApi( apiUrl );
        return ollamaApi.listModels().models().stream().map( model -> model.name() ).toList();
    }

}
