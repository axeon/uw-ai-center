package uw.ai.center.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import uw.ai.center.vendor.AiVendor;
import uw.common.app.vo.JsonConfigParam;

import java.util.List;

/**
 * AI供应商信息VO，用于接口返回，避免直接序列化AiVendor接口导致Jackson异常。
 */
@Schema(title = "AI供应商信息", description = "AI供应商信息")
public class AiVendorInfo {

    @JsonProperty("vendorName")
    @Schema(title = "供应商名称", description = "供应商名称")
    private final String vendorName;

    @JsonProperty("vendorDesc")
    @Schema(title = "供应商描述", description = "供应商描述")
    private final String vendorDesc;

    @JsonProperty("vendorVersion")
    @Schema(title = "供应商版本", description = "供应商版本")
    private final String vendorVersion;

    @JsonProperty("vendorIcon")
    @Schema(title = "供应商图标", description = "供应商图标")
    private final String vendorIcon;

    @JsonProperty("vendorClass")
    @Schema(title = "供应商类名", description = "供应商类名")
    private final String vendorClass;

    @JsonProperty("configParam")
    @Schema(title = "配置参数信息集合", description = "配置参数信息集合")
    private final List<JsonConfigParam> configParam;

    public AiVendorInfo(AiVendor vendor) {
        this.vendorName = vendor.vendorName();
        this.vendorDesc = vendor.vendorDesc();
        this.vendorVersion = vendor.vendorVersion();
        this.vendorIcon = vendor.vendorIcon();
        this.vendorClass = vendor.vendorClass();
        this.configParam = vendor.configParam();
    }

    public String getVendorName() {
        return vendorName;
    }

    public String getVendorDesc() {
        return vendorDesc;
    }

    public String getVendorVersion() {
        return vendorVersion;
    }

    public String getVendorIcon() {
        return vendorIcon;
    }

    public String getVendorClass() {
        return vendorClass;
    }

    public List<JsonConfigParam> getConfigParam() {
        return configParam;
    }
}
