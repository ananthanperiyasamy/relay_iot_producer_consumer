package com.iot.relay.consumer.stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.OffsetDateTime;
import java.util.Properties;
import java.util.function.Function;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.kstream.KStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.iot.relay.model.IOTData;

import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;

public class IOTConsumerTest {
	
	private static final Logger log = LoggerFactory.getLogger(IOTConsumerTest.class);

    public static final String INPUT_TOPIC = "iotdatain";
    public static final String OUTPUT_TOPIC = "iotdataout";

    private TopologyTestDriver testDriver;
    private TestInputTopic<String, IOTData> inputTopic;
    private TestOutputTopic<String, IOTData> outputTopic;

    // Custom JSON Serde for IOTData
    private Serde<IOTData> iotDataSerde;

    private Function<KStream<String, IOTData>, KStream<String, IOTData>> toUpperCaseFn =
            input -> input.map((key, value) -> {
                value.setName(value.getName().toUpperCase());
                return KeyValue.pair(key, value);
            });

    @BeforeEach
    void setup() {
        //  Initialize the custom Serde first
        JsonSerializer<IOTData> serializer = new JsonSerializer<>();
        JsonDeserializer<IOTData> deserializer = new JsonDeserializer<>(IOTData.class);
        deserializer.addTrustedPackages("*");
        iotDataSerde = Serdes.serdeFrom(serializer, deserializer);

        // Build topology with correct Serdes
        StreamsBuilder builder = new StreamsBuilder();
        toUpperCaseFn.apply(
            builder.stream(INPUT_TOPIC, Consumed.with(Serdes.String(), iotDataSerde))
        ).to(OUTPUT_TOPIC, Produced.with(Serdes.String(), iotDataSerde));

        Topology topology = builder.build();

        Properties props = new Properties();
        props.put("application.id", "test-app");
        props.put("bootstrap.servers", "dummy:1234"); // dummy for TopologyTestDriver
        props.put("default.key.serde", Serdes.String().getClass().getName());
        props.put("default.value.serde", Serdes.ByteArray().getClass().getName());

        testDriver = new TopologyTestDriver(topology, props);

        // Test topics
        inputTopic = testDriver.createInputTopic(
                INPUT_TOPIC,
                Serdes.String().serializer(),
                iotDataSerde.serializer()
        );

        outputTopic = testDriver.createOutputTopic(
                OUTPUT_TOPIC,
                Serdes.String().deserializer(),
                iotDataSerde.deserializer()
        );
    }



    @AfterEach
    void tearDown() {
        if (testDriver != null) {
            testDriver.close();
        }
    }

    @Test
    void testSendReceive() {
        // Prepare test message
        IOTData iotData = new IOTData();
        iotData.setName("foo");
        iotData.setId(1234567890L);
        iotData.setTimestamp(OffsetDateTime.now().toString());
        iotData.setType("type1");
        iotData.setClusterId(1L);

        // pipe the record into the input topic
        inputTopic.pipeInput(null, iotData);

        // read the output
        KeyValue<String, IOTData> record = outputTopic.readKeyValue();

        assertNotNull(record, "No record received from output topic");
        log.info("Received record: {}", record.value);
        assertEquals("FOO", record.value.getName());
    }
}
