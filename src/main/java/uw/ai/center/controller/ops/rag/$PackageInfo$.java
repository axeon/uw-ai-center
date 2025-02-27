package uw.ai.center.controller.ops.rag;

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

    @GetMapping("/ops/rag")
    @Operation(summary = "RAG管理", description = "RAG管理")
    @MscPermDeclare(user = UserType.OPS)
    public void info() {
    }

}
