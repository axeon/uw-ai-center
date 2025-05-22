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
import uw.ai.center.advisor.AiMysqlChatMemory;
import uw.ai.center.vendor.AiVendor;
import uw.ai.center.vendor.AiVendorClientWrapper;
import uw.ai.center.vo.AiModelConfigData;
import uw.common.app.vo.JsonConfigBox;
import uw.common.app.vo.JsonConfigParam;

import java.time.Duration;
import java.util.Arrays;
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
    public List<JsonConfigParam> vendorParam() {
        return Arrays.asList( OllamaParam.Vendor.values() ); // 自动获取所有枚举项
    }

    /**
     * Model参数信息集合。
     */
    @Override
    public List<JsonConfigParam> modelParam() {
        return List.of();
    }

    /**
     * embed参数信息集合，仅管理员可见。
     */
    @Override
    public List<JsonConfigParam> embedParam() {
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
        JsonConfigBox vendorParamBox = aiModelConfigData.getVendorParamBox();
        JsonConfigBox embedParamBox = aiModelConfigData.getEmbedParamBox();
        OllamaApi ollamaApi = OllamaApi.builder().baseUrl( aiModelConfigData.getApiUrl()  ).build();

        ChatModel chatModel = OllamaChatModel.builder().ollamaApi( ollamaApi ).modelManagementOptions( new ModelManagementOptions( PullModelStrategy.NEVER,
                        List.of( aiModelConfigData.getModelMain() ), Duration.ofSeconds( 0 ), 3 ) )
                .defaultOptions( OllamaOptions.builder()
                        .useNUMA( vendorParamBox.getBooleanParam( OllamaParam.Vendor.NUMA ) )
                        .numCtx( vendorParamBox.getIntParam( OllamaParam.Vendor.NUM_CTX ) )
                        .numBatch( vendorParamBox.getIntParam( OllamaParam.Vendor.NUM_BATCH ) )
                        .numGPU( vendorParamBox.getIntParam( OllamaParam.Vendor.NUM_GPU ) )
                        .mainGPU( vendorParamBox.getIntParam( OllamaParam.Vendor.MAIN_GPU ) )
                        .lowVRAM( vendorParamBox.getBooleanParam( OllamaParam.Vendor.LOW_VRAM ) )
                        .f16KV( vendorParamBox.getBooleanParam( OllamaParam.Vendor.F16_KV ) )
                        .logitsAll( vendorParamBox.getBooleanParam( OllamaParam.Vendor.LOGITS_ALL ) )
                        .vocabOnly( vendorParamBox.getBooleanParam( OllamaParam.Vendor.VOCAB_ONLY ) )
                        .useMMap( vendorParamBox.getBooleanParam( OllamaParam.Vendor.USE_MMAP ) )
                        .useMLock( vendorParamBox.getBooleanParam( OllamaParam.Vendor.USE_MLOCK ) )
                        .numThread( vendorParamBox.getIntParam( OllamaParam.Vendor.NUM_THREAD ) )
                        .numKeep( vendorParamBox.getIntParam( OllamaParam.Vendor.NUM_KEEP ) )
                        .seed( vendorParamBox.getIntParam( OllamaParam.Vendor.SEED ) )
                        .numPredict( vendorParamBox.getIntParam( OllamaParam.Vendor.NUM_PREDICT ) )
                        .topK( vendorParamBox.getIntParam( OllamaParam.Vendor.TOP_K ) )
                        .topP( vendorParamBox.getDoubleParam( OllamaParam.Vendor.TOP_P ) )
                        .tfsZ( vendorParamBox.getFloatParam( OllamaParam.Vendor.TFS_Z ) )
                        .typicalP( vendorParamBox.getFloatParam( OllamaParam.Vendor.TYPICAL_P ) )
                        .repeatLastN( vendorParamBox.getIntParam( OllamaParam.Vendor.REPEAT_LAST_N ) )
                        .repeatPenalty( vendorParamBox.getDoubleParam( OllamaParam.Vendor.REPEAT_PENALTY ) )
                        .presencePenalty( vendorParamBox.getDoubleParam( OllamaParam.Vendor.PRESENCE_PENALTY ) )
                        .frequencyPenalty( vendorParamBox.getDoubleParam( OllamaParam.Vendor.FREQUENCY_PENALTY ) )
                        .temperature( vendorParamBox.getDoubleParam( OllamaParam.Vendor.TEMPERATURE ) )
                        .mirostat( vendorParamBox.getIntParam( OllamaParam.Vendor.MIROSTAT ) )
                        .mirostatEta( vendorParamBox.getFloatParam( OllamaParam.Vendor.MIROSTAT_ETA ) )
                        .mirostatTau( vendorParamBox.getFloatParam( OllamaParam.Vendor.MIROSTAT_TAU ) )
                        .penalizeNewline( vendorParamBox.getBooleanParam( OllamaParam.Vendor.PENALIZE_NEWLINE ) )
                        .model( aiModelConfigData.getModelMain() )
                        .build()
                ).build();
        EmbeddingModel embeddingModel = OllamaEmbeddingModel.builder().ollamaApi( ollamaApi ).modelManagementOptions( new ModelManagementOptions( PullModelStrategy.NEVER,
                        List.of( aiModelConfigData.getModelEmbed() ), Duration.ofSeconds( 0 ), 3 ) )
                .defaultOptions( OllamaOptions.builder()
                        .useNUMA( vendorParamBox.getBooleanParam( OllamaParam.Vendor.NUMA ) )
                        .numCtx( vendorParamBox.getIntParam( OllamaParam.Vendor.NUM_CTX ) )
                        .numBatch( vendorParamBox.getIntParam( OllamaParam.Vendor.NUM_BATCH ) )
                        .numGPU( vendorParamBox.getIntParam( OllamaParam.Vendor.NUM_GPU ) )
                        .mainGPU( vendorParamBox.getIntParam( OllamaParam.Vendor.MAIN_GPU ) )
                        .lowVRAM( vendorParamBox.getBooleanParam( OllamaParam.Vendor.LOW_VRAM ) )
                        .f16KV( vendorParamBox.getBooleanParam( OllamaParam.Vendor.F16_KV ) )
                        .logitsAll( vendorParamBox.getBooleanParam( OllamaParam.Vendor.LOGITS_ALL ) )
                        .vocabOnly( vendorParamBox.getBooleanParam( OllamaParam.Vendor.VOCAB_ONLY ) )
                        .useMMap( vendorParamBox.getBooleanParam( OllamaParam.Vendor.USE_MMAP ) )
                        .useMLock( vendorParamBox.getBooleanParam( OllamaParam.Vendor.USE_MLOCK ) )
                        .numThread( vendorParamBox.getIntParam( OllamaParam.Vendor.NUM_THREAD ) )
                        .numKeep( vendorParamBox.getIntParam( OllamaParam.Vendor.NUM_KEEP ) )
                        .seed( vendorParamBox.getIntParam( OllamaParam.Vendor.SEED ) )
                        .numPredict( vendorParamBox.getIntParam( OllamaParam.Vendor.NUM_PREDICT ) )
                        .topK( vendorParamBox.getIntParam( OllamaParam.Vendor.TOP_K ) )
                        .topP( vendorParamBox.getDoubleParam( OllamaParam.Vendor.TOP_P ) )
                        .tfsZ( vendorParamBox.getFloatParam( OllamaParam.Vendor.TFS_Z ) )
                        .typicalP( vendorParamBox.getFloatParam( OllamaParam.Vendor.TYPICAL_P ) )
                        .repeatLastN( vendorParamBox.getIntParam( OllamaParam.Vendor.REPEAT_LAST_N ) )
                        .repeatPenalty( vendorParamBox.getDoubleParam( OllamaParam.Vendor.REPEAT_PENALTY ) )
                        .presencePenalty( vendorParamBox.getDoubleParam( OllamaParam.Vendor.PRESENCE_PENALTY ) )
                        .frequencyPenalty( vendorParamBox.getDoubleParam( OllamaParam.Vendor.FREQUENCY_PENALTY ) )
                        .temperature( vendorParamBox.getDoubleParam( OllamaParam.Vendor.TEMPERATURE ) )
                        .mirostat( vendorParamBox.getIntParam( OllamaParam.Vendor.MIROSTAT ) )
                        .mirostatEta( vendorParamBox.getFloatParam( OllamaParam.Vendor.MIROSTAT_ETA ) )
                        .mirostatTau( vendorParamBox.getFloatParam( OllamaParam.Vendor.MIROSTAT_TAU ) )
                        .penalizeNewline( vendorParamBox.getBooleanParam( OllamaParam.Vendor.PENALIZE_NEWLINE ) )
                        .model( aiModelConfigData.getEmbedData() )
                        .build()
                ).build();
        ChatClient chatClient = ChatClient.builder( chatModel )
                // 实现 Chat Memory 的 Advisor
                // 在使用 Chat Memory 时，需要指定对话 ID，以便 Spring AI 处理上下文。
                .defaultAdvisors(MessageChatMemoryAdvisor.builder( new AiMysqlChatMemory()).build() )
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
        OllamaApi ollamaApi = OllamaApi.builder().baseUrl( apiUrl ).build();
        return ollamaApi.listModels().models().stream().map( model -> model.name() ).toList();
    }

}
