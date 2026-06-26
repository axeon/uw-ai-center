package uw.ai.center.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import uw.ai.center.entity.AiSessionInfo;

/**
 * AiSessionInfoEx - 会话信息扩展视图对象。
 * @author tanlx
 * @since 1.2.0
 */
@Schema(title = "session会话(扩展)", description = "session会话扩展对象，附带模型类型等非数据库字段")
public class AiSessionInfoEx extends AiSessionInfo {

    /**
     * 模型类型（非数据库字段，由 Service 层从模型配置填充）。
     * 如 CHAT、IMAGE_GENERATION、EMBEDDING 等。
     */
    @Schema(title = "模型类型", description = "模型类型（CHAT/IMAGE_GENERATION/EMBEDDING等），由Service层填充", nullable = true)
    private String modelType;

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }
}
