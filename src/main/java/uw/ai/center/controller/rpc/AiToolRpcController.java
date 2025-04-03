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
import uw.auth.service.annotation.ResponseAdviceIgnore;
import uw.auth.service.constant.UserType;
import uw.app.common.constant.CommonState;
import uw.common.dto.ResponseData;
import uw.dao.DaoFactory;

import java.util.List;

@RestController
@Tag(name = "ToolRPC接口")
@RequestMapping("/rpc/tool")
@Primary
@ResponseAdviceIgnore
public class AiToolRpcController implements AiToolRpc {

    private static final Logger logger = LoggerFactory.getLogger( AiToolRpcController.class );
    /**
     * DaoFactory.
     */
    private final DaoFactory dao = DaoFactory.getInstance();


    /**
     * 列出指定appName下的tool列表。
     */

    @Override
    @GetMapping("/listToolMeta")
    @Operation(summary = "列出tool列表", description = "列出tool列表")
    @MscPermDeclare(user = UserType.RPC)
    public ResponseData<List<AiToolMeta>> listToolMeta(@RequestParam String appName)  {
        try {
            List<AiToolInfo> dataList = null;
            if (StringUtils.isNotBlank( appName )) {
                dataList = dao.list( AiToolInfo.class, "select * from ai_tool_info where app_name=?", new Object[]{appName} ).results();
            } else {
                dataList = dao.list( AiToolInfo.class, "select * from ai_tool_info where state=?", new Object[]{CommonState.ENABLED.getValue()} ).results();
            }
            List<AiToolMeta> aiToolMetaList = dataList.stream().map( x -> new AiToolMeta( x.getId(), x.getAppName(), x.getToolClass(), x.getToolVersion(), x.getToolName(), x.getToolDesc(), x.getToolInput(), x.getToolOutput() ) ).toList();
            return ResponseData.success( aiToolMetaList );
        } catch (Throwable e) {
            logger.error( "查询tool列表失败！", e );
            return ResponseData.errorMsg( "查询tool列表失败！"+e.getMessage() );
        }
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
            return ResponseData.warnMsg( "参数错误！" );
        }
        try {
            AiToolInfo aiToolConfig = null;
            if (aiToolMeta.getId() <= 0) {
                long count = dao.queryForSingleValue( Long.class, "select count(*) from ai_tool_info where app_name=? and tool_class=? and state=?",
                        new Object[]{aiToolMeta.getAppName(), aiToolMeta.getToolClass(), CommonState.ENABLED.getValue()} );
                if (count > 0) {
                    return ResponseData.errorMsg( "toolClass已经存在！请传递完整ID！" );
                }
                aiToolConfig = new AiToolInfo();
                aiToolConfig.setId( dao.getSequenceId( AiToolInfo.class ) );
                aiToolConfig.setAppName( aiToolMeta.getAppName() );
                aiToolConfig.setToolClass( aiToolMeta.getToolClass() );
                aiToolConfig.setToolVersion( aiToolMeta.getToolVersion() );
                aiToolConfig.setToolName( aiToolMeta.getToolName() );
                aiToolConfig.setToolDesc( aiToolMeta.getToolDesc() );
                aiToolConfig.setToolInput( aiToolMeta.getToolInput() );
                aiToolConfig.setToolOutput( aiToolMeta.getToolOutput() );
                aiToolConfig.setCreateDate( new java.util.Date() );
                aiToolConfig.setModifyDate( new java.util.Date() );
                aiToolConfig.setState( CommonState.ENABLED.getValue() );
                dao.save( aiToolConfig );
            } else {
                aiToolConfig = dao.load( AiToolInfo.class, aiToolMeta.getId() );
                aiToolConfig.setToolClass( aiToolMeta.getToolClass() );
                aiToolConfig.setToolVersion( aiToolMeta.getToolVersion() );
                aiToolConfig.setToolName( aiToolMeta.getToolName() );
                aiToolConfig.setToolDesc( aiToolMeta.getToolDesc() );
                aiToolConfig.setToolInput( aiToolMeta.getToolInput() );
                aiToolConfig.setToolOutput( aiToolMeta.getToolOutput() );
                aiToolConfig.setModifyDate( new java.util.Date() );
                dao.update( aiToolConfig );
            }
            AiToolHelper.invalidateToolCache();
            return ResponseData.success();
        } catch (Throwable e) {
            logger.error( "更新tool配置信息失败！", e );
            return ResponseData.errorMsg( "更新tool配置信息失败！"+e.getMessage() );
        }
    }
}
