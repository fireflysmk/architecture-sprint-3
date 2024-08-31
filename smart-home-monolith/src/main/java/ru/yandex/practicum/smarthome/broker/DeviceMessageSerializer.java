package ru.yandex.practicum.smarthome.broker;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class DeviceMessageSerializer implements Serializer<DeviceMessage> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, DeviceMessage data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing DeviceMessage", e);
        }
    }

    @Override
    public void close() {

    }
}
