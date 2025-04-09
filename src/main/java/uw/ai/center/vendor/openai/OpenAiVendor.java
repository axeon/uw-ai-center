package uw.ai.center.vendor.openai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseErrorHandler;
import uw.ai.center.advisor.AiChatLoggerAdvisor;
import uw.ai.center.advisor.AiMysqlChatMemory;
import uw.ai.center.vendor.AiVendor;
import uw.ai.center.vendor.AiVendorClientWrapper;
import uw.ai.center.vo.AiModelConfigData;
import uw.common.app.vo.JsonConfigBox;
import uw.common.app.vo.JsonConfigParam;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * OllamaVendor。
 */
@Service
public class OpenAiVendor implements AiVendor {
    private static final Logger logger = LoggerFactory.getLogger( OpenAiVendor.class );

    public OpenAiVendor() {
    }

    /**
     * 链接器名称
     */
    @Override
    public String vendorName() {
        return "OpenAi";
    }

    /**
     * 供应商描述
     */
    @Override
    public String vendorDesc() {
        return "OpenAi";
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
     * Vendor参数信息集合，管理员可见。
     */
    @Override
    public List<JsonConfigParam> vendorParam() {
        return Arrays.asList(OpenAiParam.Vendor.values()); // 自动获取所有枚举项
    }

    /**
     * model参数信息集合，运营商可见。
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
        return Arrays.asList(OpenAiParam.Embed.values()); // 自动获取所有枚举项
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
        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl( aiModelConfigData.getApiUrl() )
                .apiKey( new SimpleApiKey( aiModelConfigData.getApiKey() ) )
                .completionsPath( vendorParamBox.getParam( OpenAiParam.Vendor.API_PATH ) )
                .embeddingsPath( embedParamBox.getParam( OpenAiParam.Embed.API_PATH ) )
                .responseErrorHandler( new ResponseErrorHandler() {
                    @Override
                    public boolean hasError(ClientHttpResponse response) throws IOException {
                        if (response.getStatusCode() != HttpStatus.OK) {
                            return true;
                        } else {
                            return false;
                        }
                    }

                    @Override
                    public void handleError(ClientHttpResponse response) throws IOException {
                        logger.error( "OpenAiApi ConfigId[{}] handleError! statusCode: {}, statusText: {}, Body: {}", aiModelConfigData.getId(), response.getStatusCode(),
                                response.getStatusText(), response.getBody() );
                    }
                } )
                .build();
        // 初始化 ChatModel 和 EmbeddingModel
        ChatModel chatModel = OpenAiChatModel.builder()
                .openAiApi( openAiApi )
                .defaultOptions( OpenAiChatOptions.builder()
                        .model( aiModelConfigData.getModelMain() )
                        .temperature( vendorParamBox.getDoubleParam( OpenAiParam.Vendor.TEMPERATURE ) )
                        .frequencyPenalty( vendorParamBox.getDoubleParam( OpenAiParam.Vendor.FREQUENCY_PENALTY ) )
                        .maxCompletionTokens( vendorParamBox.getIntParam( OpenAiParam.Vendor.MAX_COMPLETION_TOKENS ) )
                        .N( vendorParamBox.getIntParam( OpenAiParam.Vendor.N ) )
                        .store( vendorParamBox.getBooleanParam( OpenAiParam.Vendor.STORE ) )
                        .presencePenalty( vendorParamBox.getDoubleParam( OpenAiParam.Vendor.PRESENCE_PENALTY ) )
                        .seed( vendorParamBox.getIntParam( OpenAiParam.Vendor.SEED ) )
                        .topP( vendorParamBox.getDoubleParam( OpenAiParam.Vendor.TOP_P ) )
                        .parallelToolCalls( vendorParamBox.getBooleanParam( OpenAiParam.Vendor.PARALLEL_TOOL_CALLS ) )
                        .build()
                )
                .build();

        EmbeddingModel embeddingModel = new OpenAiEmbeddingModel( openAiApi, MetadataMode.EMBED,
                OpenAiEmbeddingOptions.builder()
                        .model( aiModelConfigData.getModelEmbed() )
                        .build()
        );

        // 构建 ChatClient
        ChatClient chatClient = ChatClient.builder( chatModel )
                .defaultAdvisors( new MessageChatMemoryAdvisor( new AiMysqlChatMemory(), "0:0", 10 ) )
                .defaultAdvisors( new AiChatLoggerAdvisor() )
                .defaultOptions( OpenAiChatOptions.builder()
                        // 其他参数通过枚举获取
                        .build()
                )
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
        return List.of();
    }
}
