package ru.yandex.practicum.smarthome.broker;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class DeviceMessageDeserializer implements Deserializer<DeviceMessage> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public DeviceMessage deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, DeviceMessage.class);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing DeviceMessage", e);
        }
    }

    @Override
    public void close() {

    }
}
