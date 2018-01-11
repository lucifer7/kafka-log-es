package com.yang.log.config;

/**
 * Usage: <b> </b>
 * Will use properties later
 * @author Jingyi.Yang
 *         Date 2018/1/11
 **/
public final class KafkaConfig {
    public static final String TOPIC = "click_log_tracing_test";
    public static final String BOOTSTRAP_SERVERS = "10.200.159.84:9092";
    public static final String OFFSET_RESET = "smallest";
    public static final String OFFSETS_STORAGE = "zookeeper";       // Store offset in zookeeper or kafka
    public static final String OFFSET_STORAGE_TOPIC = "Kafka_consumer_offset";  // Topic for offset storage
    public static final String ZOOKEEPER = "10.200.159.84:2181";

}
