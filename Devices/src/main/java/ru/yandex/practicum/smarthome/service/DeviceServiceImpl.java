package ru.yandex.practicum.smarthome.service;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.smarthome.entity.Device;

import java.util.Properties;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    @Override
    public Device get_telemetry(Device device) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props);
        ProducerRecord<String, String> record = new ProducerRecord<>("telemetry-data", "telemetry", "device_id=10");
        producer.send(record);
        producer.close();

        return device;
    }
}