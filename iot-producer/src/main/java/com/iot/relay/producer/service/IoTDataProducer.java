package com.iot.relay.producer.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.iot.relay.model.IOTData;
import com.iot.relay.producer.utils.IoTConstants;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
@RequiredArgsConstructor
public class IoTDataProducer {

    private final KafkaTemplate<String, IOTData> kafkaTemplate;
    
    private final Random random = new Random();
    
    //@Value("${iot.kafka.topic}")
    //private String topic;
    
    

    private final AtomicLong idCounter = new AtomicLong(1);

    @Scheduled(fixedRate = 5000)
    public void produceIoTData() {
    	log.info("produceIoTData scheduled method called");
        IOTData data = new IOTData();
        data.setId(idCounter.getAndIncrement());
        data.setValue(BigDecimal.valueOf(Math.random() * 200));
        data.setTimestamp(Instant.now().toString());
        String randomType = IoTConstants.IOT_TYPES[random.nextInt(IoTConstants.IOT_TYPES.length)];
        data.setType(randomType);
        data.setName("Sensor-" + (data.getId() % 5));
        data.setClusterId(null);

        kafkaTemplate.send("iot-topic", data).addCallback( 
                result -> {
                    if (result != null) {
                        log.info(
                            "Successfully sent message to topic={} partition={} offset={} : {}",
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset(),
                            data
                        );
                    } else {
                        log.warn("Kafka send returned null result for data={}", data);
                    }
                },
                ex -> log.error("Failed to produce IoT message: {}", data, ex)
        );
    }
}

