package com.corepayments.transactionservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;

@Configuration
public class HttpServiceGroupConfig {

    @Bean
    public RestClientHttpServiceGroupConfigurer accountServiceGroupConfigurer() {
        return groups -> groups.filterByName("account-service")
                .forEachClient((group, clientBuilder) -> clientBuilder.baseUrl("http://localhost:8081"));
    }
}