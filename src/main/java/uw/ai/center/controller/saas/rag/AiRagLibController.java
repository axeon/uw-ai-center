package uw.ai.center.controller.saas.rag;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import uw.ai.center.dto.AiRagLibQueryParam;
import uw.ai.center.entity.AiRagLib;
import uw.auth.service.AuthServiceHelper;
import uw.auth.service.annotation.MscPermDeclare;
import uw.auth.service.constant.ActionLog;
import uw.auth.service.constant.AuthType;
import uw.auth.service.constant.UserType;
import uw.common.app.constant.CommonState;
import uw.common.app.dto.*;
import uw.common.app.entity.SysCritLog;
import uw.common.app.entity.SysDataHistory;
import uw.common.app.helper.SysDataHistoryHelper;
import uw.common.dto.ResponseData;
import uw.dao.DaoManager;
import uw.dao.DataList;

import java.util.Date;


/**
 * rag文档库管理。
 */
@RestController
@RequestMapping("/saas/rag/lib")
@Tag(name = "RAG文档库", description = "RAG文档库")
@MscPermDeclare(user = UserType.SAAS)
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
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.REQUEST)
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
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.USER, log = ActionLog.NONE)
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
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<AiRagLib> load(@Parameter(description = "主键ID", required = true) @RequestParam long id)  {
        AuthServiceHelper.logRef(AiRagLib.class,id);
        return dao.queryForSingleObject(AiRagLib.class, new AuthIdQueryParam(id));
    }

    /**
     * 查询数据历史。
     *
     * @param
     * @return
     */
    @GetMapping("/listDataHistory")
    @Operation(summary = "查询数据历史", description = "查询数据历史")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.REQUEST)
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
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.REQUEST)
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
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
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
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
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
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData enable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiRagLib.class,id,remark);
        return dao.update(new AiRagLib().modifyDate(new Date()).state(CommonState.ENABLED.getValue()), new AuthIdStateQueryParam(id, CommonState.DISABLED.getValue()));
    }

    /**
     * 禁用rag文档库。
     *
     * @param id
     *
     */
    @PutMapping("/disable")
    @Operation(summary = "禁用rag文档库", description = "禁用rag文档库")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData disable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiRagLib.class,id,remark);
        return dao.update(new AiRagLib().modifyDate(new Date()).state(CommonState.DISABLED.getValue()), new AuthIdStateQueryParam(id, CommonState.ENABLED.getValue()));
    }

    /**
     * 删除rag文档库。
     *
     * @param id
     *
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除rag文档库", description = "删除rag文档库")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData delete(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiRagLib.class,id,remark);
        return dao.update(new AiRagLib().modifyDate(new Date()).state(CommonState.DELETED.getValue()), new AuthIdStateQueryParam(id, CommonState.DISABLED.getValue()));
    }

}