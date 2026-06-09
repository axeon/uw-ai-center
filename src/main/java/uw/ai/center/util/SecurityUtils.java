package uw.ai.center.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 安全工具类，提供通用的安全校验方法。
 */
public final class SecurityUtils {

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtils.class);

    /**
     * 合法服务名正则：仅允许字母、数字、连字符，首尾必须是字母或数字。
     * 兼容单字符服务名（如 "a"）。
     * 阻止 IP地址、域名、端口号、路径遍历等SSRF攻击向量。
     */
    private static final String SERVICE_NAME_PATTERN = "^[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9]$|^[a-zA-Z0-9]$";

    private SecurityUtils() {
    }

    /**
     * 校验服务名是否合法（防SSRF）。
     * 合法的服务名仅允许字母、数字、连字符，首尾必须是字母或数字。
     * 用于校验通过服务名拼接URL的场景，防止appName被篡改为IP或恶意域名。
     *
     * @param serviceName 服务名
     * @return true=合法, false=非法
     */
    public static boolean isValidServiceName(String serviceName) {
        if (serviceName == null || serviceName.isEmpty()) {
            return false;
        }
        return serviceName.matches(SERVICE_NAME_PATTERN);
    }

    /**
     * 校验服务名是否合法（防SSRF），不合法时记录error日志。
     *
     * @param serviceName 服务名
     * @return true=合法, false=非法
     */
    public static boolean checkServiceName(String serviceName) {
        if (!isValidServiceName(serviceName)) {
            logger.error("非法服务名格式: {}", serviceName);
            return false;
        }
        return true;
    }
}
