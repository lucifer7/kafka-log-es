package com.yang.log.elasticsearch;

import com.google.common.collect.Lists;
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
    private EsClient client;

    public EsMessageWriter(BlockingQueue<String> logQueue, EsClient client) {
        this.logQueue = logQueue;
        this.client = client;
    }

    @Override
    public void run() {
        //Temp output to console
        while (true) {
            List<String> messages = Lists.newArrayListWithCapacity(BATCH_SIZE);
            logQueue.drainTo(messages, BATCH_SIZE);
            messages.forEach(message -> {
                log.info("<Writing message [{}] to ES>", message);
                client.addDefaultIndex(String.valueOf(UUID.randomUUID()), message);
            });
        }
    }
}
