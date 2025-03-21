package uw.ai.center.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

/**
 * 翻译结果数据。
 */
@Schema(title = "翻译结果数据", description = "翻译结果数据")
public class TranslateResultData {

    /**
     * 翻译结果列表。
     */
    @Schema(title = "翻译结果列表", description = "翻译结果列表")
    private List<TranslateResult> result;

    public TranslateResultData() {
    }

    public List<TranslateResult> getResult() {
        return result;
    }

    public void setResult(List<TranslateResult> result) {
        this.result = result;
    }


    /**
     * 翻译结果。
     */
    @Schema(title = "翻译结果", description = "翻译结果")
    public static class TranslateResult {
        /**
         * 目标语言。
         */
        @Schema(title = "目标语言", description = "目标语言")
        private String lang;

        /**
         * 翻译结果。
         */
        @Schema(title = "翻译结果", description = "翻译结果。key是源语言，value是翻译结果。")
        private Map<String,String> dataMap;

        public TranslateResult() {
        }

        public TranslateResult(String lang, Map<String, String> dataMap) {
            this.lang = lang;
            this.dataMap = dataMap;
        }

        public String getLang() {
            return lang;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }

        public Map<String, String> getDataMap() {
            return dataMap;
        }

        public void setDataMap(Map<String, String> dataMap) {
            this.dataMap = dataMap;
        }
    }
}
