package uw.ai.center.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uw.ai.vo.AiTranslateListParam;
import uw.ai.vo.AiTranslateMapParam;
import uw.ai.vo.AiTranslateResultData;
import uw.ai.util.BeanOutputConverter;
import uw.common.dto.ResponseData;
import uw.httpclient.json.JsonInterfaceHelper;

/**
 * AI翻译服务。
 */
public class AiTranslateService {

    private static final Logger logger = LoggerFactory.getLogger( AiTranslateService.class );

    /**
     * 翻译结果数据转换器。
     */
    private static final BeanOutputConverter<AiTranslateResultData[]> BEAN_OUTPUT_CONVERTER = new BeanOutputConverter<>( AiTranslateResultData[].class );


    /**
     * 翻译列表到实体类。
     *
     * @param saasId
     * @param userId
     * @param userType
     * @param userInfo
     * @param param
     * @return
     */
    public static ResponseData<AiTranslateResultData[]> translateListEntity(long saasId, long userId, int userType, String userInfo, AiTranslateListParam param) {
        ResponseData<String> responseData = translateList( saasId, userId, userType, userInfo, param );
        if (responseData.isNotSuccess()) {
            return responseData.prototype();
        } else {
            AiTranslateResultData[] data = BEAN_OUTPUT_CONVERTER.convert( responseData.getData() );
            return ResponseData.success( data );
        }
    }


    /**
     * 翻译Map到实体类。
     *
     * @param saasId
     * @param userId
     * @param userType
     * @param userInfo
     * @param param
     * @return
     */
    public static ResponseData<AiTranslateResultData[]> translateMapEntity(long saasId, long userId, int userType, String userInfo, AiTranslateMapParam param) {
        ResponseData<String> responseData = translateMap( saasId, userId, userType, userInfo, param );
        if (responseData.isNotSuccess()) {
            return responseData.prototype();
        } else {
            AiTranslateResultData[] data = BEAN_OUTPUT_CONVERTER.convert( responseData.getData() );
            return ResponseData.success( data );
        }
    }

    /**
     * 翻译列表。
     */
    public static ResponseData<String> translateList(long saasId, long userId, int userType, String userInfo, AiTranslateListParam param) {
        String TRANSLATE_USER_PROMPT_TEMPLATE = """
                请帮我把以下JSON数组中的文字分别翻译成 %s 语言。
                %s
                """;
        String userPrompt = String.format( TRANSLATE_USER_PROMPT_TEMPLATE, JsonInterfaceHelper.JSON_CONVERTER.toString( param.getLangList() ),
                JsonInterfaceHelper.JSON_CONVERTER.toString( param.getTextList() ) );
        String systemPrompt = param.getSystemPrompt() + "\n\n" + BEAN_OUTPUT_CONVERTER.getFormat();
        ResponseData<String> responseData = AiChatService.generate( saasId, userId, userType, userInfo, param.getConfigId(), userPrompt, systemPrompt, null, null,null );
        if (responseData.isSuccess()) {
            responseData.setData( BEAN_OUTPUT_CONVERTER.cleanJson( responseData.getData() ) );
        }
        return responseData;
    }

    /**
     * 翻译Map。
     */
    public static ResponseData<String> translateMap(long saasId, long userId, int userType, String userInfo, AiTranslateMapParam param) {
        String TRANSLATE_USER_PROMPT_TEMPLATE = """
                请帮我把以下JSON Map的Value值分别翻译成 %s 语言。
                %s
                """;
        String userPrompt = String.format( TRANSLATE_USER_PROMPT_TEMPLATE, JsonInterfaceHelper.JSON_CONVERTER.toString( param.getLangList() ),
                JsonInterfaceHelper.JSON_CONVERTER.toString( param.getTextMap() ) );
        String systemPrompt = param.getSystemPrompt() + "\n\n" + BEAN_OUTPUT_CONVERTER.getFormat();
        ResponseData<String> responseData = AiChatService.generate( saasId, userId, userType, userInfo, param.getConfigId(), userPrompt, systemPrompt, null, null,null );
        if (responseData.isSuccess()) {
            responseData.setData( BEAN_OUTPUT_CONVERTER.cleanJson( responseData.getData() ) );
        }
        return responseData;
    }


}


