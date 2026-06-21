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
import uw.common.response.ResponseData;
import uw.common.util.SystemClock;
import uw.dao.DaoManager;
import uw.common.data.PageList;

import java.util.List;


/**
 * rag文档库管理。
 * <p>运维（OPS）角色的 RAG 文档库增删改查接口，路径前缀 {@code /ops/rag/lib}。
 */
@RestController
@RequestMapping("/ops/rag/lib")
@Tag(name = "RAG文档库", description = "RAG文档库")
@MscPermDeclare(user = UserType.OPS)
public class AiRagLibController {

    private final DaoManager dao = DaoManager.getInstance();


    /**
     * 分页列表rag文档库。
     *
     * @param queryParam 查询参数
     * @return RAG 文档库分页列表
     */
    @GetMapping("/list")
    @Operation(summary = "列表rag文档库", description = "列表rag文档库")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<PageList<AiRagLib>> list(AiRagLibQueryParam queryParam){
        AuthServiceHelper.logRef(AiRagLib.class);
        return dao.list(AiRagLib.class, queryParam);
    }

    /**
     * 轻量级列表rag文档库（仅关键列，不含 libConfig 等大字段），一般用于前端 select 控件。
     *
     * @param queryParam 查询参数
     * @return RAG 文档库分页列表（精简字段）
     */
    @GetMapping("/listLite")
    @Operation(summary = "轻量级列表rag文档库", description = "轻量级列表rag文档库，一般用于select控件。")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.USER, log = ActionLog.NONE)
    public ResponseData<PageList<AiRagLib>> listLite(AiRagLibQueryParam queryParam){
        queryParam.SELECT_SQL( "SELECT id,saas_id,lib_type,lib_name,embed_config_id,embed_model_name,create_date,modify_date,state from ai_rag_lib " );
        return dao.list(AiRagLib.class, queryParam);
    }

    /**
     * 按主键加载单条rag文档库。
     *
     * @param id 主键ID
     * @return RAG 文档库
     */
    @GetMapping("/load")
    @Operation(summary = "加载rag文档库", description = "加载rag文档库")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<AiRagLib> load(@Parameter(description = "主键ID", required = true) @RequestParam long id)  {
        AuthServiceHelper.logRef(AiRagLib.class,id);
        return dao.queryForObject(AiRagLib.class, new AuthIdQueryParam(id));
    }


    /**
     * 查询指定 RAG 文档库的检索结果（向量 + BM25 双路融合）。
     *
     * @param id    RAG 文档库ID
     * @param query 用户查询文本
     * @return 拼接的检索结果文本
     */
    @GetMapping("/query")
    @Operation(summary = "查询文档库", description = "查询文档库")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.USER, log = ActionLog.REQUEST)
    public ResponseData<String> queryLib(@RequestParam long id, @RequestParam String query) {
        String data = AiRagService.query( id, query );
        return ResponseData.success( data );
    }

    /**
     * 加载 RAG 文档库的配置参数模板（供前端动态渲染配置表单）。
     *
     * @return 配置参数列表
     */
    @GetMapping("/loadConfigParam")
    @Operation(summary = "加载配置参数", description = "加载配置参数")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.USER, log = ActionLog.NONE)
    public List<JsonConfigParam> loadConfigParam() {
        return AiRagService.RAG_LIB_CONFIG_PARAMS;
    }


    /**
     * 查询指定rag文档库的数据变更历史。
     *
     * @param queryParam 历史查询参数（按 entityId 过滤）
     * @return 数据历史分页列表
     */
    @GetMapping("/listDataHistory")
    @Operation(summary = "查询数据历史", description = "查询数据历史")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<PageList<SysDataHistory>> listDataHistory(SysDataHistoryQueryParam queryParam){
        AuthServiceHelper.logRef(AiRagLib.class, queryParam.getEntityId());
        queryParam.setEntityClass(AiRagLib.class);
        return dao.list(SysDataHistory.class, queryParam);
    }

    /**
     * 查询指定rag文档库的关键操作日志。
     *
     * @param queryParam 日志查询参数（按 bizId 过滤）
     * @return 操作日志分页列表
     */
    @GetMapping("/listCritLog")
    @Operation(summary = "查询操作日志", description = "查询操作日志")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.REQUEST)
    public ResponseData<PageList<SysCritLog>> listCritLog(SysCritLogQueryParam queryParam)  {
        AuthServiceHelper.logRef(AiRagLib.class, queryParam.getBizId());
        queryParam.setBizTypeClass(AiRagLib.class);
        return dao.list(SysCritLog.class, queryParam);
    }

    /**
     * 新增rag文档库。
     * <p>saasId 绑定当前租户；保存后记录数据历史。
     *
     * @param aiRagLib RAG 文档库（libName/libType/embedConfigId/libConfig 等）
     * @return 保存后的 RAG 文档库
     */
    @PostMapping("/save")
    @Operation(summary = "新增rag文档库", description = "新增rag文档库")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData<AiRagLib> save(@RequestBody AiRagLib aiRagLib){
        long id = dao.getSequenceId(AiRagLib.class);
        AuthServiceHelper.logRef(AiRagLib.class,id);
        aiRagLib.setId(id);
        aiRagLib.setSaasId(AuthServiceHelper.getSaasId());
        aiRagLib.setCreateDate(SystemClock.nowDate());
        aiRagLib.setModifyDate(null);
        aiRagLib.setState(CommonState.ENABLED.getValue());
        //保存历史记录
        return dao.save( aiRagLib ).onSuccess(savedEntity -> {
            SysDataHistoryHelper.saveHistory(aiRagLib);
        });
    }


    /**
     * 修改rag文档库。
     * <p>更新后失效 RAG 客户端缓存，使下次请求按最新配置重建 AiRagClientWrapper。
     *
     * @param aiRagLib 待更新的 RAG 文档库
     * @param remark   操作备注（记入日志与历史）
     * @return 更新后的 RAG 文档库
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
            aiRagLibDb.setModifyDate(SystemClock.nowDate());
            return dao.update( aiRagLibDb ).onSuccess(updatedEntity -> {
                SysDataHistoryHelper.saveHistory( aiRagLibDb,remark );
                // 修改RAG库配置后失效缓存，使下次请求重新构建AiRagClientWrapper
                AiRagService.invalidateRagClientCache(aiRagLib.getId());
            } );
        } );
    }

    /**
     * 启用rag文档库（状态：禁用 → 启用），并失效 RAG 客户端缓存。
     *
     * @param id     主键ID
     * @param remark 操作备注
     * @return 操作结果
     */
    @PutMapping("/enable")
    @Operation(summary = "启用rag文档库", description = "启用rag文档库")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData enable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiRagLib.class,id,remark);
        return dao.update(new AiRagLib().modifyDate(SystemClock.nowDate()).state(CommonState.ENABLED.getValue()), new IdStateQueryParam(id, CommonState.DISABLED.getValue()))
            .onSuccess(v -> { AiRagService.invalidateRagClientCache(id); });
    }

    /**
     * 禁用rag文档库（状态：启用 → 禁用），并失效 RAG 客户端缓存。
     *
     * @param id     主键ID
     * @param remark 操作备注
     * @return 操作结果
     */
    @PutMapping("/disable")
    @Operation(summary = "禁用rag文档库", description = "禁用rag文档库")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData disable(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiRagLib.class,id,remark);
        return dao.update(new AiRagLib().modifyDate(SystemClock.nowDate()).state(CommonState.DISABLED.getValue()), new IdStateQueryParam(id, CommonState.ENABLED.getValue()))
            .onSuccess(v -> { AiRagService.invalidateRagClientCache(id); });
    }

    /**
     * 删除rag文档库（软删除：状态 → 已删除），并同步删除 ES 索引避免残留无用数据。
     *
     * @param id     主键ID
     * @param remark 操作备注
     * @return 操作结果
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除rag文档库", description = "删除rag文档库")
    @MscPermDeclare(user = UserType.OPS, auth = AuthType.PERM, log = ActionLog.CRIT)
    public ResponseData delete(@Parameter(description = "主键ID") @RequestParam long id, @Parameter(description = "备注") @RequestParam String remark){
        AuthServiceHelper.logInfo(AiRagLib.class,id,remark);
        return dao.update(new AiRagLib().modifyDate(SystemClock.nowDate()).state(CommonState.DELETED.getValue()), new IdStateQueryParam(id, CommonState.DISABLED.getValue()))
            // 删除RAG库时同步删除ES索引，避免ES中残留无用数据
            .onSuccess(v -> { AiRagService.deleteLib(id); });
    }

}
