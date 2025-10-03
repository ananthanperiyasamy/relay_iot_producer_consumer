package com.iot.relay.producer.stream;

import com.iot.relay.model.IOTData;
import com.iot.relay.producer.service.IoTDataProducer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@EnableKafka
@SpringBootTest(
        classes = IoTDataProducer.class,
        properties = {
                "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
                "spring.kafka.consumer.auto-offset-reset=earliest",
                "spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer",
                "spring.kafka.consumer.properties.spring.json.trusted.packages=*"
        }
)
@Import(com.iot.relay.producer.config.TestKafkaProducerConfig.class)
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = {"iot-topic"})
class IoTDataProducerIntegrationTest {

    @Autowired
    private IoTDataProducer producer;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private Consumer<String, IOTData> consumer;

    @BeforeEach
    void setUp() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(
                "testGroup", "true", embeddedKafkaBroker
        );
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        ConsumerFactory<String, IOTData> cf = new DefaultKafkaConsumerFactory<>(
                consumerProps,
                new StringDeserializer(),
                new JsonDeserializer<>(IOTData.class, false)
        );

        consumer = cf.createConsumer();
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    @AfterEach
    void tearDown() {
        if (consumer != null) {
            consumer.close();
        }
    }

    @Test
    void testProduceIoTDataIntegration() {
        // Produce a sample message
        producer.produceIoTData();

        // Consume the message
        ConsumerRecord<String, IOTData> record =
                KafkaTestUtils.getSingleRecord(consumer, "iot-topic");

        // Verify topic was created
        assertThat(embeddedKafkaBroker.getTopics()).contains("iot-topic");

        // Assertions
        assertNotNull(record.value().getType());
        assertEquals("Sensor-1", record.value().getName());
    }
}
