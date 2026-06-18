package uw.ai.center.controller.rpc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uw.ai.center.entity.AiModelApi;
import uw.ai.center.entity.AiModelConfig;
import uw.ai.center.vendor.AiVendorHelper;
import uw.ai.rpc.AiConfigRpc;
import uw.ai.vo.AiModelApiVo;
import uw.ai.vo.AiModelConfigVo;
import uw.auth.service.annotation.MscPermDeclare;
import uw.auth.service.annotation.ResponseAdviceIgnore;
import uw.auth.service.constant.UserType;
import uw.common.response.ResponseData;
import uw.dao.DaoManager;
import uw.common.data.PageList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI配置查询RPC接口。
 */
@RestController
@Tag(name = "ConfigRPC接口")
@RequestMapping("/rpc/config")
@Primary
@ResponseAdviceIgnore
public class AiConfigRpcController implements AiConfigRpc {

    private static final Logger log = LoggerFactory.getLogger(AiConfigRpcController.class);

    private static final DaoManager dao = DaoManager.getInstance();

    /**
     * 根据saas信息获取模型配置列表。
     */
    @Override
    @GetMapping("/listModelConfigBySaas")
    @Operation(summary = "根据saas信息获取模型配置列表", description = "根据saas信息获取模型配置列表")
    @MscPermDeclare(user = UserType.RPC)
    public ResponseData<List<AiModelConfigVo>> listModelConfigBySaas(@RequestParam Long saasId, @RequestParam(required = false) Long mchId) {
        if (saasId == null || saasId < 0) {
            return ResponseData.errorMsg("saasId有误");
        }
        StringBuilder sql = new StringBuilder("select * from ai_model_config where state=1 and saas_id=?");
        List<Object> args = new ArrayList<>();
        args.add(saasId);
        if (mchId != null && mchId > 0) {
            sql.append(" and mch_id=?");
            args.add(mchId);
        }
        PageList<AiModelConfig> pageList = dao.list(AiModelConfig.class, sql.toString(), args.toArray()).getData();
        if (pageList == null || pageList.isEmpty()) {
            return ResponseData.success(List.of());
        }
        return ResponseData.success(pageList.stream().
                map(this::toModelInfoVo).
                collect(Collectors.toList()));
    }

    /**
     * 根据API配置获取模型配置列表。
     */
    @Override
    @GetMapping("/listModelConfigByApi")
    @Operation(summary = "根据API配置获取模型配置列表", description = "根据API配置ID获取模型配置列表")
    @MscPermDeclare(user = UserType.RPC)
    public ResponseData<List<AiModelConfigVo>> listModelConfigByApi(@RequestParam(required = false) Long apiId,
                                                                    @RequestParam(required = false) String apiCode) {
        if ((apiId == null || apiId <= 0) && StringUtils.isBlank(apiCode)) {
            return ResponseData.errorMsg("配置Id有误");
        }
        Long targetApiId = AiVendorHelper.resolveApiId(apiId == null ? 0 : apiId, apiCode);
        if (targetApiId == null || targetApiId <= 0) {
            return ResponseData.errorMsg("API配置不存在或未启用");
        }
        PageList<AiModelConfig> pageList = dao.list(AiModelConfig.class,
                "select * from ai_model_config where state=1 and api_id=?",
                new Object[]{targetApiId}).getData();
        if (pageList == null || pageList.isEmpty()) {
            return ResponseData.success(List.of());
        }
        return ResponseData.success(pageList.stream().
                map(this::toModelInfoVo).
                collect(Collectors.toList()));
    }

    /**
     * 根据ID或配置代码获取模型配置
     */
    @Override
    @GetMapping("/getModelConfig")
    @Operation(summary = "根据ID或配置代码获取模型配置", description = "根据ID或配置代码获取模型配置")
    @MscPermDeclare(user = UserType.RPC)
    public ResponseData<AiModelConfigVo> getModelConfig(@RequestParam(required = false) Long id,
                                                        @RequestParam(required = false) String configCode) {
        if ((id == null || id <= 0) && StringUtils.isBlank(configCode)) {
            return ResponseData.errorMsg("id 和 configCode 不能同时为空");
        }
        Long configId = AiVendorHelper.resolveConfigId(id == null ? 0 : id, configCode);
        if (configId == null || configId <= 0) {
            return ResponseData.errorMsg("模型配置不存在或未启用");
        }
        var configData = AiVendorHelper.getModelConfigData(configId);
        if (configData == null || configData.getAiModelConfig() == null) {
            return ResponseData.errorMsg("模型配置不存在或未启用");
        }
        return ResponseData.success(toModelInfoVo(configData.getAiModelConfig()));
    }

