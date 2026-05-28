package uw.ai.center.controller.rpc;

// 当前内网Maven仓库尚无 uw.ai.rpc.AiConfigRpc 和 uw.ai.vo.AiModelConfigVo

/*
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uw.ai.center.entity.AiModelConfig;
import uw.ai.rpc.AiConfigRpc;
import uw.ai.vo.AiModelConfigVo;
import uw.auth.service.annotation.MscPermDeclare;
import uw.auth.service.annotation.ResponseAdviceIgnore;
import uw.auth.service.constant.UserType;
import uw.common.app.constant.CommonState;
import uw.common.dto.ResponseData;
import uw.dao.DaoManager;
import uw.dao.DataList;

import java.util.ArrayList;
import java.util.List;

@RestController
@Tag(name = "AI配置RPC接口")
@RequestMapping("/rpc/config")
@Primary
@ResponseAdviceIgnore
public class AiConfigRpcController implements AiConfigRpc {

    private final DaoManager dao = DaoManager.getInstance();

    @Override
    @GetMapping("/listModelConfig")
    @Operation(summary = "获取所有可用的模型配置列表")
    @MscPermDeclare(user = UserType.RPC)
    public ResponseData<List<AiModelConfigVo>> listModelConfig() {
        DataList<AiModelConfig> list = dao.list(AiModelConfig.class,
                "select * from ai_model_config where state=?", new Object[]{CommonState.ENABLED.getValue()}).getData();
        List<AiModelConfigVo> result = new ArrayList<>();
        if (list != null) {
            for (AiModelConfig config : list) {
                AiModelConfigVo vo = new AiModelConfigVo();
                vo.setId(config.getId());
                vo.setModelType(config.getModelType());
                vo.setConfigCode(config.getConfigCode());
                vo.setConfigName(config.getConfigName());
                vo.setConfigDesc(config.getConfigDesc());
                vo.setModelName(config.getModelName());
                vo.setVendorClass(config.getVendorClass());
                vo.setState(config.getState());
                vo.setCreateDate(config.getCreateDate());
                result.add(vo);
            }
        }
        return ResponseData.success(result);
    }
}
*/
