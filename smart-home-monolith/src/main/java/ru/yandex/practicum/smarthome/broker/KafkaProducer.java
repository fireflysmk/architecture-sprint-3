package ru.yandex.practicum.smarthome.broker;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    @Value( "${spring.kafka.producer.response-topic}" )
    private String COMMAND_RESPONSE_TOPIC;
    @Value( "${spring.kafka.producer.telemetry-feed-topic}" )
    private String TELEMETRY_FEED_TOPIC;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendCommandMessage(String correlationId, String message) {
        kafkaTemplate.send(COMMAND_RESPONSE_TOPIC, correlationId, message);
        System.out.println("Produced message: " + message);
    }

    public void sendTelemetryMessage(String message) {
        kafkaTemplate.send(TELEMETRY_FEED_TOPIC, message);
        System.out.println("Produced message: " + message);
    }
}

