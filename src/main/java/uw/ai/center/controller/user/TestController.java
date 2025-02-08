package uw.ai.center.controller.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试接口。
 */
@RestController
@RequestMapping("/test/")
@Tag(name = "测试接口")
@Profile({"test", "dev"})
public class TestController {


}
