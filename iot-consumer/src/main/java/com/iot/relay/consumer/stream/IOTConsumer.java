package com.iot.relay.consumer.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.iot.relay.consumer.service.IOTEventService;
import com.iot.relay.model.IOTData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/* Stream listener class which receives all sensor data from a stream.
 * It buffers incoming IoT data, processes them in batches, 
 * and periodically flushes the buffer to persist data.
 * 
 * @author 
 *   Ananthan Periyasamy
 */
@Component
@AllArgsConstructor
@Slf4j
public class IOTConsumer {

	private IOTEventService iotEventService;
    private final List<IOTData> buffer = new ArrayList<>();
    private final int BATCH_SIZE = 10;

    /* Spring Cloud Stream consumer bean.
     * This method returns a Consumer<IOTData> function which gets invoked
     * whenever a new IOTData message is received from the stream.
     * The data is added to a local buffer. If the buffer size reaches the
     * defined {@link #BATCH_SIZE}, the buffer is flushed asynchronously.
     * @return a Consumer<IOTData> that processes incoming IoT data
     */
    @Bean
    public Consumer<IOTData> iotdata() {
        return iotData -> {
            synchronized (buffer) {
                buffer.add(iotData);
                if (buffer.size() >= BATCH_SIZE) {
                    flushBuffer();
                }
            }
        };
    }

    /*
     * Scheduled job that periodically flushes the buffer.
     * Runs at a fixed interval (every 1 second). If the buffer has data,
     * it will be flushed and processed, ensuring that small batches 
     * are not stuck waiting indefinitely.
     */
    @Scheduled(fixedRate = 1000) // flush every 1 second
    public void scheduledFlush() {
        synchronized (buffer) {
            if (!buffer.isEmpty()) {
            	log.debug("Batch size reached, flushing buffer");
                flushBuffer();
            }
        }
    }

    /*
     * Flushes the buffered IoT data to the database.
     * Copies the buffer contents into a batch, clears the buffer,
     * and asynchronously delegates saving the data to {@link IOTEventService}.
     * Using {@link CompletableFuture#runAsync(Runnable)} ensures that
     * saving does not block the main consumer thread.
     */
    private void flushBuffer() {
        List<IOTData> batch = new ArrayList<>(buffer);
        buffer.clear();
        CompletableFuture.runAsync(() -> iotEventService.saveAll(batch));
    }
}

