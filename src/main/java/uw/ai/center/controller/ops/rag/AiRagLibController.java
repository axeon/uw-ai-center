package uw.ai.center.controller.ops.rag;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import uw.ai.center.dto.AiRagLibQueryParam;
import uw.ai.center.entity.AiRagLib;
import uw.ai.center.service.AiRagService;
import uw.auth.service.AuthServiceHelper;
import uw.auth.service.annotation.MscPermDeclare;
import uw.auth.service.constant.ActionLog;
import uw.auth.service.constant.AuthType;
import uw.auth.service.constant.UserType;
import uw.common.app.constant.CommonState;
import uw.common.app.dto.AuthIdQueryParam;
import uw.common.app.dto.IdStateQueryParam;
import uw.common.app.dto.SysCritLogQueryParam;
import uw.common.app.dto.SysDataHistoryQueryParam;
import uw.common.app.entity.SysCritLog;
import uw.common.app.entity.SysDataHistory;
import uw.common.app.helper.SysDataHistoryHelper;
import uw.common.app.vo.JsonConfigParam;
import uw.common.dto.ResponseData;
import uw.dao.DaoManager;
import uw.dao.DataList;

import java.util.Date;
import java.util.List;


/**
 * rag文档库管理。
 */
@RestController
@RequestMapping("/ops/rag/lib")
@Tag(name = "RAG文档库", description = "RAG文档库")
@MscPermDeclare(user = UserType.OPS)
public class AiRagLibController {

    private final DaoManager dao = DaoManager.getInstance();


    /**
     * 列表rag文档库。
     *
     * @param queryParam
     * @return
     *
     */
    @GetMapping("/list")
    @Operation(summary = "列表rag文档库", description = "列表rag文档库")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<DataList<AiRagLib>> list(AiRagLibQueryParam queryParam){
        AuthServiceHelper.logRef(AiRagLib.class);
        return dao.list(AiRagLib.class, queryParam);
    }

