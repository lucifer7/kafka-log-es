package com.yang.log.config;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Properties;

/**
 * Usage: <b> Load Kafka consumer config properties </b>
 *
 * @author Jingyi.Yang
 *         Date 2018/1/11
 **/
@Slf4j
public class ConsumerConfig {
    public static Properties getProps() {
        Properties props = new Properties();
        try {
            props.load(ConsumerConfig.class.getResourceAsStream("/kafka-consumer.properties"));
        } catch (IOException e) {
            log.error("<Load kafka-consumer.properties failed>", e);
        }
        return props;
    }
}
