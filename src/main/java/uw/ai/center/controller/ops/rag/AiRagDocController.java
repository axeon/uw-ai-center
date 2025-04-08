package uw.ai.center.controller.ops.rag;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uw.ai.center.dto.AiRagDocQueryParam;
import uw.ai.center.entity.AiRagDoc;
import uw.ai.center.service.AiRagService;
import uw.app.common.dto.AuthIdQueryParam;
import uw.app.common.dto.SysCritLogQueryParam;
import uw.app.common.dto.SysDataHistoryQueryParam;
import uw.app.common.entity.SysCritLog;
import uw.app.common.entity.SysDataHistory;
import uw.app.common.helper.SysDataHistoryHelper;
import uw.auth.service.constant.ActionLog;
import uw.auth.service.constant.AuthType;
import uw.auth.service.constant.UserType;
import uw.app.common.constant.CommonState;
import uw.auth.service.AuthServiceHelper;
import uw.auth.service.annotation.MscPermDeclare;
import uw.common.dto.ResponseData;
import uw.common.util.JsonUtils;
import uw.dao.DaoFactory;
import uw.dao.DataList;
import uw.dao.TransactionException;

import java.util.Date;
import java.util.Map;


/**
 * rag文档信息管理。
 */
@RestController
@RequestMapping("/ops/rag/lib/doc")
@Tag(name = "rag文档信息管理", description = "rag文档信息增删改查列管理")
@MscPermDeclare(user = UserType.OPS)
public class AiRagDocController {

    private final DaoFactory dao = DaoFactory.getInstance();

    /**
     * 列表rag文档信息。
     *
     * @param queryParam
     * @return
     * @throws TransactionException
     */
    @GetMapping("/list")
    @Operation(summary = "列表rag文档信息", description = "列表rag文档信息")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public DataList<AiRagDoc> list(AiRagDocQueryParam queryParam) throws TransactionException {
        AuthServiceHelper.logRef(AiRagDoc.class);
        queryParam.SELECT_SQL( "SELECT id,saas_id,lib_id,doc_type,doc_name,doc_body_size,doc_content_size,create_date,modify_date,state from ai_rag_doc " );
        return dao.list(AiRagDoc.class, queryParam);
    }

