package com.boot.webservice;

import com.boot.webservice.jpa.BaseRepositoryFactoryBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.boot")
@EntityScan(basePackages = "com.boot.webservice.entity")
@EnableJpaRepositories(basePackages = "com.boot.webservice.jpa",repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class)
@EnableDiscoveryClient
public class WebServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebServiceApplication.class, args);
        log.info("-------WebServiceApplication Server Start Success-----");
    }
}
