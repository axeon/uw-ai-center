//此处定义菜单项。
package uw.ai.center.controller.ops.log;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uw.auth.service.annotation.MscPermDeclare;
import uw.auth.service.constant.UserType;

/**
 * 主要是提供注解支持用。
 */
@RestController
public class $PackageInfo$ {
    @GetMapping("/ops/log")
    @Operation(summary = "操作日志", description = "操作日志")
    @MscPermDeclare(user = UserType.OPS)
    public void info() {
    }


}