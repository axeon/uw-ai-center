package uw.ai.center.controller.saas.session;

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

    @GetMapping("/saas/session")
    @Operation(summary = "会话管理", description = "会话管理")
    @MscPermDeclare(user = UserType.SAAS)
    public void info() {
    }

}
