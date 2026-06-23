package uw.ai.center.vendor;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import uw.common.app.vo.JsonConfigParam;

import java.util.List;

/**
 * Ai供应商接口。
 */
@Schema(title = "Ai供应商接口", description = "Ai供应商接口")
public interface AiVendor {

    /**
     * 供应商名称
     */
    @JsonProperty("vendorName")
    @Schema(title = "供应商名称", description = "供应商名称")
    String vendorName();

    /**
     * 供应商描述
     */
    @JsonProperty("vendorDesc")
    @Schema(title = "供应商描述", description = "供应商描述")
    String vendorDesc();

    /**
     * 供应商版本
     */
    @JsonProperty("vendorVersion")
    @Schema(title = "供应商版本", description = "供应商版本")
    String vendorVersion();

    /**
     * 供应商图标
     */
    @JsonProperty("vendorIcon")
    @Schema(title = "供应商图标", description = "供应商图标")
    String vendorIcon();

    /**
     * 供应商类名
     */
    @JsonProperty("vendorClass")
    @Schema(title = "供应商类名", description = "供应商类名")
    default String vendorClass() {
        return this.getClass().getName();
    }

    /**
     * 配置参数信息集合，管理员可见。
     */
    @JsonProperty("configParam")
    @Schema(title = "配置参数信息集合", description = "配置参数信息集合，管理员可见。")
    List<JsonConfigParam> configParam();

    /**
     * 获取模型列表。
     */
    List<String> listModel(String apiUrl, String apiKey);

}
