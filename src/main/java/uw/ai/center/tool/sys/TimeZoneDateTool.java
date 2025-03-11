package uw.ai.center.tool.sys;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.stereotype.Component;
import uw.ai.tool.AiTool;
import uw.ai.tool.AiToolParam;
import uw.common.dto.ResponseData;

import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * 时区日期工具。
 */
@Component
public class TimeZoneDateTool implements AiTool<TimeZoneDateTool.ToolParam, ResponseData<String>> {

    /**
     * 定义工具名称。
     *
     * @return
     */
    @Override
    public String toolName() {
        return "日期工具";
    }

    /**
     * 定义工具描述。
     *
     * @return
     */
    @Override
    public String toolDesc() {
        return "日期工具，返回当前日期";
    }

    /**
     * 定义工具版本。
     *
     * @return
     */
    @Override
    public String toolVersion() {
        return "0.0.5";
    }


    /**
     * Applies this function to the given argument.
     *
     * @param toolParam the function argument
     * @return the function result
     */
    @Override
    public ResponseData<String> apply(TimeZoneDateTool.ToolParam toolParam) {
        ZoneId zoneId = ZoneId.of( toolParam.getTimeZone() );
        ZonedDateTime zonedNow = ZonedDateTime.now( zoneId );
        String data = zonedNow.toString() + "@" + toolParam.getTimeZone();
        return ResponseData.success( data );
    }

    /**
     * 工具参数。
     */
    public static class ToolParam extends AiToolParam {

        @Schema(description ="时区，默认为UTC", requiredMode = Schema.RequiredMode.REQUIRED)
        private String timeZone = ZoneId.systemDefault().getId();

        public String getTimeZone() {
            return timeZone;
        }

        public void setTimeZone(String timeZone) {
            this.timeZone = timeZone;
        }

    }

}
