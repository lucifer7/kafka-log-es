package com.yang.log.kafka;

import com.yang.log.config.ConsumerConfig;
import com.yang.log.config.KafkaConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.yang.log.config.KafkaConfig.MESSAGE_WAIT_TIME;
import static com.yang.log.config.KafkaConfig.POLL_TIMEOUT;

/**
 * Usage: <b> Read log message form Kafka </b>
 *
 * @author Jingyi.Yang
 *         Date 2018/1/11
 **/
@Slf4j
public class KafkaMessageReader implements Runnable {
    private BlockingQueue<String> logQueue;

    public KafkaMessageReader(BlockingQueue<String> logQueue) {
        this.logQueue = logQueue;
    }

    static KafkaConsumer<Long, String> consumer() {
        return new KafkaConsumer<>(ConsumerConfig.getProps());
    }

    @Override
    public void run() {
        final KafkaConsumer<Long, String> consumer = consumer();
        consumer.subscribe(Collections.singleton(KafkaConfig.TOPIC));
        while (true) {
            final ConsumerRecords<Long, String> records = consumer.poll(POLL_TIMEOUT);
            if (records.isEmpty()) {
                try {
                    TimeUnit.MILLISECONDS.sleep(MESSAGE_WAIT_TIME);
                    continue;
                } catch (InterruptedException e) {
                    log.error("<Unexpected interruption>", e);
                }
            }

            //List<String> logs = Lists.newArrayListWithCapacity(records.count());
            records.forEach(record -> {
                try {
                    logQueue.put(record.value());
                } catch (InterruptedException e) {
                    log.error("<Put record to queue failed>", e);
                }
            });
        }
    }
}
