package com.yang.log.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Collections;
import java.util.Properties;

import static com.yang.log.config.KafkaConfig.*;

/**
 * Usage: <b> </b>
 *
 * @author Jingyi.Yang
 *         Date 2018/1/11
 **/
@Slf4j
public class SimpleKafkaConsumer {
    private static Consumer<Long, String> createConsumer() {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "KafkaConsumer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, OFFSET_RESET);

        final Consumer<Long, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singleton(TOPIC));
        return consumer;
    }

    static void runConsumer() {
        final Consumer<Long, String> consumer = createConsumer();
        final int giveUp = 100;
        int noRecordCount = 0;
        int timeout = 1000;

        while (true) {
            final ConsumerRecords<Long, String> consumerRecords = consumer.poll(timeout);
            if (consumerRecords.count() == 0) {
                noRecordCount++;
                if (noRecordCount > giveUp) break;
                else continue;
            }

            consumerRecords.forEach(record -> {
                log.info("<Consumer message: {} -> {}, partition={}, offset={}>",
                        record.key(), record.value(), record.partition(), record.offset());
            });
        }
    }

    public static void main(String[] args) {
        runConsumer();
    }
}
