package com.yang.log.elasticsearch;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
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

    public EsMessageWriter(BlockingQueue<String> logQueue) {
        this.logQueue = logQueue;
    }

    @Override
    public void run() {
        while (true) {
            List<String> logs = Lists.newArrayListWithCapacity(BATCH_SIZE);
            logQueue.drainTo(logs, BATCH_SIZE);

            //Temp output to console
            logs.forEach(System.out::println);
        }
    }
}
