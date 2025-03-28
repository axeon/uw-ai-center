package uw.ai.center.controller.saas.rag;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import uw.ai.center.dto.AiRagLibQueryParam;
import uw.ai.center.entity.AiRagLib;
import uw.ai.center.service.AiRagService;
import uw.app.common.dto.AuthIdQueryParam;
import uw.app.common.dto.SysCritLogQueryParam;
import uw.app.common.dto.SysDataHistoryQueryParam;
import uw.app.common.entity.SysCritLog;
import uw.app.common.entity.SysDataHistory;
import uw.app.common.helper.SysDataHistoryHelper;
import uw.auth.service.AuthServiceHelper;
import uw.auth.service.annotation.MscPermDeclare;
import uw.auth.service.constant.ActionLog;
import uw.auth.service.constant.AuthType;
import uw.auth.service.constant.UserType;
import uw.common.constant.StateCommon;
import uw.common.dto.ResponseData;
import uw.dao.DaoFactory;
import uw.dao.DataList;
import uw.dao.TransactionException;

import java.util.Date;


/**
 * rag文档库管理。
 */
@RestController
@RequestMapping("/saas/rag/lib")
@Tag(name = "rag文档库管理", description = "rag文档库增删改查列管理")
@MscPermDeclare(user = UserType.SAAS)
public class AiRagLibController {

    private final DaoFactory dao = DaoFactory.getInstance();

