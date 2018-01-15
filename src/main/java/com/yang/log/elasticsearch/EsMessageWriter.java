package com.yang.log.elasticsearch;

import com.google.common.collect.Lists;
import com.yang.log.config.ElasticsearchConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

import static com.yang.log.config.KafkaConfig.BATCH_SIZE;

/**
 * Usage: <b> Write log message to Elasticsearch </b>
 *
 * @author Jingyi.Yang
 *         Date 2018/1/11
 **/
@Slf4j
public class EsMessageWriter implements Runnable {
    private BlockingQueue<String> logQueue;
    private String topicName;

    public EsMessageWriter(BlockingQueue<String> logQueue, String topicName) {
        this.logQueue = logQueue;
        this.topicName = topicName;
    }

    @Override
    public void run() {
        //Temp output to console
        EsClient client = new EsClient(ElasticsearchConfig.loadProperties());
        try {

            while (true) {
                List<String> messages = Lists.newArrayListWithCapacity(BATCH_SIZE);
                logQueue.drainTo(messages, BATCH_SIZE);
                messages.forEach(message -> {
                    log.info("<Writing message [{}] to ES topic [{}]>", message, topicName);
                    client.addIndexToBulk(topicName, String.valueOf(UUID.randomUUID()), message);
                });
                client.flush();
            }
        } finally {
            client.close();;
        }
    }
}
