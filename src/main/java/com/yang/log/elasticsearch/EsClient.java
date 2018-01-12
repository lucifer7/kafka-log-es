package com.yang.log.elasticsearch;

import com.yang.log.config.ElasticsearchConfig;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.metawidget.util.simple.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.Properties;

/**
 * Usage: <b> </b>
 *
 * @author Jingyi.Yang
 *         Date 2018/1/12
 **/
@Slf4j
public class EsClient {
    private static final String BULK_PROCESSOR_PREFIX = "bulkProcessor";
    private static final char SEPARATOR = '.';

    private ElasticsearchConfig elasticsearchConfig = new ElasticsearchConfig();
    private TransportClient client;
    private BulkProcessor bulkProcessor;

    public EsClient(Properties properties) {
        BeanWrapper wrapper = new BeanWrapperImpl(elasticsearchConfig);
        BeanWrapper bulkWrapper = new BeanWrapperImpl(elasticsearchConfig.getBulkProcessor());
        wrapper.setPropertyValue("bulkProcessor", bulkWrapper.getWrappedInstance());
        properties.entrySet().forEach(item -> {
            String property = item.getKey().toString();
            Object value = item.getValue();
            /*if (property.startsWith(BULK_PROCESSOR_PREFIX)) {
                String fieldName = buildFieldName(property, BULK_PROCESSOR_PREFIX);
                if (Objects.nonNull(value) && bulkWrapper.isWritableProperty(fieldName)) {
                    bulkWrapper.setPropertyValue(fieldName, value);
                }
            } else {
                String fieldName = buildFieldName(property, "");
                if (wrapper.isWritableProperty(fieldName)) {
                    Objects.requireNonNull(value, fieldName + " should be configured");
                    wrapper.setPropertyValue(fieldName, value);
                }
            }*/

            //String prefix = property.startsWith(BULK_PROCESSOR_PREFIX) ? BULK_PROCESSOR_PREFIX + "." : "";
            //String fieldName = buildFieldName(property, prefix);
            String fieldName = property.startsWith(BULK_PROCESSOR_PREFIX) ? property : buildFieldName(property, "");
            //if (wrapper.isWritableProperty(fieldName) || bulkWrapper.isWritableProperty(fieldName)) {
            if (wrapper.isWritableProperty(fieldName)) {
                Objects.requireNonNull(value, property + " should be configured");
                wrapper.setPropertyValue(fieldName, value);
            }
        });

        initClient();
        initBulkProcessor();
    }

    private String buildFieldName(String property, String prefix) {
        String field = property.trim().replace(prefix, "");
        return StringUtils.camelCase(field, SEPARATOR);
    }

    private void initClient() {
        log.info("Initial Elasticsearch configuration: {}", elasticsearchConfig);
        Settings settings = Settings.builder()
                .put("cluster.name", elasticsearchConfig.getClusterName())
                .put("client.transport.sniff", elasticsearchConfig.isClientTransportSniff())
                .put("client.transport.ping_timeout", elasticsearchConfig.getClientTransportPing_timeout())
                .build();

        client = new PreBuiltTransportClient(settings);

        String[] nodes = elasticsearchConfig.getNodeHosts().split(",");
        for (String node : nodes) {
            String[] hostPort = node.split(":");
            client.addTransportAddress(new TransportAddress(
                    new InetSocketAddress(hostPort[0], Integer.parseInt(hostPort[1]))));
        }
    }

    private void initBulkProcessor() {
        ElasticsearchConfig.BulkProcessor bulkProcessor = elasticsearchConfig.getBulkProcessor();
        this.bulkProcessor = BulkProcessor.builder(client, new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
                // Action before commit
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                log.info("{} document(s) committed, time cost {}ms", response.getItems().length, response.getIngestTookInMillis());
                if (response.hasFailures()) {
                    log.error("Some documents committed failed", response.buildFailureMessage());
                }
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                log.error("Some documents committed failed", failure);
            }
        })
                .setBulkActions(bulkProcessor.getBulkActions())
                .setBulkSize(new ByteSizeValue(bulkProcessor.getBulkSize(), ByteSizeUnit.MB))
                .setFlushInterval(TimeValue.timeValueSeconds(bulkProcessor.getFlushInterval()))
                .setConcurrentRequests(bulkProcessor.getConcurrentRequests())
                .build();
    }

    public void addIndexToBulk(String indexName, String id, String jsonString) {
        IndexRequest indexRequest = new IndexRequest(indexName, elasticsearchConfig.getTypeName(), id)
                .source(jsonString, XContentType.JSON);
        bulkProcessor.add(indexRequest);
    }
}