    /**
     * 轻量级列表rag文档信息，一般用于select控件。
     *
     * @return
     */
    @GetMapping("/liteList")
    @Operation(summary = "轻量级列表rag文档信息", description = "轻量级列表rag文档信息，一般用于select控件。")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.USER, log = ActionLog.NONE)
    public DataList<AiRagDoc> liteList(AiRagDocQueryParam queryParam) throws TransactionException {
        queryParam.SELECT_SQL( "SELECT id,saas_id,lib_id,doc_type,doc_name,doc_body_size,doc_content_size,create_date,modify_date,state from ai_rag_doc " );
        return dao.list(AiRagDoc.class, queryParam);
    }


    /**
     * 加载rag文档信息。
     *
     * @param id
     * @throws TransactionException
     */
    @GetMapping("/load")
    @Operation(summary = "加载rag文档信息", description = "加载rag文档信息")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public AiRagDoc load(@Parameter(description = "主键ID", required = true) @RequestParam long id) throws TransactionException {
        AuthServiceHelper.logRef(AiRagDoc.class,id);
        return dao.queryForSingleObject(AiRagDoc.class, new AuthIdQueryParam(id));
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
    public DataList<SysDataHistory> listDataHistory(SysDataHistoryQueryParam queryParam) throws TransactionException {
        AuthServiceHelper.logRef(AiRagDoc.class, queryParam.getEntityId());
        queryParam.setEntityClass(AiRagDoc.class);
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
    public DataList<SysCritLog> listCritLog(SysCritLogQueryParam queryParam) throws TransactionException {
        AuthServiceHelper.logRef(AiRagDoc.class, queryParam.getRefId());
        queryParam.setRefTypeClass(AiRagDoc.class);
        return dao.list(SysCritLog.class, queryParam);
    }


    /**
     * 新增rag文档信息。
     *
     * @return
     * @throws TransactionException
     */
    @PostMapping("/save")
    @Operation(summary = "新增rag文档信息", description = "新增rag文档信息")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData<AiRagDoc> save(@RequestParam long libId, @RequestParam String docDesc, @RequestParam MultipartFile docFile) throws TransactionException {
        long id = dao.getSequenceId( AiRagDoc.class );
        AuthServiceHelper.logRef( AiRagDoc.class, id );
        AiRagDoc aiRagDoc = new AiRagDoc();
        aiRagDoc.setId( id );
        aiRagDoc.setSaasId( AuthServiceHelper.getSaasId() );
        aiRagDoc.setLibId( libId );
        aiRagDoc.setDocDesc( docDesc );
        aiRagDoc.setDocName( docFile.getOriginalFilename() );
        aiRagDoc.setDocType( FilenameUtils.getExtension( aiRagDoc.getDocName() ) );
        aiRagDoc.setDocBodySize( docFile.getSize() );
        //添加文档
        Map<String,String> fileContentMap = AiRagService.buildDocument( libId, docFile );
        aiRagDoc.setDocContent( JsonUtils.toString( fileContentMap) );
        aiRagDoc.setDocContentSize( aiRagDoc.getDocContent().length() );
        aiRagDoc.setCreateDate( new Date() );
        aiRagDoc.setModifyDate( null );
        aiRagDoc.setState( CommonState.ENABLED.getValue() );
        dao.save( aiRagDoc );
        //保存历史记录
        SysDataHistoryHelper.saveHistory( aiRagDoc.getId(), aiRagDoc, "rag文档信息", "新增rag文档信息" );
        return ResponseData.success( aiRagDoc );
    }

    /**
     * 启用rag文档信息。
     *
     * @param id
     * @throws TransactionException
     */
    @PutMapping("/enable")
    @Operation(summary = "启用rag文档信息", description = "启用rag文档信息")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData enable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark) throws TransactionException {
        AuthServiceHelper.logInfo(AiRagDoc.class,id,"启用rag文档信息！操作备注："+remark);
        AiRagDoc aiRagDoc = dao.queryForSingleObject(AiRagDoc.class, new AuthIdQueryParam(id));
        if (aiRagDoc == null) {
            return ResponseData.warnMsg("未找到指定id的rag文档信息！");
        }
        if (aiRagDoc.getState()!= CommonState.DISABLED.getValue()){
            return ResponseData.warnMsg("启用rag文档信息失败！当前状态不是禁用状态！");                
        }
        aiRagDoc.setModifyDate(new Date());
        aiRagDoc.setState( CommonState.ENABLED.getValue());
        dao.update(aiRagDoc);
        AiRagService.rebuildDocument( aiRagDoc.getLibId(), aiRagDoc );
        return ResponseData.success();
    }

    /**
     * 禁用rag文档信息。
     *
     * @param id
     * @throws TransactionException
     */
    @PutMapping("/disable")
    @Operation(summary = "禁用rag文档信息", description = "禁用rag文档信息")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData disable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark) throws TransactionException {
        AuthServiceHelper.logInfo(AiRagDoc.class,id,"禁用rag文档信息！操作备注："+remark);
        AiRagDoc aiRagDoc = dao.queryForSingleObject(AiRagDoc.class, new AuthIdQueryParam(id));
        if (aiRagDoc == null) {
            return ResponseData.warnMsg("未找到指定id的rag文档信息！");
        }			
        if (aiRagDoc.getState()!= CommonState.ENABLED.getValue()){
            return ResponseData.warnMsg("禁用rag文档信息失败！当前状态不是启用状态！");                
        }            
        aiRagDoc.setModifyDate(new Date());
        aiRagDoc.setState( CommonState.DISABLED.getValue());
        dao.update(aiRagDoc);
        AiRagService.deleteDocument( aiRagDoc.getLibId(), aiRagDoc );
        return ResponseData.success();
    }

    /**
     * 删除rag文档信息。
     *
     * @param id
     * @throws TransactionException
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除rag文档信息", description = "删除rag文档信息")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData delete(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark) throws TransactionException {
        AuthServiceHelper.logInfo(AiRagDoc.class,id,"删除rag文档信息！操作备注："+remark);
        AiRagDoc aiRagDoc = dao.queryForSingleObject(AiRagDoc.class, new AuthIdQueryParam(id));
        if (aiRagDoc == null) {
            return ResponseData.warnMsg("未找到指定id的rag文档信息！");
        }
        if (aiRagDoc.getState()!= CommonState.DISABLED.getValue()){
            return ResponseData.warnMsg("删除rag文档信息失败！当前状态不是禁用状态！");
        }            
        aiRagDoc.setModifyDate(new Date());
        aiRagDoc.setState( CommonState.DELETED.getValue());
        dao.update(aiRagDoc);
        return ResponseData.success();
    }

}