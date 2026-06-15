package uw.ai.center.controller.rpc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uw.ai.center.entity.AiModelApi;
import uw.ai.center.entity.AiModelConfig;
import uw.ai.rpc.AiConfigRpc;
import uw.ai.vo.AiApiConfigVo;
import uw.ai.vo.AiModelConfigVo;
import uw.auth.service.annotation.MscPermDeclare;
import uw.auth.service.annotation.ResponseAdviceIgnore;
import uw.auth.service.constant.UserType;
import uw.common.app.constant.CommonState;
import uw.common.response.ResponseData;
import uw.dao.DaoManager;

import java.util.ArrayList;
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

    private final DaoManager dao = DaoManager.getInstance();

    /**
     * 获取所有可用的模型配置列表。
     */
    @Override
    @GetMapping("/listModelConfig")
    @Operation(summary = "获取所有可用的模型配置列表", description = "获取所有可用的模型配置列表")
    @MscPermDeclare(user = UserType.RPC)
    public ResponseData<List<AiModelConfigVo>> listModelConfig() {
        List<AiModelConfig> configList = dao.list(AiModelConfig.class,
                "select * from ai_model_config where state=?", new Object[]{CommonState.ENABLED.getValue()}).getData().list();
        Map<Long, AiModelApi> apiMap = dao.list(AiModelApi.class,
                "select * from ai_model_api where state=?", new Object[]{CommonState.ENABLED.getValue()}).getData().list()
                .stream().collect(Collectors.toMap(AiModelApi::getId, api -> api, (a, b) -> a));
        List<AiModelConfigVo> result = new ArrayList<>();
        if (configList != null) {
            for (AiModelConfig config : configList) {
                AiModelApi api = apiMap.get(config.getApiId());
                AiModelConfigVo vo = new AiModelConfigVo();
                vo.setId(config.getId());
                vo.setModelType(config.getModelType());
                vo.setConfigCode(config.getConfigCode());
                vo.setConfigName(config.getConfigName());
                vo.setConfigDesc(config.getConfigDesc());
                vo.setModelName(config.getModelName());
                vo.setVendorClass(config.getVendorClass());
                vo.setApiId(config.getApiId());
                vo.setApiCode(api != null ? api.getApiCode() : "");
                vo.setApiName(api != null ? api.getApiName() : "");
                vo.setApiUrl(api != null ? api.getApiUrl() : "");
                vo.setState(config.getState());
                vo.setCreateDate(config.getCreateDate());
                result.add(vo);
            }
        }
        return ResponseData.success(result);
    }

    /**
     * 获取所有可用的API连接配置列表。
     */
    @Override
    @GetMapping("/listApiConfig")
    @Operation(summary = "获取所有可用的API连接配置列表", description = "获取所有可用的API连接配置列表")
    @MscPermDeclare(user = UserType.RPC)
    public ResponseData<List<AiApiConfigVo>> listApiConfig() {
        List<AiModelApi> list = dao.list(AiModelApi.class,
                "select * from ai_model_api where state=?", new Object[]{CommonState.ENABLED.getValue()}).getData().list();
        List<AiApiConfigVo> result = new ArrayList<>();
        if (list != null) {
            for (AiModelApi api : list) {
                AiApiConfigVo vo = new AiApiConfigVo();
                vo.setId(api.getId());
                vo.setApiCode(api.getApiCode());
                vo.setApiName(api.getApiName());
                vo.setApiDesc(api.getApiDesc());
                vo.setApiUrl(api.getApiUrl());
                vo.setState(api.getState());
                vo.setCreateDate(api.getCreateDate());
                result.add(vo);
            }
        }
        return ResponseData.success(result);
    }
}
