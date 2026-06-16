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
import uw.ai.rpc.AiConfigRpc;
import uw.ai.vo.AiModelApiVo;
import uw.ai.vo.AiModelInfoVo;
import uw.auth.service.annotation.MscPermDeclare;
import uw.auth.service.annotation.ResponseAdviceIgnore;
import uw.auth.service.constant.UserType;
import uw.common.app.constant.CommonState;
import uw.common.response.ResponseData;
import uw.dao.DaoManager;

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

    private final DaoManager dao = DaoManager.getInstance();

    /**
     * 根据saas信息获取模型配置列表。
     */
    @Override
    @GetMapping("/listModelConfigBySaas")
    @Operation(summary = "根据saas信息获取模型配置列表", description = "根据saas信息获取模型配置列表")
    @MscPermDeclare(user = UserType.RPC)
    public ResponseData<List<AiModelInfoVo>> listModelConfigBySaas(@RequestParam Long saasId, @RequestParam Long mchId) {
        if(saasId == null || saasId < 0){
            return ResponseData.errorMsg("租户Id有误");
        }
        List<AiModelConfig> configList;
        if (mchId != null && mchId > 0) {
            configList = dao.list(AiModelConfig.class,
                    "select * from ai_model_config where saas_id=? and mch_id=? and state=?",
                    new Object[]{saasId, mchId, CommonState.ENABLED.getValue()}).getData().list();
        } else {
            configList = dao.list(AiModelConfig.class,
                    "select * from ai_model_config where saas_id=? and state=?",
                    new Object[]{saasId, CommonState.ENABLED.getValue()}).getData().list();
        }
        List<AiModelInfoVo> result = configList.stream().map(this::toModelInfoVo).collect(Collectors.toList());
        return ResponseData.success(result);
    }

    /**
     * 根据API配置获取模型配置列表。
     */
    @Override
    @GetMapping("/listModelConfigByApi")
    @Operation(summary = "根据API配置获取模型配置列表", description = "根据API配置ID获取模型配置列表")
    @MscPermDeclare(user = UserType.RPC)
    public ResponseData<List<AiModelInfoVo>> listModelConfigByApi(@RequestParam(required = false) Long apiId,
                                                                  @RequestParam(required = false) String apiCode) {
        if ((apiId == null || apiId <= 0) && StringUtils.isBlank(apiCode)) {
            return ResponseData.errorMsg("配置Id有误");
        }
        List<AiModelConfig> configList;
        if (apiId != null && apiId > 0) {
            configList = dao.list(AiModelConfig.class,
                    "select * from ai_model_config where api_id=? and state=?",
                    new Object[]{apiId, CommonState.ENABLED.getValue()}).getData().list();
        } else {
            configList = dao.list(AiModelConfig.class,
                    "select c.* from ai_model_config c left join ai_model_api a on c.api_id=a.id " +
                            "where a.api_code=? and c.state=? and a.state=?",
                    new Object[]{apiCode, CommonState.ENABLED.getValue(), CommonState.ENABLED.getValue()}).getData().list();
        }
        List<AiModelInfoVo> result = configList.stream().map(this::toModelInfoVo).collect(Collectors.toList());
        return ResponseData.success(result);
    }

    /**
     * 根据ID或配置代码获取模型配置
     */
    @Override
    @GetMapping("/getModelConfig")
    @Operation(summary = "根据ID或配置代码获取模型配置", description = "根据ID或配置代码获取模型配置")
    @MscPermDeclare(user = UserType.RPC)
    public ResponseData<AiModelInfoVo> getModelConfig(@RequestParam(required = false) Long id,
                                                      @RequestParam(required = false) String configCode) {
        if ((id == null || id <= 0) && StringUtils.isBlank(configCode)) {
            return ResponseData.errorMsg("id 和 configCode 不能同时为空");
        }
        AiModelConfig config;
        if (id != null && id > 0) {
            config = dao.queryForObject(AiModelConfig.class,
                    "select * from ai_model_config where id=? and state=?",
                    new Object[]{id, CommonState.ENABLED.getValue()}).getData();
        } else {
            config = dao.queryForObject(AiModelConfig.class,
                    "select * from ai_model_config where config_code=? and state=?",
                    new Object[]{configCode, CommonState.ENABLED.getValue()}).getData();
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
    public ResponseData<List<AiModelInfoVo>> listModelConfigByType(@RequestParam String modelType, @RequestParam(required = false) String modelTag) {
        if (StringUtils.isBlank(modelType)){
            return ResponseData.errorMsg("模型类型输入有误");
        }
        List<AiModelConfig> configList;
        if (StringUtils.isNotBlank(modelTag)) {
            configList = dao.list(AiModelConfig.class,
                    "select * from ai_model_config where model_type=? and model_tag=? and state=?",
                    new Object[]{modelType, modelTag, CommonState.ENABLED.getValue()}).getData().list();
        } else {
            configList = dao.list(AiModelConfig.class,
                    "select * from ai_model_config where model_type=? and state=?",
                    new Object[]{modelType, CommonState.ENABLED.getValue()}).getData().list();
        }
        List<AiModelInfoVo> result = configList.stream().map(this::toModelInfoVo).collect(Collectors.toList());
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
        List<AiModelApi> apiList;
        if (mchId != null && mchId > 0) {
            apiList = dao.list(AiModelApi.class,
                    "select * from ai_model_api where saas_id=? and mch_id=? and state=?",
                    new Object[]{saasId, mchId, CommonState.ENABLED.getValue()}).getData().list();
        } else {
            apiList = dao.list(AiModelApi.class,
                    "select * from ai_model_api where saas_id=? and state=?",
                    new Object[]{saasId, CommonState.ENABLED.getValue()}).getData().list();
        }
        List<AiModelApiVo> result = apiList.stream().map(this::toModelApiVo).collect(Collectors.toList());
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
        AiModelApi api;
        if (id != null && id > 0) {
            api = dao.queryForObject(AiModelApi.class,
                    "select * from ai_model_api where id=? and state=?",
                    new Object[]{id, CommonState.ENABLED.getValue()}).getData();
        } else {
            api = dao.queryForObject(AiModelApi.class,
                    "select * from ai_model_api where api_code=? and state=?",
                    new Object[]{apiCode, CommonState.ENABLED.getValue()}).getData();
        }
        if (api == null) {
            return ResponseData.errorMsg("API配置不存在或未启用");
        }
        return ResponseData.success(toModelApiVo(api));
    }

    /**
     * AiModelConfig 实体 → AiModelInfoVo
     */
    private AiModelInfoVo toModelInfoVo(AiModelConfig config) {
        AiModelInfoVo vo = new AiModelInfoVo();
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
