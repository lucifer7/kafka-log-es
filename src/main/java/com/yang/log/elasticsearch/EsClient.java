package com.yang.log.elasticsearch;

import com.yang.log.config.ElasticsearchProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

/**
 * Usage: <b> </b>
 *
 * @author Jingyi.Yang
 *         Date 2018/1/12
 **/
@Slf4j
@Component
public class EsClient {
    private final ElasticsearchProperties esProperties;
    private final LogRollPolicy dailyIndexPolicy;

    // private TransportClient client;      // Will deprecated in future release
    // private RestHighLevelClient client;
    private RestClient client;

    @Autowired
    public EsClient(ElasticsearchProperties esProperties, LogRollPolicy dailyIndexPolicy) {
        this.esProperties = esProperties;
        this.dailyIndexPolicy = dailyIndexPolicy;
        log.info("<Initial Elasticsearch configuration: {}>", esProperties.toString());

        String[] nodes = esProperties.getNodeHosts().split(",");
        HttpHost[] hosts = Arrays.stream(nodes).map(node -> {
            String[] hostPort = node.split(":");
            return new HttpHost(hostPort[0], Integer.parseInt(hostPort[1]));
        }).toArray(HttpHost[]::new);

        this.client = RestClient.builder(hosts).build();
    }

    public void addIndexRequest(String indexName, String type, String id, String jsonString) {
        HttpEntity entity = new NStringEntity(jsonString, ContentType.APPLICATION_JSON);
        try {
            client.performRequest("POST", buildEndpoint(indexName, type, id), Collections.emptyMap(), entity);
        } catch (IOException e) {
            log.error("<Post data failed>", e);
        }
    }

    private String buildEndpoint(String indexName, String type, String id) {
        return "/" + indexName + "/" + type + "/" + id;
    }

    public void addDefaultIndex(String id, String jsonString) {
        addIndexRequest(dailyIndexPolicy.getIndexName(), dailyIndexPolicy.getType(), id, jsonString);
    }
}
