package com.yang.log.config;

import com.yang.log.elasticsearch.DailyIndexPolicy;
import com.yang.log.elasticsearch.LogRollPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Usage: <b> </b>
 *
 * @author Jingyi.Yang
 *         Date 2018/1/23
 **/
@Configuration
@EnableConfigurationProperties(ElasticsearchProperties.class)
public class CollectorConfiguration {
    @Autowired
    private ElasticsearchProperties esProperties;

    @Bean
    @ConditionalOnProperty(name = "elasticsearch.index-roll-policy", havingValue = "dailyIndexPolicy", matchIfMissing = true)
    public LogRollPolicy dailyIndexPolicy() {
        return new DailyIndexPolicy(esProperties.getIndexPrefix(), esProperties.getIndexFormat(), esProperties.getType());
    }
}
