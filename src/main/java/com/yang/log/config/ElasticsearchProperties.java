package com.yang.log.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Usage: <b> Property bean of Elasticsearch parsed from YAML, in node elasticsearch </b>
 *
 * @author Jingyi.Yang
 *         Date 2018/1/23
 **/
@Data
@ConfigurationProperties("elasticsearch")
public class ElasticsearchProperties {
    private String clusterName;         // Name of es cluster
    private String nodeHosts;           // Node list of es cluster, format: host:port[,host:port]
    private boolean clientSniff;        // If true, auto detect other node of the cluster and add to client
    private String clientPingTimeout;   // Timeout of client connection
    private String indexPrefix;         // Prefix of es index, depends on LogRollPolicy
    private String indexFormat;         // Appender pattern of es index
    private String type;                // type of data

    private BulkProcessor bulkProcessor;// Configuration of bulk processor

    @Data
    public static class BulkProcessor {
        private int bulkActions = 1000;     // Maximum number of logs in bulk
        private int bulkSize = 10;          // Maximum size of logs in bulk (mb)
        private int flushInterval = 10;     // Maximum interval of bulk flush (s)
        private int concurrentRequests = 2; // Number of concurrent requests
    }
}