    /**
     * 根据模型类型和标签获取模型配置列表。
     */
    @Override
    @GetMapping("/listModelConfigByType")
    @Operation(summary = "根据模型类型和标签获取模型配置列表", description = "根据模型类型和标签获取模型配置列表")
    @MscPermDeclare(user = UserType.RPC)
    public ResponseData<List<AiModelConfigVo>> listModelConfigByType(@RequestParam String modelType, @RequestParam(required = false) String modelTag) {
        if (StringUtils.isBlank(modelType)) {
            return ResponseData.errorMsg("模型类型输入有误");
        }
        StringBuilder sql = new StringBuilder("select * from ai_model_config where state=1 and model_type=?");
        List<Object> args = new ArrayList<>();
        args.add(modelType);
        if (StringUtils.isNotBlank(modelTag)) {
            sql.append(" and model_tag=?");
            args.add(modelTag);
        }
        PageList<AiModelConfig> pageList = dao.list(AiModelConfig.class, sql.toString(), args.toArray()).getData();
        if (pageList == null || pageList.isEmpty()) {
            return ResponseData.success(List.of());
        }
        return ResponseData.success(pageList.stream().
                map(this::toModelInfoVo).
                collect(Collectors.toList()));
    }

    /**
     * 根据saas信息获取API连接配置列表。
     */
    @Override
    @GetMapping("/listApiConfigBySaas")
    @Operation(summary = "根据saas信息获取API连接配置列表", description = "根据saas信息获取API连接配置列表")
    @MscPermDeclare(user = UserType.RPC)
    public ResponseData<List<AiModelApiVo>> listApiConfigBySaas(@RequestParam Long saasId, @RequestParam(required = false) Long mchId) {
        if (saasId == null || saasId < 0) {
            return ResponseData.errorMsg("saasId输入有误");
        }
        StringBuilder sql = new StringBuilder("select * from ai_model_api where state=1 and saas_id=?");
        List<Object> args = new ArrayList<>();
        args.add(saasId);
        if (mchId != null && mchId > 0) {
            sql.append(" and mch_id=?");
            args.add(mchId);
        }
        PageList<AiModelApi> pageList = dao.list(AiModelApi.class, sql.toString(), args.toArray()).getData();
        if (pageList == null || pageList.isEmpty()) {
            return ResponseData.success(List.of());
        }
        return ResponseData.success(pageList.stream().
                map(this::toModelApiVo).
                collect(Collectors.toList()));
    }

    /**
     * 根据ID或配置代码获取API连接配置（走 FusionCache 单对象缓存）。
     */
    @Override
    @GetMapping("/getApiConfig")
    @Operation(summary = "根据ID或配置代码获取API连接配置", description = "根据ID或配置代码获取API连接配置")
    @MscPermDeclare(user = UserType.RPC)
    public ResponseData<AiModelApiVo> getApiConfig(@RequestParam(required = false) Long id,
                                                   @RequestParam(required = false) String apiCode) {
        if ((id == null || id <= 0) && StringUtils.isBlank(apiCode)) {
            return ResponseData.errorMsg("id 和 apiCode 不能同时为空");
        }
        Long apiId = AiVendorHelper.resolveApiId(id == null ? 0 : id, apiCode);
        if (apiId == null || apiId <= 0) {
            return ResponseData.errorMsg("API配置不存在或未启用");
        }
        AiModelApi api = AiVendorHelper.getApiConfig(apiId);
        if (api == null) {
            return ResponseData.errorMsg("API配置不存在或未启用");
        }
        return ResponseData.success(toModelApiVo(api));
    }

    /**
     * AiModelConfig 实体 → AiModelConfigVo
     */
    private AiModelConfigVo toModelInfoVo(AiModelConfig config) {
        AiModelConfigVo vo = new AiModelConfigVo();
        vo.setId(config.getId());
        vo.setSaasId(config.getSaasId());
        vo.setMchId(config.getMchId());
        vo.setApiId(config.getApiId());
        vo.setModelType(config.getModelType());
        vo.setModelTag(config.getModelTag());
        vo.setConfigCode(config.getConfigCode());
        vo.setConfigName(config.getConfigName());
        vo.setConfigDesc(config.getConfigDesc());
        vo.setModelName(config.getModelName());
        vo.setState(config.getState());
        vo.setCreateDate(config.getCreateDate());
        vo.setModifyDate(config.getModifyDate());
        return vo;
    }

    /**
     * AiModelApi 实体 → AiModelApiVo
     */
    private AiModelApiVo toModelApiVo(AiModelApi api) {
        AiModelApiVo vo = new AiModelApiVo();
        vo.setId(api.getId());
        vo.setSaasId(api.getSaasId());
        vo.setMchId(api.getMchId());
        vo.setApiCode(api.getApiCode());
        vo.setApiName(api.getApiName());
        vo.setApiDesc(api.getApiDesc());
        vo.setApiUrl(api.getApiUrl());
        vo.setApiKey(AiModelApi.maskApiKey(api.getApiKey()));
        vo.setState(api.getState());
        vo.setCreateDate(api.getCreateDate());
        vo.setModifyDate(api.getModifyDate());
        return vo;
    }
}
