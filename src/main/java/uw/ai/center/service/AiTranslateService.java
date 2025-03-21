package uw.ai.center.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uw.ai.center.vo.TranslateListParam;
import uw.ai.center.vo.TranslateMapParam;
import uw.ai.center.vo.TranslateResultData;
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
    private static final BeanOutputConverter<TranslateResultData[]> BEAN_OUTPUT_CONVERTER = new BeanOutputConverter<>( TranslateResultData[].class );


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
    public static ResponseData<TranslateResultData[]> translateListEntity(long saasId, long userId, int userType, String userInfo, TranslateListParam param) {
        ResponseData<String> responseData = translateList( saasId, userId, userType, userInfo, param );
        if (responseData.isNotSuccess()) {
            return responseData.prototype();
        } else {
            TranslateResultData[] data = BEAN_OUTPUT_CONVERTER.convert( responseData.getData() );
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
    public static ResponseData<TranslateResultData[]> translateMapEntity(long saasId, long userId, int userType, String userInfo, TranslateMapParam param) {
        ResponseData<String> responseData = translateMap( saasId, userId, userType, userInfo, param );
        if (responseData.isNotSuccess()) {
            return responseData.prototype();
        } else {
            TranslateResultData[] data = BEAN_OUTPUT_CONVERTER.convert( responseData.getData() );
            return ResponseData.success( data );
        }
    }

    /**
     * 翻译列表。
     */
    public static ResponseData<String> translateList(long saasId, long userId, int userType, String userInfo, TranslateListParam param) {
        String TRANSLATE_USER_PROMPT_TEMPLATE = """
                请帮我把以下JSON数组中的文字分别翻译成 %s 语言。
                %s
                """;
        String userPrompt = String.format( TRANSLATE_USER_PROMPT_TEMPLATE, JsonInterfaceHelper.JSON_CONVERTER.toString( param.getLangList() ),
                JsonInterfaceHelper.JSON_CONVERTER.toString( param.getTextList() ) );
        String systemPrompt = param.getSystemPrompt() + "\n\n" + BEAN_OUTPUT_CONVERTER.getFormat();
        ResponseData<String> responseData = AiChatService.generate( saasId, userId, userType, userInfo, param.getConfigId(), userPrompt, systemPrompt, null, null );
        if (responseData.isSuccess()) {
            responseData.setData( BEAN_OUTPUT_CONVERTER.cleanJson( responseData.getData() ) );
        }
        return responseData;
    }

    /**
     * 翻译Map。
     */
    public static ResponseData<String> translateMap(long saasId, long userId, int userType, String userInfo, TranslateMapParam param) {
        String TRANSLATE_USER_PROMPT_TEMPLATE = """
                请帮我把以下JSON Map的Value中文字分别翻译成 %s 语言。
                %s
                """;
        String userPrompt = String.format( TRANSLATE_USER_PROMPT_TEMPLATE, JsonInterfaceHelper.JSON_CONVERTER.toString( param.getLangList() ),
                JsonInterfaceHelper.JSON_CONVERTER.toString( param.getTextMap() ) );
        String systemPrompt = param.getSystemPrompt() + "\n\n" + BEAN_OUTPUT_CONVERTER.getFormat();
        ResponseData<String> responseData = AiChatService.generate( saasId, userId, userType, userInfo, param.getConfigId(), userPrompt, systemPrompt, null, null );
        if (responseData.isSuccess()) {
            responseData.setData( BEAN_OUTPUT_CONVERTER.cleanJson( responseData.getData() ) );
        }
        return responseData;
    }


}


