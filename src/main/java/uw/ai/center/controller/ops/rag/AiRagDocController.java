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
import uw.common.dto.ResponseData;
import uw.common.util.JsonUtils;
import uw.dao.DaoManager;
import uw.dao.DataList;

import java.util.Date;
import java.util.Map;


/**
 * rag文档信息管理。
 */
@RestController
@RequestMapping("/saas/rag/lib/doc")
@Tag(name = "rag文档信息管理", description = "rag文档信息增删改查列管理")
@MscPermDeclare(user = UserType.OPS)
public class AiRagDocController {

    private final DaoManager dao = DaoManager.getInstance();

    /**
     * 列表rag文档信息。
     *
     * @param queryParam
     * @return
     *
     */
    @GetMapping("/list")
    @Operation(summary = "列表rag文档信息", description = "列表rag文档信息")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<DataList<AiRagDoc>> list(AiRagDocQueryParam queryParam){
        AuthServiceHelper.logRef(AiRagDoc.class);
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
    public ResponseData<DataList<AiRagDoc>> liteList(AiRagDocQueryParam queryParam){
        queryParam.SELECT_SQL( "SELECT id,saas_id,lib_id,doc_type,doc_name,doc_body_size,doc_content_size,create_date,modify_date,state from ai_rag_doc " );
        return dao.list(AiRagDoc.class, queryParam);
    }

    /**
     * 加载rag文档信息。
     *
     * @param id
     *
     */
    @GetMapping("/load")
    @Operation(summary = "加载rag文档信息", description = "加载rag文档信息")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<AiRagDoc> load(@Parameter(description = "主键ID", required = true) @RequestParam long id)  {
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
    public ResponseData<DataList<SysDataHistory>> listDataHistory(SysDataHistoryQueryParam queryParam){
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
    public ResponseData<DataList<SysCritLog>> listCritLog(SysCritLogQueryParam queryParam)  {
        AuthServiceHelper.logRef(AiRagDoc.class, queryParam.getBizId());
        queryParam.setBizTypeClass(AiRagDoc.class);
        return dao.list(SysCritLog.class, queryParam);
    }

    /**
     * 新增rag文档信息。
     *
     * @return
     *
     */
    @PostMapping("/save")
    @Operation(summary = "新增rag文档信息", description = "新增rag文档信息")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData<AiRagDoc> save(@RequestParam long libId, @RequestParam String docDesc, @RequestParam MultipartFile docFile) {
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
        //保存历史记录
        return dao.save( aiRagDoc ).onSuccess(savedEntity -> {
            SysDataHistoryHelper.saveHistory(aiRagDoc);
        });
    }



    /**
     * 启用rag文档信息。
     *
     * @param id
     *
     */
    @PutMapping("/enable")
    @Operation(summary = "启用rag文档信息", description = "启用rag文档信息")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData enable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiRagDoc.class,id,remark);
        return dao.update(new AiRagDoc().modifyDate(new Date()).state(CommonState.ENABLED.getValue()), new IdStateQueryParam(id, CommonState.DISABLED.getValue())).onSuccess(updatedEntity -> {
            AiRagService.rebuildDocument( id );

        });
    }

    /**
     * 禁用rag文档信息。
     *
     * @param id
     *
     */
    @PutMapping("/disable")
    @Operation(summary = "禁用rag文档信息", description = "禁用rag文档信息")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData disable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiRagDoc.class,id,remark);
        return dao.update(new AiRagDoc().modifyDate(new Date()).state(CommonState.DISABLED.getValue()), new IdStateQueryParam(id, CommonState.ENABLED.getValue())).onSuccess(updatedEntity -> {
            AiRagService.deleteDocument( id );
        });
    }

    /**
     * 删除rag文档信息。
     *
     * @param id
     *
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除rag文档信息", description = "删除rag文档信息")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData delete(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiRagDoc.class,id,remark);
        return dao.update(new AiRagDoc().modifyDate(new Date()).state(CommonState.DELETED.getValue()), new IdStateQueryParam(id, CommonState.DISABLED.getValue()));
    }

}