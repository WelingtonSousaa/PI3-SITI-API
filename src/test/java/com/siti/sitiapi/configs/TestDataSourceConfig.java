package com.siti.sitiapi.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.beans.factory.config.BeanPostProcessor;
import javax.sql.DataSource;

@Configuration
@Profile("test")
public class TestDataSourceConfig {

    @Bean
    public BeanPostProcessor dataSourceBeanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) {
                if (bean instanceof DataSource) {
                    return MySQLMetadataDataSourceProxy.wrap((DataSource) bean);
                }
                return bean;
            }
        };
    }
}
