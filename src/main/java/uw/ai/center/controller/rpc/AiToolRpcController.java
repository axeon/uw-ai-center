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
import uw.ai.center.util.SecurityUtils;
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
     * 列出指定 appName 下的工具元数据；appName 为空时返回所有启用工具。
     *
     * @param appName 应用名（为空则查全部启用工具）
     * @return 工具元数据列表
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
     * 新增或更新 tool 配置（id&lt;=0 新增，否则更新），并失效工具缓存。
     *
     * @param aiToolMeta 工具元数据
     * @return 操作结果
     */
    @Override
    @PostMapping(value = "/updateToolMeta")
    @MscPermDeclare(user = UserType.RPC)
    public ResponseData updateToolMeta(@RequestBody AiToolMeta aiToolMeta) {
        if (aiToolMeta == null) {
            return ResponseData.warnMsg("参数错误！");
        }
        // SSRF 防护：appName 必须为合法服务名（仅字母/数字/连字符），防止注册时把 appName 设为恶意域名/IP
        if (!SecurityUtils.isValidServiceName(aiToolMeta.getAppName())) {
            return ResponseData.errorMsg("非法 appName: " + aiToolMeta.getAppName());
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
                // 更新前先校验：传入 appName 必须与 DB 中原 appName 一致，
                // 防止某个微服务通过传 id 越权修改/覆盖其他微服务注册的工具
                AiToolInfo existing = dao.load(AiToolInfo.class, aiToolMeta.getId()).getData();
                if (existing == null) {
                    return ResponseData.errorMsg("工具不存在或已被删除");
                }
                if (!existing.getAppName().equals(aiToolMeta.getAppName())) {
                    logger.warn("越权更新被拒绝: 工具 appName 不一致, toolId={}, dbAppName={}, reqAppName={}",
                            aiToolMeta.getId(), existing.getAppName(), aiToolMeta.getAppName());
                    return ResponseData.errorMsg("无权修改其他应用注册的工具");
                }
                // appName/toolClass 是工具主键（缓存key=appName/toolClass），
                // 修改会导致历史对话中引用的 toolCode 失效，此处禁止修改
                existing.setToolVersion(aiToolMeta.getToolVersion());
                existing.setToolName(aiToolMeta.getToolName());
                existing.setToolDesc(aiToolMeta.getToolDesc());
                existing.setToolInput(aiToolMeta.getToolInput());
                existing.setToolOutput(aiToolMeta.getToolOutput());
                existing.setModifyDate(new java.util.Date());
                dao.update(existing);
            }
            AiToolHelper.invalidateToolCache();
            return ResponseData.success();
        } catch (Throwable e) {
            logger.error("更新tool配置信息失败！", e);
            return ResponseData.errorMsg("更新tool配置信息失败！");
        }
    }
}
