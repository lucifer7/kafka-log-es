package com.yang.log.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;
import java.util.Properties;

/**
 * Usage: <b> </b>
 *
 * @author Jingyi.Yang
 *         Date 2018/1/12
 **/
@Slf4j
//@Configuration
//@PropertySource("classpath:elasticsearch.properties")
@Getter
@Setter
@ToString
public class ElasticsearchConfig {

    // ElasticSearch的集群名称
    private String clusterName;
    // ElasticSearch的host
    private String nodeHosts;
    // 自动嗅探整个集群的状态，把集群中其它机器的ip地址加到客户端中
    private boolean clientTransportSniff;
    // ElasticSearch客户端链接超时时间
    private String clientTransportPing_timeout;
    // ElasticSearch的索引名称前缀
    private String indexPrefix;
    // ElasticSearch的索引名称后缀格式化方式
    private String indexFormat;
    // ElasticSearch的类型名称
    private String typeName;

    private BulkProcessor bulkProcessor = new BulkProcessor();

    @Getter
    @Setter
    public class BulkProcessor {
        // Optional properties
        // 缓冲池最大提交文档数（条）
        private int bulkActions = 1000;
        // 缓冲池总文档体积达到多少时提交（MB）
        private int bulkSize = 10;
        // 最大提交间隔（秒）
        private int flushInterval = 10;
        // 并行提交请求数
        private int concurrentRequests = 2;
    }

    public ElasticsearchConfig() {
    }

    public static Properties loadProperties() {
        Properties props = new Properties();
        try {
            props.load(ElasticsearchConfig.class.getResourceAsStream("/elasticsearch.properties"));
        } catch (IOException e) {
            log.error("<Load elasticsearch.properties failed>", e);
        }
        return props;
    }
}
