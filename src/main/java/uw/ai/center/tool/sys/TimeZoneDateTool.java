package uw.ai.center.tool.sys;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Component;
import uw.ai.tool.AiTool;
import uw.ai.tool.AiToolParam;
import uw.common.dto.ResponseData;
import uw.common.util.DateUtils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * 时区日期工具。
 */
@Component
public class TimeZoneDateTool implements AiTool<TimeZoneDateTool.ToolParam, ResponseData> {

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
        return "0.0.1";
    }


    /**
     * Applies this function to the given argument.
     *
     * @param toolParam the function argument
     * @return the function result
     */
    @Override
    public ResponseData apply(TimeZoneDateTool.ToolParam toolParam) {
        ZoneId zoneId = ZoneId.of( toolParam.getTimeZone() );
        ZonedDateTime zonedNow = ZonedDateTime.now( zoneId);
        String data = zonedNow.toString() + "@" + toolParam.getTimeZone();
        return ResponseData.success( data );
    }

    /**
     * 工具参数。
     */
    public static class ToolParam extends AiToolParam {

        private String timeZone;

        public String getTimeZone() {
            return timeZone;
        }

        public void setTimeZone(String timeZone) {
            this.timeZone = timeZone;
        }

    }

}
