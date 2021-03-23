package org.nozomi.jikkenkichi.machikouba.kfk;

import com.alibaba.fastjson.JSON;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.nozomi.jikkenkichi.machikouba.pojo.MqBody;
import org.nozomi.jikkenkichi.machikouba.util.DebugTool;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class KfkProducer {
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    public void send(String topic, Object msg) {
        kafkaTemplate.send(topic, JSON.toJSONString(msg));
    }

    public void send(String topic, Integer partition, String key, Object value) {
        MqBody mq = new MqBody(value);
        saveMqToDb(mq);
        kafkaTemplate.send(new ProducerRecord<>(topic, partition, key, mq.toString()))
                .addCallback(
                        (suc) -> {
                            updateMqSendStatus(mq.getUuid(), "SUC");
                        },
                        (ex) -> {
                            updateMqSendStatus(mq.getUuid(), "FAIL");
                            DebugTool.recordAndSkip(ex);
                        });
    }

    //do nothing in demo
    int saveMqToDb(MqBody mq) {
        return 1;
    }

    //do nothing in demo
    int updateMqSendStatus(String uuid, String status) {
        return 1;
    }


    //do nothing in demo
    @Scheduled(fixedDelay = 1000)
    void resendFailedMq() {
    }
}