    /**
     * 轻量级列表rag文档库，一般用于select控件。
     *
     * @return
     */
    @GetMapping("/liteList")
    @Operation(summary = "轻量级列表rag文档库", description = "轻量级列表rag文档库，一般用于select控件。")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.USER, log = ActionLog.NONE)
    public ResponseData<DataList<AiRagLib>> liteList(AiRagLibQueryParam queryParam){
        queryParam.SELECT_SQL( "SELECT id,saas_id,lib_type,lib_name,embed_config_id,embed_model_name,create_date,modify_date,state from ai_rag_lib " );
        return dao.list(AiRagLib.class, queryParam);
    }

    /**
     * 加载rag文档库。
     *
     * @param id
     *
     */
    @GetMapping("/load")
    @Operation(summary = "加载rag文档库", description = "加载rag文档库")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<AiRagLib> load(@Parameter(description = "主键ID", required = true) @RequestParam long id)  {
        AuthServiceHelper.logRef(AiRagLib.class,id);
        return dao.queryForSingleObject(AiRagLib.class, new AuthIdQueryParam(id));
    }


    /**
     * 查询rag文档信息。
     *
     * @param query
     * @throws
     */
    @GetMapping("/query")
    @Operation(summary = "查询文档库", description = "查询文档库")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.USER, log = ActionLog.REQUEST)
    public ResponseData<String> queryLib(@RequestParam long id, @RequestParam String query) {
        String data = AiRagService.query( id, query );
        return ResponseData.success( data );
    }

    /**
     * 加载配置参数。
     *
     * @return
     */
    @GetMapping("/loadConfigParam")
    @Operation(summary = "加载配置参数", description = "加载配置参数")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.USER, log = ActionLog.NONE)
    public List<JsonConfigParam> loadConfigParam() {
        return AiRagService.RAG_LIB_CONFIG_PARAMS;
    }


    /**
     * 查询数据历史。
     *
     * @param
     * @return
     */
    @GetMapping("/listDataHistory")
    @Operation(summary = "查询数据历史", description = "查询数据历史")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<DataList<SysDataHistory>> listDataHistory(SysDataHistoryQueryParam queryParam){
        AuthServiceHelper.logRef(AiRagLib.class, queryParam.getEntityId());
        queryParam.setEntityClass(AiRagLib.class);
        return dao.list(SysDataHistory.class, queryParam);
    }

    /**
     * 查询操作日志。
     *
     * @param
     * @return
     */
    @GetMapping("/listCritLog")
    @Operation(summary = "查询操作日志", description = "查询操作日志")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<DataList<SysCritLog>> listCritLog(SysCritLogQueryParam queryParam)  {
        AuthServiceHelper.logRef(AiRagLib.class, queryParam.getBizId());
        queryParam.setBizTypeClass(AiRagLib.class);
        return dao.list(SysCritLog.class, queryParam);
    }

    /**
     * 新增rag文档库。
     *
     * @param aiRagLib
     * @return
     *
     */
    @PostMapping("/save")
    @Operation(summary = "新增rag文档库", description = "新增rag文档库")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData<AiRagLib> save(@RequestBody AiRagLib aiRagLib){
        long id = dao.getSequenceId(AiRagLib.class);
        AuthServiceHelper.logRef(AiRagLib.class,id);
        aiRagLib.setId(id);
        aiRagLib.setSaasId(AuthServiceHelper.getSaasId());
        aiRagLib.setCreateDate(new Date());
        aiRagLib.setModifyDate(null);
        aiRagLib.setState(CommonState.ENABLED.getValue());
        //保存历史记录
        return dao.save( aiRagLib ).onSuccess(savedEntity -> {
            SysDataHistoryHelper.saveHistory(aiRagLib);
        });
    }


    /**
     * 修改rag文档库。
     *
     * @param aiRagLib
     * @return
     *
     */
    @PutMapping("/update")
    @Operation(summary = "修改rag文档库", description = "修改rag文档库")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData<AiRagLib> update(@RequestBody AiRagLib aiRagLib, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiRagLib.class,aiRagLib.getId(),remark);
        return  dao.load( AiRagLib.class, aiRagLib.getId() ).onSuccess(aiRagLibDb-> {
            aiRagLibDb.setLibType(aiRagLib.getLibType());
            aiRagLibDb.setLibName(aiRagLib.getLibName());
            aiRagLibDb.setLibDesc(aiRagLib.getLibDesc());
            aiRagLibDb.setEmbedConfigId(aiRagLib.getEmbedConfigId());
            aiRagLibDb.setEmbedModelName(aiRagLib.getEmbedModelName());
            aiRagLibDb.setLibConfig(aiRagLib.getLibConfig());
            aiRagLibDb.setModifyDate(new Date());
            return dao.update( aiRagLibDb ).onSuccess(updatedEntity -> {
                SysDataHistoryHelper.saveHistory( aiRagLibDb,remark );
            } );
        } );
    }

    /**
     * 启用rag文档库。
     *
     * @param id
     *
     */
    @PutMapping("/enable")
    @Operation(summary = "启用rag文档库", description = "启用rag文档库")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData enable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiRagLib.class,id,remark);
        return dao.update(new AiRagLib().modifyDate(new Date()).state(CommonState.ENABLED.getValue()), new IdStateQueryParam(id, CommonState.DISABLED.getValue()));
    }

    /**
     * 禁用rag文档库。
     *
     * @param id
     *
     */
    @PutMapping("/disable")
    @Operation(summary = "禁用rag文档库", description = "禁用rag文档库")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData disable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiRagLib.class,id,remark);
        return dao.update(new AiRagLib().modifyDate(new Date()).state(CommonState.DISABLED.getValue()), new IdStateQueryParam(id, CommonState.ENABLED.getValue()));
    }

    /**
     * 删除rag文档库。
     *
     * @param id
     *
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除rag文档库", description = "删除rag文档库")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData delete(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiRagLib.class,id,remark);
        return dao.update(new AiRagLib().modifyDate(new Date()).state(CommonState.DELETED.getValue()), new IdStateQueryParam(id, CommonState.DISABLED.getValue()));
    }

}