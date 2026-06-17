package uw.ai.center.controller.rpc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.*;
import uw.ai.center.entity.AiToolInfo;
import uw.ai.center.tool.AiToolHelper;
import uw.ai.rpc.AiToolRpc;
import uw.ai.vo.AiToolMeta;
import uw.auth.service.annotation.MscPermDeclare;
import uw.auth.service.constant.UserType;
import uw.common.app.constant.CommonState;
import uw.common.response.ResponseData;
import uw.dao.DaoManager;
import uw.common.data.PageList;

import java.util.List;

/**
 * AI工具RPC接口。
 */
@RestController
@Tag(name = "ToolRPC接口")
@RequestMapping("/rpc/tool")
@Primary
public class AiToolRpcController implements AiToolRpc {

    private static final Logger logger = LoggerFactory.getLogger(AiToolRpcController.class);
    /**
     * DaoManager.
     */
    private final DaoManager dao = DaoManager.getInstance();


    /**
     * 列出指定appName下的tool列表。
     */

    @Override
    @GetMapping("/listToolMeta")
    @Operation(summary = "列出tool列表", description = "列出tool列表")
    @MscPermDeclare(user = UserType.RPC)
    public ResponseData<List<AiToolMeta>> listToolMeta(@RequestParam String appName) {
        PageList<AiToolInfo> dataPageList;
        if (StringUtils.isNotBlank(appName)) {
            dataPageList = dao.list(AiToolInfo.class, "select * from ai_tool_info where app_name=?", new Object[]{appName}).getData();
        } else {
            dataPageList = dao.list(AiToolInfo.class, "select * from ai_tool_info where state=?", new Object[]{CommonState.ENABLED.getValue()}).getData();
        }
        if (dataPageList == null || dataPageList.isEmpty()) {
            return ResponseData.success(List.of());
        }
        List<AiToolMeta> aiToolMetaList = dataPageList.stream().map(x -> new AiToolMeta(x.getId(), x.getAppName(), x.getToolClass(), x.getToolVersion(), x.getToolName(), x.getToolDesc(), x.getToolInput(), x.getToolOutput())).toList();
        return ResponseData.success(aiToolMetaList);
    }

    /**
     * 更新tool配置信息。
     *
     * @return
     */
    @Override
    @PostMapping(value = "/updateToolMeta")
    @MscPermDeclare(user = UserType.RPC)
    public ResponseData updateToolMeta(@RequestBody AiToolMeta aiToolMeta) {
        if (aiToolMeta == null) {
            return ResponseData.warnMsg("参数错误！");
        }
        try {
            if (aiToolMeta.getId() <= 0) {
                long count = dao.queryForValue(Long.class, "select count(*) from ai_tool_info where app_name=? and tool_class=? and state=?", new Object[]{aiToolMeta.getAppName(), aiToolMeta.getToolClass(), CommonState.ENABLED.getValue()}).getData();
                if (count > 0) {
                    return ResponseData.errorMsg("toolClass已经存在！请传递完整ID！");
                }
                AiToolInfo aiToolConfig = new AiToolInfo();
                aiToolConfig.setId(dao.getSequenceId(AiToolInfo.class));
                aiToolConfig.setAppName(aiToolMeta.getAppName());
                aiToolConfig.setToolClass(aiToolMeta.getToolClass());
                aiToolConfig.setToolVersion(aiToolMeta.getToolVersion());
                aiToolConfig.setToolName(aiToolMeta.getToolName());
                aiToolConfig.setToolDesc(aiToolMeta.getToolDesc());
                aiToolConfig.setToolInput(aiToolMeta.getToolInput());
                aiToolConfig.setToolOutput(aiToolMeta.getToolOutput());
                aiToolConfig.setCreateDate(new java.util.Date());
                aiToolConfig.setModifyDate(new java.util.Date());
                aiToolConfig.setState(CommonState.ENABLED.getValue());
                dao.save(aiToolConfig);
            } else {
                dao.load(AiToolInfo.class, aiToolMeta.getId()).onSuccess(aiToolConfigDb -> {
                    aiToolConfigDb.setToolClass(aiToolMeta.getToolClass());
                    aiToolConfigDb.setToolVersion(aiToolMeta.getToolVersion());
                    aiToolConfigDb.setToolName(aiToolMeta.getToolName());
                    aiToolConfigDb.setToolDesc(aiToolMeta.getToolDesc());
                    aiToolConfigDb.setToolInput(aiToolMeta.getToolInput());
                    aiToolConfigDb.setToolOutput(aiToolMeta.getToolOutput());
                    aiToolConfigDb.setModifyDate(new java.util.Date());
                    dao.update(aiToolConfigDb);
                });
            }
            AiToolHelper.invalidateToolCache();
            return ResponseData.success();
        } catch (Throwable e) {
            logger.error("更新tool配置信息失败！", e);
            return ResponseData.errorMsg("更新tool配置信息失败！");
        }
    }
}
