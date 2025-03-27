package uw.ai.center.vendor;

import uw.ai.center.vo.AiModelConfigData;
import uw.common.vo.ConfigParam;

import java.util.List;

/**
 * Ai供应商接口。
 */
public interface AiVendor {

    /**
     * 供应商名称
     */
    String vendorName();

    /**
     * 供应商描述
     */
    String vendorDesc();

    /**
     * 供应商版本
     */
    String vendorVersion();

    /**
     * 供应商图标
     */
    String vendorIcon();

    /**
     * 供应商类名
     */
    default String vendorClass() {
        return this.getClass().getSimpleName();
    }

    /**
     * Vendor参数信息集合，管理员可见。
     */
    List<ConfigParam> vendorParam();

    /**
     * model参数信息集合，管理员可见。
     */
    List<ConfigParam> modelParam();

    /**
     * embed参数信息集合，仅管理员可见。
     */
    List<ConfigParam> embedParam();

    /**
     * 构造模型实例。
     *
     * @param aiModelConfigData
     * @return
     */
    AiVendorClientWrapper buildClientWrapper(AiModelConfigData aiModelConfigData);

    /**
     * 获取模型列表。
     * @return
     */
    List<String> listModel(String apiUrl,String apiKey);

}
