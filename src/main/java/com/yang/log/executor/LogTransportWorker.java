package com.yang.log.executor;

import com.yang.log.elasticsearch.EsClient;
import com.yang.log.elasticsearch.EsMessageWriter;
import com.yang.log.kafka.KafkaMessageReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static com.yang.log.config.KafkaConfig.BATCH_SIZE;

/**
 * Usage: <b> A log transport worker coordinates a reader and writer pair </b>
 *
 * @author Jingyi.Yang
 *         Date 2018/1/11
 **/
@Slf4j
@Component
public class LogTransportWorker implements ApplicationListener<ApplicationReadyEvent> {
    private final EsClient esClient;

    @Autowired
    public LogTransportWorker(EsClient esClient) {
        this.esClient = esClient;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        log.info("<Log transport worker start>");

        BlockingQueue<String> logQueue = new ArrayBlockingQueue<>(BATCH_SIZE);

        KafkaMessageReader reader = new KafkaMessageReader(logQueue);
        EsMessageWriter writer = new EsMessageWriter(logQueue, esClient);

        new Thread(reader).start();
        new Thread(writer).start();
    }
}
