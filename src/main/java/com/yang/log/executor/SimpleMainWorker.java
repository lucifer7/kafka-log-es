package com.yang.log.executor;

import com.yang.log.elasticsearch.EsMessageWriter;
import com.yang.log.kafka.KafkaMessageReader;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static com.yang.log.config.KafkaConfig.BATCH_SIZE;

/**
 * Usage: <b> A simple worker coordinates a reader and writer pair </b>
 *
 * @author Jingyi.Yang
 *         Date 2018/1/11
 **/
public class SimpleMainWorker {
    private static String topicName = "tracer_log";
    private static String type = "log";

    public static void main(String[] args) {
        BlockingQueue<String> logQueue = new ArrayBlockingQueue<>(BATCH_SIZE);

        KafkaMessageReader reader = new KafkaMessageReader(logQueue);
        EsMessageWriter writer = new EsMessageWriter(logQueue, topicName, type);

        new Thread(reader).start();
        new Thread(writer).start();
    }
}
