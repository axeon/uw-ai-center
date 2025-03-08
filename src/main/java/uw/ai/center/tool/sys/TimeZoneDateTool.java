package uw.ai.center.tool.sys;

import org.springframework.stereotype.Component;
import uw.ai.tool.AiTool;
import uw.common.util.DateUtils;

import java.util.Date;

/**
 * 时区日期工具。
 */
@Component
public class TimeZoneDateTool implements AiTool<String, String> {
    /**
     * 定义工具名称。
     *
     * @return
     */
    @Override
    public String name() {
        return "日期工具";
    }

    /**
     * 定义工具描述。
     *
     * @return
     */
    @Override
    public String desc() {
        return "日期工具，返回当前日期";
    }

    /**
     * 定义工具版本。
     *
     * @return
     */
    @Override
    public String version() {
        return "0.0.1";
    }

    /**
     * Applies this function to the given argument.
     *
     * @param timeZone the function argument
     * @return the function result
     */
    @Override
    public String apply(String timeZone) {
        return DateUtils.dateToString( new Date(), DateUtils.DATE_TIME_MILLIS ) + "@" + timeZone;
    }
}
