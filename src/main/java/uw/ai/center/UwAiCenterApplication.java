package uw.ai.center;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import uw.common.app.AppBootStrap;

@SpringBootApplication
@EnableDiscoveryClient
class UwAiCenterApplication {

    public static void main(String[] args) {
        AppBootStrap.run(UwAiCenterApplication.class, args);
    }
}
