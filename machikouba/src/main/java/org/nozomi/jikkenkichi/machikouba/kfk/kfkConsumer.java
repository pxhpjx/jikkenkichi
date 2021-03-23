package org.nozomi.jikkenkichi.machikouba.kfk;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.nozomi.jikkenkichi.machikouba.util.DebugTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;

@Component
public class kfkConsumer {
    @Autowired
    KafkaConsumer<String, String> consumer;

    static int POLL_TIME_OUT = 1000;

    @Scheduled(fixedDelayString = "${local.config.kfk-consume-delay}")
    public void consume() {
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(POLL_TIME_OUT));
        HashMap<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
        for (TopicPartition partition : records.partitions()) {
            long offset = 0;
            for (ConsumerRecord<String, String> rec : records.records(partition)) {
                consumeMq(rec);
                offset = rec.offset() > offset ? rec.offset() : offset;
            }
            offsets.put(partition, new OffsetAndMetadata(offset));
        }
        if (!offsets.isEmpty()) {
            consumer.commitSync(offsets);
        }

    }

    void consumeMq(ConsumerRecord<String, String> rec) {
        saveMq();
        KfkCommon.KFK_THREAD_POOL.execute(() -> {
            DebugTool.print(String.format("consumer process topic: %s value: %s", rec.topic(), rec.value()));
            updateMqStatus();
        });
    }

    int saveMq() {
        return 1;
    }

    int updateMqStatus() {
        return 1;
    }

    @Scheduled(fixedDelay = 1000)
    void reconsumeFailedMq() {

    }
}
