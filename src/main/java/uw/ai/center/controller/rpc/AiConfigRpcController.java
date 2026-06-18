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

import java.util.List;
import java.util.Map;
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

    /**
     * 根据saas信息获取模型配置列表。
     */
    @Override
    @GetMapping("/listModelConfigBySaas")
    @Operation(summary = "根据saas信息获取模型配置列表", description = "根据saas信息获取模型配置列表")
    @MscPermDeclare(user = UserType.RPC)
    public ResponseData<List<AiModelConfigVo>> listModelConfigBySaas(@RequestParam Long saasId, @RequestParam(required = false) Long mchId) {
        if(saasId == null || saasId < 0){
            return ResponseData.errorMsg("租户Id有误");
        }
        Map<Long, AiModelConfig> configMap = AiVendorHelper.getEnabledModelConfigMap();
        if (configMap == null) {
            return ResponseData.success(List.of());
        }
        List<AiModelConfigVo> result = configMap.values().stream()
                .filter(c -> saasId.equals(c.getSaasId()))
                .filter(c -> mchId == null || mchId <= 0 || mchId.equals(c.getMchId()))
                .map(this::toModelInfoVo)
                .collect(Collectors.toList());
        return ResponseData.success(result);
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
        Map<Long, AiModelConfig> configMap = AiVendorHelper.getEnabledModelConfigMap();
        if (configMap == null) {
            return ResponseData.success(List.of());
        }
        // 按apiCode查询时，先从API缓存中找出对应的apiId（API需为启用状态）
        Long targetApiId = apiId;
        if ((targetApiId == null || targetApiId <= 0) && StringUtils.isNotBlank(apiCode)) {
            Map<Long, AiModelApi> apiMap = AiVendorHelper.getEnabledModelApiMap();
            if (apiMap != null) {
                targetApiId = apiMap.values().stream()
                        .filter(a -> apiCode.equals(a.getApiCode()))
                        .map(AiModelApi::getId)
                        .findFirst()
                        .orElse(null);
            }
        }
        final Long finalApiId = targetApiId;
        List<AiModelConfigVo> result = configMap.values().stream()
                .filter(c -> finalApiId != null && finalApiId.equals(c.getApiId()))
                .map(this::toModelInfoVo)
                .collect(Collectors.toList());
        return ResponseData.success(result);
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
        Map<Long, AiModelConfig> configMap = AiVendorHelper.getEnabledModelConfigMap();
        AiModelConfig config = null;
        if (configMap != null) {
            if (id != null && id > 0) {
                config = configMap.get(id);
            } else {
                config = configMap.values().stream()
                        .filter(c -> configCode.equals(c.getConfigCode()))
                        .findFirst()
                        .orElse(null);
            }
        }
        if (config == null) {
            return ResponseData.errorMsg("模型配置不存在或未启用");
        }
        return ResponseData.success(toModelInfoVo(config));
    }

    /**
     * 根据模型类型和标签获取模型配置列表。
     */
    @Override
    @GetMapping("/listModelConfigByType")
    @Operation(summary = "根据模型类型和标签获取模型配置列表", description = "根据模型类型和标签获取模型配置列表")
    @MscPermDeclare(user = UserType.RPC)
    public ResponseData<List<AiModelConfigVo>> listModelConfigByType(@RequestParam String modelType, @RequestParam(required = false) String modelTag) {
        if (StringUtils.isBlank(modelType)){
            return ResponseData.errorMsg("模型类型输入有误");
        }
        Map<Long, AiModelConfig> configMap = AiVendorHelper.getEnabledModelConfigMap();
        if (configMap == null) {
            return ResponseData.success(List.of());
        }
        List<AiModelConfigVo> result = configMap.values().stream()
                .filter(c -> modelType.equals(c.getModelType()))
                .filter(c -> StringUtils.isBlank(modelTag) || modelTag.equals(c.getModelTag()))
                .map(this::toModelInfoVo)
                .collect(Collectors.toList());
        return ResponseData.success(result);
    }

    /**
     * 根据saas信息获取API连接配置列表。
     */
    @Override
    @GetMapping("/listApiConfigBySaas")
    @Operation(summary = "根据saas信息获取API连接配置列表", description = "根据saas信息获取API连接配置列表")
    @MscPermDeclare(user = UserType.RPC)
    public ResponseData<List<AiModelApiVo>> listApiConfigBySaas(@RequestParam Long saasId, @RequestParam(required = false) Long mchId) {
        if (saasId == null || saasId < 0){
            return ResponseData.errorMsg("租户Id输入有误");
        }
        Map<Long, AiModelApi> apiMap = AiVendorHelper.getEnabledModelApiMap();
        if (apiMap == null) {
            return ResponseData.success(List.of());
        }
        List<AiModelApiVo> result = apiMap.values().stream()
                .filter(a -> saasId.equals(a.getSaasId()))
                .filter(a -> mchId == null || mchId <= 0 || mchId.equals(a.getMchId()))
                .map(this::toModelApiVo)
                .collect(Collectors.toList());
        return ResponseData.success(result);
    }

    /**
     * 根据ID或配置代码获取API连接配置
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
        Map<Long, AiModelApi> apiMap = AiVendorHelper.getEnabledModelApiMap();
        AiModelApi api = null;
        if (apiMap != null) {
            if (id != null && id > 0) {
                api = apiMap.get(id);
            } else {
                api = apiMap.values().stream()
                        .filter(a -> apiCode.equals(a.getApiCode()))
                        .findFirst()
                        .orElse(null);
            }
        }
        if (api == null) {
            return ResponseData.errorMsg("API配置不存在或未启用");
        }
        return ResponseData.success(toModelApiVo(api));
    }

    /**
     * AiModelConfig 实体 → AiModelInfoVo
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
