package com.hpuswl.springboot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class CasProperties {

    @Value("${security.cas.server.host.url}")
    private String casServerUrl;

    @Value("${security.cas.server.host.login_url}")
    private String casServerLoginUrl;

    @Value("${security.cas.server.host.logout_url}")
    private String casServerLogoutUrl;

    @Value("${security.app.server.host.url}")
    private String appServerUrl;

    @Value("${security.app.login.url}")
    private String appLoginUrl;

    @Value("${security.app.logout.url}")
    private String appLogoutUrl;

}
