package uw.ai.center.controller.admin.rag;

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
import uw.common.response.ResponseData;
import uw.common.util.JsonUtils;
import uw.common.util.SystemClock;
import uw.dao.DaoManager;
import uw.common.data.PageList;

import java.util.Map;


/**
 * rag文档信息管理。
 * <p>平台管理（ADMIN）角色的 RAG 文档增删改查接口，路径前缀 {@code /admin/rag/lib/doc}。
 */
@RestController
@RequestMapping("/admin/rag/lib/doc")
@Tag(name = "rag文档信息管理", description = "rag文档信息增删改查列管理")
@MscPermDeclare(user = UserType.ADMIN)
public class AiRagDocController {

    private final DaoManager dao = DaoManager.getInstance();

    /**
     * 分页列表rag文档信息。
     *
     * @param queryParam 查询参数
     * @return RAG 文档分页列表
     */
    @GetMapping("/list")
    @Operation(summary = "列表rag文档信息", description = "列表rag文档信息")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<PageList<AiRagDoc>> list(AiRagDocQueryParam queryParam){
        AuthServiceHelper.logRef(AiRagDoc.class);
        return dao.list(AiRagDoc.class, queryParam);
    }

    /**
     * 轻量级列表rag文档信息（仅关键列，不含 docContent 等大字段），一般用于前端 select 控件。
     *
     * @param queryParam 查询参数
     * @return RAG 文档分页列表（精简字段）
     */
    @GetMapping("/listLite")
    @Operation(summary = "轻量级列表rag文档信息", description = "轻量级列表rag文档信息，一般用于select控件。")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.USER, log = ActionLog.NONE)
    public ResponseData<PageList<AiRagDoc>> listLite(AiRagDocQueryParam queryParam){
        queryParam.SELECT_SQL( "SELECT id,saas_id,lib_id,doc_type,doc_name,doc_body_size,doc_content_size,create_date,modify_date,state from ai_rag_doc " );
        return dao.list(AiRagDoc.class, queryParam);
    }

    /**
     * 按主键加载单条rag文档信息。
     *
     * @param id 主键ID
     * @return RAG 文档
     */
    @GetMapping("/load")
    @Operation(summary = "加载rag文档信息", description = "加载rag文档信息")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<AiRagDoc> load(@Parameter(description = "主键ID", required = true) @RequestParam long id)  {
        AuthServiceHelper.logRef(AiRagDoc.class,id);
        return dao.queryForObject(AiRagDoc.class, new AuthIdQueryParam(id));
    }

    /**
     * 查询指定rag文档的数据变更历史。
     *
     * @param queryParam 历史查询参数（按 entityId 过滤）
     * @return 数据历史分页列表
     */
    @GetMapping("/listDataHistory")
    @Operation(summary = "查询数据历史", description = "查询数据历史")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<PageList<SysDataHistory>> listDataHistory(SysDataHistoryQueryParam queryParam){
        AuthServiceHelper.logRef(AiRagDoc.class, queryParam.getEntityId());
        queryParam.setEntityClass(AiRagDoc.class);
        return dao.list(SysDataHistory.class, queryParam);
    }

    /**
     * 查询指定rag文档的关键操作日志。
     *
     * @param queryParam 日志查询参数（按 bizId 过滤）
     * @return 操作日志分页列表
     */
    @GetMapping("/listCritLog")
    @Operation(summary = "查询操作日志", description = "查询操作日志")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<PageList<SysCritLog>> listCritLog(SysCritLogQueryParam queryParam)  {
        AuthServiceHelper.logRef(AiRagDoc.class, queryParam.getBizId());
        queryParam.setBizTypeClass(AiRagDoc.class);
        return dao.list(SysCritLog.class, queryParam);
    }

    /**
     * 新增rag文档：解析上传文件、分割并向量化后入库（含 ES 向量写入）。
     * <p>文档解析或向量化失败时返回错误，不入库。
     *
     * @param libId   所属 RAG 库ID
     * @param docDesc 文档描述
     * @param docFile 上传的文档文件（由 Tika 解析）
     * @return 保存后的 RAG 文档
     */
    @PostMapping("/save")
    @Operation(summary = "新增rag文档信息", description = "新增rag文档信息")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.CRIT)
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
        if (fileContentMap == null || fileContentMap.isEmpty()) {
            return ResponseData.errorMsg("文档解析或向量化失败，请检查RAG库配置及文件内容是否为空");
        }
        aiRagDoc.setDocContent( JsonUtils.toString( fileContentMap) );
        aiRagDoc.setDocContentSize( aiRagDoc.getDocContent().length() );
        aiRagDoc.setCreateDate( SystemClock.nowDate() );
        aiRagDoc.setModifyDate( null );
        aiRagDoc.setState( CommonState.ENABLED.getValue() );
        //保存历史记录
        return dao.save( aiRagDoc ).onSuccess(savedEntity -> {
            SysDataHistoryHelper.saveHistory(aiRagDoc);
        });
    }



    /**
     * 启用rag文档（状态：禁用 → 启用），并重建 ES 向量数据。
     *
     * @param id     主键ID
     * @param remark 操作备注
     * @return 操作结果
     */
    @PutMapping("/enable")
    @Operation(summary = "启用rag文档信息", description = "启用rag文档信息")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData enable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiRagDoc.class,id,remark);
        return dao.update(new AiRagDoc().modifyDate(SystemClock.nowDate()).state(CommonState.ENABLED.getValue()), new IdStateQueryParam(id, CommonState.DISABLED.getValue())).onSuccess(updatedEntity -> {
            AiRagService.rebuildDocument( id );
        });
    }

    /**
     * 禁用rag文档（状态：启用 → 禁用），并从 ES 删除其向量数据。
     *
     * @param id     主键ID
     * @param remark 操作备注
     * @return 操作结果
     */
    @PutMapping("/disable")
    @Operation(summary = "禁用rag文档信息", description = "禁用rag文档信息")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData disable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiRagDoc.class,id,remark);
        return dao.update(new AiRagDoc().modifyDate(SystemClock.nowDate()).state(CommonState.DISABLED.getValue()), new IdStateQueryParam(id, CommonState.ENABLED.getValue())).onSuccess(updatedEntity -> {
            AiRagService.deleteDocument( id );
        });
    }

    /**
     * 删除rag文档（软删除：状态 → 已删除）。
     * <p>注：向量数据不在此删除，禁用时已移除。
     *
     * @param id     主键ID
     * @param remark 操作备注
     * @return 操作结果
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除rag文档信息", description = "删除rag文档信息")
    @MscPermDeclare(user = UserType.ADMIN, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData delete(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiRagDoc.class,id,remark);
        return dao.update(new AiRagDoc().modifyDate(SystemClock.nowDate()).state(CommonState.DELETED.getValue()), new IdStateQueryParam(id, CommonState.DISABLED.getValue()));
    }

}
