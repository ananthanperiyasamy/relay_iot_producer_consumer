package com.iot.relay.producer.stream;

import com.iot.relay.model.IOTData;
import com.iot.relay.producer.service.IoTDataProducer;
import com.iot.relay.producer.utils.IoTConstants;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IoTDataProducerTest {

    @Mock
    private KafkaTemplate<String, IOTData> kafkaTemplate;

    @InjectMocks
    private IoTDataProducer producer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test a successful Kafka send.
     */
    @Test
    void testProduceIoTData_sendsMessage() {
        // Arrange: create a future to simulate a successful Kafka send
        SettableListenableFuture<SendResult<String, IOTData>> future = new SettableListenableFuture<>();
        future.set(new SendResult<>(null, new RecordMetadata(null, 0, 0, 0, 0L, 0, 0)));

        when(kafkaTemplate.send(anyString(), any(IOTData.class))).thenReturn(future);

        // Act
        producer.produceIoTData();

        // Assert: verify KafkaTemplate.send was called once
        ArgumentCaptor<IOTData> dataCaptor = ArgumentCaptor.forClass(IOTData.class);
        verify(kafkaTemplate, times(1)).send(eq("iot-topic"), dataCaptor.capture());

        IOTData sentData = dataCaptor.getValue();
        assertNotNull(sentData);
        assertTrue(sentData.getName().startsWith("Sensor-"));
        assertTrue(java.util.Arrays.asList(IoTConstants.IOT_TYPES).contains(sentData.getType()),"Type should be one of IoTConstants.IOT_TYPES");
        assertTrue(sentData.getValue() instanceof BigDecimal);
    }

    @Test
    void testProduceIoTData_handlesSendFailure() {
        // Arrange: simulate a failed send
        SettableListenableFuture<SendResult<String, IOTData>> failedFuture = new SettableListenableFuture<>();
        failedFuture.setException(new RuntimeException("Kafka failed"));
        when(kafkaTemplate.send(anyString(), any(IOTData.class))).thenReturn(failedFuture);

        // Act: call the producer method (it will catch/log the exception internally)
        producer.produceIoTData();

        // Assert: verify send was still called
        verify(kafkaTemplate, times(1)).send(anyString(), any(IOTData.class));
    }

}
