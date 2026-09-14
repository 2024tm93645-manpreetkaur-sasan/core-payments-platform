package com.corepayments.transactionservice.config;

import com.corepayments.transactionservice.client.AccountServiceClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.registry.ImportHttpServices;

@ImportHttpServices(group = "account-service", types = AccountServiceClient.class)
@Configuration(proxyBeanMethods = false)
public class RestClientConfig {
    // No manual RestClient or ProxyFactory boilerplate needed!
}