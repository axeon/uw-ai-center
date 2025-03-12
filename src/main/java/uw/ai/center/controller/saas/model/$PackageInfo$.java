package uw.ai.center.controller.saas.model;

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

    @GetMapping("/saas/model")
    @Operation(summary = "模型管理", description = "模型管理")
    @MscPermDeclare(user = UserType.SAAS)
    public void info() {
    }

}