    /**
     * 列表rag文档库。
     *
     * @param queryParam
     * @return
     * @throws TransactionException
     */
    @GetMapping("/list")
    @Operation(summary = "列表rag文档库", description = "列表rag文档库")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public DataList<AiRagLib> list(AiRagLibQueryParam queryParam) throws TransactionException {
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
    public DataList<AiRagLib> liteList(AiRagLibQueryParam queryParam) throws TransactionException {
        queryParam.SELECT_SQL( "SELECT id,saas_id,user_id,user_type,user_info,lib_type,lib_name,create_date,modify_date,state from ai_rag_lib " );
        return dao.list(AiRagLib.class, queryParam);
    }

    /**
     * 加载rag文档库。
     *
     * @param id
     * @throws TransactionException
     */
    @GetMapping("/load")
    @Operation(summary = "加载rag文档库", description = "加载rag文档库")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public AiRagLib load(@Parameter(description = "主键ID", required = true) @RequestParam long id) throws TransactionException {
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
    public DataList<SysDataHistory> listDataHistory(SysDataHistoryQueryParam queryParam) throws TransactionException {
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
    public DataList<SysCritLog> listCritLog(SysCritLogQueryParam queryParam) throws TransactionException {
        AuthServiceHelper.logRef(AiRagLib.class, queryParam.getRefId());
        queryParam.setRefTypeClass(AiRagLib.class);
        return dao.list(SysCritLog.class, queryParam);
    }

    /**
     * 新增rag文档库。
     *
     * @param aiRagLib
     * @return
     * @throws TransactionException
     */
    @PostMapping("/save")
    @Operation(summary = "新增rag文档库", description = "新增rag文档库")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData<AiRagLib> save(@RequestBody AiRagLib aiRagLib) throws TransactionException {
        long id = dao.getSequenceId(AiRagLib.class);
        AuthServiceHelper.logRef(AiRagLib.class,id);
        aiRagLib.setId(id);
        aiRagLib.setSaasId(AuthServiceHelper.getSaasId());
        aiRagLib.setCreateDate(new Date());
        aiRagLib.setModifyDate(null);
        aiRagLib.setState(1);
        dao.save(aiRagLib);
        //保存历史记录
        SysDataHistoryHelper.saveHistory(aiRagLib.getId(),aiRagLib,"rag文档库","新增rag文档库");
        return ResponseData.success(aiRagLib);
    }

    /**
     * 修改rag文档库。
     *
     * @param aiRagLib
     * @return
     * @throws TransactionException
     */
    @PutMapping("/update")
    @Operation(summary = "修改rag文档库", description = "修改rag文档库")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData<AiRagLib> update(@RequestBody AiRagLib aiRagLib, @Parameter(description = "备注") @RequestParam String remark) throws TransactionException {
        AuthServiceHelper.logInfo(AiRagLib.class,aiRagLib.getId(),"修改rag文档库！操作备注："+remark);
        AiRagLib aiRagLibDb = dao.queryForSingleObject(AiRagLib.class, new AuthIdQueryParam(aiRagLib.getId()));
        if (aiRagLibDb == null) {
            return ResponseData.warnMsg("未找到指定ID的rag文档库！");
        }
        aiRagLibDb.setLibType(aiRagLib.getLibType());
        aiRagLibDb.setLibName(aiRagLib.getLibName());
        aiRagLibDb.setLibDesc(aiRagLib.getLibDesc());
        aiRagLibDb.setEmbedConfigId(aiRagLib.getEmbedConfigId());
        aiRagLibDb.setEmbedModelName(aiRagLib.getEmbedModelName());
        aiRagLibDb.setLibConfig( aiRagLib.getLibConfig() );
        aiRagLibDb.setModifyDate(new Date());
        dao.update(aiRagLibDb);
        SysDataHistoryHelper.saveHistory(aiRagLibDb.getId(),aiRagLibDb,"rag文档库","修改rag文档库！操作备注："+remark);
        return ResponseData.success(aiRagLibDb);
    }
    
    /**
     * 启用rag文档库。
     *
     * @param id
     * @throws TransactionException
     */
    @PutMapping("/enable")
    @Operation(summary = "启用rag文档库", description = "启用rag文档库")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData enable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark) throws TransactionException {
        AuthServiceHelper.logInfo(AiRagLib.class,id,"启用rag文档库！操作备注："+remark);
        AiRagLib aiRagLib = dao.queryForSingleObject(AiRagLib.class, new AuthIdQueryParam(id));
        if (aiRagLib == null) {
            return ResponseData.warnMsg("未找到指定id的rag文档库！");
        }
        if (aiRagLib.getState()!=StateCommon.DISABLED.getValue()){
            return ResponseData.warnMsg("启用rag文档库失败！当前状态不是禁用状态！");                
        }
        aiRagLib.setModifyDate(new Date());
        aiRagLib.setState(StateCommon.ENABLED.getValue());
        dao.update(aiRagLib);
        return ResponseData.success();
    }

    /**
     * 禁用rag文档库。
     *
     * @param id
     * @throws TransactionException
     */
    @PutMapping("/disable")
    @Operation(summary = "禁用rag文档库", description = "禁用rag文档库")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData disable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark) throws TransactionException {
        AuthServiceHelper.logInfo(AiRagLib.class,id,"禁用rag文档库！操作备注："+remark);
        AiRagLib aiRagLib = dao.queryForSingleObject(AiRagLib.class, new AuthIdQueryParam(id));
        if (aiRagLib == null) {
            return ResponseData.warnMsg("未找到指定id的rag文档库！");
        }			
        if (aiRagLib.getState()!=StateCommon.ENABLED.getValue()){
            return ResponseData.warnMsg("禁用rag文档库失败！当前状态不是启用状态！");                
        }            
        aiRagLib.setModifyDate(new Date());
        aiRagLib.setState(StateCommon.DISABLED.getValue());
        dao.update(aiRagLib);
        return ResponseData.success();
    }

    /**
     * 删除rag文档库。
     *
     * @param id
     * @throws TransactionException
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除rag文档库", description = "删除rag文档库")
    @MscPermDeclare(user = UserType.SAAS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData delete(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark) throws TransactionException {
        AuthServiceHelper.logInfo(AiRagLib.class,id,"删除rag文档库！操作备注："+remark);
        AiRagLib aiRagLib = dao.queryForSingleObject(AiRagLib.class, new AuthIdQueryParam(id));
        if (aiRagLib == null) {
            return ResponseData.warnMsg("未找到指定id的rag文档库！");
        }
        if (aiRagLib.getState()!=StateCommon.DISABLED.getValue()){
            return ResponseData.warnMsg("删除rag文档库失败！当前状态不是禁用状态！");
        }            
        aiRagLib.setModifyDate(new Date());
        aiRagLib.setState(StateCommon.DELETED.getValue());
        dao.update(aiRagLib);
        AiRagService.delLib( aiRagLib.getId() );
        return ResponseData.success();
    }

}