package com.yang.log.elasticsearch;

import com.yang.log.config.ElasticsearchConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.RestClient;
import org.metawidget.util.simple.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
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
    // private TransportClient client;
    // private RestHighLevelClient client;
    private RestClient client;

    public EsClient(Properties properties) {
        BeanWrapper wrapper = new BeanWrapperImpl(elasticsearchConfig);
        BeanWrapper bulkWrapper = new BeanWrapperImpl(elasticsearchConfig.getBulkProcessor());
        wrapper.setPropertyValue("bulkProcessor", bulkWrapper.getWrappedInstance());
        properties.entrySet().forEach(item -> {
            String property = item.getKey().toString();
            Object value = item.getValue();

            String fieldName = property.startsWith(BULK_PROCESSOR_PREFIX) ? property : buildFieldName(property, "");
            //if (wrapper.isWritableProperty(fieldName) || bulkWrapper.isWritableProperty(fieldName)) {
            if (wrapper.isWritableProperty(fieldName)) {
                Objects.requireNonNull(value, property + " should be configured");
                wrapper.setPropertyValue(fieldName, value);
            }
        });

        initClient();
    }

    private String buildFieldName(String property, String prefix) {
        String field = property.trim().replace(prefix, "");
        return StringUtils.camelCase(field, SEPARATOR);
    }

    private void initClient() {
        log.info("<Initial Elasticsearch configuration: {}>", elasticsearchConfig);

        String[] nodes = elasticsearchConfig.getNodeHosts().split(",");
        HttpHost[] hosts = Arrays.stream(nodes).map(node -> {
            String[] hostPort = node.split(":");
            return new HttpHost(hostPort[0], Integer.parseInt(hostPort[1]));
        }).toArray(HttpHost[]::new);

        client = RestClient.builder(hosts).build();
    }


    public void addIndexToBulk(String indexName, String type, String id, String jsonString) {
        HttpEntity entity = new NStringEntity(jsonString, ContentType.APPLICATION_JSON);
        try {
            client.performRequest("POST", "/" + indexName + "/" + type + "/" + id, Collections.emptyMap(), entity);
        } catch (IOException e) {
            log.error("<Post data failed>", e);
        }
    }

}
