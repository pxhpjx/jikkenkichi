package org.nozomi.jikkenkichi.machikouba.kfk;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.nozomi.jikkenkichi.machikouba.pojo.LocalConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class KfkCommon {
    static ThreadPoolExecutor KFK_THREAD_POOL = new ThreadPoolExecutor(10, 30,
            5, TimeUnit.SECONDS, new ArrayBlockingQueue<>(50));

    @Autowired
    LocalConfig localConfig;

    @Bean
    public KafkaConsumer initKafkaConsumer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", localConfig.getKfkAddress());
        props.put("group.id", localConfig.getKfkGroupId());
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());

        props.put("enable.auto.commit", "false");
        //props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("auto.offset.reset", "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("tpk".split(",")));

        return consumer;
    }

}
